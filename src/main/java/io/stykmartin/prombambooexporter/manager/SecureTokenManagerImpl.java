package io.stykmartin.prombambooexporter.manager;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@BambooComponent
public class SecureTokenManagerImpl implements SecureTokenManager {
    private static final String LEGACY_TOKEN_KEY = "PLUGIN_PROMETHEUS_FOR_BAMBOO_SECURITY_TOKEN";
    private static final String SALT_KEY = "PLUGIN_PROMETHEUS_FOR_BAMBOO_SECURITY_TOKEN_SALT_B64";
    private static final String HASH_KEY = "PLUGIN_PROMETHEUS_FOR_BAMBOO_SECURITY_TOKEN_HASH_B64";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH_BYTES = 16;

    private final PluginSettings pluginSettings;
    private final SecureRandom secureRandom = new SecureRandom();

    public SecureTokenManagerImpl(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    @Override
    public void setToken(String token) {
        PluginSettings settings = getPluginSettings();
        settings.remove(LEGACY_TOKEN_KEY);
        if (StringUtils.isBlank(token)) {
            settings.remove(SALT_KEY);
            settings.remove(HASH_KEY);
            return;
        }
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        secureRandom.nextBytes(salt);
        byte[] digest = hash(salt, token.getBytes(StandardCharsets.UTF_8));
        settings.put(SALT_KEY, Base64.getEncoder().encodeToString(salt));
        settings.put(HASH_KEY, Base64.getEncoder().encodeToString(digest));
    }

    @Override
    public boolean isConfigured() {
        PluginSettings settings = getPluginSettings();
        return readString(settings, HASH_KEY) != null
                || StringUtils.isNotBlank(readString(settings, LEGACY_TOKEN_KEY));
    }

    @Override
    public boolean matches(String candidate) {
        if (candidate == null) {
            return false;
        }
        PluginSettings settings = getPluginSettings();
        String saltB64 = readString(settings, SALT_KEY);
        String hashB64 = readString(settings, HASH_KEY);
        if (saltB64 != null && hashB64 != null) {
            byte[] expected = Base64.getDecoder().decode(hashB64);
            byte[] actual = hash(
                    Base64.getDecoder().decode(saltB64),
                    candidate.getBytes(StandardCharsets.UTF_8));
            return MessageDigest.isEqual(expected, actual);
        }
        String legacy = readString(settings, LEGACY_TOKEN_KEY);
        if (StringUtils.isBlank(legacy)) {
            return false;
        }
        boolean ok = MessageDigest.isEqual(
                legacy.getBytes(StandardCharsets.UTF_8),
                candidate.getBytes(StandardCharsets.UTF_8));
        if (ok) {
            setToken(legacy);
        }
        return ok;
    }

    private byte[] hash(byte[] salt, byte[] tokenBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            md.update(tokenBytes);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(HASH_ALGORITHM + " not available", e);
        }
    }

    private String readString(PluginSettings settings, String key) {
        Object value = settings.get(key);
        return value != null ? value.toString() : null;
    }

    private synchronized PluginSettings getPluginSettings() {
        return pluginSettings;
    }
}
