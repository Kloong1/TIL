import com.fasterxml.jackson.databind.ObjectMapper;
import dto.StartDto;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static connection.ConnectionConst.*;
import static connection.HttpConst.*;

public class RankingApp {

    private static String AUTH_KEY;

    public static void main(String[] args) throws IOException {
        StartDto startDto = requestStart(1);
        System.out.println("startDto = " + startDto);

        AUTH_KEY = startDto.getAuthKey();

        for (int turn = 1; turn <= 595; turn++) {
            
        }
    }

    private static StartDto requestStart(int problem) throws IOException {
        URL url = new URL(BASE_URL + START_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);

        conn.setRequestMethod("POST");

        conn.setRequestProperty(HEADER_X_AUTH_TOKEN, X_AUTH_TOKEN);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        ObjectMapper objectMapper = new ObjectMapper();

        String body = objectMapper.writeValueAsString(Map.of("problem", problem));

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(body);
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StartDto startDto = objectMapper.readValue(br, StartDto.class);
        br.close();

        return startDto;
    }
}
