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

import java.io.File;

import java.util.function.Supplier;

/**
 * Represents a link between a Documents and Media FileEntry and a Google Drive
 * file. You should never create instances of this class directly; they'll be
 * returned to you by the methods in the {@link DLOpenerGoogleDriveManager}
 * interface.
 *
 * @author Adolfo Pérez
 * @review
 */
public class DLOpenerGoogleDriveFileReference {

	/**
	 * Create a new instance of {@link DLOpenerGoogleDriveFileReference}.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @param titleSupplier a supplier that will provide the title of the
	 *                      document when invoked.
	 * @param fileSupplier a supplier that will provide the contents of the
	 *                     document when invoked.
	 * @param backgroundTaskId the primary key of the background process
	 *                         responsible of uploading the original file
	 *                         contents to Google Drive. If zero, no upload
	 *                         task is in progress.
	 */
	public DLOpenerGoogleDriveFileReference(
		long fileEntryId, Supplier<String> titleSupplier,
		Supplier<File> fileSupplier, long backgroundTaskId) {

		_fileEntryId = fileEntryId;
		_titleSupplier = titleSupplier;
		_fileSupplier = fileSupplier;
		_backgroundTaskId = backgroundTaskId;
	}

	/**
	 * Return the primary key of the background task responsible of uploading
	 * the original file contents to Google Drive. If zero, no upload task is in
	 * progress.
	 *
	 * @return the primary key of the background task, or 0 if there is none
	 * @review
	 */
	public long getBackgroundTaskId() {
		return _backgroundTaskId;
	}

	/**
	 * Return a file with the contents of this Google Drive file reference.
	 *
	 * @return a reference to a file with the contents of this reference
	 * @review
	 */
	public File getContentFile() {
		return _fileSupplier.get();
	}

	/**
	 * Return the primary key of the file entry linked with this reference.
	 *
	 * @return the primary key of the file entry
	 * @review
	 */
	public long getFileEntryId() {
		return _fileEntryId;
	}

	/**
	 * Return the title of this Google Drive file reference.
	 *
	 * @return the title of this Google Drive file reference
	 * @review
	 */
	public String getTitle() {
		return _titleSupplier.get();
	}

	private final long _backgroundTaskId;
	private final long _fileEntryId;
	private final Supplier<File> _fileSupplier;
	private final Supplier<String> _titleSupplier;

}