package com.liferay.mail;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommit.File;
import org.kohsuke.github.GHCompare;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

import sun.misc.BASE64Decoder;

public class GitCompareTest {

	public static void main(String[] args) throws IOException {

		String name = "codyhoag/liferay-docs";
		String branch = "cdn-docs";
		String contentPath = "use/deployment";

		// Must have ~/.github file indicating login and password to do this.
		// You can use Github.connectAnonymously() if you don't want to log in.
		GitHub github = GitHub.connect();
		GHRepository repo = github.getRepository(name);
		Map<String, GHBranch> branches = repo.getBranches();
		GHBranch ghBranch = branches.get(branch);

		//GHRef ref = repo.getRef("heads/" + branch);
		//System.out.println("ref URL: " + ref.getUrl());

		String olderId = "39597d9eacbc7d5b9ef22a32fefe452d07ffb109";
		//String olderId = null;
		String newerId = ghBranch.getSHA1();

		if (olderId == null || olderId.isEmpty()) {

			getAllMarkdownFiles(contentPath, repo);
			getAllImageFiles(contentPath, repo);
		}

		else {
			System.out.println(ghBranch + " points to commit: " + ghBranch.getSHA1());

			GHCompare compare = repo.getCompare(olderId, newerId);
			GHCommit[] commits = compare.getCommits();

			Set<File> allFiles = new HashSet<File>();

			// When comparing commits, the files that were modified/changed/deleted
			// that were attached to each commit are removed. Therefore, we must
			// grab commits (which have no files attached), convert them to SHA1
			// strings, find the commits again based on those SHA1 IDs (which will
			// now have files attached), and find the files from those commits.
			for (GHCommit commit : commits) {
				String shaCommit = commit.getSHA1();
				GHCommit ghCommit = repo.getCommit(shaCommit);
				allFiles.addAll(ghCommit.getFiles());
			}

			boolean imagePresent = false;
			Set<String> fileSet = new HashSet<String>();

			for (GHCommit.File file : allFiles) {

				System.out.println(file.getStatus() + " " + file.getFileName());

				if (file.getFileName().endsWith(".markdown") && !fileSet.contains(file.getFileName())) {
					fileSet.add(file.getFileName());
					getModifiedMarkdownFile(file, newerId, name);
				}

				else if (file.getFileName().endsWith(".png")) {
					imagePresent = true;
				}

				else if (file.getFileName().endsWith(".png") || file.getFileName().endsWith(".markdown")) {
					System.out.println("File is a duplicate");
				}
				
				else {
					System.out.println("File is not in appropriate format");
				}
			}
			
			if (imagePresent) {
				getAllImageFiles(contentPath, repo);
			}

			try {
				System.out.println("Creating ../../test-dist/diffs.zip file");
				(new java.io.File("../../test-dist")).mkdirs();
				
				FileOutputStream fileOutputStream = new FileOutputStream("../../test-dist/diffs.zip");
				ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
				//Set<String> markdownArticles = new HashSet<String>();
//
				for (String filePath : fileSet) {
					String markdownArticlePath = "E:/test/" + filePath.substring(contentPath.length() + 1, filePath.length());
					System.out.println(markdownArticlePath);
				
				//String markdownFile = "E:/test/articles/06-configuring-for-high-availability/00-configuring-for-high-availability-intro.markdown";
				
				//markdownArticles.add("E:/test/" + filePath);
//
					addToZipFile(markdownArticlePath, zipOutputStream);

				//for (String image : markdownImagesFinal) {
				//	addToZipFile(image, zipOutputStream);
				//}
			}
				zipOutputStream.close();
				fileOutputStream.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected static void addToZipFile(String modFile, ZipOutputStream zipOutputStream)
			throws FileNotFoundException, IOException {

		System.out.println("Adding " + modFile + " to zip file");

		java.io.File file = new java.io.File(modFile);
		FileInputStream fileInputStream = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(modFile);
		zipOutputStream.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int len;
		while ((len = fileInputStream.read(bytes)) >= 0) {
			zipOutputStream.write(bytes, 0, len);
		}

		zipOutputStream.closeEntry();
		fileInputStream.close();
	}

	protected static void getAllImageFiles(String contentPath, GHRepository repo)
			throws IOException {

		String imagesPath = contentPath + "/images";
		List<GHContent> articleContents = repo.getDirectoryContent(imagesPath);

		for (GHContent content : articleContents) {
			String imageContent = content.getEncodedContent();
			String imagePath = content.getPath();
			String imageFilePath = imagePath.substring(imagePath.indexOf("images/"), imagePath.length());

			try {
				BASE64Decoder decoder = new BASE64Decoder();  
				byte[] byteArray = decoder.decodeBuffer(imageContent);					
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(byteArray));
				java.io.File file = new java.io.File("../../test/" + imageFilePath);
				file.getParentFile().mkdirs();
				file.createNewFile();
				ImageIO.write(bufferedImage, "png", file);
			}		
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}

	protected static void getAllMarkdownFiles(String contentPath, GHRepository repo)
			throws IOException {

		String articlePath = contentPath + "/articles";
		List<GHContent> articleContents = repo.getDirectoryContent(articlePath);

		for (GHContent content : articleContents) {

			if (content.isDirectory()) {
				PagedIterable<GHContent> childArticleContents = content.listDirectoryContent();

				for (GHContent childArticleContent : childArticleContents) {
					String childText = childArticleContent.getContent();
					String childPath = childArticleContent.getPath();
					String filePath = childPath.substring(childPath.indexOf("articles/"), childPath.length());
					java.io.File file = new java.io.File("../../test/" + filePath);

					try {
						file.getParentFile().mkdirs();
						file.createNewFile();
						PrintWriter pw = new PrintWriter(file);
						pw.println(childText);
						pw.close();

					}
					catch (Exception e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
		}		
	}

	protected static void getModifiedMarkdownFile(GHCommit.File file, String mostRecentCommitId, String name)
			throws IOException {

		String urlString = new URL(file.getRawUrl(), "").toString();
		int a = urlString.indexOf(name) + name.length() + 5;
		int b = urlString.indexOf("/", a);
		urlString = urlString.replace(urlString.substring(a, b), mostRecentCommitId);
		URL url = new URL(urlString);
		urlString = urlString.substring(0, a);

		int x = file.getFileName().indexOf("articles/");
		int y = file.getFileName().length();
		String fileString = file.getFileName().substring(x, y);
		System.out.println("fileString: " + fileString);

		java.io.File modifiedFile = new java.io.File("../../test/" + fileString);

		try {
			modifiedFile.getParentFile().mkdirs();
			modifiedFile.createNewFile();
			String text = IOUtils.toString(url.openStream());
			PrintWriter printOut = new PrintWriter(modifiedFile);
			printOut.println(text);
			printOut.close();

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}

