/**
 * 
 */
package com.neusoft.datainsight.checkwork.util;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * @author kuangjq
 *
 */
public class RedirectIfNotError extends LaxRedirectStrategy {

	/* (non-Javadoc)
	 * @see org.apache.http.impl.client.DefaultRedirectStrategy#isRedirected(org.apache.http.HttpRequest, org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
	 */
	@Override
	public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
			throws ProtocolException {
        final Header locationHeader = response.getFirstHeader("location");
        if(locationHeader!=null&&locationHeader.getValue().contains("error")) {
        	return false;
        }
		return super.isRedirected(request, response, context);
	}
}
