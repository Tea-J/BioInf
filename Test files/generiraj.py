__author__ = 'sina'
import sys
from random import randint

if __name__ == '__main__':

    with open(sys.argv[1], "r") as genomeFile:
        genomeData = "".join(line.rstrip() for line in genomeFile if line.startswith(">") != True)
    genomeFile.close()
    n = int(sys.argv[2])
    d = int(sys.argv[3])
    randomList = []
    patternFiles = open("PatternFiles.fa", "w")

    for i in range(n):
        random = randint(0,len(genomeData) - d)
        randomList.append(random)

    randomList.sort()
    for random in randomList:
        patternFiles.write(">slijed %d: %d patterns, %d size \n" % (random, n, d))
        patternFiles.write(genomeData[random : random + d] + "\n")
    patternFiles.close()

    print (randomList)
