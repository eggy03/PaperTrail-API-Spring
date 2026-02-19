package io.github.eggy03.papertrail.api.configuration;

import io.github.eggy03.papertrail.api.util.AnsiColor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class HttpLoggingFilter extends OncePerRequestFilter {

    private String colorMethod (String method) {
        return switch (method) {
            case "GET" -> AnsiColor.BLUE;
            case "POST" -> AnsiColor.GREEN;
            case "PUT" -> AnsiColor.YELLOW;
            case "DELETE" -> AnsiColor.RED;
            default -> AnsiColor.RESET;
        };
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        long start = System.currentTimeMillis();
        filterChain.doFilter(request, response);
        long time = System.currentTimeMillis() - start;

        String httpMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        int responseStatus = response.getStatus();

        log.info("{}{}{} {} -> {} ({} ms)",
                colorMethod(httpMethod),
                httpMethod,
                AnsiColor.RESET,
                requestURI,
                responseStatus,
                time);
    }
}
