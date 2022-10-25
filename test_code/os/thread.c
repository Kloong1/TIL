#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>

void *mythread(void *arg) {
	int m = (int) arg;
	printf("%d\n", m);
	return (void *) (arg + 1);
}

int main(int argc, char *argv[]) {
	pthread_t p;
	int rc, m;
	pthread_create(&p, NULL, mythread, (void *) 100);
	pthread_join(p, (void **) &m);
	printf("returned %d\n", m);
	return 0;
}
