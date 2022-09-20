package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * 트랜잭션 매니저 적용
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final MemberRepositoryV3 memberRepository;
    private final PlatformTransactionManager transactionManager;

    //더이상 JDBC 기술에 의존하지 않음
    //private final DataSource dataSource;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        //트랜잭션 시작. 파라미터는 트랜잭션에 관련된 설정인데, 지금은 기본 설정을 사용한다.
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            //실제 비즈니스 로직 수행
            bizLogic(fromId, toId, money);

            //트랜잭션 종료(성공 시 commit)
            transactionManager.commit(status);
        } catch (Exception e) {
            //트랜잭션 종료(실패 시 rollback)
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
        //트랜잭션이 commit/rollback 되면 모든 작업이 끝난 것이므로 커넥션을 release 하면 된다.
        //따라서 그 작업을 transactionManager 가 commit/rollback 할 때 알아서 해준다.
        //더 이상 finally 에서 release를 해줄 필요가 없다.
    }

    //아직도 예외는 jdbc 예외를 사용중이다. 이건 나중에 처리한다.
    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember); //계좌 이체가 도중에 실패하는 상황 만들기
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

}
