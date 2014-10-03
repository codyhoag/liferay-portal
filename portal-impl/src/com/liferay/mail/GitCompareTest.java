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
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

import sun.misc.BASE64Decoder;

public class GitCompareTest {

	public static void main(String[] args) throws IOException {

		// These will be set by the user in the Git Configuration screen
		String name = "codyhoag/liferay-docs";
		String branch = "github-testing-branch";
		String contentPath = "develop/tutorials";

		// You can create a ~/.github file indicating login and password if
		// you'd like to sign in while connecting. If going this route, use
		// the 'Github.connect()' method.
		GitHub github = GitHub.connectAnonymously();
		GHRepository repo = github.getRepository(name);
		Map<String, GHBranch> branches = repo.getBranches();
		GHBranch ghBranch = branches.get(branch);

		// The olderId represents the commit that will be stored in the KB portlet.
		// By setting the newerId to the most recent commit on our specified branch,
		// the getCompare method (used later) scans our specified branch for all
		// commits that are new compared to the olderId.
		String olderId = "7d902e9efa6fb89b658095cfbebbfec35eba744d";
		//String olderId = null;
		String newerId = ghBranch.getSHA1();

		// This case is used for the first time importing to KB. Currently, this
		// only grabs the MD files and images from your remote master branch. I
		// have not found a way to download files from a specific branch. Worst
		// case scenario, we can import from master on the first import, and rely
		// on the 'compare' logic below to update.
		if (olderId == null || olderId.isEmpty()) {

			getAllMarkdownFiles(contentPath, repo);
			getAllImageFiles(contentPath, repo);
		}

		else if (olderId == newerId) {
			System.out.println("There are no added/modified files to import");
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
				
				// Since there can be multiple versions of the same file extracted from the commits,
				// I bypass generating the MD file if it has already been added to the 'fileSet'.
				if ((file.getFileName().endsWith(".markdown") || file.getFileName().endsWith(".md"))
						&& !fileSet.contains(file.getFileName())) {
					boolean fileExists = false;
					generateModifiedMarkdownFile(file, newerId, name, fileExists);

					if (fileExists) {
						fileSet.add(file.getFileName());
					}
					else {
						continue;
					}

				}

				// Currently, if an image is present, we're downloading all images in the 'images' dir.
				// This can be easily changed to mimic what we used in the git compare ant tasks for
				// liferay-docs, since we have the ability to download all files and images to a temp
				// local directory.
				else if (file.getFileName().endsWith(".png") || file.getFileName().endsWith(".jpg")) {
					imagePresent = true;
				}

				else if (file.getFileName().endsWith(".markdown") || file.getFileName().endsWith(".md")
						|| file.getFileName().endsWith(".png") || file.getFileName().endsWith(".jpg")) {
					System.out.println("File is a duplicate");
				}

				else {
					System.out.println("File is not in appropriate format");
				}
			}

			// Currently, I'm downloading files and creating the Zip in a local dir. This will
			// change in the KB port by using the TempFileEntryUtil class, as specified by Sergio.
			try {
				System.out.println("Creating ../../test-dist/diffs.zip file");
				(new java.io.File("../../test-dist")).mkdirs();

				FileOutputStream fileOutputStream = new FileOutputStream("../../test-dist/diffs.zip");
				ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

				for (String filePath : fileSet) {
					String markdownArticlePath = filePath.substring(contentPath.length() + 1, filePath.length());
					System.out.println(markdownArticlePath);

					addToZipFile(markdownArticlePath, zipOutputStream);

				}

				if (imagePresent) {
					getAllImageFiles(contentPath, repo);
					java.io.File imagesFolder = new java.io.File("../../test/images");

					for (java.io.File imageFile : imagesFolder.listFiles()) {
						String imagePathString = imageFile.toString();
						int x = imagePathString.indexOf("images");
						int y = imagePathString.length();
						String image = imagePathString.substring(x, y);

						addToZipFile(image, zipOutputStream);

					}		
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

	/**
	 * Adds the modified Markdown or image file to the zip output stream.
	 *
	 * @param  modFile the modified Markdown or image file
	 * @param  zipOutputStream the zip output stream
	 * @throws FileNotFoundException if the modified file could not be found
	 * @throws IOException if an IO exception occurred
	 */
	protected static void addToZipFile(String modFile, ZipOutputStream zipOutputStream)
			throws FileNotFoundException, IOException {

		//System.out.println("Adding " + modFile + " to zip file");

		// This file creation is hard-coded and will need to be edited if testing
		// on local machine.
		java.io.File file = new java.io.File("E:/test/" + modFile);
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

	/**
	 * Generates all the image files for the repository's content path.
	 *
	 * @param  contentPath the content path (e.g. develop/tutorials)
	 * @param  repo the GitHub repository
	 * @throws IOException if an IO exception occurred
	 */
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

	/**
	 * Generates all the Markdown files for the repository's content path.
	 *
	 * @param  contentPath the content path (e.g. develop/tutorials)
	 * @param  repo the GitHub repository
	 * @throws IOException if an IO exception occurred
	 */
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

	/**
	 * Generates the Markdown file that was extracted from a GitHub commit.
	 *
	 * @param  file the Markdown file to generate
	 * @param  mostRecentCommitId the most recent commit on the repository's
	 *         branch
	 * @param  name the owner/repository name of the repository
	 *         (e.g. codyhoag/liferay-docs)
	 * @param  fileExists whether the added/modified file exists in the most
	 *         recent commit
	 * @throws IOException if the URL open stream could not be established due
	 *         to a file not existing, or if an IO exception occurred
	 */
	protected static void generateModifiedMarkdownFile(GHCommit.File file, String mostRecentCommitId, String name,
			boolean fileExists) throws IOException {

		// To make sure we're generating the most recent version of the file,
		// the URL we download from is edited to point to the most recent commit.
		String urlString = new URL(file.getRawUrl(), "").toString();
		int a = urlString.indexOf(name) + name.length() + 5;
		int b = urlString.indexOf("/", a);
		urlString = urlString.replace(urlString.substring(a, b), mostRecentCommitId);

		//System.out.println("Most recent commit URL: " + urlString);

		URL url = new URL(urlString);
		urlString = urlString.substring(0, a);

		int x = file.getFileName().indexOf("articles/");
		int y = file.getFileName().length();
		String fileString = file.getFileName().substring(x, y);

		try {
			// If we're unable to set the string text, which is returned from the
			// URL open stream, an IOException is thrown, and the fileExists var
			// remains false. The non-existent case occurs when a file is renamed.
			// Both the old file and new file are added to our fileSet, but we only
			// want the existent, new file.
			String text = IOUtils.toString(url.openStream());
			fileExists = true;
			java.io.File modifiedFile = new java.io.File("../../test/" + fileString);
			modifiedFile.getParentFile().mkdirs();
			modifiedFile.createNewFile();
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
