package com.qbit.p2p.credit.commons.filter;

import com.qbit.commons.auth.AuthFilter;
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

	@Override
	public void init(FilterConfig fc) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

		String userId = (String) httpRequest.getSession().getAttribute(USER_ID_KEY);

		//System.out.println("!!! USER_FILTER: " + httpRequest.getContextPath() + " " + httpRequest.getPathInfo());

		if ("/users-profile/current".equals(httpRequest.getPathInfo())) {
			if (!userId.contains("@")) {
				System.out.println("!!! REDIRECT");
				((HttpServletResponse) servletResponse).sendRedirect("/p2p-credit");
			} else {
				filterChain.doFilter(servletRequest, servletResponse);
			}
		} else {
			//System.out.println("!!! FILTER_CHAIN");

			filterChain.doFilter(servletRequest, servletResponse);
		}

	}

	@Override
	public void destroy() {
	}
}
