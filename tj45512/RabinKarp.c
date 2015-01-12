//Rabin-Karp algorithm in C

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>
#include <errno.h>

#define N 80

int detectedHash, correctHash = 0;


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
	FILE *genome; 
	int i, j, lbuffer, offset, errnum;
	char oldbyte, newbyte;
	char *string;
	char *substring;
	int *result;
	int num_patterns = 4;
	int lpattern = 10;
	float efficiency;
	long input_file_size, hsubstring, sub_sum;
	char *buffer;
	long *hpattern;

	char *patterns[] = { "CGACTTTTGT", "TAATATGCAA", "TTTCAGCCTT", "ACGTGCCAGA" };

	time_t start, end;
	double dif;
	
	genome = fopen("C:\\Users\\tea\\Documents\\Visual Studio 2010\\Projects\\NoviRabinKarp\\NoviRabinKarp\\input.txt", "r");
	
	if (genome == NULL){
		errnum = errno;
		fprintf(stderr, "Error opening file: %s\n", strerror( errnum ));
		getchar();
		return 0;
	}
	
	result = (int *)malloc(num_patterns*sizeof(int));
	substring = (char *)malloc(lpattern*sizeof(char));
	buffer = (char *)malloc(N*sizeof(char));

	for (i=0; i<num_patterns; i++)
		*(result+i) = 0;

	
	time(&start);
	
	//ucitaj file u string
	fseek(genome, 0, SEEK_END);
	input_file_size = ftell(genome);
	rewind(genome);
	fgets(buffer, N, genome);
	offset=ftell(genome);
	string = (char*)malloc((input_file_size-offset+1) * (sizeof(char)));
	memset(string, '\0', input_file_size-offset+1);
	fread(string, sizeof(char), input_file_size-offset+1, genome);
	fclose(genome);

	time(&end);
	dif = difftime(end, start);

	printf("Processing input file took %.2lf seconds.\n", dif);
	

	time(&start);

	hpattern = (long *)malloc(num_patterns*sizeof(long));
	for(i = 0; i < num_patterns; i++) {
		strncpy_s(substring, lpattern+1, *(patterns+i), lpattern);
		*(hpattern+i) = 0;
		for(j=0; j<lpattern; j++){
			*(hpattern+i) += *(substring+j) * (lpattern-j);
		}
		//printf("za pattern %d hash je %d\n", i, *(hpattern+i));
	}
	
	hsubstring = 0;
	sub_sum = 0;
	oldbyte = 0;

	for (i = 0; i<strlen(string)-lpattern+1;i++){
		strncpy_s(substring, lpattern+1, string+i, lpattern);
			
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

	printf("\nDetected: %d\nCorrect: %d\nEfficiency: %.2f %\n", detectedHash, correctHash, efficiency);
	printf ("Calculations took %.2lf seconds to run.\n", dif );
	
	//free(string);
	getchar();
	return 0;
}