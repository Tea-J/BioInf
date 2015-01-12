/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bioinformatika;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author serz
 */
public class RabinKarpMulti {

    private byte[] genomeData = null;
    private HashClass genomeHash = null;
    private boolean sequanceHashesEqualLength = true;
    private List<HashClass> genomeHashList = new ArrayList<>();
    private List<byte[]> sequenceDataList = new ArrayList<>();
    private List<HashClass> sequanceHashList = new ArrayList<HashClass>();
    private int sequenceSize;
    private int genomeSize;
    private Path[] sequencePathArray = null;
    private Path genomePath = null;
    private int hashHits = 0;
    private int actualHits = 0;
    List<Integer> indexHitList = new ArrayList<>();
    List<Point> indexPatternHitList = new ArrayList<>();

    public static void main(String[] args) {

        Path[] sequencePathArray = new Path[args.length - 1];
        Path genPath = null;

        if (args.length <= 2) {
            System.out.println("Program requires at least two arguments: ");
            System.out.println("1. Path to genome");
            System.out.println("2. Path to sequence");
            return;
        }

        try {
            genPath = Paths.get(args[0]);
        } catch (Exception e) {
            System.out.println("First argument is not a path to file or file");
        }

        for (int i = 1; i < args.length; ++i) {
            try {
                sequencePathArray[i - 1] = Paths.get(args[i]);
            } catch (Exception e) {
                System.out.format("Argument %d is not a path to file or file", i + 1);
            }
        }

        RabinKarpMulti rabinKarpMulti = new RabinKarpMulti(genPath, sequencePathArray);
        rabinKarpMulti.startRabinKarp();
    }

    RabinKarpMulti(Path genomePath, Path[] sequencePathArray) {
        this.genomePath = genomePath;
        this.sequencePathArray = sequencePathArray;
    }

    void startRabinKarp() {
        long start = System.currentTimeMillis();
        prepareFiles();
        readFiles();
        runRabinKarp();

        long end = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (end - start) + " ms");
        System.out.format("Matched patterns/Matched Hashes: %d/%d \n", actualHits, hashHits);
        System.out.format("Hash efficiency: %f \n", ((float) actualHits / hashHits));

        for (Integer i : indexHitList) {
            System.out.println("Pattern start index : " + i);
        }
    }

    void prepareFiles() {
        String gP = genomePath.toString();
        int dot = gP.lastIndexOf('.');
        Path genomePath2 = Paths.get(gP.substring(0, dot) + "_edited" + gP.substring(dot));
        storeEditedFile(genomePath, genomePath2);
        genomePath = genomePath2;

        for (Path p : sequencePathArray) {
            String sP = p.toString();
            dot = sP.lastIndexOf('.');
            Path p2 = Paths.get(sP.substring(0, dot) + "_edited" + sP.substring(dot));
            storeEditedFile(p, p2);
            p = p2;
        }
    }

    void storeEditedFile(Path inPath, Path outPath) {
        Charset charset = StandardCharsets.ISO_8859_1;
        try (BufferedReader reader = Files.newBufferedReader(inPath, charset)) {
            FileOutputStream fos = new FileOutputStream(outPath.toFile());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, charset));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(">")) {
                    bw.write(line);
                }
            }
            reader.close();
            bw.close();
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    void readFiles() {
        try {
            genomeData = Files.readAllBytes(genomePath);
            genomeSize = genomeData.length;
            for (Path p : sequencePathArray) {
                sequenceDataList.add(Files.readAllBytes(p));
            }
        } catch (IOException ex) {
            Logger.getLogger(RabinKarp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error reading from files!");
        }
        sequenceSize = sequenceDataList.get(0).length;
        for (int i = 1; i < sequenceDataList.size(); ++i) {
            if (sequenceDataList.get(i).length != sequenceSize) {
                sequanceHashesEqualLength = false;
            }
        }
    }

    void runRabinKarp() {
        int i = 0;

        initHash();

        while (i < (genomeSize - sequenceSize)) {
            for (int j = 0; j < sequanceHashList.size(); ++j) {
                HashClass h = sequanceHashList.get(j);
                if (genomeHash.equals(h)) {
                    hashHits++;

                    if (checkPattern(i, j)) {
                        actualHits++;
                        indexPatternHitList.add(new Point(i, j));
                        //vrati odmah da ima ili broji kolko ih ima ili spremi pocetne indexe svakog ponavljanja
                    }
                }
            }
        }
        hash(++i);

        // check last sequence 
        for (int j = 0; j < sequanceHashList.size(); ++j) {
            HashClass h = sequanceHashList.get(j);
            if (genomeHash.equals(h)) {
                hashHits++;
                if (checkPattern(i, j)) {
                    actualHits++;
                    indexPatternHitList.add(new Point(i, j));
                }
            }
        }

    }

    void hash(int start) {
        genomeHash.nextHash(genomeData[start - 1], genomeData[start + sequenceSize - 1], sequenceSize);
    }

    void initHash() {
        long patMull = 0;
        int patSum = 0;

        for (int i = 0; i < sequenceSize; ++i) {
            patSum += genomeData[i];
            patMull += (sequenceSize - i) * genomeData[i];
        }
        genomeHash = new HashClass(patMull, patSum);

        for (int i = 0; i < sequenceDataList.size(); ++i) {
            patMull = 0;
            patSum = 0;
            byte[] pattern = sequenceDataList.get(i);
            for (int j = 0; j < pattern.length; ++j) {
                patSum += pattern[i];
                patMull += (sequenceSize - i) * pattern[i];
            }
            sequanceHashList.add(new HashClass(patMull, patSum));
        }
    }

    boolean checkPattern(int start, int pat) {
        byte[] pattern = sequenceDataList.get(pat);
        for (int i = 0; i < pattern.length; ++i) {
            if (pattern[i] != genomeData[start + i]) {
                return false;
            }
        }
        return true;

    }

}

class HashClass {

    private long mull;
    private int sum;

    HashClass(long mull, int sum) {
        this.mull = mull;
        this.sum = sum;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HashClass)) {
            return false;
        }

        HashClass h = (HashClass) o;
        return (this.mull == h.mull && this.sum == h.sum);
    }

    @Override
    public int hashCode() {
        int l = (int) (mull & 0x0FFFF) << 16;
        return l | (sum & 0x0FFFF);
    }

    public void nextHash(byte oldByte, byte newByte, int size) {
        sum = sum - oldByte + newByte;
        mull = mull - oldByte * size + sum;
    }

}
