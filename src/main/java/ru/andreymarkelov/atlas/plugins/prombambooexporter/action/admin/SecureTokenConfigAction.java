package ru.andreymarkelov.atlas.plugins.prombambooexporter.action.admin;

import com.atlassian.bamboo.configuration.GlobalAdminAction;
import com.atlassian.bamboo.security.BambooPermissionManager;
import com.atlassian.bamboo.user.BambooAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.apache.struts2.ActionContext;
import org.apache.struts2.Preparable;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import ru.andreymarkelov.atlas.plugins.prombambooexporter.manager.SecureTokenManager;

public class SecureTokenConfigAction extends GlobalAdminAction implements Preparable {
    private final transient SecureTokenManager secureTokenManager;
    private final transient BambooAuthenticationContext bambooAuthenticationContext;
    private final transient BambooPermissionManager bambooPermissionManager;

    private boolean saved;
    private String token;

    public SecureTokenConfigAction(
            SecureTokenManager secureTokenManager,
            @ComponentImport BambooAuthenticationContext bambooAuthenticationContext,
            @ComponentImport BambooPermissionManager bambooPermissionManager) {
        this.secureTokenManager = secureTokenManager;
        this.bambooAuthenticationContext = bambooAuthenticationContext;
        this.bambooPermissionManager = bambooPermissionManager;
    }

    @Override
    public String execute() {
        User user = bambooAuthenticationContext.getUser();
        if (user == null || !bambooPermissionManager.isAdmin(user.getName())) {
            return ERROR;
        }
        secureTokenManager.setToken(token);
        saved = true;
        return SUCCESS;
    }

    @Override
    public String input() {
        token = secureTokenManager.getToken();
        saved = false;
        return INPUT;
    }

    @Override
    public void prepare() {
        ActionContext.getContext().put("baseurl", getBambooUrl().rootContext());
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getToken() {
        return token;
    }

    @StrutsParameter
    public void setToken(String token) {
        this.token = token;
    }
}
