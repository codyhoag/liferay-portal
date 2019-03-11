import 'frontend-js-web/liferay/compat/tooltip/Tooltip.es';
import Component from 'metal-component';
import Soy from 'metal-soy';
import {Config} from 'metal-state';

import AceEditor from './AceEditor.es';
import templates from './SourceEditor.soy';
import './SourceEditorToolbar.es';

/**
 * This component creates an instance of the Ace editor
 * to allow code editing.
 */
class SourceEditor extends Component {

	/**
	 * This Callback is executed when the internal Ace editor has been
	 * modified. It simply propagates the event.
	 * @param {!Event} event
	 */
	_handleContentChanged(event) {
		this.emit(
			'contentChanged',
			{
				content: event.content,
				valid: event.valid
			}
		);
	}
}

/**
 * State definition.
 * @static
 * @type {!Object}
 */
SourceEditor.STATE = {

	/**
	 * Provides a list of tags for custom autocomplete in the HTML editor.
	 * @default []
	 * @instance
	 * @memberOf SourceEditor
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
	 * Provides the initial content sent to the editor
	 * @default undefined
	 * @instance
	 * @memberOf SourceEditor
	 * @type {!string}
	 */
	initialContent: Config.string().required(),

	/**
	 * Sets the path to images for the content.
	 * @default undefined
	 * @instance
	 * @memberOf SourceEditor
	 * @type {!string}
	 */
	spritemap: Config.string().required(),

	/**
	 * Defines the syntax used for the editor.
	 * It will be used for Ace and rendered in the interface.
	 * @default undefined
	 * @instance
	 * @memberOf SourceEditor
	 * @see AceEditor.SYNTAX
	 * @type {!string}
	 */
	syntax: Config.oneOf(Object.values(AceEditor.SYNTAX)).required()
};

Soy.register(SourceEditor, templates);

export {SourceEditor};
export default SourceEditor;