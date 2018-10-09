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

package com.liferay.sharing.filter;

import java.util.Locale;

/**
 * Provides an interface that is used to filter the shared items by the Shared
 * With Me portlet.
 *
 * <p>
 * Implementations of this interface must be registered as OSGi components using
 * the service {@link SharedWithMeFilterItem}. The
 * <code>navigation.item.order</code> property defines the order in which the
 * filter will appear in the user interface.
 * </p>
 *
 * @review
 * @author Sergio González
 */
public interface SharedWithMeFilterItem {

	/**
	 * Returns the class name that will be used to filter the sharing entries.
	 *
	 * @return the class name that will be used to filter the sharing entries
	 */
	public String getClassName();

	/**
	 * Returns the label that will be displayed in the user interface.
	 *
	 * @return the label that will be displayed in the user interface
	 */
	public String getLabel(Locale locale);

}