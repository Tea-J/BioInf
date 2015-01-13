//Rabin-Karp algorithm in C

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>
#include <errno.h>

#define N 80

int detectedHash = 0;
int correctHash = 0;


int Ascii(char c){
	return c;
}

int Check(char *A, char *B){
	if( strcmp(A, B) == 0 )
		return 1;	//Strings are equal
	else
		return 0;	//Strings are not equal
}


int main(){
	FILE *genome; FILE *input;
	int i, j, lbuffer, offset, errnum;
	char oldbyte, newbyte;
	//char *string;
	char *substring;
	int *result;
	int num_patterns = 4;
	int lpattern = 10;
	float efficiency;
	long input_file_size, hsubstring, sub_sum;
	char *buffer;
	long *hpattern;

	char *patterns[] = { "TGAAACATGG", "CAGGAAAGGT", "TTTCAGCCTT", "GCCTCTGAGC" };

	time_t start, end;
	double dif;
	
	input = fopen("C:\\Users\\tea\\Documents\\Visual Studio 2010\\Projects\\Rabin-Karp in C\\Rabin-Karp in C\\Escherichia_coli_asm59784v1.GCA_000597845.1.24.dna.toplevel.fa", "r");
	genome = fopen("C:\\Users\\tea\\Documents\\Visual Studio 2010\\Projects\\NoviRabinKarp\\NoviRabinKarp\\new.txt", "w");
	
	if (genome == NULL || input == NULL){
		errnum = errno;
		fprintf(stderr, "Error opening file: %s\n", strerror( errnum ));
		getchar();
		return 0;
	}
	
	time(&start);

	buffer = (char *)malloc(N*sizeof(char));
	
	while (fgets(buffer, N, input)){
		if (*buffer == '>')
			continue;
		fwrite(buffer, sizeof(char), strlen(buffer)-1, genome);
	}
	fwrite("\0", sizeof(char), 1, genome);

	fclose(input);
	fclose(genome);

	genome = fopen("C:\\Users\\tea\\Documents\\Visual Studio 2010\\Projects\\NoviRabinKarp\\NoviRabinKarp\\new.txt", "r");
	fseek(genome, 0, SEEK_END);
	input_file_size = ftell(genome);
	rewind(genome);

	
	//input = fopen("C:\\Users\\tea\\Documents\\Visual Studio 2010\\Projects\\NoviRabinKarp\\NoviRabinKarp\\Test_file_1.txt", "r");

	time(&end);
	dif = difftime(end, start);

	printf("Processing input data took %.2lf seconds.\n", dif);
	
	
	time(&start);

	result = (int *)malloc(num_patterns*sizeof(int));
	hpattern = (long *)malloc(num_patterns*sizeof(long));
	substring = (char *)malloc(lpattern*sizeof(char));
	
	for(i = 0; i < num_patterns; i++) {
		strncpy_s(substring, lpattern+1, *(patterns+i), lpattern);
		*(result+i) = 0;
		*(hpattern+i) = 0;
		for(j=0; j<lpattern; j++){
			*(hpattern+i) += *(substring+j) * (lpattern-j);
		}
		//printf("za pattern %d hash je %d\n", i, *(hpattern+i));
	}
	

	hsubstring = 0;
	sub_sum = 0;
	oldbyte = 0;

	for( i= 0; i<input_file_size-lpattern+1;i++){
		fread(substring, sizeof(char), lpattern, genome);
		offset=ftell(genome);
		rewind(genome);
		fseek( genome, offset-lpattern+1, SEEK_SET );
			
		if(!oldbyte){
			for(j=0; j<lpattern; j++){
				hsubstring += *(substring+j) * (lpattern-j);
				sub_sum += Ascii(*(substring + j));
			}
		}
		else{
			newbyte = *(substring + lpattern -1);
			sub_sum = sub_sum - Ascii(oldbyte) + Ascii(newbyte);
			hsubstring = hsubstring + sub_sum - lpattern*Ascii(oldbyte);
		}
		oldbyte = *substring;

		//printf("vrijednost hasha za %d.substring: %d\n", i, hsubstring);

		for (j = 0; j < num_patterns; j++){
			if (hsubstring == *(hpattern+j)){
				detectedHash++;
				*(result+j) = Check(substring, patterns[j]);
			}
			if (*(result+j)){
				correctHash++;
				printf("%d. substring found at index %d.\n", j, i);
				*(result+j)=0;
			}
		}
	}
	
	time(&end);
	dif = difftime(end, start);
	
	if (detectedHash)
		efficiency = (correctHash/detectedHash)*100;
	else
		efficiency = 0;

	printf("\nDetected: %d\nCorrect: %d\nEfficiency: %.2f %%\n", detectedHash, correctHash, efficiency);
	printf ("Calculations took %.2lf seconds to run.\n", dif );
	
	fclose(genome);
	//free(string);
	free(result);
	//free(substring);
	free(buffer);
	free(hpattern);
	
	getchar();
	return 0;
}