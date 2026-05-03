package io.stykmartin.prombambooexporter.servlet;

import io.prometheus.client.exporter.common.TextFormat;
import io.stykmartin.prombambooexporter.manager.MetricCollector;
import io.stykmartin.prombambooexporter.manager.SecureTokenManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PrometheusExporter extends HttpServlet {
    private final transient MetricCollector metricCollector;
    private final transient SecureTokenManager secureTokenManager;

    public PrometheusExporter(
            MetricCollector metricCollector,
            SecureTokenManager secureTokenManager) {
        this.metricCollector = metricCollector;
        this.secureTokenManager = secureTokenManager;
    }

    @Override
    protected void doGet(
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse) throws IOException {
        String paramToken = httpServletRequest.getParameter("token");
        String storedToken = secureTokenManager.getToken();

        if (StringUtils.isNotBlank(storedToken) && !storedToken.equals(paramToken)) {
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
