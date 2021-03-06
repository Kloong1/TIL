# [42 Seoul] Born2beroot 배경 지식 정리 3

## RHEL(Red Hat Enterprise Linux)
Red Hat이 개발한 Linux 배포판. 18~24개월에 한 번씩 새로운 버전이 공개되며, 라이선스는 별도로 판매하지 않고 있고 구독 형태로 요금을 지불하는 방식이다. Red Hat에서 공식적으로 기술 지원을 받을 수 있다. 상용이지만 라이선스는 GNU GPL을 따르고 있어서 대부분의 소스를 공개하고 있다.

커뮤니티가 아닌 회사에서 관리하기 때문에 내장되어있는 유틸리티가 다양하고 관리 툴의 성능도 우수하다.


## CentOS (Community Enterprise Operating System)
Red Hat이 공개한 RHEL을 그대로 가져와서 Red Hat의 브랜드와 로고만 제거하고 배포한 배포본. 사실상 RHEL 의 소스를 그대로 사용하고 있기에 RHEL 과 OS 버전, Kernel 버전, 패키지 구성이 똑같고 바이너리가 100%로 호환된다. 무료로 사용 가능하지만 문제 발생시 Red Hat의 기술 지��을 받을 수 없고, 커뮤니티를 통해 기술 지원을 받아야 한다.


## Debian
Debian은 온라인 커뮤니티에 의해 만들어졌고, RHEL보다 더 먼저 배포되어 시장을 선점하였다. 이 Debian에서 파생되어진 OS를 Debian 계열이라고 부른다. 하지만 자발적인 커뮤니티에 의해 만들어진 배포판이기 때문에, 전문적인 회사에서 서비스를 했던 RHEL에 비해 사후지원과 배포가 느리고, 내장 유틸리티들의 성능이 RHEL에 비해 부족하여 오랫동안 RHEL에 밀렸었다. 하지만 지속적인 업데이트를 통하여 현재는 RHEL에 견줄 수 있는 성능을 보이며, 무료 개인 사용자용으로 인기가 매우 높아졌다. 그리고 넓은 유저층을 가지고 있기 때문에, 온라인 웹사이트나 커뮤니티에 다양한 정보가 자세히 기술되어 있어서 상대적으로 초보 리눅스유저들이 접근하기 쉬운 Linux 배포판이다.

출처
https://coding-factory.tistory.com/318
https://ko.wikipedia.org/wiki/%EB%A0%88%EB%93%9C%ED%96%87_%EC%97%94%ED%84%B0%ED%94%84%EB%9D%BC%EC%9D%B4%EC%A6%88_%EB%A6%AC%EB%88%85%EC%8A%A4
https://sarc.io/index.php/os/1753-rhel-centos


## CentOS와 Debian의 차이점
CentOS는 .rpm 파일과 YUM_DNF 패키지 매니저 사용. Debian은 .deb 파일과 dpkg_APT 매니저 사용
CentOS의 default file system은 XFS. Debian의 의 default file system은 EXT4.

출처
https://www.openlogic.com/blog/centos-vs-debian


## Access Control
OS에서 Access Control은 디렉터리나 파일, 네트워크 소켓 같은 시스템 자원을 적절한 권한을 가진 사용자나 그룹이 접근하고 사용할 수 있게 통제하는 것을 의미한다.

접근 통제에서는 시스템 자원을 객체(Object)라고 하며 자원에 접근하는 사용자나 프로세스는 주체(Subject)라고 정의한다.
즉 _etc_passwd 파일은 객체이고 이 파일에 접근해서 암호를 변경하는 passwd 라는 명령어는 주체이며 아파치 웹 서버의 설정 파일인 _etc_httpd_conf_httpd.conf 는 객체이며 웹 서버 프로그램인 _sbin_httpd 는 주체가 된다.


## DAC (Discretionary Access Control)
DAC는 시스템 객체에 대한 접근을 사용자나 또는 그룹의 신분을 기준으로 제한하는 방법이다. 사용자나 그룹이 객체의 소유자라면 다른 주체에 대해 이 객체에 대한 접근 권한을 설정할 수 있다.

여기서 임의적이라는 말은 소유자는 자신의 판단에 의해서 권한을 줄 수 있다는 의미이며 구현이 용이하고 사용이 간편하기 때문에 전통적으로 유닉스나 윈도우등 대부분의 운영체제의 기본 접근 통제 모델로 사용되고 있다.

임의적 접근 통제는 사용자가 임의로 접근 권한을 지정하므로 사용자의 권한을 탈취당하면 사용자가 소유하고 있는 모든 객체의 접근 권한을 가질 수 있게 되는 치명적인 문제가 있다. 특히 root 계정은 모든 권한을 갖고 있으므로 root 권한을 탈취하면 시스템을 완벽하게 장악할 수 있다.


## MAC (Mandatory Access Control)
MAC는 미리 정해진 정책과 보안 등급에 의거하여 주체에게 허용된 접근 권한과 객체에게 부여된 허용 등급을 비교하여 접근을 통제하는 모델이다. 높은 보안을 요구하는 정보는 낮은 보안 수준의 주체가 접근할 수 없으며 소유자라고 할 지라도 정책에 어긋나면 객체에 접근할 수 없으므로 강력한 보안을 제공한다.

MAC 정책에서는 root로 구동한 http 서버라도 접근 가능한 파일과 포트가 제한된다. 즉 취약점을 이용하여 httpd 의 권한을 획득했어도 _var_www_html, /etc_httpd 등의 사전에 허용한 폴더에만 접근 가능하며 80, 443, 8080 등 웹 서버에 사전에 허용된 포트만 접근이 허용되므로 ssh로 다른 서버로 접근을 시도하는등 해킹으로 인한 2차 피해가 최소화된다.

단점으로는 구현이 복잡하고 어려우며 모든 주체와 객체에 대해서 보안 등급과 허용 등급을 부여하여야 하므로 설정이 복잡하고 시스템 관리자가 접근 통제 모델에 대해 잘 이해하고 있어야 한다.

출처
https://www.lesstif.com/ws/access-control-dac-mac-43843837.html


## AppArmor (Application Armor)
시스템 관리자가 프로그램 프로필 별로 프로그램의 권한을 제한할 수 있게 해주는 Linux 커널 보안 모듈이다. 프로필은 네트워크 액세스, raw 소켓 액세스 그리고 파일의 읽기, 쓰기, 실행 같은 능력을 허용할 수 있다. AppArmor는 MAC를 제공함으로써 UNIX의 전통적인  DAC 모델을 지원한다.

AppArmor는 LSM(Linux Security Modules) 커널 인터페이스를 사용해서 구현되었다.

Debian 계열 Linux에 기본으로 설치되어 있다.

enforce 모드와 complain 모드, 두 가지 모드가 존재한다. enforce 모드는 AppArmor의 default mode로, 허가되지 않은 파일에 접근하는 것을 거부하는 모드이다. complain 모드는 실질적으로 보안을 제공하는 것은 아닌 대신, 어플리케이션이 해야 할 행동이 아닌 다른 행동을 하는 경우 로그를 남긴다.

출처
https://ko.wikipedia.org/wiki/AppArmor
https://linuxhint.com/debian_apparmor_tutorial/
https://tbonelee.tistory.com/m/16


## *LSM(Linux Security Modules)이란?
LSM은 리눅스 커널이 단일한 보안 구현을 피하면서 다양한 컴퓨터 보안 모델을 지원하게 해주는 프레임워크이다. 이 프레임워크는 GNU 일반 공중 사용 허가서 하에 배포되며 2.6 이후 Linux 커널 버전 부터는 표준이 되었다. AppArmor, SELinux, Smack (소프트웨어) 그리고 TOMOYO Linux가 현재 공식 커널에서 받아들여진 모듈이다.

출처
https://ko.wikipedia.org/wiki/%EB%A6%AC%EB%88%85%EC%8A%A4_%EB%B3%B4%EC%95%88_%EB%AA%A8%EB%93%88


#### Tag
[[42Seoul]] [[Born2beroot]]