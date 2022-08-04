package hello.exception.reslover;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (ex instanceof UserException) {
                log.info("Resolver: UserException to 400");

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                String acceptHeader = request.getHeader("accept");

                if (acceptHeader.equals(MediaType.APPLICATION_JSON_VALUE)) {
                    log.info("Resolver: Accept application/json");

                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());

                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("utf-8");

                    String result = objectMapper.writeValueAsString(errorResult);
                    response.getWriter().write(result);

                    return new ModelAndView();
                } else {
                    log.info("Resolver: Accept other");
                    return new ModelAndView("error/4xx");
                }
            }
        } catch (IOException e) {
            log.info("Resolver IOException!", e);
        }

        return null;
    }
}
