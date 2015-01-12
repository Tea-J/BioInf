/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bioinformatika;

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
    private List<byte[]> sequenceDataList = new ArrayList<>();
    private List<HashClass> sequanceHashList = new ArrayList<>();
    private Path[] sequencePathArray = null;
    private Path genomePath = null;
    private int hashHits = 0;
    private int actualHits = 0;
    List<Integer> indexHitList = new ArrayList<>();

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
            for (Path p : sequencePathArray) {
                sequenceDataList.add(Files.readAllBytes(p));
            }
        } catch (IOException ex) {
            Logger.getLogger(RabinKarp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error reading from files!");
        }
    }

    void runRabinKarp() {

    }

    void hash(int start) {
//        byte oldByte = genomeData[start - 1];
//        byte newByte = genomeData[start + sequenceSize - 1];
//
//        genomeSum = genomeSum - oldByte + newByte;
//        genomeMull = genomeMull - oldByte * sequenceSize + genomeSum;
    }

    void initHash() {
        
//        for (int i = 0; i < sequenceSize; ++i) {
//            sequenceSum += sequenceData[i];
//            sequenceMull += (sequenceSize - i) * sequenceData[i];
//            genomeSum += genomeData[i];
//            genomeMull += (sequenceSize - i) * genomeData[i];
//        }
    }

    boolean checkPattern(int start) {
//        for (int i = 0; i < sequenceSize; ++i) {
//            if (sequenceData[i] != genomeData[start + i]) {
//                return false;
//            }
//        }
        return true;
    }
}

class HashClass {

    int mull;
    int sum;

    HashClass(int mull, int sum) {
        this.mull = mull;
        this.sum = sum;
    }

    @Override
    public boolean equals(Object o) {        
        return super.equals(o); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        return super.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
