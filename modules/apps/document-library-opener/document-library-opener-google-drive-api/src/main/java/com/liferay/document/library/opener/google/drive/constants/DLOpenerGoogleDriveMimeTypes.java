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

package com.liferay.document.library.opener.google.drive.constants;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Map;

/**
 * This util class offers a set of constants and util methods to work with
 * the mime types supported both by Liferay and Google Docs/Slides/Spreadsheets.
 *
 * @author Adolfo Pérez
 * @review
 */
public class DLOpenerGoogleDriveMimeTypes {

	/**
	 * The mime type for MS Word (docx) documents.
	 * @review
	 */
	public static final String APPLICATION_VND_DOCX =
		"application/vnd.openxmlformats-officedocument.wordprocessingml." +
			"document";

	/**
	 * The mime type for Google Documents.
	 * @review
	 */
	public static final String APPLICATION_VND_GOOGLE_APPS_DOCUMENT =
		"application/vnd.google-apps.document";

	/**
	 * The mime type for Google Slides
	 * @review
	 */
	public static final String APPLICATION_VND_GOOGLE_APPS_PRESENTATION =
		"application/vnd.google-apps.presentation";

	/**
	 * The mime type for Google Spreadsheets.
	 * @review
	 */
	public static final String APPLICATION_VND_GOOGLE_APPS_SPREADSHEET =
		"application/vnd.google-apps.spreadsheet";

	/**
	 * The mime type for MS PowerPoint (pptx) presentations.
	 * @review
	 */
	public static final String APPLICATION_VND_PPTX =
		"application/vnd.openxmlformats-officedocument.presentationml." +
			"presentation";

	/**
	 * The mime type for MS Excel (xslx) spreadsheets.
	 * @review
	 */
	public static final String APPLICATION_VND_XSLX =
		"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	/**
	 * Return the Google Docs equivalent mime type to the one received. This
	 * method will map MS Word documents and plain text files to Google
	 * Documents, MS PowerPoint presentations to Google Slides, and so on.
	 *
	 * Only mime types for which the {@link #isMimeTypeSupported(String)}
	 * returns <code>true</code> return a valid Google document type. For the
	 * rest, an exception will be thrown.
	 *
	 * @param mimeType the mime type to translate
	 * @return the equivalent Google mime type
	 * @review
	 */
	public static String getGoogleDocsMimeType(String mimeType) {
		if (!isMimeTypeSupported(mimeType)) {
			throw new UnsupportedOperationException(
				StringBundler.concat(
					"Google Docs does not support edition of documents of ",
					"type ", mimeType));
		}

		return _googleDocsMimeTypes.get(mimeType);
	}

	/**
	 * Return the canonical file extension associated with the mimeType. The
	 * canonical file extension is the most common one used for documents of
	 * this type; e.g. if the mime type indicates this is a MS Docx document,
	 * the return value will be ".docx".
	 *
	 * The returned value will always include a dot ('.') as the first
	 * character of the extension.
	 *
	 * Only mime types for which the {@link #isMimeTypeSupported(String)}
	 * returns <code>true</code> return an extension. For the rest, the empty
	 * string will be returned.
	 *
	 * @param mimeType the mime type
	 * @return the canonical extension or the empty string if none could be
	 * determined
	 * @review
	 */
	public static String getMimeTypeExtension(String mimeType) {
		return _extensions.getOrDefault(mimeType, StringPool.BLANK);
	}

	/**
	 * Test whether the given mimeType is supported or not.
	 *
	 * @param mimeType the mime type to test
	 * @return <code>true</code> if mimeType is supported; <code>false</code>
	 * otherwise
	 * @review
	 */
	public static boolean isMimeTypeSupported(String mimeType) {
		return _googleDocsMimeTypes.containsKey(mimeType);
	}

	private static final Map<String, String> _extensions = MapUtil.fromArray(
		APPLICATION_VND_DOCX, ".docx", APPLICATION_VND_PPTX, ".pptx",
		APPLICATION_VND_XSLX, ".xslx", ContentTypes.APPLICATION_TEXT, ".txt",
		ContentTypes.TEXT, ".txt", ContentTypes.TEXT_PLAIN, ".txt");
	private static final Map<String, String> _googleDocsMimeTypes =
		MapUtil.fromArray(
			APPLICATION_VND_DOCX, APPLICATION_VND_GOOGLE_APPS_DOCUMENT,
			APPLICATION_VND_PPTX, APPLICATION_VND_GOOGLE_APPS_PRESENTATION,
			APPLICATION_VND_XSLX, APPLICATION_VND_GOOGLE_APPS_SPREADSHEET,
			ContentTypes.APPLICATION_TEXT, APPLICATION_VND_GOOGLE_APPS_DOCUMENT,
			ContentTypes.TEXT, APPLICATION_VND_GOOGLE_APPS_DOCUMENT,
			ContentTypes.TEXT_PLAIN, APPLICATION_VND_GOOGLE_APPS_DOCUMENT);

}