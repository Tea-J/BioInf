//Rabin-Karp algorithm in C

#include <stdio.h>
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
		hash_value += (Ascii(*(substring + i)))*((int)(pow(base, N - i - 1)));
	}

	return hash_value;
}

int main(){

	char *pom = "bra";

	printf("Hash value for \"%s\" is %d.\n", pom, Hash(pom));

	getchar();

	return 0;
}