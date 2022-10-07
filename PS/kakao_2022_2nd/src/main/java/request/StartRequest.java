package request;

import com.fasterxml.jackson.databind.ObjectMapper;
import parser.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static connection.ConnectionConst.BASE_URL;
import static connection.ConnectionConst.X_AUTH_TOKEN;
import static connection.HttpConst.*;

public class StartRequest {

    private final URL url;
    private final ObjectMapper objectMapper;

    public StartRequest() throws IOException{
        this.url = new URL(BASE_URL + "/start");
        this.objectMapper = new ObjectMapper();
    }

    public String request(int problem) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty(HEADER_X_AUTH_TOKEN, X_AUTH_TOKEN);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        String body = objectMapper.writeValueAsString(Map.of("problem", problem));

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(body);
        bw.flush();
        bw.close();

        JsonParser<Map<String, Object>> jsonParser = new JsonParser<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Map<String, Object> jsonMap = jsonParser.parseAfterPrint(br);

        conn.disconnect();

        return (String) jsonMap.get("auth_key");
    }
}
