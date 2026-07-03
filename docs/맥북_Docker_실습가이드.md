# 맥북 Docker 실습 가이드: 처음부터 끝까지

> **대상**: Docker를 한 번도 사용해본 적 없는 macOS 사용자
> **환경**: macOS (Apple Silicon M1/M2/M3/M4 또는 Intel)
> **프로젝트**: edu_spring 교육 프로젝트

---

## 목차

- [Part 1: Docker 설치하기](#part-1-docker-설치하기)
- [Part 2: Docker 기본 명령어 마스터하기](#part-2-docker-기본-명령어-마스터하기)
- [Part 3: Java를 Docker로 실행하기](#part-3-java를-docker로-실행하기)
- [Part 4: Spring Boot를 Docker로 실행하기](#part-4-spring-boot를-docker로-실행하기)
- [Part 5: 교육 프로젝트 전체 실행 가이드](#part-5-교육-프로젝트-전체-실행-가이드)
- [Part 6: 트러블슈팅](#part-6-트러블슈팅)
- [Part 7: 유용한 Docker 팁](#part-7-유용한-docker-팁)
- [부록: 명령어 치트시트](#부록-명령어-치트시트)

---

# Part 1: Docker 설치하기

## 1.1 Docker Desktop 설치

### Docker란 무엇인가?

Docker는 애플리케이션을 **컨테이너**라는 격리된 환경에서 실행하는 도구이다.
"내 컴퓨터에서는 되는데 다른 컴퓨터에서는 안 된다"라는 문제를 해결해준다.

컨테이너 안에는 애플리케이션 실행에 필요한 모든 것(Java, 라이브러리, 설정 파일 등)이 포함되어 있어서,
어떤 컴퓨터에서든 동일하게 실행된다.

### Step 1: 내 맥북이 Apple Silicon인지 Intel인지 확인하기

터미널을 열고 다음 명령어를 입력한다.

```bash
uname -m
```

**예상 출력 (Apple Silicon M1/M2/M3의 경우):**
```
arm64
```

**예상 출력 (Intel Mac의 경우):**
```
x86_64
```

> **터미널 여는 방법**: `Cmd + Space`를 눌러 Spotlight 검색을 열고 "터미널" 또는 "Terminal"을 입력한다.

### Step 2: Docker Desktop 다운로드

1. 웹 브라우저에서 다음 주소로 이동한다:
   - https://www.docker.com/products/docker-desktop/

2. **"Download for Mac"** 버튼을 클릭한다.

3. **중요!** 두 가지 옵션이 나타난다:
   - **"Apple Silicon"**: M1, M2, M3, M4 칩 맥북인 경우 선택
   - **"Intel chip"**: Intel 칩 맥북인 경우 선택

   > 앞서 `uname -m` 결과가 `arm64`이면 Apple Silicon, `x86_64`이면 Intel을 선택한다.

4. `Docker.dmg` 파일이 다운로드된다 (약 600MB~700MB).

### Step 3: Docker Desktop 설치

1. 다운로드된 `Docker.dmg` 파일을 더블클릭한다.
2. Docker 아이콘을 **Applications** 폴더로 드래그한다.
   - (Finder 창에 Docker 고래 아이콘과 Applications 폴더가 보인다)
3. Applications 폴더에서 **Docker** 를 더블클릭하여 실행한다.
4. "Docker Desktop needs privileged access." 라는 팝업이 나타나면 **OK**를 클릭한다.
5. 맥북 비밀번호를 입력한다.
6. Docker Desktop이 시작되면 화면 상단 메뉴바에 **고래 아이콘**이 나타난다.

> **고래 아이콘이 움직이고 있다면** Docker가 아직 시작 중인 것이다.
> 고래 아이콘이 **멈추면** Docker가 준비된 것이다 (약 30초~1분 소요).

### Step 4: 설치 확인

터미널을 열고 다음 명령어를 입력한다.

```bash
docker --version
```

**예상 출력:**
```
Docker version 27.5.1, build 9f9e405
```
(버전 번호는 다를 수 있다. 숫자가 출력되면 정상이다.)

```bash
docker compose version
```

**예상 출력:**
```
Docker Compose version v2.32.4
```

두 명령어 모두 버전 정보가 출력되면 설치가 완료된 것이다.

> **만약 "command not found"가 나온다면:**
> Docker Desktop이 아직 완전히 시작되지 않았거나, 터미널을 재시작해야 한다.
> 1. Docker Desktop이 실행 중인지 확인 (메뉴바에 고래 아이콘)
> 2. 터미널을 닫았다가 다시 열기
> 3. 그래도 안 되면 맥북을 재부팅

---

## 1.2 Docker Desktop 설정 (맥북 최적화)

Docker Desktop을 최적의 상태로 설정한다.

### 설정 화면 열기

1. 메뉴바의 **고래 아이콘**을 클릭한다.
2. **Settings** (또는 **Preferences**)를 클릭한다.
3. 또는 단축키 `Cmd + ,`를 사용한다.

### Resources 설정 (CPU, Memory, Disk)

왼쪽 메뉴에서 **Resources** > **Advanced**를 클릭한다.

**교육용 권장 설정:**

| 항목 | 권장 값 | 설명 |
|------|---------|------|
| **CPUs** | 4 | 맥북 전체 코어의 절반 정도 |
| **Memory** | 4 GB | 교육용으로 충분한 크기 |
| **Swap** | 1 GB | 메모리 부족 시 사용 |
| **Disk image size** | 20 GB | 이미지와 컨테이너 저장 공간 |

> **참고**: 맥북 RAM이 8GB라면 Memory를 3~4GB로, 16GB 이상이라면 4~6GB로 설정한다.
> Docker에 너무 많은 메모리를 할당하면 맥북 전체가 느려진다.

설정을 변경한 후 **Apply & Restart** 버튼을 클릭한다.

### Apple Silicon에서 Rosetta 에뮬레이션 설정

Apple Silicon (M1/M2/M3) 맥북 사용자는 이 설정이 중요하다.

1. Settings > **General**로 이동한다.
2. **"Use Rosetta for x86_64/amd64 emulation on Apple Silicon"** 옵션을 **활성화(체크)** 한다.
3. **Apply & Restart**를 클릭한다.

> **이 설정은 왜 필요한가?**
> 많은 Docker 이미지가 Intel(x86_64/amd64) 아키텍처용으로 만들어져 있다.
> Rosetta를 활성화하면 이런 이미지도 Apple Silicon에서 실행할 수 있다.
> Rosetta가 없으면 일부 이미지에서 "exec format error"가 발생할 수 있다.

### 시작 시 자동 실행 설정

1. Settings > **General**로 이동한다.
2. **"Start Docker Desktop when you sign in to your computer"** 옵션:
   - 자주 Docker를 사용한다면 **활성화**
   - 가끔만 사용한다면 **비활성화** (맥북 부팅 속도를 위해)

### 추가 권장 설정

1. Settings > **General**:
   - "Send usage statistics" -- 비활성화 (선택사항, 개인정보 보호)

2. Settings > **Docker Engine**:
   - 기본값 그대로 둔다. 수정할 필요 없다.

---

## 1.3 Docker가 정상 작동하는지 확인

설치와 설정이 끝났으니 실제로 Docker가 잘 작동하는지 테스트한다.

### Hello World 테스트

```bash
docker run hello-world
```

**예상 출력:**
```
Unable to find image 'hello-world:latest' locally
latest: Pulling from library/hello-world
e6590344b1a5: Pull complete
Digest: sha256:...
Status: Downloaded newer image for hello-world:latest

Hello from Docker!
This message shows that your installation appears to be working correctly.

To generate this message, Docker took the following steps:
 1. The Docker client contacted the Docker daemon.
 2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
    (arm64v8)
 3. The Docker daemon created a new container from that image which runs the
    executable that produces the output you are currently reading.
 4. The Docker daemon streamed that output to the Docker client, which sent it
    to your terminal.

To try something more ambitious, you can run an Ubuntu container with:
 $ docker run -it ubuntu bash

Share images, automate workflows, and more with a free Docker ID:
 https://hub.docker.com/

For more examples and ideas, visit:
 https://docs.docker.com/get-started/
```

> **"Hello from Docker!"** 메시지가 보이면 Docker가 정상 작동하는 것이다!

### 무슨 일이 일어났는지 이해하기

1. `docker run hello-world` 명령을 실행했다.
2. Docker가 로컬에 `hello-world` 이미지가 없는 것을 확인했다.
3. Docker Hub(인터넷 저장소)에서 이미지를 다운로드(pull)했다.
4. 이미지로부터 컨테이너를 만들고 실행했다.
5. 컨테이너가 "Hello from Docker!" 메시지를 출력하고 종료했다.

### 추가 확인: Docker 정보

```bash
docker info
```

**예상 출력 (일부):**
```
Client:
 Version:    27.5.1
 Context:    desktop-linux
 ...

Server:
 Containers: 1
  Running: 0
  Paused: 0
  Stopped: 1
 Images: 1
 Server Version: 27.5.1
 Storage Driver: overlay2
 ...
 Operating System: Docker Desktop
 OSType: linux
 Architecture: aarch64
 CPUs: 4
 Total Memory: 3.836GiB
 ...
```

`Architecture`가 `aarch64`이면 Apple Silicon, `x86_64`이면 Intel이다.

---

# Part 2: Docker 기본 명령어 마스터하기

## 2.1 이미지 관련 명령어

Docker 이미지는 컨테이너를 만들기 위한 **설계도(템플릿)**이다.
이미지 자체는 읽기 전용이며, 이미지를 기반으로 컨테이너를 실행한다.

### 이미지 다운로드 (docker pull)

Docker Hub에서 이미지를 로컬로 다운로드한다.

```bash
docker pull eclipse-temurin:21-jdk
```

**예상 출력:**
```
21-jdk: Pulling from library/eclipse-temurin
afad30e59d72: Pull complete
ee1162629afa: Pull complete
42c09c3e80f1: Pull complete
8d0e8d1f9f5c: Pull complete
Digest: sha256:abc123...
Status: Downloaded newer image for eclipse-temurin:21-jdk
docker.io/library/eclipse-temurin:21-jdk
```

> `eclipse-temurin:21-jdk`는 **Java 21 개발 환경(JDK)**이 설치된 이미지이다.
> `eclipse-temurin`은 이미지 이름, `21-jdk`는 태그(버전)이다.

### 이미지 목록 확인 (docker images)

로컬에 다운로드된 모든 이미지를 확인한다.

```bash
docker images
```

**예상 출력:**
```
REPOSITORY          TAG       IMAGE ID       CREATED        SIZE
eclipse-temurin     21-jdk    a1b2c3d4e5f6   2 weeks ago    456MB
hello-world         latest    d2c94e258dcb   9 months ago   13.3kB
```

| 열 | 설명 |
|----|------|
| REPOSITORY | 이미지 이름 |
| TAG | 이미지 버전(태그) |
| IMAGE ID | 이미지 고유 ID (앞 12자리) |
| CREATED | 이미지가 생성된 시간 |
| SIZE | 이미지 크기 |

### 이미지 삭제 (docker rmi)

더 이상 필요 없는 이미지를 삭제한다.

```bash
docker rmi hello-world
```

**예상 출력:**
```
Untagged: hello-world:latest
Untagged: hello-world@sha256:...
Deleted: sha256:...
Deleted: sha256:...
```

> 이미지를 사용하는 컨테이너가 있으면 삭제되지 않는다.
> 그럴 때는 컨테이너를 먼저 삭제해야 한다.

이미지 ID로도 삭제할 수 있다:

```bash
docker rmi d2c94e258dcb
```

### 사용하지 않는 이미지 정리 (docker image prune)

태그가 없는(dangling) 이미지를 한번에 정리한다. 모든 미사용 이미지를 정리하려면 `-a` 옵션을 추가한다.

```bash
docker image prune
```

**예상 출력:**
```
WARNING! This will remove all dangling images.
Are you sure you want to continue? [y/N] y
Deleted Images:
deleted: sha256:...

Total reclaimed space: 0B
```

> `y`를 입력하고 Enter를 누른다.

확인 프롬프트 없이 바로 삭제하려면 다음과 같이 한다:

```bash
docker image prune -f
```

모든 미사용 이미지를 삭제하려면 (`-a` 옵션):

```bash
docker image prune -a
```

---

## 2.2 컨테이너 관련 명령어

컨테이너는 이미지를 실행한 **인스턴스**이다.
하나의 이미지로 여러 개의 컨테이너를 만들 수 있다.

### 컨테이너 실행 (docker run)

```bash
docker run -it eclipse-temurin:21-jdk bash
```

**예상 출력:**
```
root@a1b2c3d4e5f6:/#
```

지금 여러분은 **컨테이너 안에** 있다! 리눅스 환경이다.

```bash
# 컨테이너 안에서 Java 버전 확인
java -version
```

**예상 출력:**
```
openjdk version "21.0.5" 2024-10-15 LTS
OpenJDK Runtime Environment Temurin-21.0.5+11 (build 21.0.5+11-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.5+11 (build 21.0.5+11-LTS, mixed mode)
```

```bash
# 컨테이너에서 나가기
exit
```

> **플래그 설명:**
> - `-i` (interactive): 표준 입력을 열어둔다 (키보드 입력 가능)
> - `-t` (tty): 터미널을 할당한다 (깔끔한 프롬프트 표시)
> - `-it`: 위 두 가지를 합쳐서 대화형 터미널 모드로 실행

### 실행 중인 컨테이너 확인 (docker ps)

**새 터미널 탭**을 열고 다음을 실행한다 (`Cmd + T`로 새 탭):

```bash
docker ps
```

**예상 출력 (위에서 -it로 컨테이너를 실행 중일 때):**
```
CONTAINER ID   IMAGE                    COMMAND   CREATED          STATUS          PORTS   NAMES
a1b2c3d4e5f6   eclipse-temurin:21-jdk   "bash"    2 minutes ago    Up 2 minutes            peaceful_turing
```

**예상 출력 (실행 중인 컨테이너가 없을 때):**
```
CONTAINER ID   IMAGE   COMMAND   CREATED   STATUS   PORTS   NAMES
```

| 열 | 설명 |
|----|------|
| CONTAINER ID | 컨테이너 고유 ID |
| IMAGE | 사용된 이미지 |
| COMMAND | 실행 중인 명령어 |
| CREATED | 생성 시간 |
| STATUS | 상태 (Up = 실행 중) |
| PORTS | 포트 매핑 정보 |
| NAMES | 컨테이너 이름 (자동 생성) |

### 모든 컨테이너 확인 (중지된 것 포함)

```bash
docker ps -a
```

**예상 출력:**
```
CONTAINER ID   IMAGE                    COMMAND    CREATED          STATUS                     PORTS   NAMES
a1b2c3d4e5f6   eclipse-temurin:21-jdk   "bash"     5 minutes ago    Exited (0) 2 minutes ago           peaceful_turing
b2c3d4e5f6a7   hello-world              "/hello"   30 minutes ago   Exited (0) 30 minutes ago          quirky_einstein
```

> `-a` 옵션은 **종료된 컨테이너까지 모두** 보여준다.

### 컨테이너 중지 (docker stop)

```bash
docker stop a1b2c3d4e5f6
```

**예상 출력:**
```
a1b2c3d4e5f6
```

> 컨테이너 ID의 앞 몇 글자만 입력해도 된다 (고유하다면):
> `docker stop a1b`

컨테이너 이름으로도 중지할 수 있다:

```bash
docker stop peaceful_turing
```

### 컨테이너 삭제 (docker rm)

```bash
docker rm a1b2c3d4e5f6
```

**예상 출력:**
```
a1b2c3d4e5f6
```

> **주의**: 실행 중인 컨테이너는 삭제할 수 없다. 먼저 `docker stop`으로 중지해야 한다.
> 또는 강제 삭제: `docker rm -f a1b2c3d4e5f6`

### 중지된 컨테이너 모두 삭제

```bash
docker container prune
```

**예상 출력:**
```
WARNING! This will remove all stopped containers.
Are you sure you want to continue? [y/N] y
Deleted Containers:
a1b2c3d4e5f6...
b2c3d4e5f6a7...

Total reclaimed space: 12.5MB
```

### 실행과 동시에 자동 삭제 (--rm)

컨테이너가 종료되면 자동으로 삭제된다. 연습할 때 매우 유용하다.

```bash
docker run --rm -it eclipse-temurin:21-jdk java -version
```

**예상 출력:**
```
openjdk version "21.0.5" 2024-10-15 LTS
OpenJDK Runtime Environment Temurin-21.0.5+11 (build 21.0.5+11-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.5+11 (build 21.0.5+11-LTS, mixed mode)
```

> `--rm` 옵션 덕분에 종료 후 `docker ps -a`에 나타나지 않는다.

---

## 2.3 볼륨과 포트 매핑

### 포트 매핑 (-p)

컨테이너 내부의 포트를 맥북에서 접근할 수 있게 연결한다.

```bash
docker run -p 8080:8080 my-app
```

> **형식**: `-p 호스트포트:컨테이너포트`
>
> `-p 8080:8080` = 맥북의 8080 포트로 접속하면 컨테이너의 8080 포트로 연결
>
> 맥북에서 이미 8080 포트를 사용 중이라면 다른 포트를 사용할 수 있다:
> `-p 9090:8080` = 맥북의 9090 포트 -> 컨테이너의 8080 포트

간단한 테스트를 해보자. Nginx 웹서버를 실행한다:

```bash
docker run --rm -d -p 8888:80 --name my-nginx nginx:alpine
```

**예상 출력:**
```
Unable to find image 'nginx:alpine' locally
alpine: Pulling from library/nginx
...
Status: Downloaded newer image for nginx:alpine
c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4
```

이제 브라우저에서 http://localhost:8888 을 열어본다.
"Welcome to nginx!" 페이지가 보이면 성공이다.

확인 후 컨테이너를 중지한다:

```bash
docker stop my-nginx
```

> **플래그 설명:**
> - `-d` (detach): 백그라운드에서 실행 (터미널이 막히지 않음)
> - `--name my-nginx`: 컨테이너에 이름을 지정

### 볼륨 마운트 (-v)

맥북의 파일/폴더를 컨테이너 안에서 접근할 수 있게 연결한다.

```bash
docker run --rm -v $(pwd):/app eclipse-temurin:21-jdk ls /app
```

> **형식**: `-v 호스트경로:컨테이너경로`
>
> `-v $(pwd):/app` = 맥북의 **현재 디렉토리**를 컨테이너의 `/app`에 연결
>
> `$(pwd)`는 현재 디렉토리의 절대 경로로 치환된다.

**예시: 맥북의 파일을 컨테이너에서 읽기**

```bash
# 맥북에서 테스트 파일 생성
echo 'public class Hello { public static void main(String[] args) { System.out.println("Hello from Docker!"); } }' > /tmp/Hello.java

# 컨테이너에서 컴파일 및 실행
docker run --rm -v /tmp:/app -w /app eclipse-temurin:21-jdk sh -c "javac Hello.java && java Hello"
```

**예상 출력:**
```
Hello from Docker!
```

> `-w /app`: 컨테이너의 작업 디렉토리(Working Directory)를 `/app`으로 설정

### 환경변수 전달 (-e)

컨테이너에 환경변수를 전달한다.

```bash
docker run --rm -e "MY_NAME=Docker" eclipse-temurin:21-jdk sh -c 'echo "Hello, $MY_NAME!"'
```

**예상 출력:**
```
Hello, Docker!
```

여러 환경변수를 전달하려면 `-e`를 여러 번 사용한다:

```bash
docker run --rm \
  -e "SPRING_PROFILES_ACTIVE=dev" \
  -e "SERVER_PORT=8080" \
  my-app
```

---

## 2.4 Docker Compose 명령어

Docker Compose는 **여러 컨테이너를 한번에 관리**하는 도구이다.
`docker-compose.yml` (또는 `compose.yml`) 파일에 설정을 정의한다.

예를 들어, Spring Boot 앱 + PostgreSQL 데이터베이스를 동시에 실행할 수 있다.

### 빌드 및 실행

```bash
docker compose up --build
```

**예상 출력 (예시):**
```
[+] Building 45.2s (12/12) FINISHED
 => [app internal] load build definition from Dockerfile
 ...
[+] Running 2/2
 ✔ Container chapter06-postgres-1  Healthy
 ✔ Container chapter06-app-1       Started
app-1       |
app-1       |   .   ____          _            __ _ _
app-1       |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
app-1       | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
app-1       |  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
app-1       |   '  |____| .__|_| |_|_| |_\__, | / / / /
app-1       |  =========|_|==============|___/=/_/_/_/
app-1       |  :: Spring Boot ::                (v3.4.1)
app-1       |
app-1       | ... Started Application in 3.245 seconds
```

> `--build` 옵션은 Dockerfile을 기반으로 이미지를 새로 빌드한다.
> 소스 코드를 수정한 후에는 반드시 `--build`를 붙여야 변경사항이 반영된다.

**중지하려면**: `Ctrl + C`를 누른다.

### 백그라운드 실행

```bash
docker compose up -d --build
```

**예상 출력:**
```
[+] Building 2.1s (12/12) FINISHED
[+] Running 2/2
 ✔ Container chapter06-postgres-1  Healthy
 ✔ Container chapter06-app-1       Started
```

> `-d` (detach): 백그라운드에서 실행한다. 터미널을 계속 사용할 수 있다.

### 로그 확인

```bash
# 모든 서비스의 로그를 실시간으로 확인
docker compose logs -f
```

**예상 출력:**
```
app-1       | ... Started Application in 3.245 seconds
postgres-1  | ... database system is ready to accept connections
```

> `-f` (follow): 새로운 로그가 생기면 실시간으로 출력한다.
> 종료하려면 `Ctrl + C`를 누른다 (컨테이너는 계속 실행된다).

### 특정 서비스만 로그 확인

```bash
# app 서비스 로그만 확인
docker compose logs -f app

# postgres 서비스 로그만 확인
docker compose logs -f postgres
```

### 중지

```bash
docker compose down
```

**예상 출력:**
```
[+] Running 3/3
 ✔ Container chapter06-app-1       Removed
 ✔ Container chapter06-postgres-1  Removed
 ✔ Network chapter06_default       Removed
```

### 볼륨까지 삭제 (DB 데이터 초기화)

```bash
docker compose down -v
```

**예상 출력:**
```
[+] Running 4/4
 ✔ Container chapter06-app-1       Removed
 ✔ Container chapter06-postgres-1  Removed
 ✔ Volume chapter06_pgdata         Removed
 ✔ Network chapter06_default       Removed
```

> **주의**: `-v` 옵션은 볼륨(데이터)까지 삭제한다!
> PostgreSQL 데이터베이스의 모든 데이터가 사라진다.
> 데이터를 초기화하고 싶을 때만 사용해야 한다.

### 특정 서비스만 실행

```bash
# PostgreSQL만 백그라운드로 실행
docker compose up postgres -d
```

**예상 출력:**
```
[+] Running 1/1
 ✔ Container chapter06-postgres-1  Started
```

> 데이터베이스만 Docker로 띄우고, Spring Boot 앱은 IDE에서 직접 실행할 때 유용하다.

### 실행 중인 서비스 상태 확인

```bash
docker compose ps
```

**예상 출력:**
```
NAME                     IMAGE              COMMAND                  SERVICE    CREATED          STATUS                    PORTS
chapter06-app-1          chapter06-app      "java -jar app.jar"      app        2 minutes ago    Up 2 minutes              0.0.0.0:8080->8080/tcp
chapter06-postgres-1     postgres:16-alpine "docker-entrypoint.s..."  postgres   2 minutes ago    Up 2 minutes (healthy)   0.0.0.0:5432->5432/tcp
```

### 컨테이너 내부 접속

```bash
# Spring Boot 앱 컨테이너 내부로 접속
docker compose exec app bash

# PostgreSQL 컨테이너에서 psql 클라이언트 실행
docker compose exec postgres psql -U edu -d edu_spring
```

**psql 접속 후 예상 출력:**
```
psql (16.6)
Type "help" for help.

edu_spring=#
```

psql에서 테이블 확인:

```sql
-- 테이블 목록 보기
\dt

-- 특정 테이블 조회
SELECT * FROM member;

-- psql 종료
\q
```

---

# Part 3: Java를 Docker로 실행하기

## 3.1 Docker로 Java 코드 실행 (가장 간단한 방법)

Java가 맥북에 설치되어 있지 않아도 Docker만 있으면 Java 코드를 실행할 수 있다.

### 프로젝트 디렉토리로 이동

```bash
cd ~/edu_spring/java/chapter01-java-basics
```

### Java 컨테이너에서 직접 컴파일 및 실행

```bash
docker run --rm -v $(pwd):/app -w /app eclipse-temurin:21-jdk \
  sh -c "mkdir -p out && javac -d out src/main/java/com/edu/basics/*.java && java -cp out com.edu.basics.VariablesAndTypes"
```

**예상 출력:**
```
=== 변수와 타입 실습 ===
...
(VariablesAndTypes 클래스의 실행 결과가 출력된다)
```

### 각 플래그의 의미

이 명령어를 하나씩 분해해서 이해해보자:

| 플래그/인수 | 의미 |
|------------|------|
| `docker run` | 새 컨테이너를 만들고 실행 |
| `--rm` | 실행 후 컨테이너 자동 삭제 (깔끔한 정리) |
| `-v $(pwd):/app` | 현재 디렉토리(맥북)를 컨테이너의 `/app`에 마운트 |
| `-w /app` | 컨테이너의 작업 디렉토리를 `/app`으로 설정 |
| `eclipse-temurin:21-jdk` | 사용할 Docker 이미지 (Java 21 JDK) |
| `sh -c "..."` | 여러 명령어를 순차적으로 실행 |
| `mkdir -p out` | `out` 디렉토리 생성 (이미 있으면 무시) |
| `javac -d out src/.../*.java` | Java 소스 파일을 컴파일하여 `out`에 저장 |
| `java -cp out com.edu.basics.VariablesAndTypes` | 컴파일된 클래스를 실행 |

### 다른 클래스도 실행해보기

```bash
# ControlFlow 실행
docker run --rm -v $(pwd):/app -w /app eclipse-temurin:21-jdk \
  sh -c "mkdir -p out && javac -d out src/main/java/com/edu/basics/*.java && java -cp out com.edu.basics.ControlFlow"
```

```bash
# ArraysAndMethods 실행
docker run --rm -v $(pwd):/app -w /app eclipse-temurin:21-jdk \
  sh -c "mkdir -p out && javac -d out src/main/java/com/edu/basics/*.java && java -cp out com.edu.basics.ArraysAndMethods"
```

```bash
# ExceptionBasics 실행
docker run --rm -v $(pwd):/app -w /app eclipse-temurin:21-jdk \
  sh -c "mkdir -p out && javac -d out src/main/java/com/edu/basics/*.java && java -cp out com.edu.basics.ExceptionBasics"
```

---

## 3.2 Dockerfile로 Java 실행하기

### Dockerfile이란?

Dockerfile은 Docker 이미지를 만들기 위한 **레시피(설계서)**이다.
어떤 베이스 이미지를 사용하고, 어떤 파일을 복사하고, 어떤 명령을 실행할지 정의한다.

### java 샌드박스의 Dockerfile 분석

`java/Dockerfile` 파일의 내용:

```dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends \
    bash-completion vim less && \
    rm -rf /var/lib/apt/lists/*
COPY run.sh /app/run.sh
COPY compile.sh /app/compile.sh
RUN chmod +x /app/run.sh /app/compile.sh
CMD ["sleep", "infinity"]
```

**각 줄의 의미:**

| 줄 | 명령어 | 설명 |
|----|--------|------|
| 1 | `FROM eclipse-temurin:21-jdk` | **베이스 이미지**: Java 21 JDK가 설치된 리눅스 환경을 시작점으로 사용 |
| 2 | `WORKDIR /app` | **작업 디렉토리**: 이후 명령어가 실행될 디렉토리를 `/app`으로 설정 |
| 3 | `RUN apt-get ...` | **도구 설치**: 이미지 빌드 시 vim, less 같은 편의 도구를 설치 |
| 4~5 | `COPY run.sh / compile.sh` | **파일 복사**: 실행/컴파일 스크립트를 이미지에 복사 |
| 6 | `RUN chmod +x ...` | **실행 권한**: 스크립트를 실행 가능하게 만듦 |
| 7 | `CMD ["sleep", "infinity"]` | **실행 명령**: 컨테이너를 "켜 둔 채 대기"시킴 — 우리가 `docker exec`로 들어가서 쓰는 방식 |

> **FROM**: 모든 Dockerfile은 반드시 `FROM`으로 시작한다. "이 이미지를 기반으로 시작한다"는 의미이다.
>
> **RUN vs CMD**:
> - `RUN`은 이미지를 **빌드할 때** 실행된다 (한 번만)
> - `CMD`는 컨테이너를 **실행할 때마다** 실행된다
>
> **왜 `sleep infinity`?** 이 컨테이너는 "한 번 실행하고 끝나는 프로그램"이 아니라
> **켜 놓고 계속 드나드는 실습용 샌드박스**이기 때문이다. 소스는 이미지에 굽지 않고
> 볼륨 마운트로 연결한다(3.3절).

### 이미지 빌드하기

```bash
cd ~/edu_spring/java

docker build -t java-sandbox .
```

**예상 출력:**
```
[+] Building 25.3s (10/10) FINISHED
 => [internal] load build definition from Dockerfile                    0.0s
 => [internal] load metadata for docker.io/library/eclipse-temurin:21-jdk  1.2s
 => [1/5] FROM docker.io/library/eclipse-temurin:21-jdk@sha256:...     0.0s
 => [2/5] WORKDIR /app                                                 0.0s
 => [3/5] RUN apt-get update && apt-get install ...                    18.4s
 => [4/5] COPY run.sh /app/run.sh                                      0.0s
 => [5/5] RUN chmod +x /app/run.sh /app/compile.sh                     0.1s
 => exporting to image                                                  0.1s
 => => naming to docker.io/library/java-sandbox                        0.0s
```

> **명령어 설명:**
> - `docker build`: 이미지를 빌드한다
> - `-t java-sandbox`: 빌드된 이미지에 `java-sandbox`라는 이름(태그)을 붙인다
> - `.`: 현재 디렉토리의 Dockerfile을 사용한다 (마지막 점이 중요!)

> 실제 실습에서는 `docker build`를 직접 칠 일이 거의 없다 — 다음 절의
> `docker compose up -d`가 빌드까지 알아서 해 준다.

---

## 3.3 docker-compose로 Java 실행하기

### java 샌드박스의 docker-compose.yml 분석

`java/docker-compose.yml` 파일의 내용:

```yaml
services:
  java-sandbox:
    build: .
    container_name: java-sandbox
    volumes:
      - .:/app        # java/ 디렉토리 전체를 /app에 통마운트
    stdin_open: true
    tty: true
```

| 항목 | 설명 |
|------|------|
| `services` | 실행할 서비스(컨테이너) 목록 |
| `java-sandbox` | 서비스 이름 (= 컨테이너 이름) |
| `build: .` | 현재 디렉토리의 Dockerfile로 빌드 |
| `volumes: - .:/app` | **통마운트**: 맥북의 `java/` 폴더 전체 ↔ 컨테이너의 `/app` (파일 수정이 즉시 반영) |
| `stdin_open`/`tty` | 대화형 터미널로 접속할 수 있게 함 |

> **통마운트라서 단순하다**: 맥북에서 보이는 파일 = 컨테이너에서 보이는 파일.
> `java/chapter01-java-basics/src/...`는 컨테이너에서 `/app/chapter01-java-basics/src/...`이다.

### 실행하기

```bash
cd ~/edu_spring/java

docker compose up -d        # 컨테이너를 백그라운드로 켠다 (최초 1회 빌드 포함)

docker exec -it java-sandbox ./compile.sh              # 전체 컴파일
docker exec -it java-sandbox ./run.sh VariablesAndTypes  # 예제 실행
```

**예상 출력:**
```
[+] Running 1/1
 ✔ Container java-sandbox  Started

컴파일 중...
컴파일 성공!

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  실행: VariablesAndTypes   (com.edu.basics.VariablesAndTypes)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
=== 변수와 타입 실습 ===
...
```

> 컨테이너는 `sleep infinity`로 계속 떠 있으므로, `docker exec`로 몇 번이고 드나들며
> 예제를 실행할 수 있다. 끌 때는 `docker compose down`.

---

## 3.4 Java 대화형 실행 (JShell)

JShell은 Java 9부터 제공되는 **대화형 Java 실행 환경(REPL)**이다.
코드를 한 줄씩 입력하고 바로 결과를 확인할 수 있다.

```bash
docker run --rm -it eclipse-temurin:21-jdk jshell
```

**예상 출력:**
```
|  Welcome to JShell -- Version 21.0.5
|  For an introduction type: /help intro

jshell>
```

### JShell 안에서 실습

```java
jshell> System.out.println("Hello Docker!")
Hello Docker!

jshell> var list = List.of(1, 2, 3, 4, 5)
list ==> [1, 2, 3, 4, 5]

jshell> list.stream().map(x -> x * 2).toList()
$3 ==> [2, 4, 6, 8, 10]

jshell> String name = "Docker"
name ==> "Docker"

jshell> "Hello, %s! You are running Java %s".formatted(name, System.getProperty("java.version"))
$5 ==> "Hello, Docker! You are running Java 21.0.5"

jshell> /exit
|  Goodbye
```

> JShell은 Java 문법을 빠르게 테스트할 때 매우 유용하다.
> 세미콜론(`;`)을 생략해도 된다.

---

# Part 4: Spring Boot를 Docker로 실행하기

## 4.1 Chapter 04 실행 (기본 Spring Boot)

Chapter 04는 가장 기본적인 Spring Boot 애플리케이션이다.

### Dockerfile 분석 (멀티 스테이지 빌드)

`spring/chapter04-spring-boot-intro/Dockerfile`:

```dockerfile
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY gradlew .
COPY src src
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**멀티 스테이지 빌드란?**

두 개의 `FROM`을 사용하여 빌드 단계와 실행 단계를 분리한다:

| 단계 | 이미지 | 목적 | 크기 |
|------|--------|------|------|
| **1단계 (builder)** | `eclipse-temurin:21-jdk` | Gradle로 빌드 (JDK 필요) | ~456MB |
| **2단계 (실행)** | `eclipse-temurin:21-jre` | 빌드된 JAR 실행 (JRE면 충분) | ~200MB |

> 최종 이미지에는 2단계만 포함되므로 이미지 크기가 훨씬 작아진다.
> 소스 코드, Gradle, 빌드 도구 등은 최종 이미지에 포함되지 않는다.

### 실행하기

```bash
cd ~/edu_spring/spring/chapter04-spring-boot-intro

docker compose up --build
```

**예상 출력:**
```
[+] Building 45.2s (12/12) FINISHED
...
app-1  |   .   ____          _            __ _ _
app-1  |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
app-1  | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
app-1  |  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
app-1  |   '  |____| .__|_| |_|_| |_\__, | / / / /
app-1  |  =========|_|==============|___/=/_/_/_/
app-1  |  :: Spring Boot ::                (v3.4.1)
app-1  |
app-1  | ... Started Application in 2.345 seconds (process running for 2.789)
```

> 처음 빌드할 때는 Gradle 의존성 다운로드 때문에 **수 분이 걸릴 수 있다**.
> 두 번째부터는 캐시 덕분에 훨씬 빨라진다.

### API 테스트

**새 터미널 탭**을 열고 (`Cmd + T`):

```bash
curl http://localhost:8080/api/hello?name=Docker
```

**예상 출력:**
```
Hello, Docker!
```

```bash
curl http://localhost:8080/api/time
```

**예상 출력:**
```
{"currentTime":"2026-03-17T14:30:45.123456"}
```

또는 브라우저에서 직접 접속할 수도 있다:
- http://localhost:8080/api/hello?name=Docker
- http://localhost:8080/api/time

### 종료

원래 터미널에서 `Ctrl + C`를 누르거나:

```bash
cd ~/edu_spring/spring/chapter04-spring-boot-intro
docker compose down
```

---

## 4.2 Chapter 05 실행 (REST API)

Chapter 05는 REST API를 구현한 Spring Boot 애플리케이션이다.

### 실행하기

```bash
cd ~/edu_spring/spring/chapter05-spring-web

docker compose up --build
```

Spring Boot가 시작되면 새 터미널에서 테스트한다.

### CRUD API 테스트

**1. Todo 생성 (POST)**

```bash
curl -s -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Docker 학습", "description": "Chapter 05 실습"}' | python3 -m json.tool
```

**예상 출력:**
```json
{
    "id": 1,
    "title": "Docker 학습",
    "description": "Chapter 05 실습",
    "completed": false
}
```

> `| python3 -m json.tool`은 JSON을 보기 좋게 포매팅한다.

**2. Todo 목록 조회 (GET)**

```bash
curl -s http://localhost:8080/api/todos | python3 -m json.tool
```

**예상 출력:**
```json
[
    {
        "id": 1,
        "title": "Docker 학습",
        "description": "Chapter 05 실습",
        "completed": false
    }
]
```

**3. 여러 개 추가해보기**

```bash
curl -s -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Spring Boot 공부", "description": "Part 4 진행 중"}'

curl -s -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "JPA 학습", "description": "Chapter 06 예정"}'
```

**4. 전체 목록 다시 확인**

```bash
curl -s http://localhost:8080/api/todos | python3 -m json.tool
```

### 종료

```bash
cd ~/edu_spring/spring/chapter05-spring-web
docker compose down
```

---

## 4.3 Chapter 06 실행 (JPA + PostgreSQL)

Chapter 06부터는 **PostgreSQL 데이터베이스**가 함께 실행된다.
Docker Compose가 PostgreSQL과 Spring Boot를 동시에 관리한다.

### docker-compose.yml 핵심 포인트

```yaml
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: edu_spring
      POSTGRES_USER: edu
      POSTGRES_PASSWORD: edu1234
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U edu"]
      interval: 5s
      timeout: 5s
      retries: 5

  app:
    build: .
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/edu_spring
    depends_on:
      postgres:
        condition: service_healthy
```

> **핵심 개념:**
> - `depends_on` + `condition: service_healthy`: PostgreSQL이 **완전히 준비된 후에** Spring Boot가 시작된다
> - `SPRING_DATASOURCE_URL`에서 `postgres`는 **Docker 네트워크에서의 서비스 이름**이다 (localhost가 아님!)
> - `healthcheck`: PostgreSQL이 연결을 받을 준비가 되었는지 5초마다 확인한다

### 실행하기

```bash
cd ~/edu_spring/spring/chapter06-spring-data-jpa

docker compose up --build
```

**예상 출력 (주요 부분):**
```
[+] Running 2/2
 ✔ Container spring/chapter06-spring-data-jpa-postgres-1  Healthy
 ✔ Container spring/chapter06-spring-data-jpa-app-1       Started
postgres-1  | ... database system is ready to accept connections
app-1       | ... Started Application in 3.245 seconds
```

### PostgreSQL에 직접 접속해보기

새 터미널에서 다음을 실행한다:

```bash
cd ~/edu_spring/spring/chapter06-spring-data-jpa

docker compose exec postgres psql -U edu -d edu_spring
```

**psql 안에서:**

```sql
-- 생성된 테이블 목록 확인
\dt

-- member 테이블 데이터 조회
SELECT * FROM member;

-- 테이블 구조 확인
\d member

-- psql 종료
\q
```

### 한 줄 명령으로 SQL 실행하기

```bash
cd ~/edu_spring/spring/chapter06-spring-data-jpa

docker compose exec postgres psql -U edu -d edu_spring -c "SELECT * FROM member;"
```

**예상 출력:**
```
 id | name  | email          | created_at
----+-------+----------------+---------------------------
  1 | user1 | user1@test.com | 2026-03-17 05:30:12.345678
(1 row)
```

### 종료

```bash
cd ~/edu_spring/spring/chapter06-spring-data-jpa

# 컨테이너만 중지 (DB 데이터 유지)
docker compose down

# 또는 DB 데이터까지 삭제 (완전 초기화)
docker compose down -v
```

---

## 4.4 Chapter 07 실행 (Security + JWT)

Chapter 07은 Spring Security와 JWT(JSON Web Token) 인증을 포함한다.

### 실행하기

```bash
cd ~/edu_spring/spring/chapter07-spring-security

docker compose up --build
```

Spring Boot가 시작되면 새 터미널에서 인증 흐름을 테스트한다.

### 인증 테스트: 전체 흐름

**Step 1: 회원가입**

```bash
curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}' | python3 -m json.tool
```

**예상 출력:**
```json
{
    "id": 1,
    "username": "testuser",
    "message": "회원가입 성공"
}
```

**Step 2: 로그인하여 JWT 토큰 받기**

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}' | python3 -m json.tool
```

**예상 출력:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsIml...",
    "tokenType": "Bearer"
}
```

**Step 3: 토큰을 변수에 저장**

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")

echo $TOKEN
```

**예상 출력:**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsIml...
```

**Step 4: 토큰 없이 보호된 API 호출 (실패 확인)**

```bash
curl -s http://localhost:8080/api/users/me | python3 -m json.tool
```

**예상 출력:**
```json
{
    "status": 401,
    "error": "Unauthorized"
}
```

**Step 5: 토큰으로 인증된 요청**

```bash
curl -s http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
```

**예상 출력:**
```json
{
    "id": 1,
    "username": "testuser"
}
```

> **JWT 인증 흐름 요약:**
> 1. 회원가입 -> 2. 로그인(토큰 발급) -> 3. 토큰을 헤더에 넣어서 API 호출

### 종료

```bash
cd ~/edu_spring/spring/chapter07-spring-security
docker compose down -v
```

---

## 4.5 Chapter 09 실행 (종합 프로젝트)

Chapter 09는 게시판 REST API 종합 프로젝트이다.
PostgreSQL, Redis, Adminer(DB 관리 UI)가 함께 실행된다.

### 실행하기

```bash
cd ~/edu_spring/spring/chapter09-final-project

docker compose up --build
```

**예상 출력:**
```
[+] Running 4/4
 ✔ Container spring/chapter09-final-project-redis-1     Started
 ✔ Container spring/chapter09-final-project-postgres-1   Healthy
 ✔ Container spring/chapter09-final-project-adminer-1    Started
 ✔ Container spring/chapter09-final-project-app-1        Started
```

> 4개의 서비스가 실행된다:
> - **postgres**: PostgreSQL 데이터베이스 (포트 5432)
> - **redis**: Redis 캐시 (포트 6379)
> - **app**: Spring Boot 애플리케이션 (포트 8080)
> - **adminer**: 데이터베이스 관리 웹 UI (포트 8081)

### Adminer로 데이터베이스 관리하기

브라우저에서 http://localhost:8081 을 연다.

**로그인 정보:**

| 항목 | 값 |
|------|-----|
| 시스템 | PostgreSQL |
| 서버 | postgres |
| 사용자 이름 | edu |
| 비밀번호 | edu1234 |
| 데이터베이스 | edu_spring |

> **서버**에 `localhost`가 아닌 `postgres`를 입력하는 이유:
> Adminer 컨테이너에서 PostgreSQL 컨테이너로 접속하므로 Docker 내부 네트워크의 서비스 이름을 사용한다.

### 종합 CRUD 테스트

**1. 회원가입**

```bash
curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | python3 -m json.tool
```

**2. 로그인**

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")

echo "Token: $TOKEN"
```

**3. 게시글 작성**

```bash
curl -s -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title": "Docker로 게시판 실습", "content": "Chapter 09 종합 프로젝트 테스트입니다."}' | python3 -m json.tool
```

**4. 게시글 목록 조회**

```bash
curl -s http://localhost:8080/api/posts | python3 -m json.tool
```

**5. 특정 게시글 조회**

```bash
curl -s http://localhost:8080/api/posts/1 | python3 -m json.tool
```

**6. 게시글 수정**

```bash
curl -s -X PUT http://localhost:8080/api/posts/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title": "Docker 실습 완료!", "content": "Chapter 09 종합 프로젝트를 성공적으로 완료했습니다."}' | python3 -m json.tool
```

**7. 게시글 삭제**

```bash
curl -s -X DELETE http://localhost:8080/api/posts/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 종료 및 정리

```bash
cd ~/edu_spring/spring/chapter09-final-project

# 컨테이너 중지 (데이터 유지)
docker compose down

# 완전 초기화 (데이터 삭제)
docker compose down -v
```

---

# Part 5: 교육 프로젝트 전체 실행 가이드

## 5.1 인프라 먼저 띄우기

교육 프로젝트 전체에서 공유하는 인프라(PostgreSQL, Redis, Adminer)를 먼저 실행한다.

```bash
cd ~/edu_spring/spring/docker

docker compose -f docker-compose-infra.yml up -d
```

**예상 출력:**
```
[+] Running 3/3
 ✔ Container spring-postgres  Healthy
 ✔ Container spring-redis     Started
 ✔ Container spring-adminer   Started
```

### 상태 확인

```bash
cd ~/edu_spring/spring/docker

docker compose -f docker-compose-infra.yml ps
```

**예상 출력:**
```
NAME              IMAGE              COMMAND                  SERVICE    CREATED          STATUS                    PORTS
spring-adminer    adminer            "entrypoint.sh php..."   adminer    30 seconds ago   Up 28 seconds             0.0.0.0:8081->8080/tcp
spring-postgres   postgres:16-alpine "docker-entrypoint.s..." postgres   30 seconds ago   Up 29 seconds (healthy)   0.0.0.0:5432->5432/tcp
spring-redis      redis:7-alpine     "docker-entrypoint.s..." redis      30 seconds ago   Up 28 seconds             0.0.0.0:6379->6379/tcp
```

### 접속 정보

| 서비스 | 접속 방법 |
|--------|-----------|
| PostgreSQL | 호스트: `localhost`, 포트: `5432`, 사용자: `edu`, 비밀번호: `edu1234`, DB: `edu_spring` |
| Redis | 호스트: `localhost`, 포트: `6379` |
| Adminer (DB 관리 UI) | 브라우저: http://localhost:8081 |

**Adminer 접속 방법:**
1. 브라우저에서 http://localhost:8081 을 연다
2. 다음 정보를 입력한다:
   - 시스템: **PostgreSQL**
   - 서버: **postgres**
   - 사용자 이름: **edu**
   - 비밀번호: **edu1234**
   - 데이터베이스: **edu_spring**
3. **로그인** 버튼을 클릭한다

---

## 5.2 각 챕터 순서대로 실행하기

### Chapter 01: Java 기초

```bash
cd ~/edu_spring/java/chapter01-java-basics
docker compose up --build
```

> DB가 필요 없는 순수 Java 프로젝트이다. 실행 후 자동 종료된다.

### Chapter 02: 객체지향 프로그래밍

```bash
cd ~/edu_spring/java/chapter02-oop
docker build -t java/chapter02-oop .
docker run --rm java/chapter02-oop
```

### Chapter 03: 컬렉션

```bash
cd ~/edu_spring/java/chapter03-collections
docker build -t java/chapter03-collections .
docker run --rm java/chapter03-collections
```

### Chapter 04: Spring Boot 입문

```bash
cd ~/edu_spring/spring/chapter04-spring-boot-intro
docker compose up --build
```

테스트 (새 터미널):
```bash
curl http://localhost:8080/api/hello?name=World
curl http://localhost:8080/api/time
```

종료: `Ctrl + C` 또는 `docker compose down`

### Chapter 05: Spring Web

```bash
cd ~/edu_spring/spring/chapter05-spring-web
docker compose up --build
```

테스트 (새 터미널):
```bash
curl -s -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "테스트", "description": "Docker 실습"}'

curl -s http://localhost:8080/api/todos | python3 -m json.tool
```

종료: `Ctrl + C` 또는 `docker compose down`

### Chapter 06: Spring Data JPA

```bash
cd ~/edu_spring/spring/chapter06-spring-data-jpa
docker compose up --build
```

> 이 챕터부터 PostgreSQL이 함께 실행된다.

종료: `docker compose down -v` (DB 초기화 포함)

### Chapter 07: Spring Security

```bash
cd ~/edu_spring/spring/chapter07-spring-security
docker compose up --build
```

종료: `docker compose down -v`

### Chapter 08: 테스팅

```bash
cd ~/edu_spring/spring/chapter08-testing
docker compose up --build
```

종료: `docker compose down -v`

### Chapter 09: 종합 프로젝트

```bash
cd ~/edu_spring/spring/chapter09-final-project
docker compose up --build
```

종료: `docker compose down -v`

---

## 5.3 전체 정리 (리소스 해제)

교육이 끝나면 Docker 리소스를 정리하여 맥북의 디스크 공간을 확보한다.

### 현재 실행 중인 모든 컨테이너 확인

```bash
docker ps
```

### 특정 챕터 정리

```bash
# 현재 디렉토리의 docker compose 서비스 중지 및 삭제
docker compose down -v
```

### 인프라 정리

```bash
cd ~/edu_spring/spring/docker
docker compose -f docker-compose-infra.yml down -v
```

### 모든 컨테이너 중지

```bash
docker stop $(docker ps -q)
```

**예상 출력:**
```
a1b2c3d4e5f6
b2c3d4e5f6a7
...
```

> `docker ps -q`는 실행 중인 컨테이너 ID만 출력한다.
> 실행 중인 컨테이너가 없으면 에러 메시지가 나올 수 있지만 무시해도 된다.

### 사용하지 않는 모든 리소스 정리

```bash
docker system prune -a
```

**예상 출력:**
```
WARNING! This will remove:
  - all stopped containers
  - all networks not used by at least one container
  - all images without at least one container associated to them
  - all build cache

Are you sure you want to continue? [y/N] y
Deleted Containers:
...
Deleted Networks:
...
Deleted Images:
...

Total reclaimed space: 2.5GB
```

> `y`를 입력하고 Enter를 누른다.
> **주의**: 모든 이미지가 삭제되므로 다음에 실행할 때 다시 다운로드/빌드해야 한다.

### 볼륨까지 포함하여 완전 정리

```bash
docker system prune -a --volumes
```

> 이 명령은 **모든 Docker 데이터를 삭제**한다. 정말 깨끗하게 정리하고 싶을 때 사용한다.

### 디스크 사용량 확인

```bash
docker system df
```

**예상 출력:**
```
TYPE            TOTAL     ACTIVE    SIZE      RECLAIMABLE
Images          5         2         1.234GB   890.5MB (72%)
Containers      3         1         12.34MB   10.12MB (82%)
Local Volumes   2         1         234.5MB   120.3MB (51%)
Build Cache     15        0         456.7MB   456.7MB
```

| 열 | 설명 |
|----|------|
| TYPE | 리소스 종류 |
| TOTAL | 전체 개수 |
| ACTIVE | 사용 중인 개수 |
| SIZE | 전체 크기 |
| RECLAIMABLE | 정리 가능한 크기 |

상세 정보를 보려면 다음과 같이 한다:

```bash
docker system df -v
```

---

# Part 6: 트러블슈팅

## 6.1 자주 발생하는 에러와 해결

### 에러 1: "Cannot connect to the Docker daemon"

```
Cannot connect to the Docker daemon at unix:///var/run/docker.sock. Is the docker daemon running?
```

**원인**: Docker Desktop이 실행되지 않았다.

**해결 방법:**
1. 맥북 화면 상단 메뉴바에서 고래 아이콘이 있는지 확인한다.
2. 고래 아이콘이 없다면 **Applications** > **Docker** 를 실행한다.
3. 고래 아이콘이 움직이지 않을 때까지 기다린다 (약 30초).
4. 다시 명령어를 실행한다.

```bash
# Docker Desktop이 실행 중인지 확인
docker info
```

### 에러 2: "port is already allocated"

```
Error response from daemon: Ports are not available: listen tcp 0.0.0.0:8080: bind: address already in use
```

**원인**: 포트 8080을 이미 다른 프로그램이 사용하고 있다.

**해결 방법:**

```bash
# 8080 포트를 사용 중인 프로세스 확인
lsof -i :8080
```

**예상 출력:**
```
COMMAND   PID  USER   FD   TYPE             DEVICE SIZE/OFF NODE NAME
java     1234  user   56u  IPv6 0x12345...      0t0  TCP *:http-alt (LISTEN)
```

```bash
# 해당 프로세스 종료 (PID를 위 출력에서 확인)
kill -9 1234
```

또는 다른 포트를 사용한다:

```bash
# docker-compose.yml을 수정하지 않고 호스트 포트만 변경
# 예: 맥북의 9090 포트를 컨테이너의 8080 포트에 연결
docker run -p 9090:8080 my-app
```

### 에러 3: "no space left on device"

```
Error: no space left on device
```

**원인**: Docker에 할당된 디스크 공간이 부족하다.

**해결 방법:**

```bash
# 디스크 사용량 확인
docker system df

# 사용하지 않는 리소스 정리
docker system prune -a

# 볼륨까지 정리
docker system prune -a --volumes
```

정리 후에도 부족하다면 Docker Desktop 설정에서 디스크 크기를 늘린다:
- Settings > Resources > Disk image size

### 에러 4: Gradle 빌드 실패 - gradlew 권한 문제

```
/bin/sh: ./gradlew: Permission denied
```

**원인**: `gradlew` 파일에 실행 권한이 없다.

**해결 방법:**

```bash
# gradlew에 실행 권한 부여
chmod +x gradlew
```

> 이 프로젝트의 Dockerfile에는 이미 `chmod +x gradlew`가 포함되어 있지만,
> 로컬에서 직접 `./gradlew`를 실행할 때 이 에러가 발생할 수 있다.

### 에러 5: Apple Silicon 호환성 문제

```
WARNING: The requested image's platform (linux/amd64) does not match the detected host platform (linux/arm64/v8)
```

또는:

```
exec format error
```

**원인**: Intel용 이미지를 Apple Silicon에서 실행하려고 하고 있다.

**해결 방법 1: Rosetta 활성화 (권장)**

Docker Desktop > Settings > General > "Use Rosetta for x86_64/amd64 emulation on Apple Silicon" 활성화

**해결 방법 2: 플랫폼 명시**

```bash
docker run --platform linux/amd64 <이미지이름>
```

또는 `docker-compose.yml`에 추가:

```yaml
services:
  app:
    platform: linux/amd64
    build: .
```

**해결 방법 3: ARM 호환 이미지 사용**

```bash
# ARM 호환 이미지 예시
# eclipse-temurin:21-jdk     (ARM 지원)
# postgres:16-alpine          (ARM 지원)
# redis:7-alpine              (ARM 지원)
# nginx:alpine                (ARM 지원)
```

> 이 프로젝트에서 사용하는 이미지들은 모두 ARM(Apple Silicon)을 지원한다.

### 에러 6: "connection refused" - PostgreSQL 연결 실패

```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused.
```

**원인**: Spring Boot가 PostgreSQL보다 먼저 시작되었다.

**해결 방법:**

docker-compose.yml에 healthcheck와 depends_on을 설정한다 (이 프로젝트에는 이미 설정되어 있다):

```yaml
services:
  postgres:
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U edu"]
      interval: 5s
      timeout: 5s
      retries: 5

  app:
    depends_on:
      postgres:
        condition: service_healthy
```

수동으로 해결하려면:

```bash
# PostgreSQL만 먼저 실행
docker compose up postgres -d

# PostgreSQL이 준비될 때까지 대기 (약 10초)
sleep 10

# 그 다음 앱 실행
docker compose up app --build
```

### 에러 7: "image not found" 또는 빌드 실패

```
ERROR: failed to solve: eclipse-temurin:21-jdk: not found
```

**원인**: 네트워크 연결 문제로 이미지를 다운로드할 수 없다.

**해결 방법:**

```bash
# 네트워크 확인
curl -s https://hub.docker.com > /dev/null && echo "Docker Hub 연결 OK" || echo "연결 실패"

# 이미지 수동 다운로드
docker pull eclipse-temurin:21-jdk
docker pull postgres:16-alpine
docker pull redis:7-alpine
docker pull adminer
```

### 에러 8: "container name is already in use"

```
Error response from daemon: Conflict. The container name "/ch07-postgres" is already in use
```

**원인**: 같은 이름의 컨테이너가 이미 존재한다 (중지 상태일 수 있음).

**해결 방법:**

```bash
# 해당 컨테이너 삭제
docker rm ch07-postgres

# 또는 강제 삭제 (실행 중이어도)
docker rm -f ch07-postgres

# 또는 docker compose로 정리
docker compose down
```

---

## 6.2 유용한 디버깅 명령어

### 컨테이너 로그 확인

```bash
# 특정 컨테이너의 로그 확인
docker logs <container_id>

# 최근 100줄만 보기
docker logs --tail 100 <container_id>

# 실시간 로그 보기
docker logs -f <container_id>

# docker compose 환경에서 로그 보기
docker compose logs -f app
docker compose logs -f postgres
```

### 컨테이너 내부 접속

```bash
# 실행 중인 컨테이너에 bash로 접속
docker exec -it <container_id> bash

# bash가 없는 경우 (alpine 이미지 등) sh 사용
docker exec -it <container_id> sh

# docker compose 환경
docker compose exec app bash
docker compose exec postgres bash
```

접속 후 유용한 확인 명령어는 다음과 같다:

```bash
# 환경변수 확인
env

# 파일 시스템 확인
ls -la /app/

# 네트워크 확인
cat /etc/hosts

# Java 프로세스 확인
ps aux | grep java

# 포트 확인
netstat -tlnp
```

### 네트워크 확인

```bash
# Docker 네트워크 목록
docker network ls
```

**예상 출력:**
```
NETWORK ID     NAME                           DRIVER    SCOPE
a1b2c3d4e5f6   bridge                         bridge    local
b2c3d4e5f6a7   spring/chapter06-spring-data-jpa_default   bridge    local
c3d4e5f6a7b8   host                           host      local
d4e5f6a7b8c9   none                           null      local
```

```bash
# 특정 네트워크 상세 정보 (연결된 컨테이너 확인)
docker network inspect spring/chapter06-spring-data-jpa_default
```

### 리소스 사용량 모니터링

```bash
docker stats
```

**예상 출력:**
```
CONTAINER ID   NAME            CPU %   MEM USAGE / LIMIT   MEM %   NET I/O       BLOCK I/O     PIDS
a1b2c3d4e5f6   ch06-app-1      0.50%   245.3MiB / 4GiB     5.98%   1.2kB / 0B    0B / 0B       35
b2c3d4e5f6a7   ch06-postgres-1 0.01%   25.6MiB / 4GiB      0.63%   0B / 0B       0B / 0B       7
```

> `Ctrl + C`로 종료한다.

한 번만 확인하려면 다음과 같이 한다:

```bash
docker stats --no-stream
```

### 컨테이너 상세 정보

```bash
# 컨테이너의 모든 설정 정보 확인
docker inspect <container_id>

# IP 주소만 확인
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <container_id>

# 포트 매핑 확인
docker port <container_id>
```

---

## 6.3 맥북 성능 팁

### Docker Desktop 리소스 제한 설정

맥북의 RAM과 CPU에 따른 권장 설정:

| 맥북 RAM | Docker Memory | Docker CPUs | 설명 |
|----------|---------------|-------------|------|
| 8 GB | 3 GB | 2~4 | 최소 설정, 다른 작업과 병행 가능 |
| 16 GB | 4~6 GB | 4~6 | 교육용 권장 설정 |
| 32 GB | 8 GB | 6~8 | 넉넉한 설정, 여러 서비스 동시 실행 가능 |

### 불필요한 컨테이너 정리 습관

매일 실습이 끝나면 다음 명령어를 실행하는 습관을 들이도록 한다:

```bash
# 실행 중인 모든 컨테이너 확인
docker ps

# 모든 컨테이너 중지
docker compose down

# 중지된 컨테이너 정리
docker container prune -f

# 미사용 이미지 정리
docker image prune -f
```

### 필요한 서비스만 실행하기

전체를 실행하지 않고 필요한 서비스만 실행한다:

```bash
# PostgreSQL만 Docker로 실행하고, Spring Boot는 IDE에서 실행
cd ~/edu_spring/spring/chapter06-spring-data-jpa
docker compose up postgres -d

# IDE에서 Spring Boot 실행 시 application.yml의 datasource URL을 localhost로 설정
# spring.datasource.url=jdbc:postgresql://localhost:5432/edu_spring
```

### .dockerignore로 빌드 컨텍스트 최적화

프로젝트에 `.dockerignore` 파일을 추가하면 불필요한 파일이 빌드에 포함되지 않아
빌드 속도가 빨라진다.

**.dockerignore 예시:**

```
.git
.gradle
build
.idea
*.iml
.DS_Store
```

### Docker Desktop vs 터미널에서 리소스 확인

맥북 활동 모니터(Activity Monitor)에서 `com.docker.backend` 프로세스의
CPU와 메모리 사용량을 확인할 수 있다.

```bash
# 터미널에서 Docker 프로세스 확인
ps aux | grep -i docker | head -5
```

---

# Part 7: 유용한 Docker 팁

## 7.1 자주 쓰는 별칭(alias) 설정

매번 긴 명령어를 입력하는 것은 번거롭다. 자주 쓰는 명령어를 짧게 만들어보자.

### 별칭 추가하기

```bash
# zsh 설정 파일 열기
open -e ~/.zshrc
```

> 파일이 없으면 새로 만들어진다.

파일 맨 아래에 다음 내용을 추가한다:

```bash
# === Docker 별칭 ===
alias dc="docker compose"
alias dcu="docker compose up --build"
alias dcud="docker compose up -d --build"
alias dcd="docker compose down"
alias dcdv="docker compose down -v"
alias dcl="docker compose logs -f"
alias dps="docker ps --format 'table {{.ID}}\t{{.Names}}\t{{.Status}}\t{{.Ports}}'"
alias dpsa="docker ps -a --format 'table {{.ID}}\t{{.Names}}\t{{.Status}}\t{{.Ports}}'"
alias dprune="docker system prune -a -f"
alias dstats="docker stats --no-stream"
```

저장하고 적용한다:

```bash
source ~/.zshrc
```

### 별칭 사용 예시

설정 후에는 다음과 같이 짧게 사용할 수 있다:

```bash
# 기존 명령어          # 별칭 사용
docker compose up --build     # dcu
docker compose up -d --build  # dcud
docker compose down           # dcd
docker compose down -v        # dcdv
docker compose logs -f        # dcl
docker ps                     # dps
docker system prune -a -f     # dprune
```

**사용 예:**

```bash
cd ~/edu_spring/spring/chapter06-spring-data-jpa

# 빌드 및 실행
dcu

# (다른 터미널에서) 상태 확인
dps

# 종료
dcd
```

---

## 7.2 Docker Desktop 대안: OrbStack

### OrbStack이란?

OrbStack은 macOS 전용 Docker Desktop 대안이다.
Docker Desktop보다 **가볍고, 빠르고, 배터리를 덜 소모**한다.

### Docker Desktop과 OrbStack 비교

| 항목 | Docker Desktop | OrbStack |
|------|---------------|----------|
| 메모리 사용 | 1.5~3 GB | 0.3~1 GB |
| 시작 시간 | 30초~1분 | 2~5초 |
| 배터리 소모 | 많음 | 적음 |
| 가격 | 개인 무료 / 기업 유료 | 개인 무료 / 기업 유료 |
| Apple Silicon 지원 | 좋음 | 매우 좋음 |
| Docker 호환성 | 100% | 99.9% |

### OrbStack 설치

**방법 1: 공식 사이트**

https://orbstack.dev 에서 다운로드한다.

**방법 2: Homebrew (터미널)**

```bash
brew install orbstack
```

### OrbStack 사용법

OrbStack을 설치하면 기존 `docker` 명령어가 그대로 작동한다.
명령어를 바꿀 필요 없이 Docker Desktop 대신 OrbStack이 사용된다.

```bash
# Docker Desktop과 동일한 명령어 사용
docker --version
docker compose up --build
docker ps
```

> **주의**: Docker Desktop과 OrbStack을 동시에 실행하면 충돌이 발생한다.
> 하나만 사용해야 한다.
> OrbStack으로 전환하려면 Docker Desktop을 먼저 종료(Quit)해야 한다.

### OrbStack으로 전환하기

1. Docker Desktop을 종료한다 (메뉴바 고래 아이콘 > Quit Docker Desktop)
2. OrbStack을 실행한다
3. 기존 명령어를 그대로 사용한다

```bash
# 전환 후 테스트
docker run hello-world
```

---

## 7.3 Docker 이미지 레이어 이해하기

### 레이어란?

Dockerfile의 각 명령어(`FROM`, `COPY`, `RUN` 등)는 하나의 **레이어**를 생성한다.
Docker는 레이어를 **캐시**하여 변경되지 않은 부분을 재사용한다.

### 왜 중요한가?

Dockerfile을 잘 작성하면 빌드 시간을 크게 줄일 수 있다.

**나쁜 예:**

```dockerfile
FROM eclipse-temurin:21-jdk
COPY . /app
RUN ./gradlew bootJar
```

> 소스 코드를 한 줄만 바꿔도 `COPY . /app`부터 다시 실행된다.
> 의존성을 매번 다시 다운로드한다.

**좋은 예 (이 프로젝트에서 사용하는 방식):**

```dockerfile
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# 1. 의존성 파일만 먼저 복사 (잘 변경되지 않음)
COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY gradlew .

# 2. 의존성 다운로드 (캐시됨)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# 3. 소스 코드 복사 (자주 변경됨)
COPY src src

# 4. 빌드
RUN ./gradlew bootJar --no-daemon
```

> 소스 코드만 변경하면 3단계부터 다시 실행된다.
> 의존성 다운로드(2단계)는 캐시에서 가져오므로 빌드가 훨씬 빠르다.

### 이미지 레이어 확인

```bash
docker history java-basics
```

**예상 출력:**
```
IMAGE          CREATED         CREATED BY                                      SIZE
abc123def456   5 minutes ago   CMD ["java" "-cp" "out" "com.edu.basics.Var...  0B
def456abc789   5 minutes ago   RUN /bin/sh -c mkdir -p out && javac -d out...  12.3kB
789abc123def   5 minutes ago   COPY src/ src/                                  5.67kB
123def456abc   5 minutes ago   WORKDIR /app                                    0B
456abc789def   2 weeks ago     /bin/sh -c set -eux; ...                        200MB
```

---

## 7.4 유용한 Docker 명령어 모음

### 컨테이너 이름 지정하기

```bash
docker run --rm --name my-java -it eclipse-temurin:21-jdk bash
```

> `--name my-java`: 컨테이너에 `my-java`라는 이름을 지정한다.
> 이름으로 관리할 수 있어서 편리하다.

### 컨테이너 재시작

```bash
docker restart <container_id>
```

### 실행 중인 컨테이너에 명령어 실행

```bash
# 컨테이너에 접속하지 않고 명령어만 실행
docker exec <container_id> java -version

# 환경변수 확인
docker exec <container_id> env
```

### 컨테이너에서 파일 복사

```bash
# 컨테이너 -> 맥북
docker cp <container_id>:/app/logs/app.log ./app.log

# 맥북 -> 컨테이너
docker cp ./config.yml <container_id>:/app/config.yml
```

### 이미지 태그 변경

```bash
docker tag java-basics java-basics:v1.0
docker tag java-basics myregistry/java-basics:latest
```

### 이미지를 파일로 저장/불러오기

```bash
# 이미지를 tar 파일로 저장
docker save -o java-basics.tar java-basics

# tar 파일에서 이미지 불러오기
docker load -i java-basics.tar
```

> 네트워크 없이 이미지를 전달할 때 유용하다.

---

# 부록: 명령어 치트시트

## Docker 기본 명령어 표

| 명령어 | 설명 | 예시 |
|--------|------|------|
| `docker run` | 컨테이너 실행 | `docker run --rm -it eclipse-temurin:21-jdk bash` |
| `docker ps` | 실행 중인 컨테이너 목록 | `docker ps` |
| `docker ps -a` | 모든 컨테이너 목록 | `docker ps -a` |
| `docker stop` | 컨테이너 중지 | `docker stop <id>` |
| `docker rm` | 컨테이너 삭제 | `docker rm <id>` |
| `docker images` | 이미지 목록 | `docker images` |
| `docker pull` | 이미지 다운로드 | `docker pull postgres:16-alpine` |
| `docker rmi` | 이미지 삭제 | `docker rmi <image>` |
| `docker build` | 이미지 빌드 | `docker build -t my-app .` |
| `docker logs` | 컨테이너 로그 | `docker logs -f <id>` |
| `docker exec` | 컨테이너에 명령 실행 | `docker exec -it <id> bash` |
| `docker inspect` | 상세 정보 | `docker inspect <id>` |
| `docker stats` | 리소스 사용량 | `docker stats --no-stream` |
| `docker system df` | 디스크 사용량 | `docker system df` |
| `docker system prune` | 미사용 리소스 정리 | `docker system prune -a` |
| `docker container prune` | 중지된 컨테이너 정리 | `docker container prune -f` |
| `docker image prune` | 미사용 이미지 정리 | `docker image prune -a -f` |

## Docker Run 플래그 표

| 플래그 | 설명 | 예시 |
|--------|------|------|
| `--rm` | 종료 시 자동 삭제 | `docker run --rm image` |
| `-it` | 대화형 터미널 | `docker run -it image bash` |
| `-d` | 백그라운드 실행 | `docker run -d image` |
| `-p` | 포트 매핑 | `docker run -p 8080:8080 image` |
| `-v` | 볼륨 마운트 | `docker run -v $(pwd):/app image` |
| `-e` | 환경변수 설정 | `docker run -e "KEY=VALUE" image` |
| `-w` | 작업 디렉토리 | `docker run -w /app image` |
| `--name` | 컨테이너 이름 지정 | `docker run --name my-app image` |
| `--platform` | 플랫폼 지정 | `docker run --platform linux/amd64 image` |
| `--network` | 네트워크 지정 | `docker run --network my-net image` |

## Docker Compose 명령어 표

| 명령어 | 설명 | 예시 |
|--------|------|------|
| `docker compose up` | 서비스 시작 | `docker compose up --build` |
| `docker compose up -d` | 백그라운드 시작 | `docker compose up -d --build` |
| `docker compose down` | 서비스 중지 | `docker compose down` |
| `docker compose down -v` | 중지 + 볼륨 삭제 | `docker compose down -v` |
| `docker compose ps` | 서비스 상태 | `docker compose ps` |
| `docker compose logs` | 로그 확인 | `docker compose logs -f app` |
| `docker compose exec` | 서비스에 명령 실행 | `docker compose exec app bash` |
| `docker compose build` | 이미지만 빌드 | `docker compose build` |
| `docker compose pull` | 이미지 다운로드 | `docker compose pull` |
| `docker compose restart` | 서비스 재시작 | `docker compose restart app` |
| `docker compose stop` | 서비스 중지 (삭제 안 함) | `docker compose stop` |
| `docker compose start` | 중지된 서비스 시작 | `docker compose start` |
| `docker compose top` | 프로세스 목록 | `docker compose top` |

## 챕터별 포트 매핑 표

| 챕터 | 서비스 | 호스트 포트 | 컨테이너 포트 | URL |
|------|--------|------------|--------------|-----|
| Chapter 04 | Spring Boot | 8080 | 8080 | http://localhost:8080 |
| Chapter 05 | Spring Boot | 8080 | 8080 | http://localhost:8080 |
| Chapter 06 | Spring Boot | 8080 | 8080 | http://localhost:8080 |
| Chapter 06 | PostgreSQL | 5432 | 5432 | - |
| Chapter 07 | Spring Boot | 8080 | 8080 | http://localhost:8080 |
| Chapter 07 | PostgreSQL | 5432 | 5432 | - |
| Chapter 08 | Spring Boot | 8080 | 8080 | http://localhost:8080 |
| Chapter 08 | PostgreSQL | 5432 | 5432 | - |
| Chapter 09 | Spring Boot | 8080 | 8080 | http://localhost:8080 |
| Chapter 09 | PostgreSQL | 5432 | 5432 | - |
| Chapter 09 | Redis | 6379 | 6379 | - |
| Chapter 09 | Adminer | 8081 | 8080 | http://localhost:8081 |
| 인프라 | PostgreSQL | 5432 | 5432 | - |
| 인프라 | Redis | 6379 | 6379 | - |
| 인프라 | Adminer | 8081 | 8080 | http://localhost:8081 |

> **주의**: 같은 포트를 사용하는 챕터를 동시에 실행하면 포트 충돌이 발생한다.
> 한 번에 하나의 챕터만 실행하거나, 포트를 변경해서 사용해야 한다.

## Dockerfile 명령어 표

| 명령어 | 설명 | 예시 |
|--------|------|------|
| `FROM` | 베이스 이미지 지정 | `FROM eclipse-temurin:21-jdk` |
| `WORKDIR` | 작업 디렉토리 설정 | `WORKDIR /app` |
| `COPY` | 파일/디렉토리 복사 | `COPY src/ src/` |
| `RUN` | 빌드 시 명령어 실행 | `RUN javac Hello.java` |
| `CMD` | 컨테이너 시작 명령어 | `CMD ["java", "Hello"]` |
| `ENTRYPOINT` | 컨테이너 진입점 | `ENTRYPOINT ["java", "-jar", "app.jar"]` |
| `EXPOSE` | 포트 문서화 (실제 매핑 아님) | `EXPOSE 8080` |
| `ENV` | 환경변수 설정 | `ENV JAVA_OPTS="-Xmx512m"` |
| `ARG` | 빌드 시 인수 | `ARG JAR_FILE=app.jar` |
| `VOLUME` | 볼륨 마운트 포인트 | `VOLUME /data` |

## 자주 사용하는 이미지 목록

| 이미지 | 용도 | ARM 지원 |
|--------|------|----------|
| `eclipse-temurin:21-jdk` | Java 21 개발 환경 | O |
| `eclipse-temurin:21-jre` | Java 21 실행 환경 (경량) | O |
| `eclipse-temurin:21-jre-alpine` | Java 21 실행 환경 (초경량) | O |
| `gradle:8.8-jdk21` | Gradle 빌드 환경 | O |
| `postgres:16-alpine` | PostgreSQL 16 DB | O |
| `redis:7-alpine` | Redis 7 캐시 | O |
| `adminer` | DB 관리 웹 UI | O |
| `nginx:alpine` | Nginx 웹서버 | O |

---

## 마무리

이 가이드를 통해 다음을 학습했다:

1. **Docker Desktop 설치 및 설정** (Apple Silicon/Intel 구분)
2. **Docker 기본 명령어** (이미지, 컨테이너, 볼륨, 포트)
3. **Docker Compose** (여러 서비스 동시 관리)
4. **Java 프로젝트를 Docker로 실행** (직접 실행, Dockerfile, Compose)
5. **Spring Boot + PostgreSQL을 Docker로 실행**
6. **트러블슈팅** (자주 발생하는 에러와 해결법)
7. **맥북 최적화 팁** (리소스 관리, 별칭 설정)

Docker를 사용하면 개발 환경을 빠르게 구축하고, 누구나 동일한 환경에서 코드를 실행할 수 있다.
처음에는 명령어가 복잡해 보일 수 있지만, 이 가이드의 예제를 하나씩 따라하다 보면 금방 익숙해질 것이다.

**다음 단계로 추천:**
- Docker Hub에서 다양한 이미지 탐색해보기: https://hub.docker.com
- Docker 공식 튜토리얼: https://docs.docker.com/get-started/
- 자신만의 Dockerfile 작성해보기
