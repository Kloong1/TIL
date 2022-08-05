package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToIpPortConverter implements Converter<String, IpPort> {

    @Override
    public IpPort convert(String source) {
        log.info("Convert source = {}", source);
        String[] strings = source.split(":");
        String ip = strings[0];
        int port = Integer.parseInt(strings[1]);
        return new IpPort(ip, port);
    }
}
