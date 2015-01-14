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
    for i in xrange(len(sequenceData)):
        if (len(sequenceData[i]) != n):
            print "Sequences of different sizes!"
            exit()

    if (n > len(genomeData)):
        print "Sequences are bigger than genom!"
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
    Hits is used for calculating accuracy score.
    '''
    n = len(genomeData)
    m = len(sequenceData[0])
    k = len(sequenceData)
    result = []
    sumHash_pattern = []
    mulHash_pattern = []
    sumHash = 0
    mulHash = 0
    hits = 0

    for i in xrange(k):
        sumHash_patternTemp = 0
        mulHash_patternTemp = 0
        for j in xrange(m):
            sumHash_patternTemp += ord(sequenceData[i][j])
            mulHash_patternTemp += (m - j)*ord(sequenceData[i][j])
        sumHash_pattern.append(sumHash_patternTemp)
        mulHash_pattern.append(mulHash_patternTemp)
    for i in xrange(m):
        sumHash += ord(genomeData[i])
        mulHash += (m-i)*ord(genomeData[i])

    for i in xrange(n - m + 1):
        for j in xrange(k):
            if((sumHash_pattern[j] == sumHash) and (mulHash_pattern[j] == mulHash)):
                hits += 1
                if (str(sequenceData[j]) == str(genomeData[i:i+m])):
                    result.append([i,j])
        if i < n -m:
            sumHash = sumHash - ord(genomeData[i]) + ord(genomeData[i + m])
            mulHash = mulHash - m*ord(genomeData[i]) + sumHash

    return result, hits

if __name__ == "__main__":
    '''
    Program takes only two arguments. First one is the genom, second one are all sequences.
    '''
    start = time.time()
    if(len(sys.argv) != 3):
        print "Less arguments than needed!"
        exit()
    if((os.stat(sys.argv[1]).st_size == 0) or (os.stat(sys.argv[2]).st_size == 0)):
        print "Empty files!"
        exit()

    genomeData, sequenceData = openFile(sys.argv[1:])
    result, hits = robinKarpAlgorithm(genomeData, sequenceData)

    for i in xrange(len(result)):
        print "Pattern number: %d is found on %d position!" % (result[i][1],result[i][0])
    end = time.time()
    print "Accuracy score: %.2f %%" % (len(result)/float(hits)*100)
    print "Memory used: " + str(resource.getrusage(resource.RUSAGE_SELF).ru_maxrss) + " KB"
    print "Time to run: %.2f sec" % (end-start)
