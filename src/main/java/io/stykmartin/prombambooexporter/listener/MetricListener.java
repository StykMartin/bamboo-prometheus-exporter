package io.stykmartin.prombambooexporter.listener;

import com.atlassian.bamboo.deployments.execution.events.DeploymentFinishedEvent;
import com.atlassian.bamboo.deployments.projects.DeploymentProject;
import com.atlassian.bamboo.deployments.projects.service.DeploymentProjectService;
import com.atlassian.bamboo.deployments.results.DeploymentResult;
import com.atlassian.bamboo.deployments.results.service.DeploymentResultService;
import com.atlassian.bamboo.event.BambooErrorEvent;
import com.atlassian.bamboo.event.BuildCanceledEvent;
import com.atlassian.bamboo.event.BuildFinishedEvent;
import com.atlassian.bamboo.event.BuildQueueTimeoutEvent;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.BambooImport;
import io.stykmartin.prombambooexporter.manager.MetricCollector;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

@BambooComponent
public class MetricListener {
    private final MetricCollector metricCollector;
    private final DeploymentResultService deploymentResultService;
    private final DeploymentProjectService deploymentProjectService;
    private final ResultsSummaryManager resultsSummaryManager;
    private final EventPublisher eventPublisher;

    @Inject
    public MetricListener(
            MetricCollector metricCollector,
            @BambooImport DeploymentResultService deploymentResultService,
            @BambooImport DeploymentProjectService deploymentProjectService,
            @BambooImport ResultsSummaryManager resultsSummaryManager,
            @BambooImport EventPublisher eventPublisher) {
        this.metricCollector = metricCollector;
        this.deploymentResultService = deploymentResultService;
        this.deploymentProjectService = deploymentProjectService;
        this.resultsSummaryManager = resultsSummaryManager;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void register() {
        eventPublisher.register(this);
    }

    @PreDestroy
    public void unregister() {
        eventPublisher.unregister(this);
    }

    @EventListener
    public void buildFinishedEvent(BuildFinishedEvent buildFinishedEvent) {
        long duration = 0;
        ResultsSummary summary = resultsSummaryManager.getResultsSummary(buildFinishedEvent.getPlanResultKey());
        if (summary != null) {
            duration = summary.getDuration();
        }
        metricCollector.finishedBuildsCounter(buildFinishedEvent.getBuildPlanKey(), buildFinishedEvent.getBuildState().name());
        metricCollector.finishedBuildsDuration(buildFinishedEvent.getBuildPlanKey(), duration);
    }

    @EventListener
    public void buildCanceledEvent(BuildCanceledEvent buildCanceledEvent) {
        metricCollector.canceledBuildsCounter(buildCanceledEvent.getBuildPlanKey());
    }

    @EventListener
    public void bambooErrorEvent(BambooErrorEvent bambooErrorEvent) {
        metricCollector.errorsCounter(bambooErrorEvent.isNewError());
    }

    @EventListener
    public void buildQueueTimeoutEvent(BuildQueueTimeoutEvent buildQueueTimeoutEvent) {
        metricCollector.buildQueueTimeoutCounter(buildQueueTimeoutEvent.getBuildPlanKey());
    }

    @EventListener
    public void deploymentFinishedEvent(DeploymentFinishedEvent deploymentFinishedEvent) {
        DeploymentResult deploymentResult = deploymentResultService.getDeploymentResult(deploymentFinishedEvent.getDeploymentResultId());
        if (deploymentResult == null) {
            return;
        }
        DeploymentProject deploymentProject = deploymentProjectService.getDeploymentProject(deploymentResult.getEnvironment().getDeploymentProjectId());
        if (deploymentProject != null) {
            metricCollector.finishedDeploysCounter(deploymentProject.getPlanKey().getKey(), deploymentResult.getDeploymentState().name());
        }
    }
}
