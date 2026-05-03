package io.stykmartin.prombambooexporter.servlet;

import com.atlassian.annotations.security.UnrestrictedAccess;
import io.prometheus.client.exporter.common.TextFormat;
import io.stykmartin.prombambooexporter.manager.MetricCollector;
import io.stykmartin.prombambooexporter.manager.SecureTokenManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@UnrestrictedAccess
public class PrometheusExporter extends HttpServlet {
    private final transient MetricCollector metricCollector;
    private final transient SecureTokenManager secureTokenManager;
    private final transient BearerTokenResolver bearerTokenResolver;

    public PrometheusExporter(
            MetricCollector metricCollector,
            SecureTokenManager secureTokenManager,
            BearerTokenResolver bearerTokenResolver) {
        this.metricCollector = metricCollector;
        this.secureTokenManager = secureTokenManager;
        this.bearerTokenResolver = bearerTokenResolver;
    }

    @Override
    protected void doGet(
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse) throws IOException {
        if (secureTokenManager.isConfigured()
                && !secureTokenManager.matches(bearerTokenResolver.resolve(httpServletRequest))) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setContentType(TextFormat.CONTENT_TYPE_004);

        try (Writer writer = httpServletResponse.getWriter()) {
            TextFormat.write004(writer, metricCollector.getRegistry().filteredMetricFamilySamples(parse(httpServletRequest)));
            writer.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        doGet(httpServletRequest, httpServletResponse);
    }

    private Set<String> parse(HttpServletRequest httpServletRequest) {
        String[] includedParam = httpServletRequest.getParameterValues("name[]");
        if (includedParam == null) {
            return Collections.emptySet();
        } else {
            return new HashSet<>(Arrays.asList(includedParam));
        }
    }
}
