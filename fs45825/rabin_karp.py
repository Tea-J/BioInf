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


    for i in range(n - m + 1):
        flag = 0
        for j in range (m):
            if(genomeData[i + j - 1] != sequenceData[j]):
                flag = 1
        if(flag == 0):
            print(i)


if __name__ == "__main__":
    start = time.time()
    if(len(sys.argv) != 3):
        print ("Fale argumenti!!!")
    if((os.stat(sys.argv[1]).st_size == 0) or (os.stat(sys.argv[2]).st_size == 0)):
        print ("Prazni nizovi")
    if(os.stat(sys.argv[1]).st_size < os.stat(sys.argv[2]).st_size):
        print ("Podniz veci od niza!!!")

    genomeData, sequenceData = openFile(sys.argv[1:])
    robinKarpAlgorithm(genomeData, sequenceData)
    end = time.time()
    print (end - start)
