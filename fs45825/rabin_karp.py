__author__ = 'sina'

import sys
import os
import time
from Bio import SeqIO

def openFile(argv):

    with open (argv[0], "r") as genomeFile:
        genomeData = "".join(line.rstrip() for line in genomeFile if (line.startswith(">") != True))
    genomeFile.close()

    with open (argv[1], "r") as sequenceFile:
        sequenceData = "".join(line.rstrip() for line in sequenceFile if (line.startswith(">") != True))
    sequenceFile.close()
    return (genomeData, sequenceData)


def robinKarpAlgorithm(genomeData, sequenceData):
    n = len(genomeData)
    m = len(sequenceData)
    result = []
    sumHash_pattern = 0
    mulHash_pattern = 0
    broj = 0
    sumHash = 0
    mulHash = 0

    for i in range(m):
        sumHash_pattern += ord(sequenceData[i])
        mulHash_pattern += (m - i)*ord(sequenceData[i])
        sumHash += ord(genomeData[i])
        mulHash += (m-i)*ord(genomeData[i])

    for i in range(n - m + 1):
        if((sumHash_pattern == sumHash) and (mulHash_pattern == mulHash)):
            match = True
            broj += 1
            for j in range(m):
                if (sequenceData[j] != genomeData[i + j]):
                    match = False
                    break
            if match:
                result.append(i)
        if i < n -m:
            sumHash = sumHash - ord(genomeData[i]) + ord(genomeData[i + m])
            mulHash = mulHash - m*ord(genomeData[i]) + sumHash
    return (result, broj)


if __name__ == "__main__":

    if(len(sys.argv) != 3):
        print ("Fale argumenti!!!")
    if((os.stat(sys.argv[1]).st_size == 0) or (os.stat(sys.argv[2]).st_size == 0)):
        print ("Prazni nizovi")
    #if(os.stat(sys.argv[1]).st_size < os.stat(sys.argv[2]).st_size):
    #    print ("Podniz veci od niza!!!")



    start = time.time()
    genomeData, sequenceData = openFile(sys.argv[1:])
    end = time.time()
    print (end - start)
    start2 = time.time()
    result, broj= robinKarpAlgorithm(genomeData, sequenceData)
    end2 = time.time()
    print ("broj: " + str(broj))
    print ("result: " + str(result))
    print (end2 - start2)
