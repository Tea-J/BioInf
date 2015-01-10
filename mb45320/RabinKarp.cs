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

            var genomeFilePath = args[GENOME_FILE_INDEX];

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
            var patterns = ReadPatternsFromFile(patternFilePath);

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