package mp44464;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
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
				
				long p = 0;
				long t = 0;
				
				for(int i = 0; i < patternLength; i++)
				{
					p = (p*charMaxValue+patternArray[i]) % modValue;
					t = (t*charMaxValue+textArray[i])% modValue;
				}
				long h = rabinKarp.powMod(charMaxValue, patternLength-1, modValue);
				
				for(int i=0; i<= textLength-patternLength; i++)
				{
					if(p==t)
					{
						if(rabinKarp.compare(textArray, i, patternArray))
						{
							indexOfMatch.add(i);
						}
					}
					if(i<textLength-patternLength)
					{
						t-=h*textArray[i];
						while(t<0)
							t += modValue;
						t = (charMaxValue * t + textArray[i + patternLength]) % modValue; // veæ smo izraèunali za prvi patternlength
					}
				}
			}
			indexOfMatches.add(indexOfMatch);
		}
		rabinKarp.printResult(textArray, indexOfMatches);
	}
	
	private boolean compare(char[] text, int start, char[] pattern) {
        if (text.length - start < pattern.length)
            return false;
        for (int i = 0; i < pattern.length; i++)
            if (text[i + start] != pattern[i])
                return false;
        return true;
    }
	
	private long powMod(long d, long n, long q) {
        if (n == 0)
            return 1;
        if (n == 1)
            return d % q;
        long j = powMod(d, n / 2, q);
        j = (j * j) % q;
        if (n % 2 == 0)
            return j;
        return ((j * d) % q);
	}
	
	private void printResult (char[] textArray, LinkedList<LinkedList<Integer>> indexOfMatches)
	{
		int patternNumber = 1;
		try {
			File file = new File("result.txt");
			FileWriter writer = new FileWriter(file);
			
			if(indexOfMatches.isEmpty())
			{
				writer.write("There is no match between pattern and text!");
			}
			else
			{
				for(LinkedList<Integer> indexOfMatch : indexOfMatches)
				{
					for(int index : indexOfMatch)
					{
						writer.write("Match with pattern no."+patternNumber+" detected, starting with index: " + 
					index +	System.lineSeparator() + System.lineSeparator());
					}
					patternNumber++;
				}
			}
			//writer.write(textArray);
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
					test.append(scanner.useDelimiter("\n").nextLine());
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
