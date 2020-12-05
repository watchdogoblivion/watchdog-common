package com.oblivion.watchdogs.common.model;

import static com.oblivion.watchdogs.common.logger.Log.error;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 
 * @author Samuel D.
 *
 */
public class InMemoryTransaction extends ServletRequestAttributes {

	/**
	 * 
	 */
	private final Object attribute;

	/**
	 * 
	 */
	private final String service;

	/**
	 * 
	 * @param requestAttributes
	 * @param mainAttribute
	 */
	public InMemoryTransaction(RequestAttributes requestAttributes, String mainAttribute) {
		super(((ServletRequestAttributes) requestAttributes).getRequest(),
				((ServletRequestAttributes) requestAttributes).getResponse());
		attribute = this.getRequest().getAttribute(mainAttribute);
		service = this.getRequest().getServletPath();
	}

	/**
	 * 
	 * @return
	 */
	public Object getAttribute() {
		return attribute;
	}

	/**
	 * 
	 * @return
	 */
	public String getService() {
		return service;
	}

	/**
	 * 
	 * @param requestAttributes
	 */
	public void setRequestAttributes(RequestAttributes requestAttributes) {
		try {
			if (requestAttributes.getAttributeNames(RequestAttributes.SCOPE_REQUEST).length > 0) {
				for (String name : requestAttributes.getAttributeNames(RequestAttributes.SCOPE_REQUEST)) {
					Object scopeRequest = requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
					if (scopeRequest != null) {
						this.setAttribute(name, scopeRequest, RequestAttributes.SCOPE_REQUEST);
					}
				}
			}
			if (requestAttributes.getAttributeNames(RequestAttributes.SCOPE_SESSION).length > 0) {
				for (String name : requestAttributes.getAttributeNames(RequestAttributes.SCOPE_SESSION)) {
					Object scopeSession = requestAttributes.getAttribute(name, RequestAttributes.SCOPE_SESSION);
					if (scopeSession != null) {
						this.setAttribute(name, scopeSession, RequestAttributes.SCOPE_SESSION);
					}

				}
			}
		} catch (Exception e) {
			error(this, "An exception was thrown while setting the request attributes");
		}
	}

}