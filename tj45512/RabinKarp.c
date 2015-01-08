//Rabin-Karp algorithm in C

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>
#include <errno.h>

#define base 101
#define N 80


int Ascii(char c){
	return c;
}

long Hash(char *substring){
	int i, lsubstring;
	long hash_value;
	
	lsubstring = strlen(substring);
	
	hash_value = 0;

	for (i = 0; i < lsubstring; i++){
		hash_value += (Ascii(*(substring + i)))*((long)(pow((double)base, lsubstring - i - 1)));
	}

	return hash_value;
}

int Search(char* string, char *patterns[], int num_patterns, int lpatterns){
	int lstring, i, j, return_value;
	long hsubstring;
	long *hpattern;
	char *substring;

	hpattern = (long *)malloc(num_patterns*sizeof(long));

	for(i = 0; i < num_patterns; i++) {
		*(hpattern+i) = Hash(patterns[i]);
    }
	
	lstring = strlen(string);
	substring = (char *)malloc((lpatterns + 1)*sizeof(char));

	return_value = -1;	//if no substring was found

	for (i = 0; i < lstring - lpatterns + 1; i++){
		strncpy_s(substring, lpatterns + 1, string + i, lpatterns);
		hsubstring = Hash(substring);

		for(j = 0; j < num_patterns; j++){
			if (hsubstring == *(hpattern+j)){
				return_value = i;
				break;
			}
		}

		if (return_value != (-1)){
			break;
		}
	}

	free(substring);
	return return_value;
}

int main(){
	FILE *genome; 
	int result, lbuffer, offset, flag, errnum;
	char *buffer;
	char *string;
	//char *string = "abrakadabra";
	char *subs[] = { "aka", "TAATATGCAA", "dab", "ada" };
	//max lenght of a substring is 4 characters
	//i.e. for "TTTTT" we don't get correct hash value

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
	string = (char *)malloc(N*sizeof(char));
	flag = 0;	//from the start
	offset = 0;
	result = -1;

	time(&start);
	
	while (fgets(buffer, N, genome)){

		if (*buffer == '>'){
			flag = 0;
			continue;
		}

		//printf("buffer:\n%s", buffer);
		lbuffer = strlen(buffer)-1;	//when using fgets, strlen counts '\n'

		if (flag == 0){
			strncpy_s(string, lbuffer+1, buffer, lbuffer);
			result = Search(string, subs, 4, 10);
			if (result >= 0){
				break;
			}
			flag=1;
		} 
		else {
			strncpy_s(string, 3, string+strlen(string)-2, 2);
			strncpy_s(string+2, lbuffer+1, buffer, lbuffer);
			result = Search(string, subs, 4, 10);
			if (result >= 0){
				result=result-2;
				break;
			}
		}

		offset += lbuffer;
		//printf("offset + lbuffer %d\n", offset);
	}

	time(&end);
	dif = difftime(end, start);
	
	if (result < 0){
		printf("No results.\n");
	}
	else{
		printf("A substring was found at index %d.\n", offset + result);
	}

	printf ("Calculations took %.2lf seconds to run.\n", dif );
	
	fclose(genome);
	free(buffer);
	free(string);
	getchar();

	return 0;
}