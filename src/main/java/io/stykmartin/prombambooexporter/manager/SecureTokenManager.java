package io.stykmartin.prombambooexporter.manager;

public interface SecureTokenManager {
	void setToken(String token);
	boolean isConfigured();
	boolean matches(String candidate);
}
