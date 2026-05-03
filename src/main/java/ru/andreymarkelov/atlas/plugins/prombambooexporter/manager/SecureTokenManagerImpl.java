package ru.andreymarkelov.atlas.plugins.prombambooexporter.manager;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

@BambooComponent
public class SecureTokenManagerImpl implements SecureTokenManager {
    private final PluginSettings pluginSettings;

    public SecureTokenManagerImpl(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    @Override
    public String getToken() {
        Object storedValue = getPluginSettings().get("PLUGIN_PROMETHEUS_FOR_BAMBOO_SECURITY_TOKEN");
        return storedValue != null ? storedValue.toString() : "";
    }

    @Override
    public void setToken(String token) {
        getPluginSettings().put("PLUGIN_PROMETHEUS_FOR_BAMBOO_SECURITY_TOKEN", token);
    }

    private synchronized PluginSettings getPluginSettings() {
        return pluginSettings;
    }
}
