package com.edu.intro;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 같은 타입의 Bean이 여러 개일 때 어떤 Bean을 주입할지 정하는 방법 예제.
 *
 * GreetingService는 @Profile로 환경에 따라 하나만 등록되지만,
 * 실무에서는 동일 타입의 Bean을 동시에 여러 개 등록해 두고
 * 그 중 하나를 골라 주입해야 하는 경우가 많다.
 *
 * 이때 사용하는 두 가지 방법:
 *  - @Primary  : "기본으로 주입할 Bean"을 지정한다 (타입만 명시하면 이 Bean이 선택됨)
 *  - @Qualifier: 특정 이름의 Bean을 콕 집어 주입한다 (@Primary보다 우선함)
 *
 * 이 파일은 기존 Profile 기반 설정과 완전히 독립적인 별도 예제이다.
 */
public class QualifierExample {

    // 1. 결제 방식을 표현하는 간단한 인터페이스
    public interface PaymentService {
        String pay(int amount);
    }

    // 2. 첫 번째 구현체 - @Primary로 "기본 Bean"으로 지정
    //    Bean 이름은 클래스 이름의 첫 글자를 소문자로 바꾼 "kakaoPayService"
    @Component
    @Primary
    public static class KakaoPayService implements PaymentService {
        @Override
        public String pay(int amount) {
            return amount + "원을 카카오페이로 결제했습니다. (@Primary 기본 Bean)";
        }
    }

    // 3. 두 번째 구현체 - 특별히 지정해야만 주입되는 Bean
    @Component
    public static class NaverPayService implements PaymentService {
        @Override
        public String pay(int amount) {
            return amount + "원을 네이버페이로 결제했습니다. (@Qualifier로 선택된 Bean)";
        }
    }

    // 4. PaymentService를 주입받아 결과를 출력하는 소비자(Consumer)
    @Component
    public static class PaymentClient {

        // @Primary가 붙은 KakaoPayService가 자동으로 주입된다
        private final PaymentService primaryPayment;

        // @Qualifier("naverPayService")로 네이버페이 Bean을 콕 집어 주입한다
        // (@Qualifier가 @Primary보다 우선하므로 카카오페이가 아닌 네이버페이가 주입됨)
        private final PaymentService selectedPayment;

        public PaymentClient(
                PaymentService primaryPayment,
                @Qualifier("naverPayService") PaymentService selectedPayment
        ) {
            this.primaryPayment = primaryPayment;
            this.selectedPayment = selectedPayment;
        }

        public void demonstrate() {
            System.out.println("[@Primary 주입]   " + primaryPayment.pay(10000));
            System.out.println("[@Qualifier 주입] " + selectedPayment.pay(5000));
        }
    }

    // 5. 애플리케이션 기동 직후 자동 실행되어 어떤 Bean이 선택됐는지 보여준다
    @Configuration
    public static class QualifierDemoConfig {

        @Bean
        public CommandLineRunner qualifierDemoRunner(PaymentClient paymentClient) {
            return args -> {
                System.out.println("===== @Primary / @Qualifier 데모 =====");
                paymentClient.demonstrate();
                System.out.println("=====================================");
            };
        }
    }
}
