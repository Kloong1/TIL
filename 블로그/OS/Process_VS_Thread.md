# Process VS Thread

## Thread
- **Process** : 실행중인 프로그램
- Thread: A single execution flow. 하나의 실행 흐름.

프로그램은 하나이지만, 실행 흐름(현재 실행중인 곳)은 여러개일 수 있다.

실행 흐름이란 결국엔 CPU 의 레지스터 값, 연산 등 CPU의 상태를 의미한다고 볼 수 있다.

따라서 각 Thread 마다 PC가 필요하고, 레지스터 값을 저장해두는 과정도 필요하다. 그래서 PCB가 아닌 TCB가 필요하다. 하나의 프로세스 안에 여러개의 쓰레드가 존재할 수 있기 때문.

Thread끼리 switching이 일어날 때는 context switch에 비해 오버헤드가 적다. 실행 흐름, 즉 레지스터 값들만 TCB에 저장하면 되기 때문이다.

프로세스와 달리 Thread는 대부분의 address space를 공유한다. 따라서 switching이 일어나도 TLB miss가 날 확률도 적다.

Code segment와 Heap segment는 공유하지만, Stack segment는 Thread 별로 따로 쓴다. 그래서 Concurrency 문제는 Heap에서 주로 일어나게 된다.

### Race Condition
두 개 이상의 실행 흐름이 하나의 자원을 놓고 서로 사용하려고 경쟁하는 상황을 말한다. 공통 자원을 병행적으로(concurrently) 읽거나 쓰는 동작을 할 때, 공용 데이터에 대한 접근이 어떤 순서에 따라 이루어 졌는지에 따라 그 실행 결과가 같지 않고 달라지는 상황.

### Critical Section
공유되는 자원에 접근하는 코드. 두개 이상의 쓰레드에 의해 동시에 접근될 시 race condition 발생해서 위험함. Critical section은 프로그래머에 의해 atomicity가 보장되어야 한다. Lock을 통해 atomicity 보장 가능.

## Lock
Critical section이 하나의 atomic한 명령어처럼 실행되는 것을 보장한다.

Lock variable holds the state of the lock.
- available (or unlocked or free)
	- No thread holds the lock.
- acquired (or locked or held)
	- Exactly one thread holds the lock and presumably is in a critical section.

- lock() 의 의미
	- Try to acquire the lock.
	- If no other thread holds the lock, the thread will acquire the lock.
	- Enter the critical section.
		- This thread is said to be the owner of the lock.
	- Other threads are prevented from entering the critical section while the first thread that holds the lock is in there

> Mutex: MUTual EXclusion

#### Lock을 구현하기 위해서는 하드웨어의 도움이 필요하다
Lock을 가질 수 있는지 확인(read) -> 가질 수 있으면 Lock을 가짐(write로 값 표시)
이 작업이 atomicity하게 일어나야 하는데 코드로는 불가능. Lock을 갖기 위해 다시 Lock이 필요하고... 무한 반복

### Spinlock
Lock을 기다리기며 무한 루프를 돈다.
- Mutual exclusion을 제공하긴 한다.
- Fairness를 보장하지 않는다. Lock을 기다리는 모든 쓰레드가 무한 루프를 돌고 있는 상태이기 때문에, 먼저 Lock을 기다렸다고 해도 먼저 Lock을 얻는 것을 보장하지는 않는다. 최악의 상황에서는 영원히 기다릴 수 있다.
- Performance는 최악이다. Lock을 기다리는 내내 CPU 자원을 소모한다.

OS의 도움을 받아서 성능이 더 좋은 lock을 구현해보자!

lock을 기다리며 spin 하지 않고, CPU 자원 포기하고 OS가 해당 lock을 기다리는 queue에 락을 기다리는 쓰레드의 정보를 넣어놓는다.

lock을 가진 쓰레드가 unlock 하면서 해당 사실을 알리면, queue에서 lock을 기다리던 쓰레드를 dequeue해서 실행한다.


## Deadlock의 4가지 필요충분조건
![](스크린샷%202022-10-25%20오후%2011.27.56.png)
- 상호 배제(Mutual exclusion): 쓰레드가 Lock 등을 통해 자원을 배타적으로 소유할 수 있어야 한다.
- Hold-and-wait: 쓰레드가 자원을 소유한 채(Lock을 가진 채) 다른 자원(혹은 lock)을 얻기 위해 기다린다.
- 비선점(No preemption): 쓰레드는 다른 쓰레드가 소유하고 있는 자원을 강제로 뺐어올 수 없다.
- Circular wait: 쓰레드의 hold-and-wait이 cycle을 이뤄야 한다.

참고
https://iredays.tistory.com/125