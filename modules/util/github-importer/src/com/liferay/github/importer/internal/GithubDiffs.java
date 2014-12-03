package com.liferay.github.importer.internal;

import com.liferay.github.importer.internal.repository.AdminUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletPreferences;
import javax.servlet.http.HttpServletRequest;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommit.File;
import org.kohsuke.github.GHCompare;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class GithubDiffs {

	public String getModifiedFiles(HttpServletRequest request, PortletPreferences portletPreferences)
			throws IOException {

		String userRepository = ParamUtil.getString(request, "repositoryUrl", AdminUtil.getRepositoryUrl(portletPreferences));
		String githubBranch = ParamUtil.getString(request, "branch", AdminUtil.getBranch(portletPreferences));
		String oldCommitId = "7d902e9efa6fb89b658095cfbebbfec35eba744d";

		GitHub github = GitHub.connectAnonymously();
		GHRepository repo = github.getRepository(userRepository);
		Map<String, GHBranch> branches = repo.getBranches();
		GHBranch ghBranch = branches.get(githubBranch);

		String newCommitId = ghBranch.getSHA1();

		Set<String> fileSet = new HashSet<String>();
		String finalList = null;

		if (oldCommitId == null || oldCommitId.isEmpty()) {
			finalList = "Unable to display diffs: No previous diffs commit stored in importer.";
		}

		else if (oldCommitId == newCommitId) {
			finalList = "There are no added/modified files to import";
		}

		else {
			GHCompare compare = repo.getCompare(oldCommitId, newCommitId);
			GHCommit[] commits = compare.getCommits();
			Set<File> allFiles = new HashSet<File>();

			for (GHCommit commit : commits) {
				String shaCommit = commit.getSHA1();
				GHCommit ghCommit = repo.getCommit(shaCommit);
				allFiles.addAll(ghCommit.getFiles());
			}

			for (GHCommit.File file : allFiles) {

				if ((file.getFileName().endsWith(".markdown") || file.getFileName().endsWith(".md") ||
						file.getFileName().endsWith(".png") || file.getFileName().endsWith(".jpg")) &&
						!fileSet.contains(file.getFileName())) {

					fileSet.add(file.getStatus() + " " + file.getFileName());
				}
			}

			StringBuilder sb = new StringBuilder();

			for (String uniqueFile : fileSet) {
				sb.append(uniqueFile).append("," + System.lineSeparator());
				finalList = sb.toString();

			}
		}

		return finalList;
	}
}
