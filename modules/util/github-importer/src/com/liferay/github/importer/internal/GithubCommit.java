package com.liferay.github.importer.internal;

import com.liferay.github.importer.internal.repository.AdminUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.IOException;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.servlet.http.HttpServletRequest;

public class GithubCommit {
	
	public String getLatestCommitId(HttpServletRequest request, PortletPreferences portletPreferences)
			throws IOException {
		
		String userRepository = ParamUtil.getString(request, "repositoryUrl", AdminUtil.getRepositoryUrl(portletPreferences));
		String githubBranch = ParamUtil.getString(request, "branch", AdminUtil.getBranch(portletPreferences));

		GitHub github = GitHub.connectAnonymously();
		GHRepository repo = github.getRepository(userRepository);
		Map<String, GHBranch> branches = repo.getBranches();
		GHBranch ghBranch = branches.get(githubBranch);
		
		String latestCommitId = ghBranch.getSHA1();
		
		return latestCommitId;

	}

}
