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
public class RabinKarp {

    private byte[] genomeData = null;
    private byte[] sequenceData = null;
    private Path genomePath = null;
    private Path sequencePath = null;
    private Path genomePath2 = null;
    private Path sequencePath2 = null;
    private int sequenceSize;
    private int genomeSize;
    private long sequenceHash;
    private int sequenceSum;
    private long genomeHash;
    private int genomeSum;
    private int hashHits = 0;
    private int actualHits = 0;
    List<Integer> list = new ArrayList<>();


    private long genomeHashTest;
    private int genomeSumTest;

    //TODO provjeriti usporedbe int == int, byte == byte, long == long ili mora biti equals, razlika u brzini
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Path genomePath = Paths.get("C:\\Users\\serz\\Desktop\\BIOINF\\PROJEKT\\Escherichia_coli_asm59784v1.GCA_000597845.1.24.dna.toplevel.fa");
        Path sequencePath = Paths.get("C:\\Users\\serz\\Desktop\\BIOINF\\PROJEKT\\tekst.txt");
        
//        Path genomePath = Paths.get("C:\\Users\\serz\\Desktop\\BIOINF\\PROJEKT\\gen.txt");
//        Path sequencePath = Paths.get("C:\\Users\\serz\\Desktop\\BIOINF\\PROJEKT\\tekst.txt");
        
        if(args.length == 2){
            genomePath = Paths.get(args[0]);
            sequencePath = Paths.get(args[1]);
        }
//        else{
//            System.out.println("Program requires two arguments: ");
//            System.out.println("1. Path to genome");
//            System.out.println("2. Path to sequence");
//            return;
//        }

        RabinKarp rabinKarp = new RabinKarp(genomePath, sequencePath);
        rabinKarp.startRabinKarp();
        //rabinKarp.test();
        
    }

    RabinKarp(Path genomePath, Path sequencePath) {
        this.genomePath = genomePath;
        this.sequencePath = sequencePath;
    }
    
    void startRabinKarp(){
        long start = System.currentTimeMillis();

        prepareFiles();
        readFiles();
        setPrimes();        
        runRabinKarp();

        long end = System.currentTimeMillis();
        System.out.println("Ukupno vrijeme: " + (end - start) + " ms");
        System.out.format("Pogodenih uzoraka/Pogodenih Hasheva: %d/%d \n", actualHits, hashHits);
        
        for(Integer i : list){
            System.out.println("Polozaj pocetka podniza: " + i);
        }

    }

    void prepareFiles() {
        String gP = genomePath.toString();
        String sP = sequencePath.toString();

        int dot = gP.lastIndexOf('.');
        genomePath2 = Paths.get(gP.substring(0, dot) + "_edited" + gP.substring(dot));
        dot = sP.lastIndexOf('.');
        sequencePath2 = Paths.get(sP.substring(0, dot) + "_edited" + sP.substring(dot));

        storeEditedFile(genomePath, genomePath2);
        storeEditedFile(sequencePath, sequencePath2);
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
            genomeData = Files.readAllBytes(genomePath2);
            genomeSize = genomeData.length;
            sequenceData = Files.readAllBytes(sequencePath2);
            sequenceSize = sequenceData.length;
        } catch (IOException ex) {
            Logger.getLogger(RabinKarp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error reading from files!");
        }
    }

    private void runRabinKarp() {
        int i = 0;
        
        initHash();
        
        while (i < (genomeSize - sequenceSize)) {
            if (genomeHash == sequenceHash && genomeSum == sequenceSum) {
                hashHits++;
                if (checkPattern(i)) {
                    actualHits++;
                    list.add(i);
                    //vrati odmah da ima ili broji kolko ih ima ili spremi pocetne indexe svakog ponavljanja
                }
            }
            hash(++i);
        }
        // check last sequence 
        if (genomeHash == sequenceHash && genomeSum == sequenceSum) {
            hashHits++;
            if (checkPattern(i)) {
                actualHits++;
                list.add(i);
            }
        }
    }

    void hash(int start) {
        byte oldByte = genomeData[start - 1];
        byte newByte = genomeData[start + sequenceSize - 1];

        genomeSum = genomeSum - oldByte + newByte;
        genomeHash = genomeHash - oldByte * sequenceSize + genomeSum;
    }

    void initHash() {
        for (int i = 0; i < sequenceSize; ++i) {
            sequenceSum += sequenceData[i];
            sequenceHash += (sequenceSize - i) * sequenceData[i];
            genomeSum += genomeData[i];
            genomeHash += (sequenceSize - i) * genomeData[i];
        }
    }

    boolean checkPattern(int start) {
        for (int i = 0; i < sequenceSize; ++i) {
            if (sequenceData[i] != genomeData[start + i]) {
                return false;
            }
        }
        return true;
    }

    void setPrimes() {
        //long start = System.currentTimeMillis();
        for (int i = 0; i < genomeSize; ++i) {
            switch (genomeData[i]) {
                case 65:
                    genomeData[i] = 2;
                    break; // cahnge 'A' to 2
                case 67:
                    genomeData[i] = 19;
                    break; // cahnge 'C' to 19
                case 71:
                    genomeData[i] = 47;
                    break; // cahnge 'G' to 47
                case 84:
                    genomeData[i] = 79;
                    break; // cahnge 'T' with 79
                case 85:
                    genomeData[i] = 109;
                    break; // cahnge 'U' with 109
            }
        }
        for (int i = 0; i < sequenceSize; ++i) {
            switch (sequenceData[i]) {
                case 65:
                    sequenceData[i] = 2;
                    break; // cahnge 'A' to 2
                case 67:
                    sequenceData[i] = 19;
                    break; // cahnge 'C' to 19
                case 71:
                    sequenceData[i] = 47;
                    break; // cahnge 'G' to 47
                case 84:
                    sequenceData[i] = 79;
                    break; // cahnge 'T' with 79
                case 85:
                    sequenceData[i] = 109;
                    break; // cahnge 'U' with 109
            }
        }
        //long end = System.currentTimeMillis();
        //System.out.println("Ukupno vrijeme pretvorbe u proste: " + (end - start) + "ms");
    }

    void test() {
        long start, end;

        start = System.currentTimeMillis();
        prepareFiles();
        readFiles();
        end = System.currentTimeMillis();
        System.out.println("Time elapsed for reading: " + (end - start) + "ms");

        start = System.currentTimeMillis();
        initHash();
        end = System.currentTimeMillis();
        System.out.println("Time elapsed for init hash computation: " + (end - start) + "ms");

        for (int i = 1; i < 10; ++i) {
            start = System.nanoTime();
            hash(i);
            end = System.nanoTime();
            System.out.println("Time elapsed 1 cyclic hash: " + (end - start) / 1000 + "us");
            start = System.currentTimeMillis();
            testHash(i);
            end = System.currentTimeMillis();
            System.out.println("Time elapsed for 1 full hash: " + (end - start) + "ms");
            if (genomeHash != genomeHashTest || genomeSum != genomeSumTest) {
                System.out.println("krivo računa hash");
            }
        }
    }

    void testHash(int start) {
        genomeHashTest = 0;
        genomeSumTest = 0;
        for (int i = 0; i < sequenceSize; ++i) {
            genomeSumTest += genomeData[i + start];
            genomeHashTest += (sequenceSize - i) * genomeData[i + start];
        }
    }
}
