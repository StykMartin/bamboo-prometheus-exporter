package io.stykmartin.prombambooexporter.servlet;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@BambooComponent
public class BearerTokenResolver {
    private static final String TOKEN_QUERY_PARAM = "token";
    private static final Pattern AUTHORIZATION_PATTERN = Pattern.compile(
            "^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

    public String resolve(HttpServletRequest request) {
        String fromHeader = resolveHeaderToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        String fromQuery = request.getParameter(TOKEN_QUERY_PARAM);
        if (fromHeader != null && StringUtils.isNotBlank(fromQuery)) {
            return null;
        }
        return fromHeader != null ? fromHeader : fromQuery;
    }

    private String resolveHeaderToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            return null;
        }
        Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorizationHeader);
        return matcher.matches() ? matcher.group("token") : null;
    }
}
