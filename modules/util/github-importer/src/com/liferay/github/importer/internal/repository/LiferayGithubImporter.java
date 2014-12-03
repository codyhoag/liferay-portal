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

import com.liferay.github.importer.GithubImporter;
import com.liferay.github.importer.internal.GithubCommit;
import com.liferay.github.importer.internal.GithubDiffs;
import com.liferay.github.importer.internal.ImportContent;

import java.io.IOException;

import javax.portlet.PortletPreferences;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Cody Hoag
 */
public class LiferayGithubImporter implements GithubImporter {
	
	public LiferayGithubImporter() {
		
		_githubCommit = new GithubCommit();
		_githubDiffs = new GithubDiffs();
		_importContent = new ImportContent();
	}

	@Override
	public String getLatestCommitId(HttpServletRequest request, PortletPreferences portletPreferences)
			throws IOException {
		
		return _githubCommit.getLatestCommitId(request, portletPreferences);
	}
	
	@Override
	public String getModifiedFiles(HttpServletRequest request, PortletPreferences portletPreferences)
			throws IOException {
		
		return _githubDiffs.getModifiedFiles(request, portletPreferences);
	}
	
	@Override
	public void processZip(HttpServletRequest request, PortletPreferences portletPreferences)
		throws IOException {
		
		_importContent.processZip(request, portletPreferences);
	}

	private final GithubCommit _githubCommit;
	private final GithubDiffs _githubDiffs;
	private final ImportContent _importContent;
	
}
