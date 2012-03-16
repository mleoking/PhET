/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import flash.events.MouseEvent;

import org.aswing.dnd.*;
import org.aswing.event.*;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.basic.BasicListUI;
import org.aswing.util.*;


/**
 * Dispatched when the list selection changed.
 * @eventType org.aswing.event.SelectionEvent.LIST_SELECTION_CHANGED
 */
[Event(name="listSelectionChanged", type="org.aswing.event.SelectionEvent")]

/**
 * Dispatched when the list item be click.
 * @eventType org.aswing.event.ListItemEvent.ITEM_CLICK
 */
[Event(name="itemClick", type="org.aswing.event.ListItemEvent")]

/**
 * Dispatched when the list item be double click.
 * @eventType org.aswing.event.ListItemEvent.ITEM_DOUBLE_CLICK
 */
[Event(name="itemDoubleClick", type="org.aswing.event.ListItemEvent")]

/**
 * Dispatched when the list item be mouse down.
 * @eventType org.aswing.event.ListItemEvent.ITEM_MOUSE_DOWN
 */
[Event(name="itemMouseDown", type="org.aswing.event.ListItemEvent")]

/**
 * Dispatched when the list item be roll over.
 * @eventType org.aswing.event.ListItemEvent.ITEM_ROLL_OVER
 */
[Event(name="itemRollOver", type="org.aswing.event.ListItemEvent")]

/**
 * Dispatched when the list item be roll out.
 * @eventType org.aswing.event.ListItemEvent.ITEM_ROLL_OUT
 */
[Event(name="itemRollOut", type="org.aswing.event.ListItemEvent")]

/**
 * Dispatched when the list item be released out side.
 * @eventType org.aswing.event.ListItemEvent.ITEM_RELEASE_OUT_SIDE
 */
[Event(name="itemReleaseOutSide", type="org.aswing.event.ListItemEvent")]

/**
 * Dispatched when the viewport's state changed. the state is all about:
 * <ul>
 * <li>view position</li>
 * <li>verticalUnitIncrement</li>
 * <li>verticalBlockIncrement</li>
 * <li>horizontalUnitIncrement</li>
 * <li>horizontalBlockIncrement</li>
 * </ul>
 * </p>
 * 
 * @eventType org.aswing.event.InteractiveEvent.STATE_CHANGED
 */
[Event(name="stateChanged", type="org.aswing.event.InteractiveEvent")]

/** 
 * A component that allows the user to select one or more objects from a
 * list.  A separate model, <code>ListModel</code>, represents the contents
 * of the list.  It's easy to display an array objects, using
 * a <code>JList</code> constructor that builds a <code>ListModel</code> 
 * instance for you:
 * <pre>
 * // Create a JList that displays the strings in data[]
 *
 * var data:Array = ["one", "two", "three", "four"];
 * var dataList:JList = new JList(data);
 * 
 * // The value of the JList model property is an object that provides
 * // a read-only view of the data.  It was constructed automatically.
 *
 * for(int i = 0; i < dataList.getModel().getSize(); i++) {
 *     System.out.println(dataList.getModel().getElementAt(i));
 * }
 *
 * // Create a JList that displays the values in a IVector--<code>VectorListModel</code>.
 *
 * var vec:VectorListModel = new VectorListModel(["one", "two", "three", "four"]);
 * var vecList:JList = new JList(vec);
 * 
 * //When you add elements to the vector, the JList will be automatically updated.
 * vec.append("five");
 * </pre>
 * <p>
 * <code>JList</code> doesn't support scrolling directly. 
 * To create a scrolling
 * list you make the <code>JList</code> the viewport of a
 * <code>JScrollPane</code>.  For example:
 * <pre>
 * JScrollPane scrollPane = new JScrollPane(dataList);
 * // Or in two steps:
 * JScrollPane scrollPane = new JScrollPane();
 * scrollPane.setView(dataList);
 * </pre>
 * <p>
 * By default the <code>JList</code> selection model is 
 * <code>SINGLE_SELECTION</code>.
 * <pre>
 * String[] data = {"one", "two", "three", "four"};
 * JList dataList = new JList(data);
 *
 * dataList.setSelectedIndex(1);  // select "two"
 * dataList.getSelectedValue();   // returns "two"
 * </pre>
 * <p>
 * The contents of a <code>JList</code> can be dynamic,
 * in other words, the list elements can
 * change value and the size of the list can change after the
 * <code>JList</code> has
 * been created.  The <code>JList</code> observes changes in its model with a
 * <code>ListDataListener</code> implementation.  A correct 
 * implementation of <code>ListModel</code> notifies
 * it's listeners each time a change occurs.  The changes are
 * characterized by a <code>ListDataEvent</code>, which identifies
 * the range of list indices that have been modified, added, or removed.
 * Simple dynamic-content <code>JList</code> applications can use the
 * <code>VectorListModel</code> class to store list elements.  This class
 * implements the <code>ListModel</code> and <code>IVector</code> interfaces
 * and provides the Vector API.  Applications that need to 
 * provide custom <code>ListModel</code> implementations can subclass 
 * <code>AbstractListModel</code>, which provides basic 
 * <code>ListDataListener</code> support.
 * <p>
 * <code>JList</code> uses a <code>Component</code> provision, provided by 
 * a delegate called the
 * <code>ListCell</code>, to paint the visible cells in the list.
 * <p>
 * <code>ListCell</code> created by a <code>ListCellFactory</code>, to custom 
 * the item representation of the list, you need a custom <code>ListCellFactory</code>.
 * For example a IconListCellFactory create IconListCells.
 * <p>
 * <code>ListCellFactory</code> is related to the List's performace too, see the doc 
 * comments of <code>ListCellFactory</code> for the details.
 * And if you want a horizontal scrollvar visible when item width is bigger than the visible 
 * width, you need a not <code>shareCells</code> Factory(and of course the List should located 
 * in a JScrollPane first). <code>shareCells</code> Factory 
 * can not count the maximum width of list items.
 * @author iiley
 * @see ListCellFactory
 * @see ListCell
 * @see ListModel
 * @see VectorListModel
 */
public class JList extends Container implements LayoutManager, Viewportable, ListDataListener{
	
 	/**
 	 * The default unit/block increment, it means auto count a value.
 	 */
 	public static const AUTO_INCREMENT:int = int.MIN_VALUE;
 	
	/**
	 * Only can select one most item at a time.
	 */
	public static var SINGLE_SELECTION:int = DefaultListSelectionModel.SINGLE_SELECTION;
	/**
	 * Can select any item at a time.
	 */
	public static var MULTIPLE_SELECTION:int = DefaultListSelectionModel.MULTIPLE_SELECTION;
	
	/**
	 * Drag and drop disabled.
	 */
	public static var DND_NONE:int = DragManager.TYPE_NONE;
	
	/**
	 * Drag and drop enabled, and the action of items is move.
	 */
	public static var DND_MOVE:int = DragManager.TYPE_MOVE;
	
	/**
	 * Drag and drop enabled, and the action of items is copy.
	 */
	public static var DND_COPY:int = DragManager.TYPE_COPY;
	
	//---------------------caches------------------
	private var viewHeight:int;
	private var viewWidth:int;
	private var maxWidthCell:ListCell;
	private var cellPrefferSizes:HashMap; //use for catche sizes when not all cells same height
	private var comToCellMap:HashMap; 
	private var visibleRowCount:int;
	private var visibleCellWidth:int;
	//--
	
	private var preferredWidthWhenNoCount:int;
	
	private var tracksWidth:Boolean;
	private var verticalUnitIncrement:int;
	private var verticalBlockIncrement:int;
	private var horizontalUnitIncrement:int;
	private var horizontalBlockIncrement:int;
	
	private var viewPosition:IntPoint;
	private var selectionForeground:ASColor;
	private var selectionBackground:ASColor;
	
	protected var cellPane:CellPane;
	private var cellFactory:ListCellFactory;
	private var model:ListModel;
	private var selectionModel:ListSelectionModel;
	private var cells:ArrayList;
	
	private var firstVisibleIndex:int;
	private var lastVisibleIndex:int;
	private	var firstVisibleIndexOffset:int = 0;
	private	var lastVisibleIndexOffset:int = 0;
	
	private var autoDragAndDropType:int;
	
	/**
	 * Create a list.
	 * @param listData (optional)a ListModel or a Array.
	 * @param cellFactory (optional)the cellFactory for this List.
	 */
	public function JList(listData:*=null, cellFactory:ListCellFactory=null) {
		super();
		
		setName("JList");
		layout = this;
		cellPane = new CellPane();
		append(cellPane);
		viewPosition = new IntPoint(0, 0);
		setSelectionModel(new DefaultListSelectionModel());
		firstVisibleIndex = 0;
		lastVisibleIndex = -1;
		firstVisibleIndexOffset = 0;
		lastVisibleIndexOffset = 0;
		visibleRowCount = -1;
		visibleCellWidth = -1;
		preferredWidthWhenNoCount = 20; //Default 20
		
		verticalUnitIncrement = AUTO_INCREMENT;
		verticalBlockIncrement = AUTO_INCREMENT;
		horizontalUnitIncrement = AUTO_INCREMENT;
		horizontalBlockIncrement = AUTO_INCREMENT;
		
		tracksWidth = false;
		viewWidth = 0;
		viewHeight = 0;
		maxWidthCell = null;
		cellPrefferSizes = new HashMap();
		comToCellMap = new HashMap();
		cells = new ArrayList();
		model = null;
		autoDragAndDropType = DND_NONE;
		
		if(cellFactory == null){
			cellFactory = new DefaultListCellFactory(true);
		}
		this.cellFactory = cellFactory;
		
		if(listData == null){
			setModel(new VectorListModel());
		}else if(listData is ListModel){
			setModel(listData as ListModel);
		}else{
			setListData(listData as Array);
		}
		
		updateUI();
	}
	
    override public function updateUI():void{
    	//update cells ui
    	for(var i:int=cells.size()-1; i>=0; i--){
    		var cell:ListCell = ListCell(cells.get(i));
    		cell.getCellComponent().updateUI();
    	}
    	
    	setUI(UIManager.getUI(this));
    }
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicListUI;
    }
    	
	override public function getUIClassID():String{
		return "ListUI";
	}
	
	
	/**
	 * Can not set layout to JList, its layout is itself.
	 * @throws ArgumentError when set any layout.
	 */
	override public function setLayout(layout:LayoutManager):void{
		throw new ArgumentError("Can not set layout to JList, its layout is itself!");
	}	
	
	/**
	 * Set a array to be the list data, a new model will be created and the values is copied to the model.
	 * This is not a good way, its slow. So suggest you to create a ListMode for example VectorListMode to JList,
	 * When you modify ListMode, it will automatic update JList if necessary.
	 * @see #setModel()
	 * @see org.aswing.ListModel
	 */
	public function setListData(ld:Array):void{
		var m:ListModel = new VectorListModel(ld);
		setModel(m);
	}
	
	/**
	 * Set the list mode to provide the data to JList.
	 * @see org.aswing.ListModel
	 */
	public function setModel(m:ListModel):void{
		if(m != model){
			if(model != null){
				model.removeListDataListener(this);
			}
			model = m;
			model.addListDataListener(this);
			updateListView();
		}
	}
	
	/**
	 * @return the model of this List
	 */
	public function getModel():ListModel{
		return model;
	}

    /**
     * Sets the <code>selectionModel</code> for the list to a
     * non-<code>null</code> <code>ListSelectionModel</code>
     * implementation. The selection model handles the task of making single
     * selections, multiple selections.
     * <p>
     * @param selectionModel  the <code>ListSelectionModel</code> that
     *				implements the selections, if it is null, nothing will be done.
     * @see #getSelectionModel()
     */
	public function setSelectionModel(m:ListSelectionModel):void{
		if(m != selectionModel){
			if(selectionModel != null){
				selectionModel.removeListSelectionListener(__selectionListener);
			}
			selectionModel = m;
			selectionModel.addListSelectionListener(__selectionListener);
		}
	}
	
    /**
     * Returns the value of the current selection model. The selection
     * model handles the task of making single selections, multiple selections.
     *
     * @return the <code>ListSelectionModel</code> that implements
     *					list selections
     * @see #setSelectionModel()
     * @see ListSelectionModel
     */
	public function getSelectionModel():ListSelectionModel{
		return selectionModel;
	}
		
	/**
	 * @return the cellFactory of this List
	 */
	public function getCellFactory():ListCellFactory{
		return cellFactory;
	}
	
	/**
	 * This will cause all cells recreating by new factory.
	 * @param newFactory the new cell factory for this List
	 */
	public function setCellFactory(newFactory:ListCellFactory):void{
		cellFactory = newFactory;
		removeAllCells();
		updateListView();
	}
	
	/**
	 * Adds a listener to list selection changed.
	 * @param listener the listener to be add.
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
	 * @see org.aswing.event.SelectionEvent
	 */	
	public function addSelectionListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
		addEventListener(SelectionEvent.LIST_SELECTION_CHANGED, listener, false, priority, useWeakReference);
	}
	
	/**
	 * Removes a listener from list selection changed listeners.
	 * @param listener the listener to be removed.
	 * @see org.aswing.event.SelectionEvent
	 */	
	public function removeSelectionListener(listener:Function):void{
		removeEventListener(SelectionEvent.LIST_SELECTION_CHANGED, listener);
	}

	/**
	 * @see #setPreferredWidthWhenNoCount()
	 * @return the default preferred with of the List when <code>shareCelles</code>.
	 */
	public function getPreferredCellWidthWhenNoCount():int {
		return preferredWidthWhenNoCount;
	}

	/**
	 * The preferred with of the List, it is only used when List have no counting for its prefferredWidth.
	 * <p>
	 * When <code>ListCellFactory</code> is <code>shareCelles</code>, List will not count prefferred width.
	 * @param preferredWidthWhenNoCount the preferred with of the List.
	 */
	public function setPreferredCellWidthWhenNoCount(preferredWidthWhenNoCount:int):void {
		this.preferredWidthWhenNoCount = preferredWidthWhenNoCount;
	}	
	
	/**
	 * When your list data changed, and you want to update list view by hand.
	 * call this method.
	 * <p>This method is called automatically when setModel called with a different model to set. 
	 */
	public function updateListView() : void {
		createCells();
		validateCells();
	}
	
	/**
	 * Clears the selection - after calling this method isSelectionEmpty will return true. 
	 * This is a convenience method that just delegates to the selectionModel.
     * @param programmatic indicate if this is a programmatic change.
	 */
	public function clearSelection(programmatic:Boolean=true):void{
		getSelectionModel().clearSelection(programmatic);
	}
	
	/**
	 * Determines whether single-item or multiple-item selections are allowed.
	 * If selection mode changed, will cause clear selection;
	 * @see #SINGLE_SELECTION
	 * @see #MULTIPLE_SELECTION
	 */
	public function setSelectionMode(sm:int):void{
		getSelectionModel().setSelectionMode(sm);
	}
	
	/**
	 * Return whether single-item or multiple-item selections are allowed.
	 * @see #SINGLE_SELECTION
	 * @see #MULTIPLE_SELECTION
	 */	
	public function getSelectionMode():int{
		return getSelectionModel().getSelectionMode();
	}
	
	
    /**
     * Returns the foreground color for selected cells.
     *
     * @return the <code>Color</code> object for the foreground property
     * @see #setSelectionForeground()
     * @see #setSelectionBackground()
     */
	public function getSelectionForeground():ASColor{
		return selectionForeground;
	}
	
    /**
     * Sets the foreground color for selected cells.  Cell renderers
     * can use this color to render text and graphics for selected
     * cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * 
     * @param selectionForeground  the <code>Color</code> to use in the foreground
     *                             for selected list items
     * @see #getSelectionForeground()
     * @see #setSelectionBackground()
     * @see #setForeground()
     * @see #setBackground()
     * @see #setFont()
     */	
	public function setSelectionForeground(selectionForeground:ASColor):void{
		var old:ASColor = this.selectionForeground;
		this.selectionForeground = selectionForeground;
		if (!selectionForeground.equals(old)){
			repaint();
			validateCells();
		}
	}
	
    /**
     * Returns the background color for selected cells.
     *
     * @return the <code>Color</code> used for the background of selected list items
     * @see #setSelectionBackground()
     * @see #setSelectionForeground()
     */	
	public function getSelectionBackground():ASColor{
		return selectionBackground;
	}
	
    /**
     * Sets the background color for selected cells.  Cell renderers
     * can use this color to the fill selected cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * @param selectionBackground  the <code>Color</code> to use for the background
     *                             of selected cells
     * @see #getSelectionBackground()
     * @see #setSelectionForeground()
     * @see #setForeground()
     * @see #setBackground()
     * @see #setFont()
     */	
	public function setSelectionBackground(selectionBackground:ASColor):void{
		var old:ASColor = this.selectionBackground;
		this.selectionBackground = selectionBackground;
		if (!selectionBackground.equals(old)){
			repaint();
			validateCells();
		}
	}	
	
    /**
     * Returns the first index argument from the most recent 
     * <code>addSelectionModel</code> or <code>setSelectionInterval</code> call.
     * This is a convenience method that just delegates to the
     * <code>selectionModel</code>.
     *
     * @return the index that most recently anchored an interval selection
     * @see ListSelectionModel#getAnchorSelectionIndex
     * @see #addSelectionInterval()
     * @see #setSelectionInterval()
     * @see #addSelectionListener()
     */
    public function getAnchorSelectionIndex():int {
        return getSelectionModel().getAnchorSelectionIndex();
    }

    /**
     * Returns the second index argument from the most recent
     * <code>addSelectionInterval</code> or <code>setSelectionInterval</code>
     * call.
     * This is a convenience method that just  delegates to the 
     * <code>selectionModel</code>.
     *
     * @return the index that most recently ended a interval selection
     * @see ListSelectionModel#getLeadSelectionIndex
     * @see #addSelectionInterval()
     * @see #setSelectionInterval()
     * @see #addSelectionListener()
     */
    public function getLeadSelectionIndex():int {
        return getSelectionModel().getLeadSelectionIndex();
    }	
	
    /** 
     * @param index0 index0.
     * @param index1 index1.
     * @param programmatic indicate if this is a programmatic change.
     * @see ListSelectionModel#setSelectionInterval
     * @see #removeSelectionInterval()
     */	
	public function setSelectionInterval(index0:int, index1:int, programmatic:Boolean=true):void{
		getSelectionModel().setSelectionInterval(index0, index1, programmatic);
	}
	
    /** 
     * @param index0 index0.
     * @param index1 index1.
     * @param programmatic indicate if this is a programmatic change.
     * @see ListSelectionModel#addSelectionInterval()
     * @see #removeSelectionInterval()
     */	
	public function addSelectionInterval(index0:int, index1:int, programmatic:Boolean=true):void{
		getSelectionModel().addSelectionInterval(index0, index1, programmatic);
	}

    /** 
     * @param index0 index0.
     * @param index1 index1.
     * @param programmatic indicate if this is a programmatic change.
     * @see ListSelectionModel#removeSelectionInterval()
     */	
	public function removeSelectionInterval(index0:int, index1:int, programmatic:Boolean=true):void{
		getSelectionModel().removeSelectionInterval(index0, index1, programmatic);
	}
	
	/**
	 * Selects all elements in the list.
	 * 
     * @param programmatic indicate if this is a programmatic change.
	 * @see #setSelectionInterval
	 */
	public function selectAll(programmatic:Boolean=true):void {
		setSelectionInterval(0, getModel().getSize()-1, programmatic);
	}
	
	/**
	 * Return the selected index, if selection multiple, return the first.
	 * if not selected any, return -1.
	 * @return the selected index
	 */
	public function getSelectedIndex():int{
		return getSelectionModel().getMinSelectionIndex();	
	}
	
	/**
	 * Returns true if nothing is selected.
	 * @return true if nothing is selected, false otherwise.
	 */
	public function isSelectionEmpty():Boolean{
		return getSelectionModel().isSelectionEmpty();
	}
	
	/**
	 * Returns an array of all of the selected indices in increasing order.
	 * @return a array contains all selected indices
	 */
	public function getSelectedIndices():Array{
		var indices:Array = new Array();
		var sm:ListSelectionModel = getSelectionModel();
		var min:int = sm.getMinSelectionIndex();
		var max:int = sm.getMaxSelectionIndex();
		if(min < 0 || max < 0 || isSelectionEmpty()){
			return indices;
		}
		for(var i:int=min; i<=max; i++){
			if(sm.isSelectedIndex(i)){
				indices.push(i);
			}
		}
		return indices;
	}
	
	/**
	 * @return true if the index is selected, otherwise false.
	 */
	public function isSelectedIndex(index:int):Boolean{
		return getSelectionModel().isSelectedIndex(index);
	}
	
	/**
	 * Returns the first selected value, or null if the selection is empty.
	 * @return the first selected value
	 */
	public function getSelectedValue():*{
		var i:int = getSelectedIndex();
		if(i < 0){
			return null;
		}else{
			return model.getElementAt(i);
		}
	}
	
	/**
	 * Returns an array of the values for the selected cells.
     * The returned values are sorted in increasing index order.
     * @return the selected values or an empty list if nothing is selected
	 */
	public function getSelectedValues():Array{
		var values:Array = new Array();
		var sm:ListSelectionModel = getSelectionModel();
		var min:int = sm.getMinSelectionIndex();
		var max:int = sm.getMaxSelectionIndex();
		if(min < 0 || max < 0 || isSelectionEmpty()){
			return values;
		}
		var vm:ListModel = getModel();
		for(var i:int=min; i<=max; i++){
			if(sm.isSelectedIndex(i)){
				values.push(vm.getElementAt(i));
			}
		}
		return values;
	}
	
	/**
     * Selects a single cell.
     * @param index the index to be seleted.
     * @param programmatic indicate if this is a programmatic change.
     * @see ListSelectionModel#setSelectionInterval
     * @see #isSelectedIndex()
     * @see #addSelectionListener()
	 * @see #ensureIndexIsVisible()
	 */
	public function setSelectedIndex(index:int, programmatic:Boolean=true):void{
		if(index >= getModel().getSize()){
			return;
		}
		getSelectionModel().setSelectionInterval(index, index, programmatic);
	}
	
	/**
	 * Selects a set of cells. 
	 * <p> This will not cause a scroll, if you want to 
	 * scroll to visible the selected value, call ensureIndexIsVisible().
	 * @param indices an array of the indices of the cells to select.
     * @param programmatic indicate if this is a programmatic change.
     * @see #isSelectedIndex()
     * @see #addSelectionListener()
	 * @see #ensureIndexIsVisible()
	 */	
	public function setSelectedIndices(indices:Array, programmatic:Boolean=true):void{
        var sm:ListSelectionModel = getSelectionModel();
        sm.clearSelection();
		var size:int = getModel().getSize();
        for(var i:int = 0; i < indices.length; i++) {
	    	if (indices[i] < size) {
				sm.addSelectionInterval(indices[i], indices[i], programmatic);
	    	}
        }
	}
	
	/**
	 * Selects the specified object from the list. This will not cause a scroll, if you want to 
	 * scroll to visible the selected value, call ensureIndexIsVisible().
	 * @param value the value to be selected.
     * @param programmatic indicate if this is a programmatic change.
	 * @see #setSelectedIndex()
	 * @see #ensureIndexIsVisible()
	 */
	public function setSelectedValue(value:*, programmatic:Boolean=true):void{
		var n:int = model.getSize();
		for(var i:int=0; i<n; i++){
			if(model.getElementAt(i) == value){
				setSelectedIndex(i, programmatic);
				return;
			}
		}
		setSelectedIndex(-1, programmatic); //there is not this value
	}
	
	/**
	 * Selects a set of cells. 
	 * <p> This will not cause a scroll, if you want to 
	 * scroll to visible the selected value, call ensureIndexIsVisible().
	 * @param values an array of the values to select.
     * @param programmatic indicate if this is a programmatic change.
     * @see #isSelectedIndex()
     * @see #addSelectionListener()
	 * @see #ensureIndexIsVisible()
	 */	
	public function setSelectedValues(values:Array, programmatic:Boolean=true):void{
        var sm:ListSelectionModel = getSelectionModel();
        sm.clearSelection();
		var size:int = getModel().getSize();
        for(var i:int=0; i<values.length; i++) {
        	for(var j:int=0; j<size; j++){
        		if(values[i] == getModel().getElementAt(j)){
					sm.addSelectionInterval(j, j, programmatic);
					break;
        		}
        	}
        }
	}	
		
	/**
	 * Scrolls the JList to make the specified cell completely visible.
	 * @see #setFirstVisibleIndex()
	 */
	public function ensureIndexIsVisible(index:int):void{
		if(index<=getFirstVisibleIndex()){
			setFirstVisibleIndex(index);
		}else if(index>=getLastVisibleIndex()){
			setLastVisibleIndex(index);
		}
	}
	
	public function getFirstVisibleIndex():int{
		return firstVisibleIndex;
	}
	
	/**
	 * scroll the list to view the specified index as first visible.
	 * If the list data elements is too short can not move the specified
	 * index to be first, just scroll as top as can.
	 * @see #ensureIndexIsVisible()
	 * @see #setLastVisibleIndex()
	 */
	public function setFirstVisibleIndex(index:int):void{
    	var factory:ListCellFactory = getCellFactory();
		var p:IntPoint = getViewPosition();
		if(factory.isAllCellHasSameHeight() || factory.isShareCells()){
			p.y = index * factory.getCellHeight();
		}else{
			var num:int = Math.min(cells.getSize()-1, index);
			var y:int = 0;
			for(var i:int=0; i<num; i++){
				var cell:ListCell = ListCell(cells.get(i));
				var s:IntDimension = getCachedCellPreferSize(cell);
				if(s == null){
					s = cell.getCellComponent().getPreferredSize();
					trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
				}
				y += s.height;
			}
			p.y = y;
		}
		p.y = Math.max(0, Math.min(getViewMaxPos().y, p.y));
		setViewPosition(p);
	}
	
	public function getLastVisibleIndex():int{
		return lastVisibleIndex;
	}
	
	/**
	 * scroll the list to view the specified index as last visible
	 * If the list data elements is too short can not move the specified
	 * index to be last, just scroll as bottom as can.
	 * @see ensureIndexIsVisible()
	 * @see setFirstVisibleIndex()
	 */
	public function setLastVisibleIndex(index:int):void{
    	var factory:ListCellFactory = getCellFactory();	
		var p:IntPoint = getViewPosition();
		if(factory.isAllCellHasSameHeight() || factory.isShareCells()){
			p.y = (index + 1) * factory.getCellHeight() - getExtentSize().height;
		}else{
			var num:int = Math.min(cells.getSize(), index+2);
			var y:int = 0;
			for(var i:int=0; i<num; i++){
				var cell:ListCell = ListCell(cells.get(i));
				var s:IntDimension = getCachedCellPreferSize(cell);
				if(s == null){
					s = cell.getCellComponent().getPreferredSize();
					trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
				}
				y += s.height;
			}
			p.y = y - getExtentSize().height;
		}
		p.y = Math.max(0, Math.min(getViewMaxPos().y, p.y));
		setViewPosition(p);
	}
	
    /**
     * Returns the prefferred number of visible rows.
     *
     * @return an integer indicating the preferred number of rows to display
     *         without using a scroll bar, -1 means perffered number is <code>model.getSize()</code>
     * @see #setVisibleRowCount()
     */
	public function getVisibleRowCount():int{
		return visibleRowCount;
	}
	
    /**
     * Sets the preferred number of rows in the list that can be displayed.
     * -1 means prefer to display all rows.
     * <p>
     * The default value of this property is -1.
     * The rowHeight will be counted as 20 if the cell factory produces not same height cells.
     * <p>
     *
     * @param visibleRowCount  an integer specifying the preferred number of
     *                         visible rows
     * @see #setVisibleCellWidth()
     * @see #getVisibleRowCount()
     */	
	public function setVisibleRowCount(c:int):void{
		if(c != visibleRowCount){
			visibleRowCount = c;
			revalidate();
		}
	}
	
    /**
     * Returns the preferred width of visible list pane. -1 means return the view width.
     *
     * @return an integer indicating the preferred width to display.
     * @see #setVisibleCellWidth()
     */
	public function getVisibleCellWidth():int{
		return visibleCellWidth;
	}
	
    /**
     * Sets the preferred width the list that can be displayed.
     * <p>
     * The default value of this property is -1.
     * -1 means the width that can display all content.
     * <p>
     *
     * @param visibleRowCount  an integer specifying the preferred width.
     * @see #setVisibleRowCount()
     * @see #getVisibleCellWidth()
     * @see #setPreferredCellWidthWhenNoCount()
     */	
	public function setVisibleCellWidth(w:int):void{
		if(w != visibleCellWidth){
			visibleCellWidth = w;
			revalidate();
		}
	}
	
	/**
	 * Sets true to make the cell always have same width with the List container, 
	 * and the herizontal scrollbar will not shown if the list is in a <code>JScrollPane</code>; 
	 * false to make it as same as its preffered width.
	 * @param b tracks width, default value is false
	 */
	public function setTracksWidth(b:Boolean):void{
		if(b != tracksWidth){
			tracksWidth = b;
		}
	}
	
	/**
	 * Returns tracks width value.
	 * @return tracks width
	 * @see #setTracksWidth()
	 */
	public function isTracksWidth():Boolean{
		return tracksWidth;
	}
	
	/**
	 * Scrolls to view bottom left content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */
	public function scrollToBottomLeft():void{
		setViewPosition(new IntPoint(0, int.MAX_VALUE));
	}
	/**
	 * Scrolls to view bottom right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToBottomRight():void{
		setViewPosition(new IntPoint(int.MAX_VALUE, int.MAX_VALUE));
	}
	/**
	 * Scrolls to view top left content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopLeft():void{
		setViewPosition(new IntPoint(0, 0));
	}
	/**
	 * Scrolls to view to right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopRight():void{
		setViewPosition(new IntPoint(int.MAX_VALUE, 0));
	}	
	
	/**
     * Enables the list so that items can be selected.
     */
	override public function setEnabled(b:Boolean):void{
		super.setEnabled(b);
		mouseChildren = b;
	}
		
	/**
	 * Sets auto drag and drop type.
	 * @see #DND_NONE
	 * @see #DND_MOVE
	 * @see #DND_COPY
	 */
	/*public function setAutoDragAndDropType(type:int):void{
		autoDragAndDropType = type;
		if(dndListener == null){
			dndListener = new Object();
			dndListener[ON_DRAG_RECOGNIZED] = Delegate.create(this, ____onDragRecognized);
			dndListener[ON_DRAG_ENTER] = Delegate.create(this, ____onDragEnter);
			dndListener[ON_DRAG_OVERRING] = Delegate.create(this, ____onDragOverring);
			dndListener[ON_DRAG_EXIT] = Delegate.create(this, ____onDragExit);
			dndListener[ON_DRAG_DROP] = Delegate.create(this, ____onDragDrop);
		}
		removeEventListener(dndListener);
		if(isAutoDragAndDropAllown()){
			setDropTrigger(true);
			setDragEnabled(true);
			addEventListener(dndListener);
		}else{
			setDropTrigger(false);
			setDragEnabled(false);
		}
	}*/
	
	/**
	 * Returns the auto drag and drop type.
	 * @see #DND_NONE
	 * @see #DND_MOVE
	 * @see #DND_COPY
	 */
	public function getAutoDragAndDropType():int{
		return autoDragAndDropType;
	}
	
	private function isAutoDragAndDropAllown():Boolean{
		return autoDragAndDropType == DND_MOVE || autoDragAndDropType == DND_COPY;
	}
	
	/**
	 * Returns is this list allown to automatically be as an drag and drop initiator.
	 * @see #org.aswing.MutableListModel
	 * @see #DND_NONE
	 * @see #DND_MOVE
	 * @see #DND_COPY
	 */
	public function isAutoDnDInitiatorAllown():Boolean{
		if(!isAutoDragAndDropAllown()){
			return false;
		}
		if(!isMutableModel()){
			return autoDragAndDropType == DND_COPY;
		}else{
			return true;
		}
	}
	
	/**
	 * Returns is this list allown to automatically be as an drag and drop target.
	 * @see #org.aswing.MutableListModel
	 * @see #DND_NONE
	 * @see #DND_MOVE
	 * @see #DND_COPY
	 */
	public function isAutoDnDDropTargetAllown():Boolean{
		return isAutoDragAndDropAllown() && isMutableModel();
	}
	
	//----------------------privates-------------------------
	
	protected function addCellToContainer(cell:ListCell):void{
		cell.getCellComponent().setFocusable(false);
		cellPane.append(cell.getCellComponent());
		comToCellMap.put(cell.getCellComponent(), cell);
		addHandlersToCell(cell.getCellComponent());
	}
	
	protected function removeCellFromeContainer(cell:ListCell):void{
		cell.getCellComponent().removeFromContainer();
		comToCellMap.remove(cell.getCellComponent());
		removeHandlersFromCell(cell.getCellComponent());
	}
	
	private function checkCreateCellsWhenShareCells():void{
		createCellsWhenShareCells();
	}
	
	private function createCellsWhenShareCells():void{
		var ih:int = getCellFactory().getCellHeight();
		var needNum:int = Math.floor(getExtentSize().height/ih) + 2;
		
		viewWidth = getPreferredCellWidthWhenNoCount();
		viewHeight = getModel().getSize()*ih;
		
		if(cells.getSize() == needNum/* || !displayable*/){
			return;
		}
		
		var i:int;
		var cell:ListCell;
		//create needed
		if(cells.getSize() < needNum){
			var addNum:int = needNum - cells.getSize();
			for(i=0; i<addNum; i++){
				cell = createNewCell();
				addCellToContainer(cell);
				cells.append(cell);
			}
		}else if(cells.getSize() > needNum){ //remove mored
			var removeIndex:int = needNum;
			var removed:Array = cells.removeRange(removeIndex, cells.getSize()-1);
			for(i=0; i<removed.length; i++){
				cell = ListCell(removed[i]);
				removeCellFromeContainer(cell);
			}
		}
	}
	
	private function createCellsWhenNotShareCells():void{
		var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		
		var w:int = 0;
		var h:int = 0;
		var sameHeight:Boolean = factory.isAllCellHasSameHeight();
		
		var mSize:int = m.getSize();
		var cSize:int = cells.getSize();
		
		cellPrefferSizes.clear();
		
		var n:int = Math.min(mSize, cSize);
		var i:int;
		var cell:ListCell;
		var s:IntDimension;
		//reuse created cells
		for(i=0; i<n; i++){
			cell = ListCell(cells.get(i));
			cell.setCellValue(m.getElementAt(i));
			s = cell.getCellComponent().getPreferredSize();
			cellPrefferSizes.put(cell.getCellComponent(), s);
			if(s.width > w){
				w = s.width;
				maxWidthCell = cell;
			}
			if(!sameHeight){
				h += s.height;
			}
		}
		
		//create lest needed cells
		if(mSize > cSize){
			for(i = cSize; i<mSize; i++){
				cell = createNewCell();
				cells.append(cell);
				cell.setCellValue(m.getElementAt(i));
				addCellToContainer(cell);
				s = cell.getCellComponent().getPreferredSize();
				cellPrefferSizes.put(cell.getCellComponent(), s);
				if(s.width > w){
					w = s.width;
					maxWidthCell = cell;
				}
				if(!sameHeight){
					h += s.height;
				}
			}
		}else if(mSize < cSize){ //remove unwanted cells
			var removed:Array = cells.removeRange(mSize, cSize-1);
			for(i=0; i<removed.length; i++){
				cell = ListCell(removed[i]);
				removeCellFromeContainer(cell);
				cellPrefferSizes.remove(cell.getCellComponent());
			}
		}
		
		if(sameHeight){
			h = m.getSize()*factory.getCellHeight();
		}
		
		viewWidth = w;
		viewHeight = h;
	}
	
	private function createNewCell():ListCell{
		return getCellFactory().createNewCell();
	}
	
	private function createCells():void{
		if(getCellFactory().isShareCells()){
			createCellsWhenShareCells();
		}else{
			createCellsWhenNotShareCells();
		}
	}
	
	private function removeAllCells() : void {
		for(var i:int=0; i<cells.getSize(); i++){
			var cell:ListCell = cells.get(i);
			cell.getCellComponent().removeFromContainer();
		}
		cells.clear();
	}
	
	private function validateCells():void{
		revalidate();
	}
	
	//--------------------------------------------------------
	
	protected function fireStateChanged(programmatic:Boolean=true):void{
		dispatchEvent(new InteractiveEvent(InteractiveEvent.STATE_CHANGED, programmatic));
	}
	
	public function getVerticalUnitIncrement() : int {
		if(verticalUnitIncrement != AUTO_INCREMENT){
			return verticalUnitIncrement;
		}else if(getCellFactory().isAllCellHasSameHeight()){
			return getCellFactory().getCellHeight();
		}else{
			return 18;
		}
	}

	public function getVerticalBlockIncrement() : int {
		if(verticalBlockIncrement != AUTO_INCREMENT){
			return verticalBlockIncrement;
		}else if(getCellFactory().isAllCellHasSameHeight()){
			return getExtentSize().height - getCellFactory().getCellHeight();
		}else{
			return getExtentSize().height - 10;
		}
	}

	public function getHorizontalUnitIncrement() : int {
		if(horizontalUnitIncrement == AUTO_INCREMENT){
			return 1;
		}else{
			return horizontalUnitIncrement;
		}
	}

	public function getHorizontalBlockIncrement() : int {
		if(horizontalBlockIncrement == AUTO_INCREMENT){
			return getExtentSize().width - 1;
		}else{
			return horizontalBlockIncrement;
		}
	}
	
    public function setVerticalUnitIncrement(increment:int):void{
    	if(verticalUnitIncrement != increment){
    		verticalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    public function setVerticalBlockIncrement(increment:int):void{
    	if(verticalBlockIncrement != increment){
    		verticalBlockIncrement = increment;
			fireStateChanged();
    	}
    }
    
    public function setHorizontalUnitIncrement(increment:int):void{
    	if(horizontalUnitIncrement != increment){
    		horizontalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    public function setHorizontalBlockIncrement(increment:int):void{
    	if(horizontalBlockIncrement != increment){
    		horizontalBlockIncrement = increment;
			fireStateChanged();
    	}
    }
	
    public function setViewportTestSize(s:IntDimension):void{
    	setSize(s);
    }	
		
	public function getExtentSize() : IntDimension {	
    	return getInsets().getInsideSize(getSize());
	}

	public function getViewSize() : IntDimension {
		var w:int = isTracksWidth() ? getExtentSize().width : viewWidth;
		return new IntDimension(w, viewHeight);
	}

	public function getViewPosition() : IntPoint {
		return new IntPoint(viewPosition.x, viewPosition.y);
	}

	public function setViewPosition(p : IntPoint, programmatic:Boolean=true) : void {
		restrictionViewPos(p);
		if(!viewPosition.equals(p)){
			viewPosition.setLocation(p);
			fireStateChanged(programmatic);
			//revalidate();
			valid = false;
			RepaintManager.getInstance().addInvalidRootComponent(this);
		}
	}

	public function scrollRectToVisible(contentRect : IntRectangle, programmatic:Boolean=true) : void {
		setViewPosition(new IntPoint(contentRect.x, contentRect.y), programmatic);
	}
	
	private function restrictionViewPos(p:IntPoint):IntPoint{
		var maxPos:IntPoint = getViewMaxPos();
		p.x = Math.max(0, Math.min(maxPos.x, p.x));
		p.y = Math.max(0, Math.min(maxPos.y, p.y));
		return p;
	}
	
	private function getViewMaxPos():IntPoint{
		var showSize:IntDimension = getExtentSize();
		var viewSize:IntDimension = getViewSize();
		var p:IntPoint = new IntPoint(viewSize.width-showSize.width, viewSize.height-showSize.height);
		if(p.x < 0) p.x = 0;
		if(p.y < 0) p.y = 0;
		return p;
	}

	/**
	 * Add a listener to listen the viewpoat state change event.
	 * <p>
	 * When the viewpoat's state changed, the state is all about:
	 * <ul>
	 * <li>viewPosition</li>
	 * <li>verticalUnitIncrement</li>
	 * <li>verticalBlockIncrement</li>
	 * <li>horizontalUnitIncrement</li>
	 * <li>horizontalBlockIncrement</li>
	 * </ul>
	 * @param listener the listener
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
	 * @see org.aswing.event.InteractiveEvent#STATE_CHANGED
	 */
	public function addStateListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
		addEventListener(InteractiveEvent.STATE_CHANGED, listener, false, priority);
	}	
	
	/**
	 * Removes a state listener.
	 * @param listener the listener to be removed.
	 * @see org.aswing.event.AWEvent#STATE_CHANGED
	 */	
	public function removeStateListener(listener:Function):void{
		removeEventListener(InteractiveEvent.STATE_CHANGED, listener);
	}

	public function getViewportPane() : Component {
		return this;
	}
	//------------------------Layout implementation---------------------
	

    /**
     * do nothing
     */
    public function addLayoutComponent(comp:Component, constraints:Object):void{
    }

    /**
     * do nothing
     */
    public function removeLayoutComponent(comp:Component):void{
    }
	
    public function preferredLayoutSize(target:Container):IntDimension{
    	var viewSize:IntDimension = getViewSize();
    	var rowCount:int = getVisibleRowCount();
    	if(rowCount > 0){
	    	var rowHeight:int = 20;
	    	if(getCellFactory().isAllCellHasSameHeight()){
	    		rowHeight = getCellFactory().getCellHeight();
	    	}
    		viewSize.height = rowCount * rowHeight;
    	}
    	var cellWidth:int = getVisibleCellWidth();
    	if(cellWidth > 0){
    		viewSize.width = cellWidth;
    	}
    	return getInsets().getOutsideSize(viewSize);
    }

    public function minimumLayoutSize(target:Container):IntDimension{
    	return getInsets().getOutsideSize();
    }
	
    public function maximumLayoutSize(target:Container):IntDimension{
    	return IntDimension.createBigDimension();
    }
    
    /**
     * position and fill cells here
     */
    public function layoutContainer(target:Container):void{
    	var factory:ListCellFactory = getCellFactory();
    	
		var ir:IntRectangle = getInsets().getInsideBounds(getSize().getBounds());
    	cellPane.setComBounds(ir);
    	
    	if(factory.isShareCells()){
    		layoutWhenShareCells();
    	}else{
    		if(factory.isAllCellHasSameHeight()){
    			layoutWhenNotShareCellsAndSameHeight();
    		}else{
    			layoutWhenNotShareCellsAndNotSameHeight();
    		}
    	}
    }
    
    private function layoutWhenShareCells():void{
    	checkCreateCellsWhenShareCells();
    	
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		var ir:IntRectangle = getInsets().getInsideBounds(getSize().getBounds());
    	var cellWidth:int = ir.width;
    	ir.x = ir.y = 0; //this is because the cells is in cellPane, not in JList
    	
    	restrictionViewPos(viewPosition);
		var x:int = viewPosition.x;
		var y:int = viewPosition.y;
		var ih:int = factory.getCellHeight();
		var startIndex:int = Math.floor(y/ih);
		var startY:int = startIndex*ih - y;
		var listSize:int = m.getSize();
		var cx:int = ir.x - x;
		var cy:int = ir.y + startY;
		var maxY:int = ir.y + ir.height;
		var cellsSize:int = cells.getSize();
		if(listSize < 0){
			lastVisibleIndex = -1;
		}
		for(var i:int = 0; i<cellsSize; i++){
			var cell:ListCell = cells.get(i);
			var ldIndex:int = startIndex + i;
			var cellCom:Component = cell.getCellComponent();
			if(ldIndex < listSize){
				cell.setCellValue(m.getElementAt(ldIndex));
				cellCom.setVisible(true);
				cellCom.setComBoundsXYWH(cx, cy, cellWidth, ih);
				if(cy < maxY){
					lastVisibleIndex = ldIndex;
				}
				cy += ih;
				cell.setListCellStatus(this, isSelectedIndex(ldIndex), ldIndex);
			}else{
				cellCom.setVisible(false);
			}
			cellCom.validate();
		}
		firstVisibleIndex = startIndex;
    }
    
    private function layoutWhenNotShareCellsAndSameHeight():void{
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		var ir:IntRectangle = getInsets().getInsideBounds(getSize().getBounds());
    	var cellWidth:int = Math.max(ir.width, viewWidth);
    	ir.x = ir.y = 0; //this is because the cells is in cellPane, not in JList
    	
    	restrictionViewPos(viewPosition);
		var x:int = viewPosition.x;
		var y:int = viewPosition.y;
		var ih:int = factory.getCellHeight();
		var startIndex:int = Math.floor(y/ih);
		var listSize:int = m.getSize();
		var startY:int = startIndex*ih - y;
		
		var endIndex:int = startIndex + Math.ceil((ir.height-(ih+startY))/ih);
		if(endIndex >= listSize){
			endIndex = listSize - 1;
		}
		
		var cx:int = ir.x - x;
		var cy:int = ir.y + startY;
		var maxY:int = ir.y + ir.height;
		var i:int;
		var cellCom:Component;
		//invisible last viewed
		for(i=Math.max(0, firstVisibleIndex+firstVisibleIndexOffset); i<startIndex; i++){
			cellCom = ListCell(cells.get(i)).getCellComponent();
			cellCom.setVisible(false);
			cellCom.validate();
		}
		var rlvi:int = Math.min(lastVisibleIndex+lastVisibleIndexOffset, listSize-1);
		for(i=endIndex+1; i<=rlvi; i++){
			cellCom = ListCell(cells.get(i)).getCellComponent();
			cellCom.setVisible(false);
			cellCom.validate();
		}
		if(endIndex < 0 || startIndex > endIndex){
			lastVisibleIndex = -1;
		}
		//visible current needed
		for(i=startIndex; i<=endIndex; i++){
			var cell:ListCell = ListCell(cells.get(i));
			cellCom = cell.getCellComponent();
			cellCom.setVisible(true);
			var s:IntDimension = getCachedCellPreferSize(cell);
			if(s == null){
				s = cell.getCellComponent().getPreferredSize();
				trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
			}
			var finalWidth:int = isTracksWidth() ? ir.width : Math.max(cellWidth, s.width);
			cellCom.setComBoundsXYWH(cx, cy, finalWidth, ih);
			if(cy < maxY){
				lastVisibleIndex = i;
			}
			cy += ih;
			cell.setListCellStatus(this, isSelectedIndex(i), i);
			cellCom.validate();
		}
		firstVisibleIndex = startIndex;
		firstVisibleIndexOffset = lastVisibleIndexOffset = 0;
    }
    
    private function getCachedCellPreferSize(cell:ListCell):IntDimension{
    	return IntDimension(cellPrefferSizes.get(cell.getCellComponent()));
    }
    
    private function layoutWhenNotShareCellsAndNotSameHeight():void{
		var m:ListModel = getModel();
		var ir:IntRectangle = getInsets().getInsideBounds(getSize().getBounds());
    	var cellWidth:int = Math.max(ir.width, viewWidth);
    	ir.x = ir.y = 0; //this is because the cells is in cellPane, not in JList
    	
    	restrictionViewPos(viewPosition);
		var x:int = viewPosition.x;
		var y:int = viewPosition.y;
		var startIndex:int = 0;
		var cellsCount:int = cells.getSize();
		
		var tryY:int = 0;
		var startY:int = 0;
		var i:int;
		var s:IntDimension;
		var cell:ListCell;
		
		for(i=0; i<cellsCount; i++){
			cell = ListCell(cells.get(i));
			s = getCachedCellPreferSize(cell);
			if(s == null){
				s = cell.getCellComponent().getPreferredSize();
				trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
			}
			tryY += s.height;
			if(tryY > y){
				startIndex = i;
				startY = -(s.height - (tryY - y));
				break;
			}
		}
		
		var listSize:int = m.getSize();
		var cx:int = ir.x - x;
		var cy:int = ir.y + startY;
		var maxY:int = ir.y + ir.height;
		var tempLastVisibleIndex:int = -1;
		var cellCom:Component;
		//visible current needed
		var endIndex:int = startIndex;
		for(i=startIndex; i<cellsCount; i++){
			cell = ListCell(cells.get(i));
			cellCom = cell.getCellComponent();
			s = getCachedCellPreferSize(cell);
			if(s == null){
				s = cell.getCellComponent().getPreferredSize();
				trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
			}
			cell.setListCellStatus(this, isSelectedIndex(i), i);
			cellCom.setVisible(true);
			var finalWidth:int = isTracksWidth() ? ir.width : Math.max(cellWidth, s.width);
			cellCom.setComBoundsXYWH(cx, cy, finalWidth, s.height);
			cellCom.validate();
			if(cy < maxY){
				tempLastVisibleIndex = i;
			}
			cy += s.height;
			endIndex = i;
			if(cy >= maxY){
				break;
			}
		}
		
		//invisible last viewed
		for(i=Math.max(0, firstVisibleIndex+firstVisibleIndexOffset); i<startIndex; i++){
			cellCom = ListCell(cells.get(i)).getCellComponent();
			cellCom.setVisible(false);
			cellCom.validate();
		}
		var rlvi:int = Math.min(lastVisibleIndex+lastVisibleIndexOffset, listSize-1);
		for(i=endIndex+1; i<=rlvi; i++){
			cellCom = ListCell(cells.get(i)).getCellComponent();
			cellCom.setVisible(false);
			cellCom.validate();
		}
		lastVisibleIndex = tempLastVisibleIndex;
		firstVisibleIndex = startIndex;
		firstVisibleIndexOffset = lastVisibleIndexOffset = 0;
    }
    
	/**
	 * return 0
	 */
    public function getLayoutAlignmentX(target:Container):Number{
    	return 0;
    }

	/**
	 * return 0
	 */
    public function getLayoutAlignmentY(target:Container):Number{
    	return 0;
    }

    public function invalidateLayout(target:Container):void{
    }
	
	//------------------------ListMode Listener Methods-----------------
	
	/**
	 * data in list has changed, update JList if needed.
	 */
    public function intervalAdded(e:ListDataEvent):void{
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		
		var w:int = viewWidth;
		var h:int = viewHeight;
		var sameHeight:Boolean = factory.isAllCellHasSameHeight();
		
		var i0:int = Math.min(e.getIndex0(), e.getIndex1());
		var i1:int = Math.max(e.getIndex0(), e.getIndex1());
		
		if(factory.isShareCells()){
			w = getPreferredCellWidthWhenNoCount();
			h = m.getSize()*factory.getCellHeight();
		}else{
			for(var i:int=i0; i<=i1; i++){
				var cell:ListCell = createNewCell();
				cells.append(cell, i);
				cell.setCellValue(m.getElementAt(i));
				addCellToContainer(cell);
				var s:IntDimension = cell.getCellComponent().getPreferredSize();
				cell.getCellComponent().setVisible(false);
				cellPrefferSizes.put(cell.getCellComponent(), s);
				if(s.width > w){
					w = s.width;
					maxWidthCell = cell;
				}
				w = Math.max(w, s.width);
				if(!sameHeight){
					h += s.height;
				}
			}
			if(sameHeight){
				h = m.getSize()*factory.getCellHeight();
			}
			
			if(i0 > lastVisibleIndex + lastVisibleIndexOffset){
				//nothing needed
			}else if(i0 >= firstVisibleIndex + firstVisibleIndexOffset){
				lastVisibleIndexOffset += (i1 - i0 + 1);
			}else if(i0 < firstVisibleIndex + firstVisibleIndexOffset){
				firstVisibleIndexOffset += (i1 - i0 + 1);
				lastVisibleIndexOffset += (i1 - i0 + 1);
			}
		}
		
		viewWidth = w;
		viewHeight = h;
		getSelectionModel().insertIndexInterval(i0, i1-i0+1, true);
		revalidate();
    }
    
	/**
	 * data in list has changed, update JList if needed.
	 */
    public function intervalRemoved(e:ListDataEvent):void{
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		
		var w:int = viewWidth;
		var h:int = viewHeight;
		var sameHeight:Boolean = factory.isAllCellHasSameHeight();
		
		var i0:int = Math.min(e.getIndex0(), e.getIndex1());
		var i1:int = Math.max(e.getIndex0(), e.getIndex1());
		
		var i:int;
		var s:IntDimension;
		var cell:ListCell;
		
		if(factory.isShareCells()){
			w = getPreferredCellWidthWhenNoCount();
			h = m.getSize()*factory.getCellHeight();
		}else{
			var needRecountWidth:Boolean = false;
			for(i=i0; i<=i1; i++){
				cell = ListCell(cells.get(i));
				if(cell == maxWidthCell){
					needRecountWidth = true;
				}
				if(!sameHeight){
					s = getCachedCellPreferSize(cell);
					if(s == null){
						s = cell.getCellComponent().getPreferredSize();
						trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
					}
					h -= s.height;
				}
				removeCellFromeContainer(cell);
				cellPrefferSizes.remove(cell.getCellComponent());
			}
			cells.removeRange(i0, i1);
			if(sameHeight){
				h = m.getSize()*factory.getCellHeight();
			}
			if(needRecountWidth){
				w = 0;
				for(i=cells.getSize()-1; i>=0; i--){
					cell = ListCell(cells.get(i));
					s = getCachedCellPreferSize(cell);
					if(s == null){
						s = cell.getCellComponent().getPreferredSize();
						trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
					}
					if(s.width > w){
						w = s.width;
						maxWidthCell = cell;
					}
				}
			}
			if(i0 > lastVisibleIndex + lastVisibleIndexOffset){
				//nothing needed
			}else if(i0 >= firstVisibleIndex + firstVisibleIndexOffset){
				lastVisibleIndexOffset -= (i1 - i0 + 1);
			}else if(i0 < firstVisibleIndex + firstVisibleIndexOffset){
				firstVisibleIndexOffset -= (i1 - i0 + 1);
				lastVisibleIndexOffset -= (i1 - i0 + 1);
			}
		}
		
		viewWidth = w;
		viewHeight = h;
		getSelectionModel().removeIndexInterval(i0, i1);
		revalidate();
    }
    
	/**
	 * data in list has changed, update JList if needed.
	 */
    public function contentsChanged(e:ListDataEvent):void{
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		
		var w:int = viewWidth;
		var h:int = viewHeight;
		var sameHeight:Boolean = factory.isAllCellHasSameHeight();
		
		var i0:int = Math.min(e.getIndex0(), e.getIndex1());
		var i1:int = Math.max(e.getIndex0(), e.getIndex1());
		var i:int;
		var s:IntDimension;
		var cell:ListCell;
		var ns:IntDimension;
		
		if(factory.isShareCells()){
			w = getPreferredCellWidthWhenNoCount();
			h = m.getSize()*factory.getCellHeight();
		}else{
			var needRecountWidth:Boolean = false;
			for(i=i0; i<=i1; i++){
				var newValue:Object = m.getElementAt(i);
				cell = ListCell(cells.get(i));
				s = getCachedCellPreferSize(cell);
				if(s == null){
					s = cell.getCellComponent().getPreferredSize();
					trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
				}
				if(cell == maxWidthCell){
					h -= s.height;
					cell.setCellValue(newValue);
					ns = cell.getCellComponent().getPreferredSize();
					cellPrefferSizes.put(cell.getCellComponent(), ns);
					if(ns.width < s.width){
						needRecountWidth = true;
					}else{
						w = ns.width;
					}
					h += ns.height;
				}else{
					h -= s.height;
					cell.setCellValue(newValue);
					ns = cell.getCellComponent().getPreferredSize();
					cellPrefferSizes.put(cell.getCellComponent(), ns);
					h += ns.height;
					if(!needRecountWidth){
						if(ns.width > w){
							maxWidthCell = cell;
							w = ns.width;
						}
					}
				}
			}
			if(sameHeight){
				h = m.getSize()*factory.getCellHeight();
			}
			if(needRecountWidth || maxWidthCell == null){
				w = 0;
				for(i=cells.getSize()-1; i>=0; i--){
					cell = cells.get(i);
					s = getCachedCellPreferSize(cell);
					if(s == null){
						s = cell.getCellComponent().getPreferredSize();
						trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
					}
					if(s.width > w){
						w = s.width;
						maxWidthCell = cell;
					}
				}
			}
		}
		
		viewWidth = w;
		viewHeight = h;
		
		revalidate();
    }
        
    private function __selectionListener(e:SelectionEvent):void{
    	dispatchEvent(new SelectionEvent(SelectionEvent.LIST_SELECTION_CHANGED, e.getFirstIndex(), e.getLastIndex(), e.isProgrammatic()));
    	revalidate();
    }
    
    //-------------------------------Event Listener For All Items----------------
    
    protected function addHandlersToCell(cellCom:Component):void{
    	cellCom.addEventListener(MouseEvent.CLICK, __onItemClick);
    	cellCom.addEventListener(MouseEvent.DOUBLE_CLICK, __onItemDoubleClick);
    	cellCom.addEventListener(MouseEvent.MOUSE_DOWN, __onItemMouseDown);
    	cellCom.addEventListener(MouseEvent.ROLL_OVER, __onItemRollOver);
    	cellCom.addEventListener(MouseEvent.ROLL_OUT, __onItemRollOut);
    	cellCom.addEventListener(ReleaseEvent.RELEASE_OUT_SIDE, __onItemReleaseOutSide);
    }
    
    protected function removeHandlersFromCell(cellCom:Component):void{
    	cellCom.removeEventListener(MouseEvent.CLICK, __onItemClick);
    	cellCom.removeEventListener(MouseEvent.DOUBLE_CLICK, __onItemDoubleClick);
    	cellCom.removeEventListener(MouseEvent.MOUSE_DOWN, __onItemMouseDown);
    	cellCom.removeEventListener(MouseEvent.ROLL_OVER, __onItemRollOver);
    	cellCom.removeEventListener(MouseEvent.ROLL_OUT, __onItemRollOut);
    	cellCom.removeEventListener(ReleaseEvent.RELEASE_OUT_SIDE, __onItemReleaseOutSide);
    }
    	
	protected function createItemEventObj(cellCom:*, type:String, e:MouseEvent):ListItemEvent{
		var cell:ListCell = getCellByCellComponent(Component(cellCom));
		var event:ListItemEvent = new ListItemEvent(type, cell.getCellValue(), cell, e);
		return event;
	}
	
	protected function getItemIndexByCellComponent(item:Component):int{
		var cell:ListCell = comToCellMap.get(item);
		return getItemIndexByCell(cell);
	}
	
	/**
	 * Returns the index of the cell.
	 */
	public function getItemIndexByCell(cell:ListCell):int{
		if(getCellFactory().isShareCells()){
			return firstVisibleIndex + cells.indexOf(cell);
		}else{
			return cells.indexOf(cell);
		}
	}
	
	private function getCellByCellComponent(item:Component):ListCell{
		return comToCellMap.get(item);
	}
	
	/**
	 * Returns the cell of the specified index
	 */
	public function getCellByIndex(index:int):ListCell{
		if(getCellFactory().isShareCells()){
			return ListCell(cells.get(index - firstVisibleIndex));
		}else{
			return ListCell(cells.get(index));
		}
	}
	
	
    /**
     * Event Listener For All Items
     */
	private function __onItemMouseDown(e:MouseEvent):void{
		dispatchEvent(createItemEventObj(e.currentTarget, ListItemEvent.ITEM_MOUSE_DOWN, e));
	}
		
    /**
     * Event Listener For All Items
     */	
	private function __onItemClick(e:MouseEvent):void{
		dispatchEvent(createItemEventObj(e.currentTarget, ListItemEvent.ITEM_CLICK, e));
	}
	
    /**
     * Event Listener For All Items
     */	
	private function __onItemReleaseOutSide(e:ReleaseEvent):void{
		dispatchEvent(createItemEventObj(e.currentTarget, ListItemEvent.ITEM_RELEASE_OUT_SIDE, e));
	}
	
    /**
     * Event Listener For All Items
     */	
	private function __onItemRollOver(e:MouseEvent):void{
		dispatchEvent(createItemEventObj(e.currentTarget, ListItemEvent.ITEM_ROLL_OVER, e));
	}
	
    /**
     * Event Listener For All Items
     */	
	private function __onItemRollOut(e:MouseEvent):void{
		dispatchEvent(createItemEventObj(e.currentTarget, ListItemEvent.ITEM_ROLL_OUT, e));
	}
	
    /**
     * Event Listener For All Items
     */	
	private function __onItemDoubleClick(e:MouseEvent):void{
		dispatchEvent(createItemEventObj(e.currentTarget, ListItemEvent.ITEM_DOUBLE_CLICK, e));
	}
	
	//-------------------------------Drag and Drop---------------------------------

	/*private var dndAutoScrollTimer:Timer;	
	private var dnd_line_mc:MovieClip;
	
	private function __onDragRecognized(dragInitiator:Component, touchedChild:Component):void{
		if(isAutoDnDInitiatorAllown()){
			var data:Array = getSelectedIndices();
			var sourceData:ListSourceData = new ListSourceData("ListSourceData", data);
			
			var firstIndex:int = getFirstVisibleIndex();
			var lastIndex:int = getLastVisibleIndex();
			var mp:IntPoint = getMousePosition();
			var ib:Rectangle = new Rectangle();
			var offsetY:int = mp.y;
			for(var i:int=firstIndex; i<=lastIndex; i++){
				ib = getCellByIndex(i).getCellComponent().getBounds(ib);
				if(mp.y < ib.y + ib.height){
					offsetY = ib.y;
					break;
				}
			}
			
			DragManager.startDrag(this, sourceData, new ListDragImage(this, offsetY));
		}
	}
	private function __onDragEnter(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:IntPoint):void{
		dndInsertPosition = -1;
		if(!(isAcceptableListSourceData(dragInitiator, sourceData) && isAutoDnDDropTargetAllown())){
			DragManager.getCurrentDragImage().switchToRejectImage();
		}else{
			DragManager.getCurrentDragImage().switchToAcceptImage();
			checkStartDnDAutoScroll();
		}
	}
	private function __onDragOverring(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:IntPoint):void{
		if(isAcceptableListSourceData(dragInitiator, sourceData) && isAutoDnDDropTargetAllown()){
			checkStartDnDAutoScroll();
			drawInsertLine();
		}
	}
	private function __onDragExit(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:IntPoint):void{
		checkStopDnDAutoScroll();
	}
	private function __onDragDrop(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:IntPoint):void{
		checkStopDnDAutoScroll();
		if(isAcceptableListSourceData(dragInitiator, sourceData) && isAutoDnDDropTargetAllown()){
			if(dndInsertPosition >= 0){
				var indices:Array = (ListSourceData(sourceData)).getItemIndices();
				if(indices == null || indices.length == null || indices.length <= 0){
					return;
				}
				var initiator:JList = JList(dragInitiator);
				var items:Array = new Array(indices.length);
				for(var i:int=0; i<indices.length; i++){
					items[i] = initiator.getModel().getElementAt(indices[i]);
				}
				var insertOffset:int = 0;
				if(initiator.getAutoDragAndDropType() == DND_MOVE){
					var imm:MutableListModel = MutableListModel(initiator.getModel());
					var sameModel:Boolean = (imm == getModel());
					for(var i:int=0; i<indices.length; i++){
						var rindex:int = indices[i];
						imm.removeElementAt(rindex-i);
						if(sameModel && rindex<dndInsertPosition){
							insertOffset ++;
						}
					}
				}
				var index:int = dndInsertPosition - insertOffset;
				var mm:MutableListModel = MutableListModel(getModel());
				for(var i:int=0; i<items.length; i++){
					mm.insertElementAt(items[i], index);
					index++;
				}
				return;
			}
		}
		DragManager.setDropMotion(DragManager.DEFAULT_REJECT_DROP_MOTION);
	}
	*/
	/**
	 * Returns is the source data is acceptale to drop in this list as build-in support
	 */
	/*public function isAcceptableListSourceData(dragInitiator:Component, sd:SourceData):Boolean{
		return (sd is ListSourceData) && isDragAcceptableInitiator(dragInitiator);
	}*/
	
	/**
	 * Returns is the model is mutable
	 */
	public function isMutableModel():Boolean{
		return getModel() is MutableListModel;
	}
	
	/*private function checkStartDnDAutoScroll():void{
		if(dndAutoScrollTimer == null){
			dndAutoScrollTimer = new Timer(200);
			dndAutoScrollTimer.addActionListener(__dndAutoScroll, this);
		}
		if(!dndAutoScrollTimer.isRunning()){
			dndAutoScrollTimer.start();
		}
		if(!MCUtils.isMovieClipExist(dnd_line_mc)){
			dnd_line_mc = createMovieClip("line_mc");
		}
	}
	private function checkStopDnDAutoScroll():void{
		if(dndAutoScrollTimer != null){
			dndAutoScrollTimer.stop();
		}
		if(dnd_line_mc != null){
			dnd_line_mc.removeMovieClip();
			dnd_line_mc = null;
		}
	}
	
	private function __dndAutoScroll():void{
		var lastCellBounds:Rectangle = getCellByIndex(getLastVisibleIndex()).getCellComponent().getBounds();
		var firstCellBounds:Rectangle = getCellByIndex(getFirstVisibleIndex()).getCellComponent().getBounds();
		var vp:IntPoint = getViewPosition();
		var mp:IntPoint = getMousePosition();
		var ins:Insets = getInsets();
		
		if(mp.y < ins.top + firstCellBounds.height/2){
			vp.y -= firstCellBounds.height;
			setViewPosition(vp);
			drawInsertLine();
		}else if(mp.y > getHeight() - ins.bottom - lastCellBounds.height/2){
			vp.y += lastCellBounds.height;
			setViewPosition(vp);
			drawInsertLine();
		}
	}
	
	private var dndInsertPosition:int;
	private function drawInsertLine():void{
		var firstIndex:int = getFirstVisibleIndex();
		var lastIndex:int = getLastVisibleIndex();
		
		var mp:IntPoint = getMousePosition();
		var ib:Rectangle = new Rectangle();
		var insertIndex:int = -1;
		var insertY:int;
		for(var i:int=firstIndex; i<=lastIndex; i++){
			ib = getCellByIndex(i).getCellComponent().getBounds(ib);
			if(mp.y < ib.y + ib.height/2){
				insertIndex = i;
				insertY = ib.y;
				break;
			}
		}
		if(insertIndex < 0){
			ib = getCellByIndex(lastIndex).getCellComponent().getBounds(ib);
			insertIndex = lastIndex+1;
			insertY = ib.y + ib.height;
		}
		dndInsertPosition = insertIndex;
				
		dnd_line_mc.clear();
		var g:Graphics = new Graphics(dnd_line_mc);
		var pen:Pen = new Pen(0, 2, 70);
		var ins:Insets = this.getInsets();
		
		g.drawLine(pen, ins.left+1, insertY, getWidth()-ins.right-1, insertY);
	}
	
	
	private function ____onDragRecognized(dragInitiator:Component, touchedChild:Component):void{
		__onDragRecognized(dragInitiator, touchedChild);
	}
	private function ____onDragEnter(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:IntPoint):void{
		__onDragEnter(source, dragInitiator, sourceData, mousePos);
	}
	private function ____onDragOverring(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:IntPoint):void{
		__onDragOverring(source, dragInitiator, sourceData, mousePos);
	}
	private function ____onDragExit(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:IntPoint):void{
		__onDragExit(source, dragInitiator, sourceData, mousePos);
	}
	private function ____onDragDrop(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:IntPoint):void{
		__onDragDrop(source, dragInitiator, sourceData, mousePos);
	}	
	*/
}
}