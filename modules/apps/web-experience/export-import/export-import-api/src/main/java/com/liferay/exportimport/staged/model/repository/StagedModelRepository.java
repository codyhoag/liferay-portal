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

package com.liferay.exportimport.staged.model.repository;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.StagedModel;

import java.util.List;

/**
 * @author Daniel Kocsis
 */

/**
 * StagedModelRepository provides an interface for CRUD operations for a given staged model.
 * The goal is simplify the StagedModelDataHandlers by removing these CRUD operations and the code logic related to these operations.
 *
 * The get and fetch methods are typically used during an export process to query/find staged models.
 *
 * The staged model parameter can be either
 * - the old staged model that was serialized at export time and deserialized at import time, meaning that it is more like a POJO and its key attributes (primary key and foreign keys) should not be used during the CRUD operations (more on that later)
 * - or the new staged model that is from the portal that is being called right now, meaning that the staged model can be used as a regular entity.
 * @param <T> is the type of the staged model on which the StagedModelRepository operates.
 */
public interface StagedModelRepository<T extends StagedModel> {

	/**
	 * Persists a new staged model.
	 * @param portletDataContext
	 * @param stagedModel is the old staged model which is being copied.
	 *                    Primary Key attribute should not be used.
	 *                    Foreign Keys should be set before calling this method.
	 *                    Example: a BookmarksEntry is being imported. It has a groupId and a folderId as foreign keys which are required by the local service.
	 *                    These keys should be set on the stagedModel before calling the the addStagedModel method because addStagedModel will forward these values to the service method like this:
	 *                    _bookmarksEntryLocalService.addEntry(userId, bookmarksEntry.getGroupId(), bookmarksEntry.getFolderId(), bookmarksEntry.getName(), bookmarksEntry.getUrl(), bookmarksEntry.getDescription(), serviceContext);
	 * @return returns the newly persisted staged model.
	 * @throws PortalException
	 */
	public T addStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortalException;

	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException;

	public void deleteStagedModel(T stagedModel) throws PortalException;

	/**
	 * Deletes all the staged models this repository handles. Usually deletes the data from the scope group of portlet data context.
	 * @param portletDataContext
	 * @throws PortalException
	 */
	public void deleteStagedModels(PortletDataContext portletDataContext)
		throws PortalException;

	/**
	 * fetchMissingReference is used when the model (that is handled by this repository) needs to be fetched as a missing reference.
	 * If possible, use return (T)_stagedModelRepositoryHelper.fetchMissingReference(uuid, groupId, this); as the implementation.
	 * It is not just a regular fetchStagedModelByUuidAndGroupId (although the paramaters are the same), but it has a set of rules where to find a missing reference
	 * (e.g. first in the importing site, then in its parent sites, then in the global site)
	 * @param uuid
	 * @param groupId of the importing group
	 * @return returns the missing reference entity if it is found or null if it is not found
	 */
	public default T fetchMissingReference(String uuid, long groupId) {
		return null;
	}

	public T fetchStagedModelByUuidAndGroupId(String uuid, long groupId);

	public List<T> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * getExportActionableDynamicQuery is a wrapper for the same method in the service builder generated local service.
	 * If possible, use return _yourEntityLocalService.getExportActionableDynamicQuery(portletDataContext); as the implementation.
	 * @param portletDataContext
	 * @return returns an ActionableDynamicQuery which queries the entities from the database based on modified date, status, etc. depending on the parameters in portletDataContext.
	 * The performActionMethod of the ActionableDynamicQuery is an API call (StagedModelDataHandlerUtil.exportStagedModel()) to export each entity that is returned.
	 */
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	/**
	 * @param portletDataContext
	 * @param stagedModel is the old staged model. It needs to be fetched from the current portal and restored from trash.
	 * @throws PortletDataException
	 */
	public default void restoreStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {
	}

	public T saveStagedModel(T stagedModel) throws PortalException;

	/**
	 * @param portletDataContext
	 * @param stagedModel is the new staged model that is from the portal being called right now.
	 * @return returns the updated staged model
	 * @throws PortalException
	 */
	public T updateStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortalException;

}