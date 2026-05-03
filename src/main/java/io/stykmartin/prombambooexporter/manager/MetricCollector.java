package io.stykmartin.prombambooexporter.manager;

import io.prometheus.client.CollectorRegistry;

public interface MetricCollector {
    CollectorRegistry getRegistry();
    void errorsCounter(boolean isNew);
    void finishedBuildsCounter(String planKey, String state);

    void finishedBuildsDuration(String planKey, long durationMillis);

    void canceledBuildsCounter(String planKey);
    void finishedDeploysCounter(String planKey, String state);
    void buildQueueTimeoutCounter(String planKey);
}
