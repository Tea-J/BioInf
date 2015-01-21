__author__ = 'sina'

import sys
import random

if __name__ == '__main__':

    n = int(sys.argv[1])
    d = int(sys.argv[2])
    randomList = []
    patternFiles = open("Random%dpatterns%dsize.fa" % (n, d), "w")

    letters = ['A', 'C', 'G', 'T']
    for i in range(n):
        patternFiles.write(">slijed %d: %d patterns, %d size \n" % (i, n, d))
        for j in range(d):
            patternFiles.write(random.choice(letters))
        patternFiles.write("\n")
    patternFiles.close()
