__author__ = 'sina'

import sys
import os
import time

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
    hash_string = 0
    hash_pattern = 0
    for i in range(m):
        hash_string += ord(genomeData[i])
        hash_pattern += ord(sequenceData[i])
    for j in range(n-m+1):
        if hash_string==hash_pattern:
            match = True
            for k in range(m):
                if sequenceData[k] != genomeData[j+k]:
                    match = False
                    break
            if match:
                result.append(j)
        if j < n -m:
            hash_string -= ord(genomeData[j])
            hash_string += ord(genomeData[j + m])
    return result


if __name__ == "__main__":
    start = time.time()
    if(len(sys.argv) != 3):
        print ("Fale argumenti!!!")
    if((os.stat(sys.argv[1]).st_size == 0) or (os.stat(sys.argv[2]).st_size == 0)):
        print ("Prazni nizovi")
    if(os.stat(sys.argv[1]).st_size < os.stat(sys.argv[2]).st_size):
        print ("Podniz veci od niza!!!")

    genomeData, sequenceData = openFile(sys.argv[1:])
    position = robinKarpAlgorithm(genomeData, sequenceData)
    print (position)
    end = time.time()
    print (end - start)
