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

package com.liferay.jenkins.results.parser;

import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PortalTopLevelBuildData
	extends TopLevelBuildData implements PortalBuildData {

	public static boolean isValidJSONObject(JSONObject jsonObject) {
		return isValidJSONObject(jsonObject, _TYPE);
	}

	@Override
	public String getPortalGitHubURL() {
		return getString("portal_github_url");
	}

	@Override
	public String getPortalUpstreamBranchName() {
		return getString("portal_upstream_branch_name");
	}

	protected PortalTopLevelBuildData(JSONObject jsonObject) {
		super(jsonObject);

		validateKeys(_REQUIRED_KEYS);
	}

	protected PortalTopLevelBuildData(Map<String, String> buildParameters) {
		super(buildParameters);

		put("portal_github_url", _getPortalGitHubURL(buildParameters));
		put(
			"portal_upstream_branch_name",
			_getPortalUpstreamBranchName(buildParameters));

		validateKeys(_REQUIRED_KEYS);
	}

	@Override
	protected String getType() {
		return _TYPE;
	}

	private String _getPortalGitHubURL(Map<String, String> buildParameters) {
		if (!buildParameters.containsKey("PORTAL_GITHUB_URL")) {
			throw new RuntimeException("Please set PORTAL_GITHUB_URL");
		}

		return buildParameters.get("PORTAL_GITHUB_URL");
	}

	private String _getPortalUpstreamBranchName(
		Map<String, String> buildParameters) {

		if (!buildParameters.containsKey("PORTAL_UPSTREAM_BRANCH_NAME")) {
			throw new RuntimeException(
				"Please set PORTAL_UPSTREAM_BRANCH_NAME");
		}

		return buildParameters.get("PORTAL_UPSTREAM_BRANCH_NAME");
	}

	private static final String[] _REQUIRED_KEYS =
		{"portal_github_url", "portal_upstream_branch_name"};

	private static final String _TYPE = "portal_top_level";

}