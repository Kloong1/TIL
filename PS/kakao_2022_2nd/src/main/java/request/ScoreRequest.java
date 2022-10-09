package request;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static connection.ConnectionConst.BASE_URL;
import static connection.HttpConst.*;

public class ScoreRequest {

    private final URL url;
    private final ObjectMapper objectMapper;

    public ScoreRequest() throws IOException {
        this.url = new URL(BASE_URL + "/score");
        this.objectMapper = new ObjectMapper();
    }

    public void request(String authKey) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty(HEADER_AUTHORIZATION, authKey);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;
        StringBuilder sb = new StringBuilder();

        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }

        br.close();
        conn.disconnect();

        System.out.println(sb.toString());
    }
}
