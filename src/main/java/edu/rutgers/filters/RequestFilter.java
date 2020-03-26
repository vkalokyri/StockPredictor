package edu.rutgers.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.rutgers.util.LoggerFactory;
import edu.rutgers.util.Utilities;
import edu.rutgers.util.enums.CONSTANTS;
import edu.rutgers.util.enums.LOGGER_TYPE;
import edu.rutgers.util.enums.LOG_TYPE;
import edu.rutgers.util.enums.PAGES;

/**
 * Main applications filter which will be in charge of session and authentication handling.
 *
 */
public class RequestFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException { }
	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpSession session = ((HttpServletRequest)request).getSession(false);
		if(session == null) {
			// just go straight into portal.jsp, save some server time
			((HttpServletResponse)response).sendRedirect(PAGES.PORTAL.toString());
			return;
		} else {
			LoggerFactory.getLogger(LOGGER_TYPE.CONSOLE).log(Utilities.getDataStampString()+
					" - Filter: "+this+" - "+
					(httpReq.getSession(false).getAttribute(CONSTANTS.USER_ID.toString()) != null ? httpReq.getSession(false).getAttribute(CONSTANTS.USER_ID.toString()) : "Session: "+httpReq.getSession().getId())
							+" - "+httpReq.getRequestURI(), LOG_TYPE.GRAL);
			chain.doFilter(request, response);
		}
	}
	
	public String toString() {
		return "RequestFilter";
	}
}
