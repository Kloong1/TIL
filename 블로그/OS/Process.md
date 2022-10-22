# Process

프로세스는 "실행 중인 프로그램"이다.

- 프로세스는 다음의 것들로 이루어져 있다.
	- CPU state (registers)
		- Program counter
		- Stack pointer
		- General registers
	- Memory (address space)
		- Instructions
		- Data section


- ESP 
	- 함수가 진행하고 있을 때  stack의 제일 아래 부분,현재 진행 stack 지점, Stack Pointer. stack 메모리는 아래로 성장하기 때문에 제일 아래가 제일 마지막이 된다.
- EBP
	- 스택의 가장 윗 부분(기준점), Base Pointer
- EIP
	- 실행할 명령의 주소, Instruction Pointer

>E가 붙는 것은 16비트에서 32비트 시스템으로 오면서 Extended 된 개념, 64비트에서는 R이 붙음


![](스크린샷%202022-10-13%20오후%205.36.20.png)

출처
https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=byunhy69&logNo=140112048445

### 프로세스 생성
1. Disk에 있는 프로그램의 코드가 메모리에 올라온다.
	1. Address space의 Code section 에 올라온다.
	2. 기타 static variables도 올라오는 듯.
	3. 모든 코드가 한번에 올라오지는 않는다. OS는 프로그램 실행 중에 필요한 코드를  올린다.
2. Runtime stack이 할당된다.
	1. 스택은 지역 변수, function parameter, return address(자신을 호출한 instruction의 address. 함수가 끝나면 그 곳으로 돌아간다) 를 저장하는 데 사용된다.
	2. 스택을 할당하고 초기화한다. C 기준으로 `int main(int argc, char*[] argv` 의 인자인 `int argc` 와 `char*[] argv` 을 스택에 올린다. 위의 스택 구조 참고
3. Heap이 생성된다.
	1. 메모리 동적 할당 구역이다.
4. OS가 기타 초기화 작업을 수행한다.
	1. I/O setup
		1. stdin, stdout, stderr 등에 쉽게 접근할 수 있게 file descriptor를 프로세스에게 넘겨준다.
5. 프로그램의 시작 지점을 실행시킨다.
	1. C에서의 `main()` 이다.
	2. 이제부터 CPU가 열일한다.

![](스크린샷%202022-10-13%20오후%205.58.51.png)

## 프로세스 관리를 위한 자료 구조
OS는 프로세스 관리를 위해 프로세스의 정보가 들어있는 다양한 자료 구조를 활용한다.

스케줄링을 기다리고 있는 프로세스는 ready queue 에 저장한다던가 하는 방식으로 말이다.

또 PCB(Process Control Block) 라는 구조체를 가지고 있다. PCB 에는 각각의 process의 context가 저장되어 있다.

**proc.c(xv6 code)**
```C
// the registers xv6 will save and restore
// to stop and subsequently restart a process
struct context {
	int eip; // Index pointer register
	int esp; // Stack pointer register
	int ebx; // Called the base register
	int ecx; // Called the counter register
	int edx; // Called the data register
	int esi; // Source index register
	int edi; // Destination index register
	int ebp; // Stack base pointer register
};

// the different states a process can be in
enum proc_state { UNUSED, EMBRYO, SLEEPING, RUNNABLE, RUNNING, ZOMBIE };

// the information xv6 tracks about each process
// including its register context and state
struct proc {
	char *mem; // Start of process memory
	uint sz; // Size of process memory
	char *kstack; // Bottom of kernel stack for this process
	
	enum proc_state state; // Process state
	int pid; // Process ID
	struct proc *parent; // Parent process
	void *chan; // If non-zero, sleeping on chan
	int killed; // If non-zero, have been killed
	struct file *ofile[NOFILE]; // Open files
	struct inode *cwd; // Current directory
	struct context context; // Switch here to run process
	struct trapframe *tf; // Trap frame for the current interrup
};
```

## Paging
