package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedExceptionTest {

    @Test
    void uncheckedCatch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void uncheckedThrow() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyUncheckedException.class);
    }

    /**
     * RuntimeException 을 상속받으면 unchecked exception이 된다
     */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * Unchecked 예외의 경우 잡아서 처리하거나 throws 로 명시하지 않아도 된다
     * 무시하면 알아서 던져진다
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 필요한 경우 예외를 잡아서 처리하면 된다
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                //예외 처리 로직 실행
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
            log.info("정상 흐름 복귀");
        }

        /**
         * 예외를 잡아서 처리하지 않아도 된다.
         * 예외를 던져도 throws 로 명시할 필요가 없다
         */
        public void callThrow() {
            repository.call();
            log.info("정상 흐름 복귀 X - 이 코드 실행 안됨");
        }
    }


    static class Repository {

        /**
         * unchecked exception을 던지기 때문에 throws 생략 가능
         * throws를 해도 상관 없지만 컴파일 과정에서 달라지는 건 없음
         * 다른 개발자가 쉽게 알 수 있는 정도...?
         */
        public void call() {
            log.info("예외 발생");
            throw new MyUncheckedException("ex");
        }
    }
}
