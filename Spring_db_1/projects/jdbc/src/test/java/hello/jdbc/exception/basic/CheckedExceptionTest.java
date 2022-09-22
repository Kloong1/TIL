package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CheckedExceptionTest {

    @Test
    void checkedCatch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checkedThrow() throws MyCheckedException {
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyCheckedException.class);
    }

    /* Exception 을 상속받는 예외는 checked exception이 된다 */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Service {

        Repository repository = new Repository();

        /**
         *  예외를 잡아서 처리하는 코드
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                //예외 처리 로직 실행
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
            log.info("정상 흐름 복귀");
        }

        /**
         * 체크 예외를 밖으로 던진다
         * throws로 어떤 체크 예외가 던져지는지 선언해야함
         * @throws MyCheckedException
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
            log.info("정상 흐름 복귀 X - 이 코드 실행 안됨");
        }
    }

    static class Repository {
        public void call() throws MyCheckedException {
            log.info("예외 발생");
            throw new MyCheckedException("ex");
        }
    }

}
