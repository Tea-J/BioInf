__author__ = 'sina'

import sys
import os
import time
import resource

def openFile(argv):
    '''
    Fuction is responsible for reading two fasta files. First file is stored in one string, and second file,
    with multiple sequences, is stored in list of strings.
    '''
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

    n = len(sequenceData[0])
    for i in range(len(sequenceData)):
        if (len(sequenceData[i]) != n):
            print ("Sequences of different sizes!")
            exit()

    if (n > len(genomeData)):
        print ("Sequences are bigger than genom!")
        exit()

    return genomeData, sequenceData

def robinKarpAlgorithm(genomeData, sequenceData):
    '''
    Function is responsible for finding sequences in genome. First it calculates sumHash and mulHash of all sequences.
    In main for loop it goes through whole genom and calculates sumHash and mulHash of that part of the genom. If they
    are equal than that part of genom is tested for equality with that sequence. It they are also equal then in list
    result is stored current position in genom.
    SumHash is sum of ASCII values of all letters in string. MulHash is also sum of ASCII values of all letters multiply
    by a factor. First one with a factor length of string, second with that factor minus 1, and so on to the last factor
    which is one.
    In every iteration new sumHash is calculated by subtraction ASCII value of old letter and by adding ASCII letter
    of new letter. MulHash is calculated by subtraction of ASCII value of old letter multiplied with factor length of
    string and by adding current value of sumHash.
    '''
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
                #broj += 1
                if (str(sequenceData[j]) == str(genomeData[i:i+m])):
                    result.append([i,j])
        if i < n -m:
            sumHash = sumHash - ord(genomeData[i]) + ord(genomeData[i + m])
            mulHash = mulHash - m*ord(genomeData[i]) + sumHash


    print  (resource.getrusage(resource.RUSAGE_SELF).ru_maxrss)
    #+ sys.getsizeof(sumHash_pattern) + sys.getsizeof(mulHash_pattern)
    #print ("Memory used: " + str(memory_size))
    return result, broj

if __name__ == "__main__":
    '''
    Program takes only two arguments. First one is the genom, second one are all sequences.
    '''
    start = time.time()
    if(len(sys.argv) != 3):
        print ("Less arguments than needed!")
        exit()
    if((os.stat(sys.argv[1]).st_size == 0) or (os.stat(sys.argv[2]).st_size == 0)):
        print ("Empty files!")
        exit()

    genomeData, sequenceData = openFile(sys.argv[1:])

    result, broj= robinKarpAlgorithm(genomeData, sequenceData)
    #print (resource.getrusage(resource.RUSAGE_SELF).ru_maxrss)
    #print ("broj: " + str(broj))
    #print ("result: " + str(result))
    for i in range(len(result), 0, -1):
        print ("Pattern number: %d is found on %d position!" % (result[i - 1][1] + 1,result[i - 1][0] + 1))
    end = time.time()
    print (end - start)
