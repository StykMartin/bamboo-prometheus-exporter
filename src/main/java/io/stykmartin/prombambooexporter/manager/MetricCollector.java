package io.stykmartin.prombambooexporter.manager;

import io.prometheus.client.CollectorRegistry;

public interface MetricCollector {
	CollectorRegistry getRegistry();
	void errorsCounter(boolean isNew);
	void finishedBuildsCounter(String state);
	void finishedBuildsDuration(long durationMillis);
	void canceledBuildsCounter();
	void finishedDeploysCounter(String state);
	void buildQueueTimeoutCounter();
}
