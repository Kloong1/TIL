# [42 Seoul] Born2beroot 배경 지식 정리 1
## Disk signature(Disk identifier)
Disk signature는 MBR(Master Boot Record)/Partition Table이 처음 생성될 때 랜덤하게 생성되는 4 byte 길이의 숫자이다. 파티션이 아닌 하드 디스크 드라이브 전체에 적용된다.

MBR(Master Boot Record)에 저장되는 “Disk Signature”를 이용하면 디스크 타입의 저장매체를 특정하는데 도움을 받을 수 있다. 디스크 구매 후 초기 시스템에 연결하면 파티션 포맷 전 “디스크 초기화” 작업을 수행한다. 이 작업이 MBR 구조를 생성해주는 것인데 Disk Signature 값은 이때 생성된다. 따라서, 파티션을 삭제하고 재생성한다고 해도 “디스크 초기화”를 재수행하지 않는 이상 이 값은 변경되지 않는다.

출처
https://www.linuxquestions.org/questions/linux-general-1/what-is-disk-identifier-740408/
http://forensic-proof.com/archives/6817


## Disk signature 확인하는 방법
`sud fdisk -l`

출처
https://gymbombom.github.io/2020/10/06/1-linux-filesystem-management/


## *UUID란?
GUID(Globally Unique Identifier)는 응용 소프트웨어에서 사용되는 유사난수이다. GUID는 생성할 때 항상 유일한 값이 만들어진다는 보장은 없지만, 사용할 수 있는 모든 값의 수가 2^128개로 매우 크기 때문에, 적절한 알고리즘이 있다면 같은 숫자를 두 번 생성할 가능성은 매우 적다.
GUID는 오라클 데이터베이스 등 많은 곳에서 쓰인다. 표준으로는 OSF(Open Software Foundation)가 지정한 UUID(Universally Unique Identifier)가 있다.

네트워크 상에서 서로 모르는 개체들을 식별하고 구별하기 위해서는 각각의 고유한 이름이 필요하다.  같은 이름을 갖는 개체가 존재한다면 구별이 불가능해 지기 때문에 고유성이 중요하다. 고유성을 완벽하게 보장하려면 중앙관리시스템이 있어서 일련번호를 부여해 주면 간단하지만 동시다발적이고 독립적으로 개발되고 있는 시스템들의 경우 중앙관리시스템은 불가능하다. 개발주체가 스스로 이름을 짓도록 하되 고유성을 충족할 수 있는 방법이 필요하다. 이를 위하여 탄생한 것이 UUID이며 국제기구에서 표준으로 정하고 있다.

UUID 표준에 따라 이름을 부여하면 고유성을 완벽하게 보장할 수는 없지만 실제 사용상에서 중복될 가능성이 거의 없다고 인정되기 때문에 많이 사용되고 있다.
340,282,366,920,938,463,463,374,607,431,768,211,456개의 사용 가능한 UUID가 있다.

File system, partition 등을 구분할 때도 UUID가 사용된다.

출처
https://ko.wikipedia.org/wiki/%EB%B2%94%EC%9A%A9_%EA%B3%A0%EC%9C%A0_%EC%8B%9D%EB%B3%84%EC%9E%90https://ko.wikipedia.org/wiki/%EC%A0%84%EC%97%AD_%EA%B3%A0%EC%9C%A0_%EC%8B%9D%EB%B3%84%EC%9E%90


## UNIX
Multi-user, interactive, time sharing 시스템용 OS이다.
1970년대 초반 벨 연구소 직원인 켄 톰슨, 데니스 리치 등이 소형 컴퓨터용으로 처음 개발하였다.
오늘날의 유닉스 시스템은 여러 회사들과 비영리 단체들이 이 커널로 활용하여 다양한 운영체제를 개발하고 있다.

출처
https://ko.wikipedia.org/wiki/%EC%9C%A0%EB%8B%89%EC%8A%A4


## Linux
1991년 9월 17일 리누스 토르발스(Linus Torvalds)가 처음 출시한 운영 체제 커널인 Linux 커널에 기반을 둔 오픈 소스 UNIX 계열(UNIX-like) 운영 체제 계열이다. Linux는 일반적으로 Linux 배포판(Linux distributions) 안에 패키지 처리된다.
배포판에는 Linux 커널과 지원 시스템 소프트웨어, 그리고 라이브러리를 포함하고 있으며 이들 가운데 다수가 GNU(GNU is Not UNIX) 프로젝트에 의해 제공된다. 수많은 리눅스 배포판은 배포판 이름에 "Linux"라는 단어를 사용하지만 Free Software Foundation(FSF)은 GNU 소프트웨어의 중요성을 강조하기 위해 (이름에 일부 논란이 있기는 하지만) GNU/Linux라는 이름을 사용한다.
저명한 Linux 배포판에는 데비안, 페도라, 우분투가 있다. 상용 배포판에는 Red Hat Enterprise Linux(RHEL), SUSE Linux Enterprise가 포함된다.  Linux는 자유로이 재배포가 가능하므로 누구든지 어떠한 목적으로든 배포판을 만들 수 있다.
Linux는 처음에는 Intel x86 아키텍처에 기반한 개인용 컴퓨터용으로 개발되었으나 그 뒤로 기타 운영 체제 외에 더 많은 플랫폼으로 이식되었다. Linux는 또한 일반적으로 운영 체제가 펌웨어로 빌드되면서 시스템에 높은 수준으로 맞추어진 임베디드 시스템에서도 동작한다.

출처
https://ko.wikipedia.org/wiki/%EB%A6%AC%EB%88%85%EC%8A%A4#cite_ref-14
 

## *UNIX-like(유닉스 계열)란?
'UNIX와 비슷하면서 UNIX가 아니다'는 뜻으로, UNIX와는 별개의 용어이다.
UNIX가 아님에도 기능적으로 UNIX 규격에 호환되어 UNIX의 대체품으로 쓸 수 있는, UNIX와 비슷한 운영 체제를 말한다. 대표적으로는 Linux가 있다.

Linux는 UNIX가 아니기에, Linux 재단에서도 UNIX 표기는 쓰지 않으며, 소스코드또한 UNIX와 별개이기에 Linux 운영체제는 항상 Unix-like로 분류된다.
애플의 macOS는 UNIX 운영체제이기에 Unix-like가 아니라 항상 UNIX로 분류된다.

출처
https://ko.wikipedia.org/wiki/%EC%9C%A0%EB%8B%89%EC%8A%A4_%EA%B3%84%EC%97%B4


## *GNU란?
GNU project는 리처드 스톨먼의 주도하에 시작된 공개 소프트웨어 프로젝트이다. GNU 프로젝트는 누구나 자유롭게 "실행, 복사, 수정, 배포"할 수 있고, 누구도 그런 권리를 제한하면 안 된다는 사용 허가권(License) 아래 소프트웨어를 배포한다.

GNU는 "GNU's Not UNIX"의 약자로, 원래의 문장 안에 자신이 이미 들어 있는 재귀 약자(recursive acronym)이다. UNIX는 이미 널리 쓰이던 독점 소프트웨어 운영 체제로, UNIX의 아키텍처는 기술적으로 믿을만 한 것으로 증명되어 있어, GNU 시스템은 UNIX와 호환될 수 있도록 만들어졌다.

1985년에 스톨만은 GNU 프로젝트를 철학적, 법률적, 금융적으로 지원하기 위해 자선단체인 Free Software Foundation(FSF)을 세웠다.

1990년까지 GNU 시스템엔 Emacs, GCC, 그리고 표준 UNIX 배포판의 핵심 라이브러리와 유틸리티가 있었다. 하지만, 여기엔 주요 구성요소인 커널이 빠져 있었다.

GNU 선언문에서, 스톨만은 "기본적인 커널은 있지만 UNIX를 흉내내려면 아직 더 많은 기능이 필요하다"라고 했다. 여기서 그가 지칭한 것은 MIT에서 개발하여 자유롭게 배포했고, UNIX 7번째 판과 호환되는 트릭스(TRIX)라는 원격 프로시저 호출 커널이었다. 1986년 12월, 이 커널을 고치는 작업이 시작됐다. 하지만, 개발자들은 결국 트릭스(TRIX)를 기반으로 새 커널을 만드는 것은 어렵다는 결론을 내렸다.

1991년에 리누스 토르발스는 UNIX 호환의 Linux 커널을 작성하여 GPL 라이선스 아래에 배포했다. 다른 여러 프로그래머들은 인터넷을 통해 Linux를 더욱 발전시켰다. 1992년 Linux는 GNU 시스템과 통합되었고, 이로써 완전한 공개 운영 체제가 탄생되었다. GNU 시스템들 가운데 가장 흔한 것이, "GNU/Linux" 또는 "Linux 배포판"이라고 불리는 바로 이 시스템이다.

또한, 비공개 UNIX 시스템에도 GNU의 구성 요소들이 본래의 UNIX 프로그램을 대신하여 들어 있는 경우도 많다. 이는 GNU 프로젝트를 통해 쓰여진 프로그램들이 질적으로 우수하다는 사실을 증명한다. 종종, 이런 구성 요소들은 "GNU 툴"로 불리기도 한다. 다수의 GNU 프로그램은 마이크로소프트 윈도우나 맥 OS X 등으로 포팅되기도 했다.

출처
https://ko.wikipedia.org/wiki/GNU_%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8


## *Free Software Foundation(FSF)이란?
미국의 자유 소프트웨어 관련 재단이다. 자유 소프트웨어의 생산과 보급을 장려하기 위해 리처드 스톨만이 세운 재단으로, 주로 컴퓨터 소프트웨어를 만들어 배포하고 수정하는 보편적인 자유를 제고한다.

설립 이후부터 1990년대 중반까지 자유 소프트웨어 재단 기금은 GNU 프로젝트의 자유 소프트웨어를 작성하기 위해 소프트웨어 개발자를 고용하는데 대부분 사용되었다. 1990년대 중반 이후로 이 재단의 직원들과 자발적인 기여자들은 대개 자유 소프트웨어 운동과 자유 소프트웨어 커뮤니티를 위한 법적, 구조적 문제에 대한 작업을 처리하고 있다.

출처
https://ko.wikipedia.org/wiki/%EC%9E%90%EC%9C%A0_%EC%86%8C%ED%94%84%ED%8A%B8%EC%9B%A8%EC%96%B4_%EC%9E%AC%EB%8B%A8


#### Tag
[[42Seoul]] [[Born2beroot]]