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

long Hash(long oldhash, char *substring, char oldbyte){
	int i, sum, lsubstring;
	char newbyte;
	long hash_value;
	
	lsubstring = strlen(substring);
	hash_value = 0;

	if (!oldbyte){
		for(i=0;i<lsubstring; i++){
			hash_value += *(substring+i) * (lsubstring-i-1);
		}
	}
	else {
		sum = 0;
		for (i=0; i<lsubstring; i++){
			sum+=*(substring+i);
		}
		newbyte = *(substring + lsubstring -1);
		hash_value = oldhash + sum - lsubstring*Ascii(oldbyte);
	}

	return hash_value;
}

int Check(char *A, char *B){
	if( strcmp(A, B) == 0 )
		return 1;	//Strings are equal
	else
		return 0;	//Strings are not equal
}

int *Search(char* substring, char *patterns[], int num_patterns, int lpatterns, char oldbyte){
	int lstring, i, result;
	static long hsubstring = 0;
	long *hpattern = 0;
	int *return_value;

	hpattern = (long *)malloc(num_patterns*sizeof(long));
	return_value = (int *)malloc(num_patterns*sizeof(int));

	for(i = 0; i < num_patterns; i++) {
		*(hpattern+i) = Hash(0, patterns[i], 0);
		*(return_value+i) = 0;
    }
	
	lstring = strlen(substring);

	for (i = 0; i < num_patterns; i++){
		if (hsubstring == *(hpattern+i)){
			detectedHash++;
			result = Check(substring, patterns[i]);

			if (result){
				correctHash++;
				*(return_value+i) = 1;
			}
		}
	}

	return return_value;
}


int main(){
	FILE *genome; 
	int i, j, lbuffer, offset, flag, errnum;
	char oldbyte = 0;
	char *buffer;
	char *string;
	char *substring;
	int *result;
	int num_patterns = 4;
	int lpattern = 10;
	float efficiency;

	char *subs[] = { "aka", "TAATATGCAA", "dab", "ada" };

	time_t start, end;
	double dif;

	genome = fopen("C:\\Users\\tea\\Documents\\Visual Studio 2010\\Projects\\Rabin-Karp in C\\Rabin-Karp in C\\Escherichia_coli_asm59784v1.GCA_000597845.1.24.dna.toplevel.fa", "r");
	
	if (genome == NULL){
		errnum = errno;
		fprintf(stderr, "Error opening file: %s\n", strerror( errnum ));
		getchar();
		return 0;
	}

	buffer = (char *)malloc(N*sizeof(char));
	result = (int *)malloc(num_patterns*sizeof(int));
	substring = (char *)malloc(lpattern*sizeof(char));
	
	flag = 1;	//from the start
	offset = 0;

	for (i=0; i<num_patterns; i++)
		*(result+i) = 0;

	time(&start);
	
	while (fgets(buffer, N, genome)){
		if (*buffer == '>'){
			continue;
			flag = 1;
		}

		lbuffer = strlen(buffer);

		if (flag){
			string = (char *)malloc(lbuffer*sizeof(char));
			strncpy_s(string, lbuffer+1, buffer, lbuffer);
			flag = 0;
		}
		else{
			offset = strlen(string)-1;
			string = (char *)realloc(string, offset+lbuffer*sizeof(char));
			strncpy_s(string+offset, lbuffer+1, buffer, lbuffer);
		}
	}

	time(&end);
	dif = difftime(end, start);

	printf("Processing input file took %.2lf seconds.\n", dif);
	
	time(&start);
	
	for (i = 0; i<strlen(string);i++){
		strncpy_s(substring, lpattern+1, string, lpattern);
		result = Search(substring, subs, num_patterns, lpattern, oldbyte);
		oldbyte= *substring;

		for(j = 0; j < num_patterns; j++){
			if (*(result+j)){
				printf("%d. substring found at index %d.\n", j, i);
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
	
	fclose(genome);
	free(buffer);
	//free(string);
	getchar();

	return 0;
}