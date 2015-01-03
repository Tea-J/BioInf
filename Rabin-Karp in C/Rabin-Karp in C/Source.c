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

int Search(char *string, char *pattern){
	int hpattern, lpattern, lstring, hsubstring, i;
	char *substring;

	hpattern = Hash(pattern);
	lpattern = strlen(pattern);
	lstring = strlen(string);
	substring = (char *)malloc(sizeof(char)*lpattern);

	for (i = 0; i < lstring - lpattern + 1; i++){
		strncpy_s(substring, lpattern + 1, string + i, lpattern);
		hsubstring = Hash(substring);

		if (hpattern == hsubstring)
			return i;
	}

	return -1;	//ako ne nadje
}

int main(){
	int pom;
	char *Niz = "abrakadabra";
	//podniz do 4 znaka
	char *Podniz = "rak";

	pom = Search(Niz, Podniz);

	if (pom < 0){
		printf("Nema rezultata.\n");
	}
	else{
		printf("Podniz se nalazi na indexu %d.\n", pom);
	}

	getchar();

	return 0;
}