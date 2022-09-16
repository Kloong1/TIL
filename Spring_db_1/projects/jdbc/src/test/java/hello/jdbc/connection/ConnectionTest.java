package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException {
        Connection conn1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection conn2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection={}, class={}", conn1, conn1.getClass());
        log.info("connection={}, class={}", conn2, conn2.getClass());
    }

    @Test
    void driverManagerDataSource() throws SQLException {
        //DriverManagerDataSource - 항상 새로운 커넥션 획득
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    @Test
    void connectionPoolDataSource() throws SQLException, InterruptedException {
        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10); //default값이 10
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        Thread.sleep(1000); //커넥션 풀에 커넥션을 생성하는 시간 대기
        //커넥션 생성은 별도의 스레드에서 하므로 해당 로그를 확인하기 위해서는 sleep 필요
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection conn1 = dataSource.getConnection();
        Connection conn2 = dataSource.getConnection();

        /* 커넥션 풀 테스트 용
        Connection conn3 = dataSource.getConnection();
        Connection conn4 = dataSource.getConnection();
        Connection conn5 = dataSource.getConnection();
        Connection conn6 = dataSource.getConnection();
        Connection conn7 = dataSource.getConnection();
        Connection conn8 = dataSource.getConnection();
        Connection conn9 = dataSource.getConnection();
        Connection conn10 = dataSource.getConnection();
        Connection conn11 = dataSource.getConnection();
        */

        log.info("connection={}, class={}", conn1, conn1.getClass());
        log.info("connection={}, class={}", conn2, conn2.getClass());
    }
}
