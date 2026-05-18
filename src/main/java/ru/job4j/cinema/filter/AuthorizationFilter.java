package ru.job4j.cinema.filter;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.jcip.annotations.ThreadSafe;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@ThreadSafe
@Component
@Order(1)
public class AuthorizationFilter implements Filter {
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/", "/index", "/films", "/sessions", "/login",
            "/users/register", "/error", "/favicon.ico"
    );
    private static final Set<String> PUBLIC_PREFIXES = Set.of(
            "/tickets/buy/", "/tickets/failure/", "/files/",
            "/css/", "/js/", "/images/", "/webjars/"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        if (isPublic(httpRequest) || SessionUser.isAuthorized(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
    }

    private boolean isPublic(HttpServletRequest request) {
        var path = getPath(request);
        return PUBLIC_PATHS.contains(path) || PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private String getPath(HttpServletRequest request) {
        var uri = request.getRequestURI();
        var contextPath = request.getContextPath();
        if (!contextPath.isBlank() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }
}
