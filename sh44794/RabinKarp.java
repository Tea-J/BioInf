/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

/**
 *
 * @author serz
 */
public class RabinKarp {

    private byte[] genomeData = null;
    private HashClass genomeHash = null;
    private boolean sequanceHashesEqualLength = true;
    private List<HashClass> genomeHashList = null;
    private List<byte[]> sequenceDataList = new ArrayList<>();
    private List<HashClass> sequanceHashList = new ArrayList<HashClass>();
    private int sequenceSize;
    private int minSequenceSize;
    private int genomeSize;
    private Path[] sequencePathArray = null;
    private Path genomePath = null;
    private int hashHits = 0;
    private int actualHits = 0;
    private List<Integer> indexHitList = new ArrayList<>();
    private List<Point> indexPatternHitList = new ArrayList<>();
    private HashSet<HashClass> hashSet = new HashSet<>();
    private boolean sequancesHaveEqualHash = false;

    public static void main(String[] args) {

        Path[] sequencePathArray = new Path[args.length - 1];
        Path genPath = null;

        if (args.length < 2) {
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

        RabinKarp rabinKarpMulti = new RabinKarp(genPath, sequencePathArray);
        rabinKarpMulti.startRabinKarp();
    }

    RabinKarp(Path genomePath, Path[] sequencePathArray) {
        this.genomePath = genomePath;
        this.sequencePathArray = sequencePathArray;
    }

    void startRabinKarp() {
        long start = System.currentTimeMillis();
        long total = System.currentTimeMillis();
        prepareFiles();
        readFiles();
        long end = System.currentTimeMillis();
        System.out.println("Time elapsed for preparing and reading: " + (end - start) + " ms");
        start = System.nanoTime();
        if (sequanceHashesEqualLength) {
            initHash();
        } else {
            initHashDiff();
        }
        end = System.nanoTime();
        System.out.println("Time elapsed for init hash: " + (end - start) / 1000 + " us");

        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / mb + " Mb");
        start = System.currentTimeMillis();
        if (sequanceHashesEqualLength) {
            if (!sequancesHaveEqualHash) {
                runRabinKarp();
            } else {
                runRabinKarpHasEqual();
            }
        } else {
            runRabinKarpDifferentLengths();
        }

        end = System.currentTimeMillis();
        System.out.println("Time elapsed for algorithm: " + (end - start) + " ms");
        System.out.println("Total time elapsed: " + (end - total) + " ms");
        System.out.format("Matched patterns/Matched Hashes: %d/%d \n", actualHits, hashHits);
        System.out.format("Hash efficiency: %f \n", ((float) actualHits / hashHits));

        for (Point p : indexPatternHitList) {
            System.out.format("Pattern start index %d, pattern num %d \n", p.x, (p.y + 1));
        }
    }

    void prepareFiles() {
        String gP = genomePath.toString();
        int dot = gP.lastIndexOf('.');
        Path genomePath2 = Paths.get(gP.substring(0, dot) + "_edited" + gP.substring(dot));
        storeEditedFile(genomePath, genomePath2);
        genomePath = genomePath2;

        for (int i = 0; i < sequencePathArray.length; ++i) {
            Path p = sequencePathArray[i];
            String sP = p.toString();
            dot = sP.lastIndexOf('.');
            Path p2 = Paths.get(sP.substring(0, dot) + "_edited" + sP.substring(dot));
            File f = p2.toFile();
            if(!f.exists()){ //&& !f.isDirectory()
                storeEditedFile(p, p2);
            }
            sequencePathArray[i] = p2;
        }
    }

    void storeEditedFile(Path inPath, Path outPath) {
        Charset charset = StandardCharsets.ISO_8859_1;
        try (BufferedReader reader = Files.newBufferedReader(inPath, charset)) {
            FileOutputStream fos = new FileOutputStream(outPath.toFile());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, charset));
            String line = null;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(">")) {
                    bw.write(line);
                    firstLine = false;
                } else {
                    if (!firstLine) {
                        bw.write(System.lineSeparator());
                    }
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
            Charset charset = StandardCharsets.ISO_8859_1;
            for (Path p : sequencePathArray) {
                BufferedReader reader = Files.newBufferedReader(p, charset);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.equals("")) {
                        sequenceDataList.add(line.getBytes(charset));
                    }
                }
                //sequenceDataList.add(Files.readAllBytes(p));
            }
        } catch (IOException ex) {
            System.out.println("Error reading from files!");
        }
        sequenceSize = sequenceDataList.get(0).length;
        for (int i = 1; i < sequenceDataList.size(); ++i) {
            if (sequenceDataList.get(i).length != sequenceSize) {
                sequanceHashesEqualLength = false;
                if (sequenceDataList.get(i).length < sequenceSize) {
                    minSequenceSize = sequenceDataList.get(i).length;
                }
            }
        }
    }

    void runRabinKarp() {
        int i = 0;

        //initHash();
        while (i < (genomeSize - sequenceSize)) {
            if (hashSet.contains(genomeHash)) {
                int index = sequanceHashList.indexOf(genomeHash);
                if (index != -1) {
                    hashHits++;

                    if (checkPattern(i, index)) {
                        actualHits++;
                        indexPatternHitList.add(new Point(i, index));
                        //vrati odmah da ima ili broji kolko ih ima ili spremi pocetne indexe svakog ponavljanja
                    }
                }
            }
            hash(++i);
        }

        // check last sequence 
        for (int j = 0; j < sequanceHashList.size(); ++j) {
            if (hashSet.contains(genomeHash)) {
                int index = sequanceHashList.lastIndexOf(genomeHash);
                if (index != -1) {
                    hashHits++;
                    if (checkPattern(i, index)) {
                        actualHits++;
                        indexPatternHitList.add(new Point(i, index));
                        //vrati odmah da ima ili broji kolko ih ima ili spremi pocetne indexe svakog ponavljanja
                    }
                }
            }
        }
    }

    // jos to provjeriti
    void runRabinKarpHasEqual() {
        int i = 0;

        //initHash();
        while (i < (genomeSize - sequenceSize)) {
            if (hashSet.contains(genomeHash)) {
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
        }

        // check last sequence 
        if (hashSet.contains(genomeHash)) {
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

    }

    void runRabinKarpDifferentLengths() {
        int i = 0;

        //initHashDiff();
        //TODO provjeravati za do min duljinu podniza i paziti da ove duze ne gledam pred kraj
        while (i < (genomeSize - minSequenceSize)) {
            for (int j = 0; j < sequanceHashList.size(); ++j) {
                if (i <= genomeSize - sequenceDataList.get(j).length) {
                    HashClass h = sequanceHashList.get(j);
                    HashClass g = genomeHashList.get(j);
                    if (g.equals(h)) {
                        hashHits++;
                        if (checkPattern(i, j)) {
                            actualHits++;
                            indexPatternHitList.add(new Point(i, j));
                        }
                    }
                }
            }
            hashDiff(++i);
        }

        // check last sequence 
        for (int j = 0; j < sequanceHashList.size(); ++j) {
            if (i <= genomeSize - sequenceDataList.get(j).length) {
                HashClass h = sequanceHashList.get(j);
                HashClass g = genomeHashList.get(j);
                if (g.equals(h)) {
                    hashHits++;
                    if (checkPattern(i, j)) {
                        actualHits++;
                        indexPatternHitList.add(new Point(i, j));
                    }
                }
            }
        }
    }

    void hash(int start) {
        genomeHash.nextHash(genomeData[start - 1], genomeData[start + sequenceSize - 1], sequenceSize);
    }

    void hashDiff(int start) {
        for (int i = 0; i < sequenceDataList.size(); ++i) {
            HashClass gen = genomeHashList.get(i);
            int pattLen = sequenceDataList.get(i).length;
            if (start <= genomeSize - pattLen) {
                gen.nextHash(genomeData[start - 1], genomeData[start + pattLen - 1], pattLen);
            }
        }

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
                patSum += pattern[j];
                patMull += (sequenceSize - j) * pattern[j];
            }
            sequanceHashList.add(new HashClass(patMull, patSum));
        }

        for (int i = 0; i < sequanceHashList.size() - 1; i++) {
            HashClass h = sequanceHashList.get(i);
            hashSet.add(h);
            for (int j = i + 1; j < sequanceHashList.size(); j++) {
                if (h.equals(sequanceHashList.get(j))) {
                    sequancesHaveEqualHash = false;
                }

            }
        }
        hashSet.add(sequanceHashList.get(sequanceHashList.size() - 1));
    }

    void initHashDiff() {
        long patMull;
        long genMull;
        int patSum;
        int genSum;

        genomeHashList = new ArrayList<>();

        for (int i = 0; i < sequenceDataList.size(); ++i) {
            patMull = 0;
            patSum = 0;
            genMull = 0;
            genSum = 0;
            byte[] pattern = sequenceDataList.get(i);
            for (int j = 0; j < pattern.length; ++j) {
                patSum += pattern[j];
                patMull += (pattern.length - j) * pattern[j];
                genSum += genomeData[j];
                genMull += (pattern.length - j) * genomeData[j];
            }
            sequanceHashList.add(new HashClass(patMull, patSum));
            genomeHashList.add(new HashClass(genMull, genSum));
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
