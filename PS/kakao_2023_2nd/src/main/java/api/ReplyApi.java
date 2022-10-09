package api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ReplyReservationDto;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static connection.ConnectionConst.*;

public class ReplyApi {

    private final URL url;
    private final ObjectMapper objectMapper;
    private final String authKey;

    public ReplyApi(String authKey) throws MalformedURLException {
        this.url = new URL(BASE_URL + "/reply");
        this.objectMapper = new ObjectMapper();
        this.authKey = authKey;
    }

    public int replyReservations(List<ReplyReservationDto> replyReservationList) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setRequestProperty(HEADER_AUTHORIZATION, authKey);
        conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        objectMapper.writeValue(bw, Map.of("replies", replyReservationList));

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Map<String, Integer> jsonMap = objectMapper.readValue(br, new TypeReference<>() {});
        br.close();

        return jsonMap.get("day");
    }

}
