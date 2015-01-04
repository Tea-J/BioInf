//Rabin-Karp algorithm in C

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#define base 101


int Ascii(char c){
	return c;
}

int Hash(char *substring){
	int i, hash_value;
	int N = strlen(substring);

	hash_value = 0;
	for (i = 0; i < N; i++){
		hash_value += (Ascii(*(substring + i)))*((int)(pow((double)base, N - i - 1)));
	}

	return hash_value;
}

int Search(char* string, char *patterns[], int num_patterns, int lpatterns){
	int lstring, hsubstring, i, j, return_value;
	int *hpattern;
	char *substring;

	hpattern = (int *)malloc(num_patterns*sizeof(int));

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

	return return_value;
}

int main(){
	int result;
	char *string = "abrakadabra";

	char *subs[] = { "aka", "aba", "dab", "ada" };
	//Currently, max lenght of a substring is 4 characters
	//TO DO: test max lenght if the hash value is unsigned int or long
    
    result = Search(string, subs, 4, 3);

	if (result < 0){
		printf("No results.\n");
	}
	else{
		printf("The substring is found at index %d.\n", result);
	}

	getchar();

	return 0;
}