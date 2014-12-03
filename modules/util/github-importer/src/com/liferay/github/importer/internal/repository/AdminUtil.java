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

package com.liferay.github.importer.internal.repository;

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

import javax.portlet.PortletPreferences;

/**
 * @author Cody Hoag
 */
public class AdminUtil {
	
	public static String getBaseArticlePath(PortletPreferences preferences) {
		String baseArticlePath = preferences.getValue(
				"baseArticlePath", StringPool.BLANK);

			if (Validator.isNotNull(baseArticlePath)) {
				return baseArticlePath;
			}

			return StringPool.BLANK;
	}

	public static String getBranch(PortletPreferences preferences) {
		String branch = preferences.getValue(
				"branch", StringPool.BLANK);

			if (Validator.isNotNull(branch)) {
				return branch;
			}

			return StringPool.BLANK;
	}
	
	public static String getRepositoryUrl(
			PortletPreferences preferences) {

			String repositoryUrl = preferences.getValue(
				"repositoryUrl", StringPool.BLANK);

			if (Validator.isNotNull(repositoryUrl)) {
				return repositoryUrl;
			}

			return StringPool.BLANK;
	}
	
	

}
