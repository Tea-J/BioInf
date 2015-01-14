package mp44464;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Rabin Karp algoritam pretrage stringa s više ulaznih nizeva 
 * 
 * @author Matija Petanjek
 */
public class RabinKarp {

	public static void main(String[] args) {
		
		char[] textArray = null;		
		LinkedList <char[]>listOfPatternArrays = new LinkedList<char[]>();
		LinkedList<LinkedList<Integer>> indexOfMatches = new LinkedList<LinkedList<Integer>>();
		RabinKarp rabinKarp = new RabinKarp();
		final long charMaxValue = Character.MAX_VALUE + 1;	
	    final long modValue = 96293; 
		
		String workingDirectory = System.getProperty("user.dir");
		
		textArray = rabinKarp.readTextFile(workingDirectory, args[0]);
		int textLength = textArray.length;
		
		listOfPatternArrays = rabinKarp.readPatternFile(workingDirectory, args[1]);
		
		for(char[] patternArray : listOfPatternArrays)
		{		
			int patternLength = patternArray.length;
			LinkedList<Integer> indexOfMatch = new LinkedList<Integer>();
						
			if (patternLength < textLength && patternLength > 0){
				
				long patternHash = 0;
				long textHash = 0;
				
				for(int i = 0; i < patternLength; i++)
				{
					patternHash = (patternHash*charMaxValue+patternArray[i]) % modValue;
					textHash = (textHash*charMaxValue+textArray[i])% modValue;
				}
				long h = rabinKarp.powMod(charMaxValue, patternLength-1, modValue);
				
				for(int i=0; i<= textLength-patternLength; i++)
				{
					if(patternHash==textHash)
					{
						if(rabinKarp.compare(textArray, i, patternArray))
						{
							indexOfMatch.add(i);
						}
					}
					if(i<textLength-patternLength)
					{
						textHash-=h*textArray[i];
						while(textHash<0)
							textHash += modValue;
						textHash = (charMaxValue * textHash + textArray[i + patternLength]) % modValue; 
					}
				}
			}
			indexOfMatches.add(indexOfMatch);
		}
		rabinKarp.printResult(indexOfMatches);
	}
	
	/**
	 * Metoda compare usporeðuje dva niza nakon što smo ustanovili da su
	 * im hash kodovi jednaki, kako bi potvrdili da nije rijeè o false positive sluèaju
	 * 
	 * @param text 		podniz iz genom.fa 
	 * @param start    	poèetni index podniza u genom.fa 
	 * @param pattern   niz iz pattern.txt
	 * @return			vraæa true ako su nizevi jednaki, inaèe false 
	 */
	
	private boolean compare(char[] text, int start, char[] pattern) {
        if (text.length - start < pattern.length)
            return false;
        for (int i = 0; i < pattern.length; i++)
            if (text[i + start] != pattern[i])
                return false;
        return true;
    }
	
	/**
	 * Metoda powMod raèuna vrijednost za odbacivanje zadnjeg 
	 * znaka iz podniza textArray
	 *
	 * @param d
	 * @param n
	 * @param q
	 * @return d^n mod q
	 */
	private long powMod(long d, long n, long q) {
        if (n == 0)
            return 1;
        if (n == 1)
            return d % q;
        long temp = powMod(d, n / 2, q);
        temp = (temp * temp) % q;
        if (n % 2 == 0)
            return temp;
        return ((temp * d) % q);
	}
	
	/**
	 * Metoda printResult u result.txt file ispisuje poèetne indexe 
	 * preklapanja ulaznih nizeva iz pattern.txt i genoma iz genom.fa
	 * 
	 * @param indexOfMatches
	 */
	private void printResult (LinkedList<LinkedList<Integer>> indexOfMatches)
	{
		int patternNumber = 1;
		try {
			File file = new File("result.txt");
			//File file = new File("mp44464","result.txt");
			FileWriter writer = new FileWriter(file);
			
			if(indexOfMatches.isEmpty())
			{
				writer.write("There is no match between pattern and text!");
			}
			else
			{
				for(LinkedList<Integer> indexOfMatch : indexOfMatches)
				{
					if (indexOfMatch.isEmpty())
					{
						writer.write("There is no match between pattern no."+patternNumber+" and text!");
					}
					else
					{
						for(int index : indexOfMatch)
						{
							writer.write("Match with pattern no."+patternNumber+" detected, starting with index: " + 
						index +	System.lineSeparator() + System.lineSeparator());
						}
					}
					patternNumber++;
				}
			}
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda readPatternFile uèitava iz file-a ulazni niz u polje 
	 * char-ova, a zatim polje dodaje u LinkedList-u, i tako za svaki ulazni niz
	 * 
	 * @param workingDirectory	put do file-a
	 * @param string			ime file-a
	 * @return 					Linked List-u koja sadrži polja charova ulaznih nizeva
	 */
	private LinkedList<char[]> readPatternFile(String workingDirectory, String string)
	{
		LinkedList<char[]> ListOfCharArrays = new LinkedList<char[]>();
		char[] charArray = null;
		Scanner scanner = null;
		StringBuffer stringBuffer =  new StringBuffer();
		
		if (string.contains(".txt"))
		{		
			try { 
				scanner = new Scanner(new File(workingDirectory, string));
				
				while (scanner.hasNextLine())
		        {
					stringBuffer.append(scanner.nextLine());
					charArray = stringBuffer.toString().toCharArray();
					ListOfCharArrays.add(charArray);
					stringBuffer.delete(0, stringBuffer.length());
		        }
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			finally {
				if (scanner != null)
					scanner.close();
			}
		}
		else
		{
			charArray = string.toCharArray();
			ListOfCharArrays.add(charArray);
		}
		return ListOfCharArrays;
	}
	
	/**
	 * Metoda readTextFile uèitava cijeli genom u polje charova
	 * @param workingDirectory	put do file-a
	 * @param string			ime file-a
	 * @return					polje charova sastavljeno od genoma
	 */
	private char[] readTextFile(String workingDirectory, String string)
	{
		char[] charArray = null;
		Scanner scanner = null;
		StringBuffer stringBuffer =  new StringBuffer();
		StringBuffer test = new StringBuffer();
		
		if (string.contains(".fa") || string.contains(".txt"))
		{		
			try { 
				scanner = new Scanner(new File(workingDirectory, string));
				
				while (scanner.hasNext())
		        {
					test.append(scanner.useDelimiter(System.lineSeparator()).nextLine());
					if (test.charAt(0) != '>') // ako se radi o komentaru preskoèi taj red
						stringBuffer.append(test);
					test.delete(0, test.length());
		        }
				charArray = stringBuffer.toString().toCharArray();
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			finally {
				if (scanner != null)
					scanner.close();
			}
		}
		else
		{
			charArray = string.toCharArray();
		}
		return charArray;
	}
}
