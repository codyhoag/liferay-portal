import 'frontend-js-web/liferay/compat/tooltip/Tooltip.es';
import Component from 'metal-component';
import Soy from 'metal-soy';
import {Config} from 'metal-state';

import AceEditor from './AceEditor.es';
import templates from './SourceEditor.soy';
import './SourceEditorToolbar.es';

/**
 * Creates a Source Editor Component
 *
 * Lets you use an instance of Source editor to edit code
 */
class SourceEditor extends Component {

	/**
	 * Callback that propagates the `contentChanged` event when 
	 * the internal Ace editor is modified
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
	 * List of tags to support custom autocomplete in the HTML editor
	 * @default []
	 * @instance
	 * @memberOf SourceEditor
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
	 * Initial content sent to the editor
	 * @default undefined
	 * @instance
	 * @memberOf SourceEditor
	 * @type {!string}
	 */
	initialContent: Config.string().required(),

	/**
	 * Path of the available icons
	 * @default undefined
	 * @instance
	 * @memberOf SourceEditor
	 * @type {!string}
	 */
	spritemap: Config.string().required(),

	/**
	 * Syntax used for the Ace editor that is rendered on the interface
	 * @default undefined
	 * @instance
	 * @memberOf SourceEditor
	 * @see {@link AceEditor.SYNTAX|SYNTAX}
	 * @type {!string}
	 */
	syntax: Config.oneOf(Object.values(AceEditor.SYNTAX)).required()
};

Soy.register(SourceEditor, templates);

export {SourceEditor};
export default SourceEditor;