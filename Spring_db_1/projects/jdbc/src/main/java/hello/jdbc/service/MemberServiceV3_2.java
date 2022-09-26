package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 템플릿 적용
 */
@Slf4j
public class MemberServiceV3_2 {

    private final MemberRepositoryV3 memberRepository;
    private final TransactionTemplate transactionTemplate;

    //트랜잭션의 시작, commit, rollback 등을 transactionTemplate 를 통해 하기 때문에
    //더이상 transactionManager 를 갖고 있을 필요가 없다.
    //private final PlatformTransactionManager transactionManager;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    //transactionTemplate를 통해 transaction 관련 로직을 전부 없앨 수 있다.
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        //트랜잭션을 시작해준다.
        //로직 수행에 성공하면 내부에서 commit, 예외가 발생하면 rollback을 실행해준다.
        //정확히는 unchecked 예외일 경우에만 rollback 하고 checked 예외일 경우 commit한다.
        //이렇게 하는 이유는 뒤에서 설명한다.
        transactionTemplate.executeWithoutResult((status) -> {
            //executeWithoutResult() 에서 checked 예외를 처리하지 못하기 때문에
            //try catch로 감싸준 것임.
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
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
