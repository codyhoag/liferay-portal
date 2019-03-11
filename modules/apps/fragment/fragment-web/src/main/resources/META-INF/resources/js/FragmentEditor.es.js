import Soy from 'metal-soy';
import {Config} from 'metal-state';
import {openToast} from 'frontend-js-web/liferay/toast/commands/OpenToast.es';
import PortletBase from 'frontend-js-web/liferay/PortletBase.es';

import templates from './FragmentEditor.soy';
import './FragmentPreview.es';
import './SourceEditor.es';

/**
 * Creates a Fragment Editor component that lets you create a new fragment or
 * edit an existing fragment. This component integrates three
 * <code>&lt;SourceEditor /></code> components for each part of the fragment and
 * a <code>&lt;FragmentPreview /></code> component for the preview.
 */
class FragmentEditor extends PortletBase {

	/**
	 * @inheritDoc
	 */
	shouldUpdate(changes) {
		return changes._html ||
			changes._js ||
			changes._css ||
			changes._saving;
	}

	/**
	 * This method returns the content for the Fragment.
	 * @public
	 * @return {{
	 *   css: string,
	 *   html: string,
	 *   js: string
	 * }}
	 */
	getContent() {
		return ({
			css: this._css,
			html: this._html,
			js: this._js
		});
	}

	/**
	 * This method returns true when HTML content is valid, false otherwise.
	 * @public
	 * @return {boolean} True if HTML is valid
	 */
	isHtmlValid() {
		return this._htmlValid;
	}

	/**
	 * The Event handler is executed when any content is changed.
	 * @private
	 */
	_handleContentChanged() {
		this.emit(
			'contentChanged',
			this.getContent()
		);
	}

	/**
	 * This Callback is executed when the CSS editor changes.
	 * @param {!Event} event
	 * @private
	 */
	_handleCSSChanged(event) {
		this._css = event.content;
		this._handleContentChanged();
	}

	/**
	 * This Callback is executed when the HTML editor changes.
	 * @param {!Event} event
	 * @private
	 */
	_handleHTMLChanged(event) {
		this._html = event.content;
		this._htmlValid = event.valid;

		this._handleContentChanged();
	}

	/**
	 * This Callback is executed when the JS editor changes.
	 * @param {!Event} event
	 * @private
	 */
	_handleJSChanged(event) {
		this._js = event.content;
		this._handleContentChanged();
	}

	/**
	 * This method saves the fragment content with the Save button is clicked.
	 * @param {!Event} event
	 * @private
	 */
	_handleSaveButtonClick(event) {
		const content = this.getContent();
		const status = event.delegateTarget.value;

		if (this.isHtmlValid()) {
			this._saving = true;

			this.fetch(
				this.urls.edit,
				{
					cssContent: content.css,
					fragmentCollectionId: this.fragmentCollectionId,
					fragmentEntryId: this.fragmentEntryId,
					htmlContent: content.html,
					jsContent: content.js,
					name: this.name,
					status
				}
			)
				.then(
					response => response.json()
				)
				.then(
					response => {
						if (response.error) {
							throw response.error;
						}

						return response;
					}
				)
				.then(
					response => {
						const redirectURL = (
							response.redirect ||
							this.urls.redirect
						);

						Liferay.Util.navigate(redirectURL);
					}
				)
				.catch(
					error => {
						this._saving = false;

						const message = typeof error === 'string' ?
							error :
							Liferay.Language.get('error');

						openToast(
							{
								message,
								title: Liferay.Language.get('error'),
								type: 'danger'
							}
						);
					}
				);
		}
		else {
			alert(Liferay.Language.get('fragment-html-is-invalid'));
		}
	}

}

/**
 * State definition.
 * @static
 * @type {!Object}
 */
FragmentEditor.STATE = {

	/**
	 * Provides a list of tags for custom autocomplete in the HTML editor.
	 * @default []
	 * @instance
	 * @memberOf FragmentEditor
	 * @review
	 * @type Array
	 */
	autocompleteTags: Config.arrayOf(
		Config.shapeOf(
			{
				content: Config.string(),
				name: Config.string()
			}
		)
	),

	/**
	 * The ID for the Fragment collection.
	 * @default undefined
	 * @instance
	 * @memberOf FragmentEditor
	 * @type {!string}
	 */
	fragmentCollectionId: Config.string().required(),

	/**
	 * The ID for the Fragment entry.
	 * @default undefined
	 * @instance
	 * @memberOf FragmentEditor
	 * @type {!string}
	 */
	fragmentEntryId: Config.string().required(),

	/**
	 * The name for the Fragment entry.
	 * @default undefined
	 * @instance
	 * @memberOf FragmentEditor
	 * @type {!string}
	 */

	name: Config.string().required(),

	/**
	 * URLs used for communicating with backend.
	 * @instance
	 * @memberOf FragmentEditor
	 * @type {{
	 *  edit: !string,
	 *	redirect: !string
	 * }}
	 */

	urls: Config.shapeOf(
		{
			edit: Config.string().required(),
			redirect: Config.string().required()
		}
	).required(),

	/**
	 * This contains the updated CSS content of
	 * the editor that will be propagated to the preview pane.
	 * @default ''
	 * @instance
	 * @memberOf FragmentEditor
	 * @private
	 * @type {string}
	 */
	_css: Config.string()
		.internal()
		.value(''),

	/**
	 * This contains the updated HTML content of
	 * the editor that will be propagated to the preview pane.
	 * @default ''
	 * @instance
	 * @memberOf FragmentEditor
	 * @private
	 * @type {string}
	 */

	_html: Config.string()
		.internal()
		.value(''),

	/**
	 * This property contains a flag if updated HTML content of
	 * the editor is not valid.
	 * @default true
	 * @instance
	 * @memberOf FragmentEditor
	 * @private
	 * @type {boolean}
	 */

	_htmlValid: Config.bool()
		.internal()
		.value(true),

	/**
	 * This contains the updated JS content of
	 * the editor that  will be propagated to the preview pane.
	 * @default ''
	 * @instance
	 * @memberOf FragmentEditor
	 * @private
	 * @type {string}
	 */

	_js: Config.string()
		.internal()
		.value(''),

	/**
	 * This is set to true while the fragment is saved.
	 * @default false
	 * @instance
	 * @memberOf FragmentEditor
	 * @private
	 * @type {bool}
	 */

	_saving: Config.bool()
		.internal()
		.value(false)
};

Soy.register(FragmentEditor, templates);

export {FragmentEditor};
export default FragmentEditor;