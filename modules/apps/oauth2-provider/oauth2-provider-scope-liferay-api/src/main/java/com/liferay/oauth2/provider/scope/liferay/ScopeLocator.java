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

package com.liferay.oauth2.provider.scope.liferay;

import java.util.Collection;

/**
 * Lists scope aliases and matching {@link LiferayOAuth2Scope}s based on a
 * portal instance configuration of the OAuth2 Framework. Scope aliases can
 * match multiple {@link LiferayOAuth2Scope}s
 * from OAuth2 frameworks in different portal instances.
 *
 * @author Carlos Sierra Andrés
 */
public interface ScopeLocator {

	/**
	 * Returns a collection of application exported scopes matching a {@code
	 * scopesAlias} in the given portal instance.
	 *
	 * @param  companyId the ID of the portal instance containing the scopes
	 * @param  scopesAlias the alias mapped to scopes
	 * @return a collection of one or more matching scopes
	 */
	public Collection<LiferayOAuth2Scope> getLiferayOAuth2Scopes(
		long companyId, String scopesAlias);

	/**
	 * Returns a collection of application exported scopes matching a {@code
	 * scopesAlias} in the given portal instance, filtered by {@code
	 * applicationName}.
	 *
	 * @param  companyId the ID of the portal instance containing the scopes
	 * @param  scopesAlias the alias mapped to scopes
	 * @param  applicationName the application containing the scopes

	 * @return a collection of one or more matching scopes, filtered by {@code
	 * applicationName}
	 */
	public Collection<LiferayOAuth2Scope> getLiferayOAuth2Scopes(
		long companyId, String scopesAlias, String applicationName);

	/**
	 * Returns a list of scope aliases available for the given portal instance.
	 * <br />
	 *
	 * @param  companyId the ID of the portal instance containing the scope aliases

	 * @return a non-<code>null</code> collection of scope aliases
	 */
	public Collection<String> getScopeAliases(long companyId);

	/**
	 * Returns a list of scope aliases available for the given portal instance,
	 * filtered by application name.
	 *
	 * @param  companyId the ID of the portal instance containing the scope aliases
	 * @param  applicationName the application exporting the scopes
	 * @return a non-<code>null</code> collection of scope aliases
	 * filtered by {@code applicationName}
	 */
	public Collection<String> getScopeAliases(
		long companyId, String applicationName);

}
