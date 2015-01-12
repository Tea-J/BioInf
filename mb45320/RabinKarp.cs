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
        const ulong HASH_SIZE = (2 << 20);

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

            Console.WriteLine("Attempting to verify the genome file...");
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

            Console.WriteLine("Attempting to verify the pattern file...");
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

            Console.WriteLine("Reading the genome file...");
            Stopwatch stopwatch = new Stopwatch();
            stopwatch.Reset();
            stopwatch.Start();
            var genome = ReadGenomeFromFile(genomeFilePath);
            stopwatch.Stop();
            Console.WriteLine("Reading the genome file took {0} ms.", stopwatch.ElapsedMilliseconds);
            if (string.IsNullOrEmpty(genome))
            {
                Console.WriteLine("Genome cannot be an empty string!");
                return;
            }

            Console.WriteLine("Reading the pattern file...");
            stopwatch.Reset();
            stopwatch.Start();
            var patterns = ReadPatternsFromFile(patternFilePath);
            stopwatch.Stop();
            Console.WriteLine("Reading the pattern file took {0} ms.", stopwatch.ElapsedMilliseconds);

            if (patterns.Length == 0)
            {
                Console.WriteLine("No patterns found!");
                return;
            }

            if (patterns.Length == 1)
            {
                Console.WriteLine("Starting the search...");
                Console.WriteLine();
                var instances = Search(genome, patterns[0]);
                Console.WriteLine();
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

            StartMultipleSearch(genome, patterns);
        }

        private static void StartMultipleSearch(string genome, string[] patterns)
        {
            if (patterns.Contains(null))
                throw new ArgumentException("Patterns cannot contain null values!", "patterns");

            var groups = patterns.GroupBy(pattern => pattern.Length);
            foreach (var group in groups)
            {
                var groupArray = group.ToArray();
                SearchGroup(genome, groupArray);
            }

            Console.WriteLine();
            Console.WriteLine("False positives: {0} ", falsePositive);
            Console.ReadKey();
        }

        private static void SearchGroup(string genome, string[] patterns)
        {
            var instances = SearchMultiple(genome, patterns);

            bool found = false;
            for (int i = 0; i < patterns.Length; i++)
            {
                if (instances[i].Count > 0)
                {
                    Console.Write("{0} found on indices: ", patterns[i]);
                    foreach (var index in instances[i])
                        Console.Write("{0} ", index);
                    Console.WriteLine();
                    found = true;
                }
                if (!found)
                {
                    Console.WriteLine("No patterns of length {0} found in the genome.", patterns[0].Length);
                }
            }
            Console.WriteLine();
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

        private static List<int>[] SearchMultiple(string genome, string[] patterns)
        {
            if (ReferenceEquals(genome, null))
                throw new ArgumentNullException("genome", "Genome cannot be null!");

            if (ReferenceEquals(patterns, null))
                throw new ArgumentNullException("pattern", "Patterns cannot be null!");

            if (string.IsNullOrEmpty(genome))
                throw new ArgumentException("Genome cannot be an empty string!", "genome");

            if (patterns.Contains(null))
                throw new ArgumentException("Patterns cannot contain null values!", "pattern");

            if (patterns.Any(pattern => pattern.Length != patterns[0].Length))
                throw new ArgumentException("All patterns must have the same size!", "pattern");

            if (genome.Length < patterns[0].Length)
                throw new ArgumentException("Genome cannot be shoter than the pattern.");

            Stopwatch stopwatch = new Stopwatch();
            stopwatch.Reset();

            var patternInstances = CreateReturnLists(patterns.Length);

            ulong patternLength = (ulong)patterns[0].Length, genomeLength = (ulong)genome.Length;

            stopwatch.Start();
            var hashes = CalculateInitialHash(genome, patterns, patternLength, BASE);
            stopwatch.Stop();
            Console.WriteLine("Calculating the initial hashes took {0} ms or {1} timer ticks.",
                    stopwatch.ElapsedMilliseconds, stopwatch.ElapsedTicks);

            ulong genomeHash = hashes.Item1;
            var patternHashes = hashes.Item2;

            // check for match at offset 0
            for (int i = 0; i < patternHashes.Length; i++)
            {
                if (genomeHash == patternHashes[i])
                {
                    stopwatch.Reset();
                    stopwatch.Start();
                    if (IsSubstring(genome, patterns[i], 0))
                        patternInstances[i].Add(0);
                    stopwatch.Stop();
                    Console.WriteLine("Comparison of pattern and substring took {0} ms or {1} timer ticks.",
                        stopwatch.ElapsedMilliseconds, stopwatch.ElapsedTicks);
                }
            }

            ulong factor = CalculateFactor(patternLength, BASE);

            stopwatch.Reset();
            stopwatch.Start();
            for (int i = (int)patternLength; i < (int)genomeLength; i++)
            {
                genomeHash -= (genome[i - (int)patternLength] * factor);// % HASH_SIZE;
                genomeHash *= BASE;
                genomeHash += genome[i];

                for (int j = 0; j < patterns.Length; j++)
                {
                    if (genomeHash == patternHashes[j])
                        if (IsSubstring(genome, patterns[j], i - (int)patternLength + 1))
                            patternInstances[j].Add(i - (int)patternLength + 1);
                }
            }
            stopwatch.Stop();
            Console.WriteLine("The search took {0} ms or {1} timer ticks. Number of loop iterations was {2}.",
                stopwatch.ElapsedMilliseconds, stopwatch.ElapsedTicks, genomeLength - 2);
            Console.WriteLine();
            if (stopwatch is IDisposable)
                (stopwatch as IDisposable).Dispose();

            return patternInstances;
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

            Stopwatch stopwatch = new Stopwatch();
            stopwatch.Reset();

            var patternInstances = new List<int>();

            ulong patternLength = (ulong)pattern.Length, genomeLength = (ulong)genome.Length;

            stopwatch.Start();
            var hashes = CalculateInitialHash(genome, pattern, patternLength, BASE);
            stopwatch.Stop();
            Console.WriteLine("Calculating the initial hashes took {0} ms or {1} timer ticks.",
                stopwatch.ElapsedMilliseconds, stopwatch.ElapsedTicks);

            ulong genomeHash = hashes.Item1, patternHash = hashes.Item2;

            // check for match at offset 0
            if (genomeHash == patternHash)
            {
                stopwatch.Reset();
                stopwatch.Start();
                if (IsSubstring(genome, pattern, 0))
                    patternInstances.Add(0);
                stopwatch.Stop();
                Console.WriteLine("Comparison of pattern and substring took {0} ms or {1} timer ticks.",
                    stopwatch.ElapsedMilliseconds, stopwatch.ElapsedTicks);
            }

            ulong factor = CalculateFactor(patternLength, BASE);

            stopwatch.Reset();
            stopwatch.Start();
            for (int i = (int)patternLength; i < (int)genomeLength; i++)
            {
                genomeHash -= (genome[i - (int)patternLength] * factor);// % HASH_SIZE;
                genomeHash *= BASE;
                genomeHash += genome[i];
                //genomeHash %= HASH_SIZE;

                //genomeHash = (BASE * (genomeHash - (factor * genome[i - (int)patternLength])) + genome[i]) % HASH_SIZE;
                if (genomeHash == patternHash)
                    if (IsSubstring(genome, pattern, i - (int)patternLength + 1))
                        patternInstances.Add(i - (int)patternLength + 1);
            }
            stopwatch.Stop();
            Console.WriteLine("The search took {0} ms or {1} timer ticks. Number of loop iterations was {2}.",
                stopwatch.ElapsedMilliseconds, stopwatch.ElapsedTicks, genomeLength - 2);
            if (stopwatch is IDisposable)
                (stopwatch as IDisposable).Dispose();

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

        private static Tuple<ulong, ulong> CalculateInitialHash(string genome, string pattern, ulong patternSize, ulong pBase)
        {
            ulong genomeHash = 0;
            ulong patternHash = 0;
            ulong basePower = 1;
            for (int i = (int)patternSize - 1; i >= 0; i--, basePower *= pBase)
            {
                genomeHash += (genome[i] * basePower);// % HASH_SIZE;
                patternHash += (pattern[i] * basePower);// % HASH_SIZE;
            }
            return new Tuple<ulong, ulong>(genomeHash, patternHash);
        }

        private static Tuple<ulong, ulong[]> CalculateInitialHash(string genome, string[] patterns, ulong patternSize, ulong pBase)
        {
            ulong genomeHash = 0;
            ulong[] patternHashes = new ulong[patterns.Length];
            for (int i = 0; i < patternHashes.Length; i++)
                patternHashes[i] = 0;

            ulong basePower = 1;
            for (int i = (int)patternSize - 1; i >= 0; i--, basePower *= pBase)
            {
                genomeHash += (genome[i] * basePower);
                for (int j = 0; j < patterns.Length; j++)
                    patternHashes[j] += (patterns[j][i] * basePower);
            }
            return new Tuple<ulong, ulong[]>(genomeHash, patternHashes);
        }

        private static List<int>[] CreateReturnLists(int numberOfPatterns)
        {
            var returnLists = new List<int>[numberOfPatterns];
            for (int i = 0; i < numberOfPatterns; i++)
                returnLists[i] = new List<int>();
            return returnLists;
        }

        private static ulong CalculateFactor(ulong patternSize, ulong pBase)
        {
            ulong power = 1;
            for (int i = 0; i < (int)patternSize - 1; i++)
                power = (power * pBase);// % HASH_SIZE;
            return power;
        }
    }
}