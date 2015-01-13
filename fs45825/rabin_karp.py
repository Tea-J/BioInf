__author__ = 'sina'

import sys
import os
import time

#from memory_profiler import profile

#@profile
def openFile(argv):

    with open (argv[0], "r") as genomeFile:
        genomeData = "".join(line.rstrip() for line in genomeFile if (line.startswith(">") != True))
    genomeFile.close()

    sequenceData = []
    sequenceFile = open(argv[1], "r")
    str = ""
    while 1:
        line = sequenceFile.readline().rstrip()
        if line.startswith(">") != True:
            str += line
        else:
            if str!="":
                sequenceData.append(str)
            str = ""
        if line == "":
            if str!="":
                sequenceData.append(str)
            break
    sequenceFile.close()
    return genomeData, sequenceData

def robinKarpAlgorithm(genomeData, sequenceData):
    n = len(genomeData)
    m = len(sequenceData[0])
    k = len(sequenceData)
    result = []
    sumHash_pattern = []
    mulHash_pattern = []
    broj = 0
    sumHash = 0
    mulHash = 0

    for i in range(k):
        sumHash_patternTemp = 0
        mulHash_patternTemp = 0
        for j in range(m):
            sumHash_patternTemp += ord(sequenceData[i][j])
            mulHash_patternTemp += (m - j)*ord(sequenceData[i][j])
        sumHash_pattern.append(sumHash_patternTemp)
        mulHash_pattern.append(mulHash_patternTemp)
    for i in range(m):
        sumHash += ord(genomeData[i])
        mulHash += (m-i)*ord(genomeData[i])


    for i in range(n - m + 1):
        for j in range(k):
            if((sumHash_pattern[j] == sumHash) and (mulHash_pattern[j] == mulHash)):
                broj += 1
                if (str(sequenceData[j]) == str(genomeData[i:i+m])):
                    result.append(i)
        if i < n -m:
            sumHash = sumHash - ord(genomeData[i]) + ord(genomeData[i + m])
            mulHash = mulHash - m*ord(genomeData[i]) + sumHash

    return (result, broj)


if __name__ == "__main__":
    start = time.time()
    if(len(sys.argv) != 3):
        print ("Fale argumenti!!!")
    if((os.stat(sys.argv[1]).st_size == 0) or (os.stat(sys.argv[2]).st_size == 0)):
        print ("Prazni nizovi")
    #if(os.stat(sys.argv[1]).st_size < os.stat(sys.argv[2]).st_size):
    #    print ("Podniz veci od niza!!!")

    genomeData, sequenceData = openFile(sys.argv[1:])

    result, broj= robinKarpAlgorithm(genomeData, sequenceData)

    print ("broj: " + str(broj))
    print ("result: " + str(result))

    end = time.time()
    print (end - start)
