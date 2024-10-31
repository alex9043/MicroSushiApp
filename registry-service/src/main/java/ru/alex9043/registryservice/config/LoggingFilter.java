package ru.alex9043.registryservice.config;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        MDC.put("customTag", "myLog");
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("customTag");
        }
    }
}
