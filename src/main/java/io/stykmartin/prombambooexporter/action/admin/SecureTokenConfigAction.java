package io.stykmartin.prombambooexporter.action.admin;

import com.atlassian.bamboo.configuration.GlobalAdminAction;
import com.atlassian.bamboo.security.BambooPermissionManager;
import com.atlassian.bamboo.user.BambooAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.user.User;
import io.stykmartin.prombambooexporter.manager.SecureTokenManager;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class SecureTokenConfigAction extends GlobalAdminAction {
	private final transient SecureTokenManager secureTokenManager;
	private final transient BambooAuthenticationContext bambooAuthenticationContext;
	private final transient BambooPermissionManager bambooPermissionManager;
	private final transient HttpContext httpContext;

	private boolean saved;

	public SecureTokenConfigAction(SecureTokenManager secureTokenManager,
			@ComponentImport BambooAuthenticationContext bambooAuthenticationContext,
			@ComponentImport BambooPermissionManager bambooPermissionManager,
			@ComponentImport HttpContext httpContext) {
		this.secureTokenManager = secureTokenManager;
		this.bambooAuthenticationContext = bambooAuthenticationContext;
		this.bambooPermissionManager = bambooPermissionManager;
		this.httpContext = httpContext;
	}

	@Override
	public String execute() {
		User user = bambooAuthenticationContext.getUser();
		if (user == null || !bambooPermissionManager.isAdmin(user.getName())) {
			return ERROR;
		}
		HttpServletRequest request = httpContext.getRequest();
		String token = request.getParameter("token");
		boolean clear = Boolean.parseBoolean(request.getParameter("clear"));
		if (clear) {
			secureTokenManager.setToken("");
			saved = true;
		} else if (StringUtils.isNotBlank(token)) {
			secureTokenManager.setToken(token);
			saved = true;
		}
		return SUCCESS;
	}

	@Override
	public String input() {
		saved = false;
		return INPUT;
	}

	public String getMetricsUrl() {
		return getBambooUrl().rootContext() + "/plugins/servlet/prometheus/metrics";
	}

	public boolean isSaved() {
		return saved;
	}

	public boolean isTokenConfigured() {
		return secureTokenManager.isConfigured();
	}

	public String getToken() {
		return null;
	}

	public boolean isClear() {
		return false;
	}
}
