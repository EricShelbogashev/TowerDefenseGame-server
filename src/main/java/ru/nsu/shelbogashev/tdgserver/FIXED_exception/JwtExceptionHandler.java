package ru.nsu.shelbogashev.tdgserver.FIXED_exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;

import java.io.IOException;
import java.util.Map;

@Log4j2
public class JwtExceptionHandler {

    public static void factoryHandler(Exception exception, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) {
        if (exception instanceof ServletException) {
            handleServletException(exception, request, response, filterChain);
            return;
        }
        defaultHandler(exception, request, response, filterChain);
    }

    public static void defaultHandler(@NotNull Exception exception, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) {
        setMessage(response, exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static void handleServletException(@NotNull Exception exception, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) {
        setMessage(response, exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    public static void setMessage(@NotNull HttpServletResponse response, @NotNull String message, @NotNull HttpStatus status) {
        Map<String, Object> errorResponse = Mapper.toMessageJsonMap(message);
        response.setStatus(status.value());
        try {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(convertMapToJson(errorResponse).toCharArray());
        } catch (IOException e) {
            log.error("defaultHandler() : caught exception " + e.getMessage());
        }
    }

    public static String convertMapToJson(Map<String, Object> map) throws JsonProcessingException {
        return new Gson().toJson(map);
    }
}
