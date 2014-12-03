package com.liferay.github.importer.internal;

import com.liferay.github.importer.internal.repository.AdminUtil;
import com.liferay.portal.kernel.util.ParamUtil;

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
import javax.portlet.PortletPreferences;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCompare;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.GHCommit.File;

import sun.misc.BASE64Decoder;

public class ImportContent {
	
	public void processZip(HttpServletRequest request, PortletPreferences portletPreferences)
			throws IOException {
		
		// These will be set by the user in the Git Configuration screen
		String userRepository = ParamUtil.getString(request, "repositoryUrl", AdminUtil.getRepositoryUrl(portletPreferences));
		String githubBranch = ParamUtil.getString(request, "branch", AdminUtil.getBranch(portletPreferences));
		String contentPath = ParamUtil.getString(request, "baseArticlePath", AdminUtil.getBaseArticlePath(portletPreferences));
		String oldCommitId = "7d902e9efa6fb89b658095cfbebbfec35eba744d";

			// You can create a ~/.github file indicating login and password if
			// you'd like to sign in while connecting. If going this route, use
			// the 'Github.connect()' method.
			GitHub github = GitHub.connect();
			GHRepository repo = github.getRepository(userRepository);
			Map<String, GHBranch> branches = repo.getBranches();
			GHBranch ghBranch = branches.get(githubBranch);

			// The olderId represents the commit that will be stored in the KB portlet.
			// By setting the newerId to the most recent commit on our specified branch,
			// the getCompare method (used later) scans our specified branch for all
			// commits that are new compared to the olderId.
			//String olderId = "7d902e9efa6fb89b658095cfbebbfec35eba744d";

			String latestCommitId = ghBranch.getSHA1();
			
			String systemTempDirPath = System.getProperty("java.io.tmpdir");
			java.io.File tempDir = new java.io.File(systemTempDirPath, "github-import-folder");

			if (!tempDir.exists()) {
				boolean tempFileCreated = tempDir.mkdirs();
				
				if (!tempFileCreated) {
					throw new IOException("The temporary directory was unable to be created");
				}
			}
			
			String customTempDirPath = tempDir.getAbsolutePath();

			// This case is used for the first time importing to KB. Currently, this
			// only grabs the MD files and images from your remote master branch. I
			// have not found a way to download files from a specific branch. Worst
			// case scenario, we can import from master on the first import, and rely
			// on the 'compare' logic below to update.
			if (oldCommitId == null || oldCommitId.isEmpty()) {

				addAllMarkdownFiles(contentPath, repo, customTempDirPath);
				addAllImageFiles(contentPath, repo, customTempDirPath);
			}

			else if (oldCommitId == latestCommitId) {
				System.out.println("There are no added/modified files to import");
			}

			else {
				System.out.println(ghBranch + " points to commit: " + ghBranch.getSHA1());

				GHCompare compare = repo.getCompare(oldCommitId, latestCommitId);
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
						
						URL url = getFileUrl(file, latestCommitId, userRepository);
						
						generateModifiedMarkdownFile(file, url, customTempDirPath);
						
						if (isExistingFile(url)) {
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

				// The files are downloaded and zipped in a temporary dir.
				try {

					System.out.println("Creating diffs.zip file in temporary dir");
					(new java.io.File(customTempDirPath)).mkdirs();

					FileOutputStream fileOutputStream = new FileOutputStream(customTempDirPath + "/diffs.zip");
					ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
					
					System.out.println("fileSet: " + fileSet);

					for (String filePath : fileSet) {
						String markdownArticlePath = filePath.substring(contentPath.length() + 1, filePath.length());
						System.out.println(markdownArticlePath);

						addToZipFile(markdownArticlePath, zipOutputStream, customTempDirPath);

					}

					if (imagePresent) {
						addAllImageFiles(contentPath, repo, customTempDirPath);
						java.io.File imagesFolder = new java.io.File(customTempDirPath + "/diffs/images");

						for (java.io.File imageFile : imagesFolder.listFiles()) {
							String imagePathString = imageFile.toString();
							int x = imagePathString.indexOf("images");
							int y = imagePathString.length();
							String image = imagePathString.substring(x, y);

							addToZipFile(image, zipOutputStream, customTempDirPath);

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
			
			//FileUtils.deleteDirectory(tempDir);
		}
		
		/**
		 * Generates all the image files for the repository's content path.
		 *
		 * @param  contentPath the content path (e.g. develop/tutorials)
		 * @param  repo the GitHub repository
		 * @throws IOException if an IO exception occurred
		 */
		protected static void addAllImageFiles(String contentPath, GHRepository repo, String customTempDirPath)
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
					java.io.File file = new java.io.File(customTempDirPath + "/diffs/" + imageFilePath);
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
		protected static void addAllMarkdownFiles(String contentPath, GHRepository repo, String customTempDirPath)
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
						java.io.File file = new java.io.File(customTempDirPath + "/diffs/" + filePath);

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
		 * Adds the modified Markdown or image file to the zip output stream.
		 *
		 * @param  modFile the modified Markdown or image file
		 * @param  zipOutputStream the zip output stream
		 * @throws FileNotFoundException if the modified file could not be found
		 * @throws IOException if an IO exception occurred
		 */
		protected static void addToZipFile(String modFile, ZipOutputStream zipOutputStream, String customTempDirPath)
				throws FileNotFoundException, IOException {

			//System.out.println("Adding " + modFile + " to zip file");

			// This file creation is hard-coded and will need to be edited if testing
			// on local machine.
			java.io.File file = new java.io.File(customTempDirPath + "/diffs/" + modFile);
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
		protected static void generateModifiedMarkdownFile(GHCommit.File file, URL url, String customTempDirPath) throws IOException {

			int x = file.getFileName().indexOf("articles/");
			int y = file.getFileName().length();
			String fileString = file.getFileName().substring(x, y);

			try {

				String text = IOUtils.toString(url.openStream());
				java.io.File modifiedFile = new java.io.File(customTempDirPath + "/diffs/" + fileString);
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
		
		protected static URL getFileUrl(GHCommit.File file, String mostRecentCommitId,
				String name) throws IOException {

			// To make sure we're generating the most recent version of the file,
			// the URL we download from is edited to point to the most recent commit.
			String urlString = new URL(file.getRawUrl(), "").toString();
			int a = urlString.indexOf(name) + name.length() + 5;
			int b = urlString.indexOf("/", a);
			urlString = urlString.replace(urlString.substring(a, b), mostRecentCommitId);

			URL url = new URL(urlString);
			
			return url;
		}
		
		protected static boolean isExistingFile(URL url) {

			boolean fileExists = true;

			try {
				IOUtils.toString(url.openStream());
			}
			catch (Exception e)
			{
				fileExists = false;
			}

			return fileExists;		
		}
	


}
