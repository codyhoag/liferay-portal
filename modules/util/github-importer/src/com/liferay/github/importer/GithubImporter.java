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

package com.liferay.github.importer;

import java.io.IOException;

import javax.portlet.PortletPreferences;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Cody Hoag
 */
public interface GithubImporter {
	
	public abstract String getModifiedFiles(
			HttpServletRequest request, PortletPreferences portletPreferences)
					throws IOException;
	
	public abstract String getLatestCommitId(
			HttpServletRequest request, PortletPreferences portletPreferences)
					throws IOException;

	public abstract void processZip(
			HttpServletRequest request, PortletPreferences portletPreferences)
					throws IOException;

}
