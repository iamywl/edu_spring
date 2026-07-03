package com.edu.javabook.ch06;

/**
 * 6.12 패키지
 *
 * 패키지(package)는 관련 있는 클래스들을 묶는 '폴더' 같은 개념이다.
 *  - 파일 맨 위의 'package com.edu.javabook.ch06;' 가 이 클래스의 소속 패키지다.
 *  - 패키지는 디렉토리 구조와 일치한다(com/edu/javabook/ch06/).
 *  - 이름 충돌을 막고(같은 이름 클래스도 패키지가 다르면 공존), 접근 범위를 나눈다.
 *  - 완전한 이름(FQCN) = 패키지명 + 클래스명 (예: com.edu.javabook.ch06.PackageDemo)
 *
 * 다른 패키지의 클래스는 import 로 불러오거나 완전한 이름으로 사용한다.
 */
public class PackageDemo {

    static class Sample {
    }

    public static void main(String[] args) {

        System.out.println("=== 6.12 패키지 ===");

        // [1] 현재 클래스의 패키지 확인
        System.out.println("\n[1] 이 클래스의 패키지");
        Package pkg = PackageDemo.class.getPackage();
        System.out.println("getPackageName() → " + PackageDemo.class.getPackageName());
        System.out.println("getName()        → " + (pkg != null ? pkg.getName() : "이름없음"));

        // [2] 완전한 이름(FQCN) vs 단순 이름
        System.out.println("\n[2] 완전한 이름 vs 단순 이름");
        System.out.println("getName()       (FQCN)  → " + PackageDemo.class.getName());
        System.out.println("getSimpleName() (단순명) → " + PackageDemo.class.getSimpleName());

        // [3] 표준 라이브러리 클래스의 패키지도 확인 가능
        System.out.println("\n[3] 표준 클래스의 패키지");
        System.out.println("String  의 패키지 → " + String.class.getPackageName());
        System.out.println("Object  의 패키지 → " + Object.class.getPackageName());
        System.out.println("→ java.lang 패키지는 import 없이 자동으로 사용 가능");

        // [4] 같은 패키지의 다른 클래스는 import 없이 사용
        System.out.println("\n[4] 같은 패키지 접근");
        Sample s = new Sample();
        System.out.println("같은 패키지의 Sample 사용 → " + s.getClass().getName());

        // [왜?] 패키지는 이름 충돌을 막고 코드를 논리적으로 조직화한다.
        System.out.println("\n[왜?] 패키지로 클래스를 폴더처럼 묶어 대규모 코드를 체계적으로 관리한다.");

        System.out.println("\n프로그램 정상 종료");
    }
}
