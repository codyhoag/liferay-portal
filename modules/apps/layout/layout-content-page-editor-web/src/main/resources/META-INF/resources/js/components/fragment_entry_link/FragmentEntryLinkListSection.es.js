import Component from 'metal-component';
import Soy from 'metal-soy';
import {Config} from 'metal-state';

import '../floating_toolbar/background_color/FloatingToolbarBackgroundColorPanel.es';
import '../floating_toolbar/background_image/FloatingToolbarBackgroundImagePanel.es';
import '../floating_toolbar/spacing/FloatingToolbarSpacingPanel.es';
import './ColumnOverlayGrid.es';
import './FragmentEntryLink.es';
import {MOVE_SECTION, REMOVE_SECTION, UPDATE_SECTION_COLUMNS} from '../../actions/actions.es';
import {FLOATING_TOOLBAR_BUTTONS, FRAGMENTS_EDITOR_ITEM_TYPES} from '../../utils/constants';
import {getItemMoveDirection, getSectionIndex, getTargetBorder} from '../../utils/FragmentsEditorGetUtils.es';
import {moveItem, removeItem, setIn, updateSection} from '../../utils/FragmentsEditorUpdateUtils.es';
import {shouldUpdatePureComponent} from '../../utils/FragmentsEditorComponentUtils.es';
import FloatingToolbar from '../floating_toolbar/FloatingToolbar.es';
import getConnectedComponent from '../../store/ConnectedComponent.es';
import templates from './FragmentEntryLinkListSection.soy';

/**
 * This method defines the list of available panels.
 * @type {object[]}
 */
const SECTION_FLOATING_TOOLBAR_BUTTONS = [
	FLOATING_TOOLBAR_BUTTONS.backgroundColor,
	FLOATING_TOOLBAR_BUTTONS.backgroundImage,
	FLOATING_TOOLBAR_BUTTONS.spacing
];

/**
 * FragmentEntryLinkListSection
 */
class FragmentEntryLinkListSection extends Component {

	created() {
		this._handleBodyMouseLeave = this._handleBodyMouseLeave.bind(this);
		this._handleBodyMouseMove = this._handleBodyMouseMove.bind(this);
		this._handleBodyMouseUp = this._handleBodyMouseUp.bind(this);

		document.body.addEventListener('mouseleave', this._handleBodyMouseLeave);
		document.body.addEventListener('mouseup', this._handleBodyMouseUp);
	}

	/**
	 * @inheritdoc
	 * @review
	 */
	disposed() {
		this._disposeFloatingToolbar();

		document.body.removeEventListener('mouseleave', this._handleBodyMouseLeave);
		document.body.removeEventListener('mousemove', this._handleBodyMouseMove);
		document.body.removeEventListener('mouseup', this._handleBodyMouseUp);
	}

	/**
	 * @inheritdoc
	 * @param {object} state
	 * @return {object}
	 * @review
	 */
	prepareStateForRender(state) {
		let columnResizerVisible;
		let nextState = state;

		if (this.rowId === this.activeItemId &&
			this.activeItemType === FRAGMENTS_EDITOR_ITEM_TYPES.section) {

			columnResizerVisible = true;
		}

		nextState = setIn(nextState, ['_columnResizerVisible'], columnResizerVisible);

		if (nextState._resizing && nextState._resizeSectionColumns) {
			nextState = setIn(nextState, ['columns'], state._resizeSectionColumns);
		}

		return nextState;
	}

	/**
	 * @inheritdoc
	 * @review
	 */
	rendered() {
		if (
			(this.rowId === this.activeItemId) &&
			(this.activeItemType === FRAGMENTS_EDITOR_ITEM_TYPES.section) &&
			!this._resizing
		) {
			this._createFloatingToolbar();
		}
		else {
			this._disposeFloatingToolbar();
		}

		if (this._resizing) {
			this.element.focus();
		}
	}

	/**
	 * @inheritdoc
	 * @return {boolean}
	 * @review
	 */
	shouldUpdate(changes) {
		return shouldUpdatePureComponent(changes);
	}

	/**
	 * Clear resizing properties
	 * @private
	 * @review
	 */
	_clearResizing() {
		document.body.removeEventListener('mousemove', this._handleBodyMouseMove);

		this._resizeColumnIndex = 0;
		this._resizeHighlightedColumn = null;
		this._resizeInitialPosition = 0;
		this._resizeSectionColumns = null;
		this._resizing = false;
	}

	/**
	 * This method creates a new instance of FloatingToolbar
	 * @private
	 */
	_createFloatingToolbar() {
		const config = {
			anchorElement: this.element,
			buttons: SECTION_FLOATING_TOOLBAR_BUTTONS,
			item: this.row,
			itemId: this.rowId,
			itemType: FRAGMENTS_EDITOR_ITEM_TYPES.section,
			portalElement: document.body,
			store: this.store
		};

		if (this._floatingToolbar) {
			this._floatingToolbar.setState(config);
		}
		else {
			this._floatingToolbar = new FloatingToolbar(config);
		}
	}

	/**
	 * This method disposes of an existing instance of FloatingToolbar
	 * @private
	 */
	_disposeFloatingToolbar() {
		if (this._floatingToolbar) {
			this._floatingToolbar.dispose();

			this._floatingToolbar = null;
		}
	}

	/**
	 * This Callback is executed when a section loses the focus.
	 * @private
	 * @review
	 */
	_handleBodyMouseLeave() {
		if (this._resizing) {
			this._clearResizing();
		}
	}

	/**
	 * This Callback is executed when a section is clicked.
	 * @param {Event} event
	 * @private
	 */
	_handleBodyMouseMove(event) {
		const nextColumnRect = this.refs.resizeNextColumn.getBoundingClientRect();

		const maxPosition = nextColumnRect.x + nextColumnRect.width;
		const minPosition = this.refs.resizeColumn.getBoundingClientRect().x;
		const position = Math.max(Math.min(event.clientX, maxPosition), minPosition);

		const column = this._resizeSectionColumns[this._resizeColumnIndex];
		const nextColumn = this._resizeSectionColumns[this._resizeColumnIndex + 1];

		const maxColumns = (parseInt(column.size, 10) || 1) + (parseInt(nextColumn.size, 10) || 1) - 1;

		const columns = Math.max(
			Math.round(
				((position - minPosition) / (maxPosition - minPosition)) *
				(maxColumns)
			),
			1
		);

		this._resizeSectionColumns = setIn(
			this._resizeSectionColumns,
			[this._resizeColumnIndex, 'size'],
			columns.toString()
		);

		this._resizeSectionColumns = setIn(
			this._resizeSectionColumns,
			[this._resizeColumnIndex + 1, 'size'],
			(maxColumns - columns + 1).toString()
		);

		this._resizeHighlightedColumn = this._resizeSectionColumns
			.slice(0, this._resizeColumnIndex + 1)
			.map(column => parseInt(column.size, 10) || 1)
			.reduce((size, columnSize) => size + columnSize, 0) - 1;
	}

	/**
	 * This Callback is executed when the mouse hovers over a section.
	 * @param {Event} event
	 * @private
	 */
	_handleBodyMouseUp() {
		if (this._resizing) {
			this._updateSectionColumns(this._resizeSectionColumns);

			this._clearResizing();
		}
	}

	/**
	 * @private
	 * @review
	 */
	_handleResizerMouseDown(event) {
		this._resizeColumnIndex = this.row.columns.findIndex(
			column => column.columnId === event.delegateTarget.dataset.columnId
		);

		this._resizeInitialPosition = event.clientX;
		this._resizeSectionColumns = this.row.columns;
		this._resizing = true;

		document.body.addEventListener('mousemove', this._handleBodyMouseMove);
	}

	/**
	 * This Callback is executed when a key is pressed on the focused section.
	 * @private
	 * @param {Event} event
	 */
	_handleSectionKeyUp(event) {
		const direction = getItemMoveDirection(event.which);
		const sectionId = document.activeElement.dataset.layoutSectionId;
		const sectionIndex = getSectionIndex(
			this.layoutData.structure,
			sectionId
		);
		const targetItem = this.layoutData.structure[
			sectionIndex + direction
		];

		if (direction && targetItem) {
			const moveItemPayload = {
				sectionId,
				targetBorder: getTargetBorder(direction),
				targetItemId: targetItem.rowId
			};

			moveItem(this.store, MOVE_SECTION, moveItemPayload);
		}
	}

	/**
	 * Callback executed when the remove section button is clicked
	 * @param {Event} event
	 * @private
	 */
	_handleSectionRemoveButtonClick(event) {
		event.stopPropagation();

		removeItem(
			this.store,
			REMOVE_SECTION,
			{
				sectionId: this.hoveredItemId
			}
		);
	}

	/**
	 * Updates section columns
	 * @param {array} columns Section columns
	 * @private
	 * @review
	 */
	_updateSectionColumns(columns) {
		updateSection(
			this.store,
			UPDATE_SECTION_COLUMNS,
			{
				columns,
				sectionId: this.rowId
			}
		);
	}
}

/**
 * State definition.
 * @static
 * @type {!Object}
 */
FragmentEntryLinkListSection.STATE = {

	/**
	 * FloatingToolbar instance for internal use.
	 * @default null
	 * @instance
	 * @memberOf FragmentEntryLinkListSection
	 * @type {object|null}
	 */
	_floatingToolbar: Config.internal().value(null),

	/**
	 * Index of the column being resized
	 * @default 0
	 * @instance
	 * @memberOf FragmentEntryLinkListSection
	 * @private
	 * @review
	 * @type {number}
	 */
	_resizeColumnIndex: Config.internal().number().value(0),

	/**
	 * Index of the column that should be highlighted on resize
	 * @default null
	 * @instance
	 * @memberOf FragmentEntryLinkListSection
	 * @private
	 * @review
	 * @type {number}
	 */
	_resizeHighlightedColumn: Config.internal().number().value(null),

	/**
	 * Position of the mouse when the resize started
	 * @default 0
	 * @instance
	 * @memberOf FragmentEntryLinkListSection
	 * @private
	 * @review
	 * @type {number}
	 */
	_resizeInitialPosition: Config.internal().number().value(0),

	/**
	 * Copy of section columns for resizing
	 * @default null
	 * @instance
	 * @memberOf FragmentEntryLinkListSection
	 * @review
	 * @type {array}
	 */
	_resizeSectionColumns: Config.internal().array().value(null),

	/**
	 * True when user is resizing a column.
	 * @default false
	 * @instance
	 * @memberOf FragmentEntryLinkListSection
	 * @review
	 * @type {boolean}
	 */
	_resizing: Config.internal().bool().value(false),

	/**
	 * Defines the section row
	 * @default undefined
	 * @instance
	 * @memberof FragmentEntryLinkListSection
	 * @type {object}
	 */
	row: Config.object()
		.required(),

	/**
	 * Defines the section row ID
	 * @default undefined
	 * @instance
	 * @memberof FragmentEntryLinkListSection
	 * @type {string}
	 */
	rowId: Config.string()
		.required()
};

const ConnectedFragmentEntryLinkListSection = getConnectedComponent(
	FragmentEntryLinkListSection,
	[
		'activeItemId',
		'activeItemType',
		'dropTargetBorder',
		'dropTargetItemId',
		'dropTargetItemType',
		'hoveredItemId',
		'hoveredItemType',
		'selectedMappingTypes',
		'spritemap'
	]
);

Soy.register(ConnectedFragmentEntryLinkListSection, templates);

export {ConnectedFragmentEntryLinkListSection, FragmentEntryLinkListSection};
export default FragmentEntryLinkListSection;