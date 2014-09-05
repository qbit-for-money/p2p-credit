package com.qbit.p2p.credit.commons.filter;

import static com.qbit.commons.auth.AuthFilter.USER_ID_KEY;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Alexander_Sergeev
 */
public class UserProfileFilter implements Filter {
	
	private FilterConfig filterConfig;

	@Override
	public void init(FilterConfig fc) throws ServletException {
		filterConfig = fc;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

		String userId = (String) httpRequest.getSession().getAttribute(USER_ID_KEY);
		
		String contextPath = ((HttpServletRequest) servletRequest).getContextPath();

		boolean isRequestToCurrentProfile = "/users-profile/current".equals(httpRequest.getPathInfo());
		boolean userIdIsMail = ((userId != null) && userId.contains("@"));
		if (isRequestToCurrentProfile && !userIdIsMail) {
			if (contextPath.startsWith(filterConfig.getInitParameter("context-path"))) {
				((HttpServletResponse) servletResponse).sendRedirect(contextPath);
			} else {
				((HttpServletResponse) servletResponse).sendRedirect("/");
			}
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	@Override
	public void destroy() {
	}
}
