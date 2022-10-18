#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]){
	printf("%d\n", argc);
	printf("location of code : %p\n", (void *) main);
	printf("location of heap : %p\n", (void *) malloc(17));
	printf("location of heap : %p\n", (void *) malloc(17));
	printf("location of heap : %p\n", (void *) malloc(17));

	printf("location of argv : %p\n", (void *) argv);
	printf("location of argc : %p\n", (void *) &argc);

	int x = 3;
	int y = 3;
	printf("location of stack : %p\n", (void *) &x);
	printf("location of stack : %p\n", (void *) &y);

	printf("%lu", sizeof(int));

	return x;
}
