package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection을 파라미터로 넘겨서 커넥션 연동. 트랜잭션 적용.
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection conn = dataSource.getConnection();
        try {
            //트랜잭션 시작.
            conn.setAutoCommit(false);
            
            //실제 비즈니스 로직 수행
            bizLogic(conn, fromId, toId, money);

            //트랜잭션 종료(성공 시 commit)
            conn.commit();
        } catch (Exception e) {
            //트랜잭션 종료(실패 시 rollback)
            conn.rollback();
            throw new IllegalStateException(e);
        } finally {
            if (conn != null) {
                try {
                    release(conn);
                } catch (Exception e) {
                    log.error("error", e);
                }
            }
        }
    }

    private void bizLogic(Connection conn, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(conn, fromId);
        Member toMember = memberRepository.findById(conn, toId);

        memberRepository.update(conn, fromId, fromMember.getMoney() - money);

        validation(toMember); //계좌 이체가 도중에 실패하는 상황 만들기

        memberRepository.update(conn, toId, toMember.getMoney() + money);
    }

    private void release(Connection conn) throws SQLException {
        //Connection Pool을 사용하는 경우 connection을 반환했을 때 connection이 종료되지 않으므로
        //autocommit 설정이 그대로 남아있음
        //의도치 않은 에러를 막기 위해 autocommit을 true로 바꾸고 반환해줘야한다
        conn.setAutoCommit(true);
        conn.close();
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

}
