package api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static connection.ConnectionConst.*;

public class StartApi {

    private final URL url;
    private final ObjectMapper objectMapper;

    public StartApi() throws MalformedURLException {
        this.url = new URL(BASE_URL + "/start");
        this.objectMapper = new ObjectMapper();
    }

    public String getAuthorizationKey(int problem) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty(HEADER_X_AUTH_TOKEN, X_AUTH_TOKEN);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        objectMapper.writeValue(bw, Map.of("problem", problem));

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Map<String, Object> jsonMap = objectMapper.readValue(br, new TypeReference<>() {});
        br.close();

        String authKey = (String) jsonMap.get("auth_key");

        conn.disconnect();

        return authKey;
    }
}
