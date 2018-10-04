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

package com.liferay.document.library.opener.google.drive;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;

import java.io.IOException;

/**
 * This interface is responsible of performing operations on a Google Drive
 * file that is related (linked) to a local FileEntry.
 *
 * For a file entry to be linked to a Google Drive file means that both
 * represent the same contents. The file entry will keep some state regarding
 * its sister Google Drive file. This linking -- as well as the unlinking -- is
 * performed automatically by the methods in this interface.
 *
 * Under the hood, these links are represented by the {@link
 * DLOpenerGoogleDriveFileReference} model.
 *
 * @author Adolfo Pérez
 * @review
 */
public interface DLOpenerGoogleDriveManager {

	/**
	 * Check out the contents of the fileEntry to Google Drive. To check out
	 * in this context means creating a copy of the contents of fileEntry,
	 * linking this fileEntry to the Google Drive file, and returning a
	 * reference to the Google Drive file.
	 *
	 * This method will <em>not</em> checkout the file entry in Liferay. To do
	 * that, see {@link
	 * com.liferay.document.library.kernel.service.DLAppService#checkOutFileEntry(long, ServiceContext)}
	 *
	 * @param userId the primary key of the user performing the checkout
	 * @param fileEntry the file entry to check out
	 * @return a reference to the Google Drive file
	 * @throws PortalException if an error occurs while performing the operation
	 * @review
	 */
	public DLOpenerGoogleDriveFileReference checkOut(
			long userId, FileEntry fileEntry)
		throws PortalException;

	/**
	 * Create a new (empty) Google Drive file, which will be linked to the
	 * given fileEntry, returning a reference to the new Google Drive file.
	 *
	 * This operation is similar to {@link #checkOut(long, FileEntry)}; the main
	 * difference is that this method will not copy the contents of the file
	 * entry to Google Drive. When you know the file entry is empty (e.g. you're
	 * creating a new one), this operation will be substantially cheaper than
	 * {@link #checkOut(long, FileEntry)}.
	 *
	 * @param userId the primary key of the user performing the operation
	 * @param fileEntry the file entry to link the Google Drive file to
	 * @return a reference to the new Google Drive file
	 * @review
	 */
	public DLOpenerGoogleDriveFileReference create(
			long userId, FileEntry fileEntry)
		throws PortalException;

	/**
	 * Delete the Google Drive file linked to the given fileEntry.
	 *
	 * This method will <em>not</em> delete the fileEntry, only the contents in
	 * Google Drive.
	 *
	 * @param userId the primary key of the user performing the operation.
	 * @param fileEntry the fileEntry
	 * @throws PortalException
	 * @review
	 */
	public void delete(long userId, FileEntry fileEntry) throws PortalException;

	/**
	 * Ask Google Drive for the authorization URL to use during the OAuth2 flow.
	 *
	 * @param state the current state of the user interaction
	 * @param redirectUri the url to redirect back to when the flow finishes
	 * @return the authorization URL to use in the OAuth2 flow
	 * @review
	 */
	public String getAuthorizationURL(String state, String redirectUri);

	/**
	 * Test whether the given userId has a valid credential available. The
	 * credential is stored as part of the OAuth2 flow.
	 * @param userId the primary key of the user
	 * @return <code>true</code> is the user has a valid credential; <code>false
	 * </code> otherwise
	 * @throws IOException if an error occurs while checking the existence of
	 * the credential
	 * @review
	 */
	public boolean hasValidCredential(long userId) throws IOException;

	/**
	 * Test whether the Google Drive connection is configured or not. If this
	 * method returns <code>false</code>, the behaviour of the rest of method
	 * is undefined.
	 *
	 * @return <code>true</code> if the Google Drive connection is configured;
	 * <code>false</code> otherwise
	 * @review
	 */
	public boolean isConfigured();

	/**
	 * Test whether the given fileEntry is linked to a Google Drive file.
	 *
	 * @param fileEntry the file entry to test
	 * @return <code>true</code> if the file entry is linked to a Google Drive
	 * file; <code>false</code> otherwise
	 * @review
	 */
	public boolean isGoogleDriveFile(FileEntry fileEntry);

	/**
	 * Request an authorization token; this method should be used only as part
	 * of the OAuth2 flow with Google Drive.
	 *
	 * @param userId the primary key of the user doing the OAuth2 flow
	 * @param code the code received as part of the OAuth2 flow
	 * @param redirectUri the redirect URI
	 * @throws IOException if an error occurs during the OAuth2 flow
	 * @review
	 */
	public void requestAuthorizationToken(
			long userId, String code, String redirectUri)
		throws IOException;

	/**
	 * Request Google Drive permissions to edit the give file entry. If the
	 * request is sucessfull, a reference to the Google Drive file will be
	 * returned.
	 *
	 * For this method to succeed, the user must have completed the OAuth2 flow
	 * and have a valid credential (see {@link #hasValidCredential(long)}).
	 *
	 * @param userId the primary key of the user requesting edit access
	 * @param fileEntry the file entry the user wants to edit
	 * @return a reference to the Google Drive file
	 * @throws PortalException if the user doesn't have permission to edit the
	 * file
	 * @review
	 */
	public DLOpenerGoogleDriveFileReference requestEditAccess(
			long userId, FileEntry fileEntry)
		throws PortalException;

}