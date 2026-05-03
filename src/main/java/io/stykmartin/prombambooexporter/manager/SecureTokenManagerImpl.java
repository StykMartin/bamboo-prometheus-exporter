package io.stykmartin.prombambooexporter.manager;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

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

    @Override
    public boolean isConfigured() {
        return StringUtils.isNotBlank(getToken());
    }

    @Override
    public boolean matches(String candidate) {
        if (candidate == null) {
            return false;
        }
        String stored = getToken();
        if (StringUtils.isBlank(stored)) {
            return false;
        }
        return MessageDigest.isEqual(
                stored.getBytes(StandardCharsets.UTF_8),
                candidate.getBytes(StandardCharsets.UTF_8));
    }

    private synchronized PluginSettings getPluginSettings() {
        return pluginSettings;
    }
}
