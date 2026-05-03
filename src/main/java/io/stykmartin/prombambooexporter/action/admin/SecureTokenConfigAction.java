package io.stykmartin.prombambooexporter.action.admin;

import com.atlassian.bamboo.configuration.GlobalAdminAction;
import com.atlassian.bamboo.security.BambooPermissionManager;
import com.atlassian.bamboo.user.BambooAuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import io.stykmartin.prombambooexporter.manager.SecureTokenManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ActionContext;
import org.apache.struts2.Preparable;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

public class SecureTokenConfigAction extends GlobalAdminAction implements Preparable {
	private final transient SecureTokenManager secureTokenManager;
	private final transient BambooAuthenticationContext bambooAuthenticationContext;
	private final transient BambooPermissionManager bambooPermissionManager;

	private boolean saved;
	private String token;
	private boolean clear;

	public SecureTokenConfigAction(SecureTokenManager secureTokenManager,
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
		if (clear) {
			secureTokenManager.setToken("");
			saved = true;
		} else if (StringUtils.isNotBlank(token)) {
			secureTokenManager.setToken(token);
			saved = true;
		}
		token = null;
		clear = false;
		return SUCCESS;
	}

	@Override
	public String input() {
		token = null;
		clear = false;
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

	public boolean isTokenConfigured() {
		return secureTokenManager.isConfigured();
	}

	public String getToken() {
		return token;
	}

	@StrutsParameter
	public void setToken(String token) {
		this.token = token;
	}

	public boolean isClear() {
		return clear;
	}

	@StrutsParameter
	public void setClear(boolean clear) {
		this.clear = clear;
	}
}
