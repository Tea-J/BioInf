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


int main(){
	FILE *genome; FILE *input;
	int i, j, lbuffer, offset, num_patterns, lpattern, errnum;
	int *result;
	char oldbyte, newbyte;
	char *substring, *buffer, *patterns;
	long input_file_size, hsubstring, sub_sum;
	long *hpattern;
	float efficiency;

	time_t start, end;
	double dif;

	errnum = fopen_s(&input, "Escherichia_coli_asm59784v1.GCA_000597845.1.24.dna.toplevel.fa", "r");
	errnum = fopen_s(&genome, "new.txt", "w");

	if (errnum){
		printf("Error opening file.\n");
		getchar();
		return 0;
	}

	start = clock();

	buffer = (char *)malloc(N*sizeof(char));

	while (fgets(buffer, N, input)){
		if (*buffer == '>')
			continue;
		fwrite(buffer, sizeof(char), strlen(buffer) - 1, genome);
	}
	fwrite("\0", sizeof(char), 1, genome);

	fclose(input);
	fclose(genome);

	errnum = fopen_s(&input, "Test_file_2.txt", "r");
	errnum = fopen_s(&genome, "new.txt", "r");

	if (errnum){
		printf("Error opening file.\n");
		getchar();
		return 0;
	}

	fseek(genome, 0, SEEK_END);
	input_file_size = ftell(genome);
	rewind(genome);

	num_patterns = 0;
	lpattern = 0;

	fscanf_s(input, "%d %d", &num_patterns, &lpattern);
	fgets(buffer, N, input);
	fgets(buffer, N, input);

	patterns = (char *)malloc(num_patterns*(lpattern + 1)*sizeof(char));
	buffer = (char *)realloc(buffer, (lpattern + N)*sizeof(char));
	memset(buffer, '\0', (lpattern + N)*sizeof(char));

	for (i = 0; i < num_patterns; i++){
		fgets(buffer, (lpattern + N)*sizeof(char), input);
		strncpy_s(patterns + i*lpattern, lpattern + 1, buffer, lpattern);
	}

	fclose(input);

	end = clock();
	dif = ((double)(end - start)) / CLOCKS_PER_SEC;

	printf("Processing input data took %.2lf ms.\n", dif*1000);

	start = clock();

	result = (int *)malloc(num_patterns*sizeof(int));
	hpattern = (long *)malloc(num_patterns*sizeof(long));
	substring = (char *)malloc(lpattern*sizeof(char));

	for (i = 0; i < num_patterns; i++) {
		strncpy_s(substring, lpattern + 1, patterns + i*lpattern, lpattern);
		*(result + i) = 0;
		*(hpattern + i) = 0;
		for (j = 0; j < lpattern; j++){
			*(hpattern + i) += *(substring + j) * (lpattern - j);
		}
	}

	hsubstring = 0;
	sub_sum = 0;
	oldbyte = 0;

	for (i = 0; i < input_file_size - lpattern + 1; i++){
		fread(substring, sizeof(char), lpattern, genome);
		offset = ftell(genome);
		rewind(genome);
		fseek(genome, offset - lpattern + 1, SEEK_SET);

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

		for (j = 0; j < num_patterns; j++){
			if (hsubstring == *(hpattern + j)){
				detectedHash++;
				strncpy_s(buffer, lpattern + 1, patterns + j*lpattern, lpattern);
				*(result + j) = Check(substring, buffer);
			}
			if (*(result + j)){
				correctHash++;
				printf("%d. substring found at index %d.\n", j, i);
				*(result + j) = 0;
			}
		}
	}

	end = clock();
	dif = ((double)(end - start)) / CLOCKS_PER_SEC;

	if (detectedHash)
		efficiency = ((float)correctHash / detectedHash) * 100;
	else
		efficiency = 0;

	printf("\nDetected: %d\nCorrect: %d\nEfficiency: %.2f %%\n", detectedHash, correctHash, efficiency);
	printf("Calculations took %.2lf ms.\n", dif*1000);

	fclose(genome);
	free(result);
	free(buffer);
	free(patterns);
	free(hpattern);
	//free(substring);

	getchar();
	return 0;
}