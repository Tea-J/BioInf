//Rabin-Karp algorithm in C

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
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
	int result, flag, errnum;
	char *buffer;
	char *string;
	//char *string = "abrakadabra";
	char *subs[] = { "aka", "TCA", "dab", "ada" };
	//max lenght of a substring is 4 characters
	//i.e. for "TTTTT" we don't get correct hash value

	genome = fopen("C:\\Users\\tea\\Documents\\Visual Studio 2010\\Projects\\Rabin-Karp in C\\Rabin-Karp in C\\Escherichia_coli_asm59784v1.GCA_000597845.1.24.dna.toplevel.fa", "r");
	
	if (genome == NULL){
		errnum = errno;
		fprintf(stderr, "Error opening file: %s\n", strerror( errnum ));
		getchar();
		return 0;
	}

	// 4758629+1;

	buffer = (char *)malloc(N*sizeof(char));
	string = (char *)malloc(N*sizeof(char));
	flag = 0;	//from the start
	result = -1;

	//fgets(ulaz, N, genome);	//read line
	
	while (fgets(buffer, N, genome)){

		if (*buffer == '>'){
			flag = 0;
			continue;
		}

		if (flag == 0){
			strncpy_s(string, strlen(buffer)+1, buffer, strlen(buffer));
			result = Search(string, subs, 4, 3);
			flag=1;
		} else {
			strncpy_s(string, 3, string+strlen(string)-2, 2);
			strncpy_s(string+2, strlen(buffer)+1, buffer, strlen(buffer));
			result = Search(string, subs, 4, 3);
		}

		if (result >= 0)
				break;

	}

		

	//Initial test
    //result = Search(string, subs, 4, 3); 
	
	if (result < 0){
		printf("No results.\n");
	}
	else{
		printf("A substring was found at index %d.\n", result);
	}
	
	fclose(genome);
	free(buffer);
	free(string);
	getchar();

	return 0;
}