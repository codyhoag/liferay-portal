/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.portlet.toolbar.contributor.locator;

import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.locator.PortletToolbarContributorLocator;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.collections.ServiceReferenceMapper;
import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerMap;

import java.util.List;

import javax.portlet.PortletRequest;

import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides an implementation of PortletToolbarContributorLocator for portlets
 * using Struts as MVC pattern, allowing to have different
 * PortletToolbarContributor for different struts actions.
 *
 * <p>
 * PortletToolbarContributor implementations will need to be registered in the
 * OSGI Registry using the following properties:
 * </p>
 *
 * <ul>
 * <li>
 * &quot;javax.portlet.name&quot; the portlet id of the portlet whose portlet
 * toolbar will be extended.
 * </li>
 * <li>
 * &quot;struts.action&quot; this property is optional. If this property is not
 * present, the portlet toolbar will always be extended. If it contains a value
 * (i.e: /blogs/view_entry) the portlet toolbar will be extended only for that
 * specific struts action. If the value is &quot;-&quot; the portlet toolbar
 * will be extended when there is no strutsAction specified in the request
 * (typically when rendering the first view of the portlet).
 * </li>
 * </ul>
 *
 * <p>
 * A single PortletToolbarContributor implementation can be used for different
 * portlets and struts actions by including multiple times the same properties.
 * </p>
 *
 * @author Sergio González
 */
@Component(immediate = true)
public class StrutsPortletToolbarContributorLocator
	implements PortletToolbarContributorLocator {

	@Override
	public List<PortletToolbarContributor> getPortletToolbarContributors(
		String portletId, PortletRequest portletRequest) {

		String strutsAction = ParamUtil.getString(
			portletRequest, "struts_action", "-");

		List<PortletToolbarContributor> portletToolbarContributors =
			_serviceTrackerMap.getService(
				portletId.concat(StringPool.PERIOD).concat(strutsAction));

		if (ListUtil.isEmpty(portletToolbarContributors)) {
			portletToolbarContributors = _serviceTrackerMap.getService(
				portletId);
		}

		return portletToolbarContributors;
	}

	@Activate
	protected void activate() {
		_serviceTrackerMap = ServiceTrackerCollections.multiValueMap(
			PortletToolbarContributor.class, "(javax.portlet.name=*)",
			new ServiceReferenceMapper<String, PortletToolbarContributor>() {

				@Override
				public void map(
					ServiceReference<PortletToolbarContributor>
						serviceReference,
					Emitter<String> emitter) {

					String portletName = (String)serviceReference.getProperty(
						"javax.portlet.name");
					String strutsAction = (String)serviceReference.getProperty(
						"struts.action");

					String key = portletName;

					if (strutsAction != null) {
						key += StringPool.PERIOD.concat(strutsAction);
					}

					emitter.emit(key);
				}

			});

		_serviceTrackerMap.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Reference(target = "(original.bean=*)", unbind = "-")
	protected void setServletContext(ServletContext servletContext) {
	}

	private static ServiceTrackerMap<String, List<PortletToolbarContributor>>
		_serviceTrackerMap;

}