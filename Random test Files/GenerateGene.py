__author__ = 'sina'

import sys
import random

if __name__ == '__main__':

    n = int(sys.argv[1]) #duljina stringa
    randomList = []
    gene = open("GeneFile%dsize.fa" % n, "w")

    gene.write(">GeneFile%dsize\n" % n)

    letters = ['A', 'C', 'G', 'T']
    for i in range(n):
        gene.write(random.choice(letters))
    gene.close()
