import State, {Config} from 'metal-state';
import dom from 'metal-dom';
import {Store} from '../../store/store.es';

import FragmentEditableFieldTooltip from './FragmentEditableFieldTooltip.es';
import FragmentStyleEditors from '../fragment_processors/FragmentStyleEditors.es';
import {OPEN_MAPPING_FIELDS_DIALOG} from '../../actions/actions.es';

/**
 * FragmentStyleEditor
 */
class FragmentStyleEditor extends State {

	/**
	 * @inheritDoc
	 */
	constructor(...args) {
		super(...args);

		this.disposeStyleTooltip = this.disposeStyleTooltip.bind(this);
		this._handleButtonClick = this._handleButtonClick.bind(this);
		this._handleChangeStyle = this._handleChangeStyle.bind(this);
		this._handleNodeClick = this._handleNodeClick.bind(this);

		this.syncType();

		this._onNodeClickHandler = dom.on(this.node, 'click', this._handleNodeClick);
	}

	/**
	 * This Callback updates the styleEditor being used any time the type property is changed.
	 */
	syncType() {
		this._styleEditor = FragmentStyleEditors[this.type];
	}

	/**
	 * This method handles click events for styled nodes.
	 * @param {Event} event
	 * @private
	 */
	_handleNodeClick(event) {
		if (event.target === this.node) {
			event.preventDefault();
			event.stopPropagation();

			if (this._tooltip) {
				this.disposeStyleTooltip();
			}
			else if (this._styleEditor) {
				this.emit('openTooltip');

				this._tooltip = new FragmentEditableFieldTooltip(
					{
						alignElement: this.node,
						buttons: this._styleEditor.getButtons(this.showMapping)
					}
				);

				this._tooltip.on('buttonClick', this._handleButtonClick);
			}
		}
	}

	/**
	 * This method handles click events for tooltip buttons.
	 * @param {MouseEvent} event
	 */
	_handleButtonClick(event) {
		if (this._styleEditor) {
			this._styleEditor.init(
				event.buttonId,
				this.node,
				this.portletNamespace,
				this.editorsOptions,
				this._handleChangeStyle,
				this.disposeStyleTooltip
			);
		}
	}

	/**
	 * This method handles a change style event.
	 * @param {Object} event
	 * @param {string} event.eventType
	 */
	_handleChangeStyle(event) {
		if (event.eventType === 'map') {
			this.store
				.dispatchAction(
					OPEN_MAPPING_FIELDS_DIALOG,
					{
						editableId: `${this.selectorText} ${event.name}`,
						editableType: event.type,
						fragmentEntryLinkId: this.fragmentEntryLinkId,
						mappedFieldId: ''
					}
				);

			this.disposeStyleTooltip();
		}
		else {
			this.emit(
				'styleChanged',
				{
					name: `${this.selectorText} ${event.name}`,
					value: event.value
				}
			);
		}
	}

	/**
	 * This method disposes of the style tooltip if it exists.
	 */
	disposeStyleTooltip() {
		if (this._tooltip) {
			this._tooltip.dispose();

			this._tooltip = null;
		}
	}

}

/**
 * This defines the state for the FragmentStyleEditor.
 * @static
 * @type {!Object}
 */
FragmentStyleEditor.STATE = {

	/**
	 * Sets the CSS rules text
	 * @default undefined
	 * @instance
	 * @memberOf FragmentStyleEditor
	 * @type {!string}
	 */
	cssText: Config.string().required(),

	/**
	 * Defines the options that are sent to the editors.
	 * @default undefined
	 * @instance
	 * @memberOf FragmentStyleEditor
	 * @type {!object}
	 */
	editorsOptions: Config.object().required(),

	/**
	 * Sets the FragmentEntryLink ID
	 * @default undefined
	 * @instance
	 * @memberOf FragmentStyleEditor
	 * @type {!string}
	 */
	fragmentEntryLinkId: Config.string().required(),

	/**
	 * Sets the node to instantiate style editor on
	 * @default undefined
	 * @instance
	 * @memberOf FragmentStyleEditor
	 * @type {HTMLElement}
	 */
	node: Config.internal().object(),

	/**
	 * Sets the portlet namespace
	 * @default undefined
	 * @instance
	 * @memberOf FragmentStyleEditor
	 * @type {!string}
	 */
	portletNamespace: Config.string().required(),

	/**
	 * Sets the CSS selector text
	 * @default undefined
	 * @instance
	 * @memberOf FragmentStyleEditor
	 * @type {!string}
	 */
	selectorText: Config.string().required(),

	/**
	 * If set to True then mapping is activated
	 * @default undefined
	 * @instance
	 * @memberOf FragmentStyleEditor
	 * @type {!boolean}
	 */
	showMapping: Config.bool().required(),

	/**
	 * Sets the store instance
	 * @default undefined
	 * @instance
	 * @memberOf FragmentStyleEditor
	 * @type {Store}
	 */
	store: Config.instanceOf(Store),

	/**
	 * Sets the type for the style editor
	 * @default undefined
	 * @instance
	 * @memberOf FragmentStyleEditor
	 * @type {!string}
	 */
	type: Config.string().required(),

	/**
	 * Sets the instance of the style editor being used.
	 * @default null
	 * @instance
	 * @memberOf FragmentStyleEditor
	 * @type {FragmentStyleEditors}
	 */
	_styleEditor: Config
		.internal()
		.object()
		.value(null)

};

export {FragmentStyleEditor};
export default FragmentStyleEditor;