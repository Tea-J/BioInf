using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Text;
using System.Threading.Tasks;
using System.Diagnostics;

namespace Bioinformatika
{
    static class RabinKarp
    {
        const ulong BASE = 11;
        //const ulong HASH_SIZE = (2 << 20);

		
		public static void Main(string[] args)
        {
            const int NUMBER_OF_INPUT_ARGUMENTS = 2;
            const int GENOME_FILE_INDEX = 0;
            const int PATTERN_FILE_INDEX = 1;

            if (args.Length < NUMBER_OF_INPUT_ARGUMENTS)
            {
                Console.WriteLine("Wrong number of input arguments.");
                Console.WriteLine("Usage:");
                Console.WriteLine("RabinKarp [genome file path] [pattern file path]");
                return;
            }

            var genomeFilePath = args[GENOME_FILE_INDEX];

            if (!File.Exists(genomeFilePath))
            {
                Console.WriteLine("Genome file does not exist!");
                return;
            }

            try
            {
                var genomeTry = File.OpenRead(genomeFilePath);
                genomeTry.Close();
                genomeTry.Dispose();
            }
            catch
            {
                Console.WriteLine("Could not open the genome file!");
                return;
            }

            var patternFilePath = args[PATTERN_FILE_INDEX];

            if (!File.Exists(patternFilePath))
            {
                Console.WriteLine("Pattern file does not exist!");
                return;
            }

            try
            {
                var patternTry = File.OpenRead(patternFilePath);
                patternTry.Close();
                patternTry.Dispose();
            }
            catch
            {
                Console.WriteLine("Could not open the pattern file!");
                return;
            }
			
            var genome = ReadGenomeFromFile(genomeFilePath);

            if (string.IsNullOrEmpty(genome))
            {
                Console.WriteLine("Genome cannot be an empty string!");
                return;
            }

            var patterns = ReadPatternsFromFile(patternFilePath);

            if (patterns.Length == 0)
            {
                Console.WriteLine("No patterns found!");
                return;
            }

            if (patterns.Length == 1)
            {
                var instances = Search(genome, patterns[0]);
                if (instances.Count > 0)
                {
                    Console.Write("Pattern instances found on the following indices: ");
                    foreach (var instance in instances)
                        Console.Write("{0} ", instance);
                    Console.WriteLine();
                }
                else
                {
                    Console.WriteLine("No pattern instances found.");
                }

                Console.WriteLine();
                Console.WriteLine("{0} false positives found.", falsePositive);

                Console.ReadKey();
                return;
            }

            Console.WriteLine("Multiple  patterns  not supported... :(");
        }
		
        private static string ReadGenomeFromFile(string filePath)
        {
            return File.ReadAllText(filePath);
            //string genome = "";
            //var lines = File.ReadAllLines(filePath);
            //if (lines == null)
            //    return null;
            //int lineNumber = lines.Length;
            //for (int i = 0; i < lineNumber; i++)
            //{
            //    var line = lines[i];
            //    //if (string.IsNullOrEmpty(line))
            //    //    continue;
            //    //var gen = line.Trim();
            //    //if (string.IsNullOrEmpty(gen))
            //    //    continue;
            //    //if (line.StartsWith(">"))
            //    //    continue;
            //    genome += line;
            //}

            //return genome;
        }

        private static string[] ReadPatternsFromFile(string filePath)
        {
            var patterns = new List<string>();

            var lines = File.ReadAllLines(filePath);
            if (lines == null)
                return null;
            foreach (var line in lines)
            {
                if (string.IsNullOrEmpty(line))
                    continue;
                if (line.StartsWith(">"))
                    continue;
                patterns.Add(line);
            }
            return patterns.ToArray();
        }

        private static List<int> Search(string genome, string pattern) 
        {

            if (ReferenceEquals(genome, null))
                throw new ArgumentNullException("genome", "Genome cannot be null!");

            if (ReferenceEquals(pattern, null))
                throw new ArgumentNullException("pattern", "Pattern cannot be null!");

            if (string.IsNullOrEmpty(genome))
                throw new ArgumentException("Genome cannot be an empty string!", "genome");

            if (string.IsNullOrEmpty(pattern))
                throw new ArgumentException("Pattern  cannot be an empty string!", "pattern");

            if (genome.Length < pattern.Length)
                throw new ArgumentException("Genome cannot be shoter than the pattern.");

            var patternInstances = new List<int>();

            ulong patternLength = (ulong) pattern.Length, genomeLength = (ulong) genome.Length;

            var hashes = CalculateInitialHash(genome, pattern, patternLength, BASE);

            ulong genomeHash = hashes.Item1, patternHash = hashes.Item2;

            if (genomeHash == patternHash)
            {
                if (IsSubstring(genome, pattern, 0))
                    patternInstances.Add(0);
            }
			
			ulong factor = CalculateFactor(patternLength, BASE);

            for (int i = (int) patternLength; i < (int) genomeLength; i++)
            {
                genomeHash -= (genome[i - (int)patternLength] * factor);// % HASH_SIZE;
                genomeHash *= BASE;
                genomeHash += genome[i];
                //genomeHash %= HASH_SIZE;

                //genomeHash = (BASE * (genomeHash - (factor * genome[i - (int)patternLength])) + genome[i]) % HASH_SIZE;
                if (genomeHash == patternHash)
                    if (IsSubstring(genome, pattern, i - (int)patternLength + 1))
                        patternInstances.Add(i - (int) patternLength + 1);
            }

            return patternInstances;
        }
		
        static ulong falsePositive = 0;

        private static bool IsSubstring(string genome, string pattern, int index)
        {
            for (int i = 0; i < pattern.Length; i++)
                if (genome[index + i] != pattern[i])
                {
                    falsePositive++;
                    return false;
                }
            return true;
        }
		
        private static Tuple<ulong,ulong> CalculateInitialHash(string genome, string pattern, ulong patternSize, ulong pBase)
        {
            ulong genomeHash = 0;
            ulong patternHash = 0;
            ulong basePower = 1;
            for (int i = (int)patternSize - 1; i >= 0; i--, basePower*= pBase)
            {
                genomeHash += (genome[i] * basePower);// % HASH_SIZE;
                patternHash += (pattern[i] * basePower);// % HASH_SIZE;
            }
            return new Tuple<ulong, ulong>(genomeHash, patternHash);
        }

        private static ulong CalculateFactor(ulong patternSize, ulong pBase)
        {
            ulong power = 1;
            for (int i = 0; i < (int) patternSize - 1; i++)
                power = (power * pBase);// % HASH_SIZE;
            return power;
        }
	}
}