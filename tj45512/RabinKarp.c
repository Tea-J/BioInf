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
	if (strcmp(A, B) == 0)
		return 1;	//Strings are equal
	else
		return 0;	//Strings are not equal
}


int main(int argc, char *argv[]){
	FILE *genome; FILE *input;
	int i, j, no_results, lpattern;
	int num_patterns = 10;
	int *result;
	char oldbyte, newbyte;
	char *substring, *buffer, *patterns, *string;
	long input_file_size, hsubstring, sub_sum;
	long *hpattern;
	float efficiency;

	time_t start, end;
	double dif;

	if (argc != 2){
		fprintf(stderr, "Usage: RabinKarp [-n]\n-n	  test file index [1-5]\n");
		getchar();
		return 0;
	}

	input = fopen("Escherichia_coli_asm59784v1.GCA_000597845.1.24.dna.toplevel.fa", "r");
	genome = fopen("new.txt", "w");

	if (input == NULL || genome == NULL){
		fprintf(stderr, "Error opening file.\n");
		getchar();
		return 0;
	}

	start = clock();

	buffer = (char *)malloc(N*sizeof(char));

	//Saving data to a new file, without comments or blank spaces
	while (fgets(buffer, N, input)){
		if (*buffer == '>')
			continue;
		fwrite(buffer, sizeof(char), strlen(buffer) - 1, genome);
	}
	fwrite("\0", sizeof(char), 1, genome);

	fclose(input);
	fclose(genome);

	switch (*argv[argc-1]){
	case '1': 
		input = fopen("10patterns100size.fa", "r");
		lpattern = 100;
		break;
	case '2':
		input = fopen("10patterns1000size.fa", "r");
		lpattern = 1000;
		break;
	case '3':
		input = fopen("10patterns10000size.fa", "r");
		lpattern = 10000;
		break;
	case '4':
		input = fopen("10patterns100000size.fa", "r");
		lpattern = 100000;
		break;
	case '5':
		input = fopen("10patterns1000000size.fa", "r");
		lpattern = 1000000;
		break;
	default:
		fprintf(stderr, "Unallowed argumen.\n");
		fprintf(stderr, "Usage: RabinKarp [-n]\n-n test file index [1-5]\n");
		getchar();
		return 0;
	}
	
	genome = fopen("new.txt", "r");

	if (input == NULL || genome == NULL){
		fprintf(stderr, "Error opening file.\n");
		getchar();
		return 0;
	}

	fseek(genome, 0, SEEK_END);
	input_file_size = ftell(genome);
	rewind(genome);

	patterns = (char *)malloc(num_patterns*(lpattern + 1)*sizeof(char));
	buffer = (char *)realloc(buffer, (lpattern + N)*sizeof(char));

	for (i = 0; i < num_patterns; i++){
		fgets(buffer, (lpattern + N)*sizeof(char), input);	//Reads comment line
		memset(buffer, '\0', (lpattern + N)*sizeof(char));
		fgets(buffer, (lpattern + N)*sizeof(char), input);	//Reads pattern
		strncpy(patterns + i*lpattern, buffer, lpattern);	//Saves pattern
	}

	fclose(input);

	end = clock();
	dif = ((double)(end - start)) / CLOCKS_PER_SEC;

	fprintf(stderr, "Processing input data took %.2lf ms.\n", dif * 1000);

	start = clock();

	result = (int *)malloc(num_patterns*sizeof(int));
	hpattern = (long *)malloc(num_patterns*sizeof(long));
	substring = (char *)malloc((lpattern+1)*sizeof(char));

	//Calculating hash values for patterns
	for (i = 0; i < num_patterns; i++) {
		memset(substring, '\0', (lpattern + 1)*sizeof(char));
		strncpy(substring, patterns + i*lpattern, lpattern);
		*(result + i) = 0;
		*(hpattern + i) = 0;
		for (j = 0; j < lpattern; j++){
			*(hpattern + i) += *(substring + j) * (lpattern - j);
		}
	}

	hsubstring = 0;
	sub_sum = 0;
	oldbyte = 0;
	no_results = 1;
	string = (char*)malloc(input_file_size*sizeof(char));
	memset(string, '\0', input_file_size);
	fread(string, sizeof(char), input_file_size, genome);

	//Searchig for patterns in input data
	for (i = 0; i < input_file_size - lpattern + 1; i++){
		strncpy(substring, string + i, lpattern);

		//Calculating hash value for a substring of input data
		if (!oldbyte){
			for (j = 0; j < lpattern; j++){
				hsubstring += *(substring + j) * (lpattern - j);
				sub_sum += Ascii(*(substring + j));
			}
		}
		else{
			newbyte = *(substring + lpattern - 1);
			sub_sum = sub_sum - Ascii(oldbyte) + Ascii(newbyte);
			hsubstring = hsubstring + sub_sum - lpattern*Ascii(oldbyte);
		}
		oldbyte = *substring;

		//Comparing hash value of a substring with hash values of patterns
		//If we find same hash values, we still have compare substring with pattern
		for (j = 0; j < num_patterns; j++){
			if (hsubstring == *(hpattern + j)){
				detectedHash++;
				memset(buffer, '\0', (lpattern + N)*sizeof(char));
				strncpy(buffer, patterns + j*lpattern, lpattern);
				*(result + j) = Check(substring, buffer);
			}

			if (*(result + j)){
				correctHash++;
				fprintf(stderr, "%d. pattern found at index %d.\n", j, i);
				*(result + j) = 0;

				if (no_results)
					no_results = 0;
			}
		}
	}

	if (no_results)
		fprintf(stderr, "No pattern was found.\n");

	end = clock();
	dif = ((double)(end - start)) / CLOCKS_PER_SEC;

	if (detectedHash)
		efficiency = ((float)correctHash / detectedHash) * 100;
	else
		efficiency = 0;

	fprintf(stderr, "\nDetected: %d\nCorrect: %d\nEfficiency: %.2f %%\n", detectedHash, correctHash, efficiency);
	fprintf(stderr, "Calculations took %.2lf ms.\n", dif * 1000);

	fclose(genome);
	free(result);
	free(buffer);
	free(patterns);
	free(hpattern);
	//free(substring);

	getchar();
	return 0;
}