package parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class JsonParser<T> {

    private final static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    public T parse(BufferedReader br) throws IOException {
        T object = objectMapper.readValue(br, new TypeReference<T>() {});
        return object;
    }

    public T parseAfterPrint(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }

        String json = sb.toString();
        System.out.println(json);

        return objectMapper.readValue(json, new TypeReference<T>() {});
    }
}
