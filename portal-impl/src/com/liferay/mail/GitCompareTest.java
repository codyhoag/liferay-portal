package com.liferay.mail;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommit.File;
import org.kohsuke.github.GHCompare;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class GitCompareTest {

	public static void main(String[] args) throws IOException {
		
		String name = "codyhoag/liferay-docs";
		String branch = "cdn-docs";
		
		// Must have ~/.github file indicating login and password to do this.
		// You can use Github.connectAnonymously() if you don't want to log in.
		GitHub github = GitHub.connect();
		GHRepository repo = github.getRepository(name);
		Map<String, GHBranch> branches = repo.getBranches();
		GHBranch ghBranch = branches.get(branch);

		String olderId = "39597d9eacbc7d5b9ef22a32fefe452d07ffb109";
		String newerId = ghBranch.getSHA1();
		
		System.out.println(ghBranch + " points to commit: " + ghBranch.getSHA1());

		GHCompare compare = repo.getCompare(olderId, newerId);
		GHCommit[] commits = compare.getCommits();
		
		Set<File> fileList = new HashSet<File>();
		
		// When comparing commits, the files that were modified/changed/deleted
		// that were attached to each commit are removed. Therefore, we must
		// grab commits (which have no files attached), convert them to SHA1
		// strings, find the commits again based on those SHA1 IDs (which will
		// now have files attached), and find the files from those commits.
		for (GHCommit commit : commits) {
			String shaCommit = commit.getSHA1();
			GHCommit ghCommit = repo.getCommit(shaCommit);
			fileList.addAll(ghCommit.getFiles());
		}
		
		System.out.println(fileList);


		for (GHCommit.File file : fileList) {
			System.out.println(file.getStatus() + " " + file.getFileName());
			System.out.println(file.getRawUrl());
			
			URL url = new URL(file.getRawUrl(), "");
		    
		    java.io.File localPath;
		    
		    if (file.getFileName().endsWith(".markdown")) {
		    	localPath = java.io.File.createTempFile("TestRepo", ".markdown");
		    	String text = IOUtils.toString(url.openStream());
		    	PrintWriter printOut = new PrintWriter(localPath);
				printOut.println(text);
				printOut.close();
		    }
		    
		    else if (file.getFileName().endsWith(".png")) {
		    	localPath = java.io.File.createTempFile("TestRepo", ".png");
		    	InputStream inputStream = url.openStream();
				OutputStream outputStream = new FileOutputStream(localPath);

				byte[] byteArray = new byte[2048];
				int length;

				while ((length = inputStream.read(byteArray)) != -1) {
					outputStream.write(byteArray, 0, length);
				}

				inputStream.close();
				outputStream.close();
		    }
		    
		    else {
		    	System.out.println("File is not a Markdown or PNG file");
		    }
		} 
	}
}
