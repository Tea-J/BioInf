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
    private int prime = 7;
    private Path genomePath2 = null;
    private Path sequencePath2 = null;
    private int sequenceSize;
    private int genomeSize;
    private long sequenceHash;
    private int hashHits = 0;
    private int actualHits = 0;

    //TODO provjeriti usporedbe int == int, byte == byte ili mora biti equals, razlika u brzini
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //File file = new File("C:\\Users\\serz\\Desktop\\BIOINF\\PROJEKT\\Escherichia_coli_asm59784v1.GCA_000597845.1.24.dna.toplevel.fa");
        Path genomePath = Paths.get("C:\\Users\\serz\\Desktop\\BIOINF\\PROJEKT\\Escherichia_coli_asm59784v1.GCA_000597845.1.24.dna.toplevel.fa");
        Path sequencePath = Paths.get("C:\\Users\\serz\\Desktop\\BIOINF\\PROJEKT\\tekst.txt");//

        long start = System.currentTimeMillis();

         int primeNum = 101;
        if (args.length == 3) {
            try {
                primeNum = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.out.println("Third argument is not a number!");
                return;
            }
        }

        RabinKarp rabinKarp = new RabinKarp(genomePath, sequencePath, primeNum);
        rabinKarp.hash(1);
        rabinKarp.prepareFiles();
        rabinKarp.readFiles();
        long end = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (end - start) + "ms");

        rabinKarp.runRabinKarp();
    }

    RabinKarp(Path genomePath, Path sequencePath, int prime) {
        this.genomePath = genomePath;
        this.sequencePath = sequencePath;
        this.prime = prime;
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
        long genomeHash = initHash();
        int i = 0;
        while(i <= (genomeSize - sequenceSize)){
            if(genomeHash == sequenceHash){
                hashHits++;
                if(checkPattern(i)){
                    actualHits++;
                    //vrati odmah da ima ili broji kolko ih ima ili spremi pocetne indexe svakog ponavljanja
                }
            }
            genomeHash = hash(++i); // paziti na zadnjeg da en cita iz elementa većeg od veličine polja
        }
    }

    long hash(int start) {
        long hash = 0;
        

        return hash;
    }

    long initHash() {
        long seqhash = 0;
        long hash = 0;
        for(int i = 0; i < sequenceSize ; ++i){

            
        }
        sequenceHash = seqhash;
        return hash;
    }
    
    boolean checkPattern(int start){
        boolean b = true;
        for(int i = 0; i < sequenceSize; ++i){
            if(sequenceData[i] != genomeData[start + i]){
               return false;
            }
        }        
        return b;
    }
}
