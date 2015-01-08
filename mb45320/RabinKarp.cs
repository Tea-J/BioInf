using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace Bioinformatika
{
    class RabinKarp
    {

        private String genome;
        private String pattern;
        private int patternSize;
        private long prime;

        private int genomeHash;
        private int patternHash;

        public RabinKarp(String genome, String pattern)
        {
            this.genome = genome;
            this.pattern = pattern;
            this.patternSize = pattern.Length;
            this.prime = 11;

            hash(genome, pattern, patternSize, prime);
        }

        public static void Main(string[] args)
        {
            String genome = "ACAGTACGUTACGA";
            String pattern = "CGU";

            //var filePath = args[0];
            //ReadGenomeFromFile(filePath);

            RabinKarp rc = new RabinKarp(genome, pattern);
            var index = rc.search();
            Console.WriteLine("Done");
        }


        static void ReadGenomeFromFile(string filePath)
        {
            using (var reader = new StreamReader(filePath))
            {
                var line = "";
                do
                {
                    line = reader.ReadLine();
                    if (string.IsNullOrEmpty(line))
                        continue;
                    //line = line.Trim();
                    if (string.IsNullOrEmpty(line))
                        continue;
                    if (line[0] == '>')
                    {
                        Console.WriteLine("found '>'");
                        continue;
                    }
                    Console.WriteLine("line ->  " + line);
                    
                } while (!reader.EndOfStream);
            }
        }

        private int search() 
        {
            /*
            if (substring == None)
                return -1;
            if (substring == "")
                return -1;
            */
            int genomeSize = genome.Length;
            if (genomeSize < patternSize) return genomeSize;
            Console.Write("genome length: ");
            Console.WriteLine(genome.Length.ToString());

            // check for match at offset 0
            if (genomeHash == patternHash) {
                if (verifyHash(0))
                    return 0;
            }
            int h = (int)(Math.Pow(prime, patternSize - 1));
            Console.WriteLine("genomeHash: 0 " + genomeHash);
            for (int i = patternSize; i < genomeSize; i++)
            {
                genomeHash = (int)(prime*(genomeHash - h * (int)genome[i - patternSize]) + (int)genome[i]);
                Console.Write("genomeHash: " + i + " ");
                Console.WriteLine(genomeHash.ToString());
                if (genomeHash == patternHash)
                {
                    if (verifyHash(i - patternSize + 1)) 
                    {
                        Console.WriteLine("index: " + i);
                        return i;
                    }
                }
            }
            return 0;
        }

        private bool verifyHash(int index)
        {
            for (int j = 0; j < patternSize; j++)
                if (genome[index + j] != pattern[j]) return false;
            return true;
        }


        private void hash(String genome, String pattern, int patternSize, long prime)
        {
            for (int i = 0; i < patternSize; i++)
            {
                genomeHash += (int)((int)genome[i]*(Math.Pow(prime, (patternSize - i - 1))));
                patternHash += (int)((int)pattern[i]*(Math.Pow(prime, (patternSize - i - 1))));
            }
            Console.WriteLine(genomeHash.ToString());
            Console.WriteLine(patternHash.ToString());

        }

	}
}