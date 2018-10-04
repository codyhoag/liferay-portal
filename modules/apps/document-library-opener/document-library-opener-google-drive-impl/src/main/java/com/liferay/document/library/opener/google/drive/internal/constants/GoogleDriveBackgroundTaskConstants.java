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

package com.liferay.document.library.opener.google.drive.internal.constants;

/**
 * Set of constants used to initialize a background task to upload content to
 * Google Drive.
 *
 * @author Sergio González
 * @review
 */
public class GoogleDriveBackgroundTaskConstants {

	/**
	 * Value for the {@link #CMD} attribute. This tells the background task that
	 * it should perform a checkout, copying the D&M file contents to a new
	 * Google Drive file.
	 * @review
	 */
	public static final String CHECKOUT = "checkout";

	/**
	 * Attribute name used to indicate to the background task which operation is
	 * being performed by D&M. Possible values are:
	 * <ul>
	 *     <li>{@link #CHECKOUT}</li>
	 *     <li>{@link #CREATE}</li>
	 * </ul>
	 * @review
	 */
	public static final String CMD = "cmd";

	/**
	 * Attribute name used to provide the company ID of the file to upload.
	 * @review
	 */
	public static final String COMPANY_ID = "companyId";

	/**
	 * Value for the {@link #CMD} attribute. This tells the background task that
	 * it should create a new empty Google Drive file.
	 * @review
	 */
	public static final String CREATE = "create";

	/**
	 * Attribute name used to provide the file entry ID to which the Google
	 * Drive file will be linked.
	 * @review
	 */
	public static final String FILE_ENTRY_ID = "fileEntryId";

	/**
	 * Attribute name used to indicate in which phase the upload progress is.
	 * There are two possible values:
	 * <ul>
	 *     <li>
	 *         {@link #PORTAL_START}, the background task has begun the upload
	 *     </li>
	 *     <li>
	 *         {@link #PORTAL_END}, the background task has finished the upload
	 *     </li>
	 * </ul>
	 * @review
	 */
	public static final String PHASE = "phase";

	/**
	 * Value for the {@link #PHASE} attribute. This value indicates the
	 * background task has begun the upload process.
	 * @review
	 */
	public static final String PORTAL_END = "portalEnd";

	/**
	 * Value for the {@link #PHASE} attribute. This value indicates the
	 * background task has finished the upload process.
	 * @review
	 */
	public static final String PORTAL_START = "portalStart";

	public static final String PROGRESS = "progress";

	/**
	 * Attribute name used to provide the ID of the user which is requesting
	 * the operation on Google Drive.
	 * @review
	 */
	public static final String USER_ID = "userId";

}