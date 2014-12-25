/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bioinformatika;

import java.io.IOException;
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
    private Path genome = null;
    private Path sequence = null;
    private int prime = 101;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //File file = new File("C:\\Users\\serz\\Desktop\\BIOINF\\PROJEKT\\Escherichia_coli_asm59784v1.GCA_000597845.1.24.dna.toplevel.fa");
        Path genomePath = Paths.get("C:\\Users\\serz\\Desktop\\BIOINF\\PROJEKT\\tekst.txt");
        Path sequencePath = Paths.get("C:\\Users\\serz\\Desktop\\BIOINF\\PROJEKT\\tekst.txt");//
        int primeNum = 101;
        if(args.length == 3) {
            try {
                primeNum = Integer.parseInt(args[2]);
            }catch(NumberFormatException e){
                System.out.println("Treći argument nije broj");
            }
        }
        
        RabinKarp rabinKarp = new RabinKarp(genomePath, sequencePath, primeNum);
        rabinKarp.readFiles();
        rabinKarp.runRabinKarp();
    }
    
    RabinKarp(Path genome, Path sequence, int prime){
        this.genome = genome;
        this.sequence = sequence;
        this.prime = prime;
    }

    
    void readFiles(){
        try {
            genomeData = Files.readAllBytes(genome);
            sequenceData = Files.readAllBytes(sequence);
        } catch (IOException ex) {
            Logger.getLogger(RabinKarp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error reading from files!");
        }
    }

    private void runRabinKarp() {
        
    }
    
    int hash (byte[] pattern, int start, int end){
        int hash = 0;
        
        return hash;
    }
    
}


