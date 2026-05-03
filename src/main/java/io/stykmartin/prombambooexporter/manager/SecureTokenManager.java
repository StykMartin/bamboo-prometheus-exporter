package io.stykmartin.prombambooexporter.manager;

public interface SecureTokenManager {
    String getToken();
    void setToken(String token);
}
