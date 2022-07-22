package com.ptn.internal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LoggingService {

    @SneakyThrows
    public void logRequest(HttpServletRequest httpServletRequest, Object body) {
        log.info("=====================================>> LOGGING REQUEST <<======================================");
        System.out.println("---------------------- REQUEST -----------------------");
        System.out.println("METHOD = " + httpServletRequest.getMethod());
        System.out.println("URL = " + httpServletRequest.getRequestURL()
                .append(httpServletRequest.getQueryString()!=null ? "?"+httpServletRequest.getQueryString() : "").toString());
        try {
        } catch (Exception e) {
        } finally {
            if (body != null) {
                System.out.println(this.getObjectMapper().writeValueAsString(body));
            }
        }

    }

    @SneakyThrows
    public void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object body) {
        System.out.println("---------------------- RESPONSE ----------------------- ");
        System.out.println(this.getObjectMapper().writeValueAsString(body));
    }

    private Map<String, String> buildParametersMap(HttpServletRequest httpServletRequest) {
        Map<String, String> resultMap = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = httpServletRequest.getParameter(key);
            resultMap.put(key, value);
        }

        return resultMap;
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

}