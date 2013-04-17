/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.ChildrenMaskedContainer;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.DefaultListCellFactory;
import org.aswing.DefaultListSelectionModel;
import org.aswing.dnd.DragManager;
import org.aswing.dnd.ListDragImage;
import org.aswing.dnd.ListSourceData;
import org.aswing.dnd.SourceData;
import org.aswing.Event;
import org.aswing.event.ListDataEvent;
import org.aswing.event.ListDataListener;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.Insets;
import org.aswing.LayoutManager;
import org.aswing.ListCell;
import org.aswing.ListCellFactory;
import org.aswing.ListModel;
import org.aswing.ListSelectionModel;
import org.aswing.MutableListModel;
import org.aswing.plaf.ListUI;
import org.aswing.RepaintManager;
import org.aswing.UIManager;
import org.aswing.util.ArrayUtils;
import org.aswing.util.Delegate;
import org.aswing.util.HashMap;
import org.aswing.util.MCUtils;
import org.aswing.util.Timer;
import org.aswing.util.Vector;
import org.aswing.VectorListModel;
import org.aswing.Viewportable;

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
class org.aswing.overflow.JList extends ChildrenMaskedContainer implements Viewportable, LayoutManager, ListDataListener{
	
	/**
	 * When the JList Viewportable state changed.
	 *<br>
	 * onStateChanged(source:JList)
	 */	
	public static var ON_STATE_CHANGED:String = "onStateChanged";// Component.ON_STATE_CHANGED; 	
	
	/**
	 * When the list selection changed.<br>
	 * onSelectionChanged(source:JList, firstIndex:Number, lastIndex:Number)<br>
	 * firstIndex the first selection changed index<br>
	 * lastIndex the last selection changed index
	 */	
	public static var ON_SELECTION_CHANGED:String = DefaultListSelectionModel.ON_SELECTION_CHANGED;
	
	/**
	 * onListScroll(source:JList)
	 */	
	public static var ON_LIST_SCROLL:String = "onListScroll";
	
	/**
	 * onItemPress(source:JList, value, cell:ListCell)
	 */
	public static var ON_ITEM_PRESS:String = "onItemPress";
	/**
	 * onItemRelease(source:JList, value, cell:ListCell)
	 */	
	public static var ON_ITEM_RELEASE:String = "onItemRelease";	
	/**
	 * onItemReleaseOutSide(source:JList, value, cell:ListCell)
	 */	
	public static var ON_ITEM_RELEASEOUTSIDE:String = "onItemReleaseOutSide";
	/**
	 * onItemRollOver(source:JList, value, cell:ListCell)
	 */	
	public static var ON_ITEM_ROLLOVER:String = "onItemRollOver";
	/**
	 * onItemRollOut(source:JList, value, cell:ListCell)
	 */
	public static var ON_ITEM_ROLLOUT:String = "onItemRollOut";		
	/**
	 * onItemClicked(source:JList, value, cell:ListCell, clickCount:Number)
	 */
	public static var ON_ITEM_CLICKED:String = "onItemClicked";
	
	//---------------	
	
	/**
	 * Only can select one most item at a time.
	 */
	public static var SINGLE_SELECTION:Number = DefaultListSelectionModel.SINGLE_SELECTION;
	/**
	 * Can select any item at a time.
	 */
	public static var MULTIPLE_SELECTION:Number = DefaultListSelectionModel.MULTIPLE_SELECTION;
	
	/**
	 * Drag and drop disabled.
	 */
	public static var DND_NONE:Number = DragManager.TYPE_NONE;
	
	/**
	 * Drag and drop enabled, and the action of items is move.
	 */
	public static var DND_MOVE:Number = DragManager.TYPE_MOVE;
	
	/**
	 * Drag and drop enabled, and the action of items is copy.
	 */
	public static var DND_COPY:Number = DragManager.TYPE_COPY;
	
	//---------------------caches------------------
	private var viewHeight:Number;
	private var viewWidth:Number;
	private var maxWidthCell:ListCell;
	private var cellPrefferSizes:HashMap; //use for catche sizes when not all cells same height
	private var comToCellMap:HashMap; 
	private var visibleRowCount:Number;
	private var visibleCellWidth:Number;
	//--
	
	private var preferredWidthWhenNoCount:Number;
	
	private var tracksWidth:Boolean;
	private var verticalUnitIncrement:Number;
	private var verticalBlockIncrement:Number;
	private var horizontalUnitIncrement:Number;
	private var horizontalBlockIncrement:Number;
	
	private var viewPosition:Point;
	private var selectionForeground:ASColor;
	private var selectionBackground:ASColor;
	
	private var cellFactory:ListCellFactory;
	private var model:ListModel;
	private var selectionModel:ListSelectionModel;
	private var selectionListener:Object;
	private var cells:Vector;
	
	private var firstVisibleIndex:Number;
	private var lastVisibleIndex:Number;
	private	var firstVisibleIndexOffset:Number = 0;
	private	var lastVisibleIndexOffset:Number = 0;
	
	private var itemHandler:Object;
	private var autoDragAndDropType:Number;
	private var dndListener:Object;
	
	/**
	 * JList(listData:Array, cellFactory:ListCellFactory)<br>
	 * JList(model:ListModel, cellFactory:ListCellFactory)<br>
	 * JList(listData:Array)<br>
	 * JList(model:ListModel)<br>
	 * JList()
	 * @param listData a ListModel or a Array.
	 * @param cellFactory the cellFactory for this List.
	 */
	public function JList(listData:Object, cellFactory:ListCellFactory) {
		super();
		
		setName("JList");
		layout = this;
		viewPosition = new Point(0, 0);
		selectionListener = new Object();
		selectionListener[ON_SELECTION_CHANGED] = Delegate.create(this, __selectionListener);
		setSelectionModel(new DefaultListSelectionModel());
		firstVisibleIndex = 0;
		lastVisibleIndex = 0;
		firstVisibleIndexOffset = 0;
		lastVisibleIndexOffset = 0;
		visibleRowCount = -1;
		visibleCellWidth = -1;
		preferredWidthWhenNoCount = 20; //Default 20
		
		tracksWidth = false;
		viewWidth = 0;
		viewHeight = 0;
		maxWidthCell = null;
		cellPrefferSizes = new HashMap();
		comToCellMap = new HashMap();
		cells = new Vector();
		model = null;
		autoDragAndDropType = DND_NONE;
		initItemHandler();
		
		if(cellFactory == undefined){
			cellFactory = new DefaultListCellFactory(true);
		}
		this.cellFactory = cellFactory;
		
		if(listData instanceof ListModel){
			setModel(ListModel(listData));
		}else{
			var o = listData;//avoid Array casting
			if(o instanceof Array){
				setListData(o);
			}else{
				setListData(null); //create new
			}
		}
		
		updateUI();
	}
	
    public function updateUI():Void{
    	//update cells ui
    	for(var i:Number=0; i<cells.size(); i++){
    		var cell:ListCell = ListCell(cells.get(i));
    		cell.getCellComponent().updateUI();
    	}
    	
    	setUI(ListUI(UIManager.getUI(this)));
    }
    
    public function setUI(newUI:ListUI):Void{
    	super.setUI(newUI);
    }
	
	public function getUIClassID():String{
		return "ListUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicListUI;
    }
	
	
	/**
	 * Can not set layout to JList, its layout is itself.
	 * @throws Error when set any layout.
	 */
	public function setLayout(layout:LayoutManager):Void{
		trace("Can not set layout to JList, its layout is itself");
		throw new Error("Can not set layout to JList, its layout is itself");
	}	
	
	/**
	 * set a array to be the list data, but array is not a List Model.
	 * So when the array content was changed, a new List Model will be created and then 
	 * <code>updateListView</code> will be called to update the JList.But this is not a good way,
	 *  its slow. So suggest you to create a ListMode for example VectorListMode to JList,
	 * When you modify ListMode, it will automatic update JList if necessary.
	 * @see #setModel()
	 * @see org.aswing.ListModel
	 */
	public function setListData(ld:Array):Void{
		var m:ListModel = new VectorListModel(ld);
		setModel(m);
	}
	
	/**
	 * Set the list mode to provide the data to JList.
	 * @see org.aswing.ListModel
	 */
	public function setModel(m:ListModel):Void{
		if(m == null) return;
		if(m != model){
			model.removeListDataListener(this);
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
	public function setSelectionModel(m:ListSelectionModel):Void{
		if(m != null && m != selectionModel){
			selectionModel.removeEventListener(selectionListener);
			selectionModel = m;
			selectionModel.addEventListener(selectionListener);
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
	public function setCellFactory(newFactory:ListCellFactory):Void{
		cellFactory = newFactory;
		removeAllCells();
		updateListView();
	}
	
	/**
	 * Add listener to selection changed.
	 * @see #ON_SELECTION_CHANGED
	 */	
	public function addSelectionListener(func:Function, obj:Object):Object{
		return addEventListener(ON_SELECTION_CHANGED, func, obj);
	}

	/**
	 * @see #setPreferredWidthWhenNoCount()
	 * @return the default preferred with of the List when <code>shareCelles</code>.
	 */
	public function getPreferredCellWidthWhenNoCount():Number {
		return preferredWidthWhenNoCount;
	}

	/**
	 * The preferred with of the List, it is only used when List have no counting for its prefferredWidth.
	 * <p>
	 * When <code>ListCellFactory</code> is <code>shareCelles</code>, List will not count prefferred width.
	 * @param preferredWidthWhenNoCount the preferred with of the List.
	 */
	public function setPreferredCellWidthWhenNoCount(preferredWidthWhenNoCount:Number):Void {
		this.preferredWidthWhenNoCount = preferredWidthWhenNoCount;
	}	
	
	/**
	 * When your list data changed, and you want to update list view by hand.
	 * call this method.
	 * <p>This method is called automatically when setModel called with a different model to set. 
	 */
	public function updateListView() : Void {
		createCells();
		validateCells();
	}
	
	/**
	 * Clears the selection - after calling this method isSelectionEmpty will return true. 
	 * This is a convenience method that just delegates to the selectionModel.
	 */
	public function clearSelection():Void{
		getSelectionModel().clearSelection();
	}
	
	/**
	 * Determines whether single-item or multiple-item selections are allowed.
	 * If selection mode changed, will cause clear selection;
	 * @see #SINGLE_SELECTION
	 * @see #MULTIPLE_SELECTION
	 */
	public function setSelectionMode(sm:Number):Void{
		getSelectionModel().setSelectionMode(sm);
	}
	
	/**
	 * Return whether single-item or multiple-item selections are allowed.
	 * @see #SINGLE_SELECTION
	 * @see #MULTIPLE_SELECTION
	 */	
	public function getSelectionMode():Number{
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
	public function setSelectionForeground(selectionForeground:ASColor):Void{
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
	public function setSelectionBackground(selectionBackground:ASColor):Void{
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
    public function getAnchorSelectionIndex():Number {
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
    public function getLeadSelectionIndex():Number {
        return getSelectionModel().getLeadSelectionIndex();
    }	
	
    /** 
     * @see ListSelectionModel#setSelectionInterval
     * @see #removeSelectionInterval()
     */	
	public function setSelectionInterval(index0:Number, index1:Number):Void{
		getSelectionModel().setSelectionInterval(index0, index1);
	}
	
    /** 
     * @see ListSelectionModel#addSelectionInterval()
     * @see #removeSelectionInterval()
     */	
	public function addSelectionInterval(index0:Number, index1:Number):Void{
		getSelectionModel().addSelectionInterval(index0, index1);
	}

    /** 
     * @see ListSelectionModel#removeSelectionInterval()
     */	
	public function removeSelectionInterval(index0:Number, index1:Number):Void{
		getSelectionModel().removeSelectionInterval(index0, index1);
	}
	
	/**
	 * Selects all elements in the list.
	 * 
	 * @see #setSelectionInterval
	 */
	public function selectAll():Void {
		setSelectionInterval(0, getModel().getSize()-1);
	}
	
	/**
	 * Return the selected index, if selection multiple, return the first.
	 * if not selected any, return -1.
	 * @return the selected index
	 */
	public function getSelectedIndex():Number{
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
		var min:Number = sm.getMinSelectionIndex();
		var max:Number = sm.getMaxSelectionIndex();
		if(min < 0 || max < 0 || isSelectionEmpty()){
			return indices;
		}
		for(var i:Number=min; i<=max; i++){
			if(sm.isSelectedIndex(i)){
				indices.push(i);
			}
		}
		return indices;
	}
	
	/**
	 * @return true if the index is selected, otherwise false.
	 */
	public function isSelectedIndex(index:Number):Boolean{
		return getSelectionModel().isSelectedIndex(index);
	}
	
	/**
	 * Returns the first selected value, or null if the selection is empty.
	 * @return the first selected value
	 */
	public function getSelectedValue(){
		var i:Number = getSelectedIndex();
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
		var min:Number = sm.getMinSelectionIndex();
		var max:Number = sm.getMaxSelectionIndex();
		if(min < 0 || max < 0 || isSelectionEmpty()){
			return values;
		}
		var vm:ListModel = getModel();
		for(var i:Number=min; i<=max; i++){
			if(sm.isSelectedIndex(i)){
				values.push(vm.getElementAt(i));
			}
		}
		return values;
	}
	
	/**
     * Selects a single cell.
     *
     * @param index the index of the one cell to select
     * @see ListSelectionModel#setSelectionInterval
     * @see #isSelectedIndex()
     * @see #addSelectionListener()
	 * @see #ensureIndexIsVisible()
	 */
	public function setSelectedIndex(index:Number):Void{
		if(index >= getModel().getSize()){
			return;
		}
		getSelectionModel().setSelectionInterval(index, index);
	}
	
	/**
	 * Selects a set of cells. 
	 * <p> This will not cause a scroll, if you want to 
	 * scroll to visible the selected value, call ensureIndexIsVisible().
	 * @param indices an array of the indices of the cells to select
     * @see #isSelectedIndex()
     * @see #addSelectionListener()
	 * @see #ensureIndexIsVisible()
	 */	
	public function setSelectedIndices(indices:Array):Void{
        var sm:ListSelectionModel = getSelectionModel();
        sm.clearSelection();
		var size:Number = getModel().getSize();
        for(var i:Number = 0; i < indices.length; i++) {
	    	if (indices[i] < size) {
				sm.addSelectionInterval(indices[i], indices[i]);
	    	}
        }
	}
	
	/**
	 * Selects the specified object from the list. This will not cause a scroll, if you want to 
	 * scroll to visible the selected value, call ensureIndexIsVisible().
	 * @param value the value to be selected
	 * @see #setSelectedIndex()
	 * @see #ensureIndexIsVisible()
	 */
	public function setSelectedValue(value):Void{
		var n:Number = model.getSize();
		for(var i:Number=0; i<n; i++){
			if(model.getElementAt(i) == value){
				setSelectedIndex(i);
				return;
			}
		}
		setSelectedIndex(-1); //there is not this value
	}
	
	/**
	 * Selects a set of cells. 
	 * <p> This will not cause a scroll, if you want to 
	 * scroll to visible the selected value, call ensureIndexIsVisible().
	 * @param values an array of the values to select
     * @see #isSelectedIndex()
     * @see #addSelectionListener()
	 * @see #ensureIndexIsVisible()
	 */	
	public function setSelectedValues(values:Array):Void{
        var sm:ListSelectionModel = getSelectionModel();
        sm.clearSelection();
		var size:Number = getModel().getSize();
        for(var i:Number=0; i<values.length; i++) {
        	for(var j:Number=0; j<size; j++){
        		if(values[i] == getModel().getElementAt(j)){
					sm.addSelectionInterval(j, j);
					break;
        		}
        	}
        }
	}	
		
	/**
	 * Scrolls the JList to make the specified cell completely visible.
	 * @see #setFirstVisibleIndex()
	 */
	public function ensureIndexIsVisible(index:Number):Void{
		if(index<=getFirstVisibleIndex()){
			setFirstVisibleIndex(index);
		}else if(index>=getLastVisibleIndex()){
			setLastVisibleIndex(index);
		}
	}
	
	public function getFirstVisibleIndex():Number{
		return firstVisibleIndex;
	}
	
	/**
	 * scroll the list to view the specified index as first visible.
	 * If the list data elements is too short can not move the specified
	 * index to be first, just scroll as top as can.
	 * @see #ensureIndexIsVisible()
	 * @see #setLastVisibleIndex()
	 */
	public function setFirstVisibleIndex(index:Number):Void{
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();		
		var p:Point = getViewPosition();
		if(factory.isAllCellHasSameHeight() || factory.isShareCells()){
			p.y = index * factory.getCellHeight();
		}else{
			var num:Number = Math.min(cells.getSize()-1, index);
			var y:Number = 0;
			for(var i:Number=0; i<num; i++){
				var cell:ListCell = ListCell(cells.get(i));
				var s:Dimension = getCachedCellPreferSize(cell);
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
	
	public function getLastVisibleIndex():Number{
		return lastVisibleIndex;
	}
	
	/**
	 * scroll the list to view the specified index as last visible
	 * If the list data elements is too short can not move the specified
	 * index to be last, just scroll as bottom as can.
	 * @see ensureIndexIsVisible()
	 * @see setFirstVisibleIndex()
	 */
	public function setLastVisibleIndex(index:Number):Void{
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();		
		var p:Point = getViewPosition();
		if(factory.isAllCellHasSameHeight() || factory.isShareCells()){
			p.y = (index + 1) * factory.getCellHeight() - getExtentSize().height;
		}else{
			var num:Number = Math.min(cells.getSize(), index+2);
			var y:Number = 0;
			for(var i:Number=0; i<num; i++){
				var cell:ListCell = ListCell(cells.get(i));
				var s:Dimension = getCachedCellPreferSize(cell);
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
	public function getVisibleRowCount():Number{
		return visibleRowCount;
	}
	
    /**
     * Sets the preferred number of rows in the list that can be displayed.
     * -1 means display all rows.
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
	public function setVisibleRowCount(c:Number):Void{
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
	public function getVisibleCellWidth():Number{
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
	public function setVisibleCellWidth(w:Number):Void{
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
	public function setTracksWidth(b:Boolean):Void{
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
	public function scrollToBottomLeft():Void{
		setViewPosition(new Point(0, Number.MAX_VALUE));
	}
	/**
	 * Scrolls to view bottom right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToBottomRight():Void{
		setViewPosition(new Point(Number.MAX_VALUE, Number.MAX_VALUE));
	}
	/**
	 * Scrolls to view top left content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopLeft():Void{
		setViewPosition(new Point(0, 0));
	}
	/**
	 * Scrolls to view to right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopRight():Void{
		setViewPosition(new Point(Number.MAX_VALUE, 0));
	}	
	
	/**
     * Enables the list so that items can be selected.
     */
	public function setEnabled(b:Boolean):Void{
		if(b != isEnabled()){
			var n:Number = getComponentCount();
			for(var i:Number=n-1; i>=0; i--){
				var com:Component = getComponent(i);
				com.setEnabled(b);
			}
			repaint();
		}
		super.setEnabled(b);
	}	
		
	/**
	 * Sets auto drag and drop type.
	 * @see #DND_NONE
	 * @see #DND_MOVE
	 * @see #DND_COPY
	 */
	public function setAutoDragAndDropType(type:Number):Void{
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
	}
	
	/**
	 * Returns the auto drag and drop type.
	 * @see #DND_NONE
	 * @see #DND_MOVE
	 * @see #DND_COPY
	 */
	public function getAutoDragAndDropType():Number{
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
	
	private function addCellToContainer(cell:ListCell):Void{
		cell.getCellComponent().setFocusable(false);
		append(cell.getCellComponent());
		comToCellMap.put(cell.getCellComponent().getID(), cell);
		cell.getCellComponent().addEventListener(itemHandler);
	}
	
	private function removeCellFromeContainer(cell:ListCell):Void{
		remove(cell.getCellComponent());
		comToCellMap.remove(cell.getCellComponent().getID());
		cell.getCellComponent().removeEventListener(itemHandler);
	}
	
	private function checkCreateCellsWhenShareCells():Void{
		createCellsWhenShareCells();
	}
	
	private function createCellsWhenShareCells():Void{
		var ih:Number = getCellFactory().getCellHeight();
		var needNum:Number = Math.floor(getExtentSize().height/ih) + 2;
		
		viewWidth = getPreferredCellWidthWhenNoCount();
		viewHeight = getModel().getSize()*ih;
		
		if(cells.getSize() == needNum/* || !displayable*/){
			return;
		}
		
		var iw:Number = getWidth();
		//create needed
		if(cells.getSize() < needNum){
			var addNum:Number = needNum - cells.getSize();
			for(var i:Number=0; i<addNum; i++){
				var cell:ListCell = createNewCell();
				addCellToContainer(cell);
				cells.append(cell);
			}
		}else if(cells.getSize() > needNum){ //remove mored
			var removeIndex:Number = needNum;
			var removed:Array = cells.removeRange(removeIndex, cells.getSize()-1);
			for(var i:Number=0; i<removed.length; i++){
				var cell:ListCell = ListCell(removed[i]);
				removeCellFromeContainer(cell);
			}
		}
	}
	
	private function createCellsWhenNotShareCells():Void{
		var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		
		var w:Number = 0;
		var h:Number = 0;
		var sameHeight:Boolean = factory.isAllCellHasSameHeight();
		
		var mSize:Number = m.getSize();
		var cSize:Number = cells.getSize();
		
		cellPrefferSizes.clear();
		
		var n:Number = Math.min(mSize, cSize);
		//reuse created cells
		for(var i:Number=0; i<n; i++){
			var cell:ListCell = ListCell(cells.get(i));
			cell.setCellValue(m.getElementAt(i));
			var s:Dimension = cell.getCellComponent().getPreferredSize();
			cellPrefferSizes.put(cell.getCellComponent().getID(), s);
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
			for(var i:Number = cSize; i<mSize; i++){
				var cell:ListCell = createNewCell();
				cells.append(cell);
				cell.setCellValue(m.getElementAt(i));
				addCellToContainer(cell);
				var s:Dimension = cell.getCellComponent().getPreferredSize();
				cellPrefferSizes.put(cell.getCellComponent().getID(), s);
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
			for(var i:Number=0; i<removed.length; i++){
				var cell:ListCell = ListCell(removed[i]);
				cell.getCellComponent().removeFromContainer();
				removeCellFromeContainer(cell);
				cellPrefferSizes.remove(cell.getCellComponent().getID());
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
	
	private function createCells():Void{
		if(getCellFactory().isShareCells()){
			createCellsWhenShareCells();
		}else{
			createCellsWhenNotShareCells();
		}
	}
	
	private function removeAllCells() : Void {
		for(var i:Number=0; i<cells.getSize(); i++){
			var cell:ListCell = ListCell(cells.get(i));
			cell.getCellComponent().removeFromContainer();
		}
		cells.clear();
	}
	
	private function validateCells():Void{
		revalidate();
	}
	
	//--------------------------------------------------------

	public function getVerticalUnitIncrement() : Number {
		if(verticalUnitIncrement != undefined){
			return verticalUnitIncrement;
		}else if(getCellFactory().isAllCellHasSameHeight()){
			return getCellFactory().getCellHeight();
		}else{
			return 1;
		}
	}

	public function getVerticalBlockIncrement() : Number {
		if(verticalBlockIncrement != undefined){
			return verticalBlockIncrement;
		}else if(getCellFactory().isAllCellHasSameHeight()){
			return getExtentSize().height - getCellFactory().getCellHeight();
		}else{
			return getExtentSize().height - 10;
		}
	}

	public function getHorizontalUnitIncrement() : Number {
		if(horizontalUnitIncrement == undefined){
			return 1;
		}else{
			return horizontalUnitIncrement;
		}
	}

	public function getHorizontalBlockIncrement() : Number {
		if(horizontalBlockIncrement == undefined){
			return getExtentSize().width - 1;
		}else{
			return horizontalBlockIncrement;
		}
	}
	
    public function setVerticalUnitIncrement(increment:Number):Void{
    	if(verticalUnitIncrement != increment){
    		verticalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    public function setVerticalBlockIncrement(increment:Number):Void{
    	if(verticalBlockIncrement != increment){
    		verticalBlockIncrement = increment;
			fireStateChanged();
    	}
    }
    
    public function setHorizontalUnitIncrement(increment:Number):Void{
    	if(horizontalUnitIncrement != increment){
    		horizontalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    public function setHorizontalBlockIncrement(increment:Number):Void{
    	if(horizontalBlockIncrement != increment){
    		horizontalBlockIncrement = increment;
			fireStateChanged();
    	}
    }
	
    public function setViewportTestSize(s:Dimension):Void{
    	setSize(s);
    }	
		
	public function getExtentSize() : Dimension {	
    	return getInsets().getInsideSize(getSize());
	}

	public function getViewSize() : Dimension {
		var w:Number = isTracksWidth() ? getExtentSize().width : viewWidth;
		return new Dimension(w, viewHeight);
	}

	public function getViewPosition() : Point {
		return new Point(viewPosition.x, viewPosition.y);
	}

	public function setViewPosition(p : Point) : Void {
		restrictionViewPos(p);
		if(!viewPosition.equals(p)){
			viewPosition.setLocation(p);
			fireStateChanged();
			//revalidate();
			valid = false;
			RepaintManager.getInstance().addInvalidRootComponent(this);
		}
	}

	public function scrollRectToVisible(contentRect : Rectangle) : Void {
		setViewPosition(new Point(contentRect.x, contentRect.y));
	}
	
	private function restrictionViewPos(p:Point):Point{
		var maxPos:Point = getViewMaxPos();
		p.x = Math.max(0, Math.min(maxPos.x, p.x));
		p.y = Math.max(0, Math.min(maxPos.y, p.y));
		return p;
	}
	
	private function getViewMaxPos():Point{
		var showSize:Dimension = getExtentSize();
		var viewSize:Dimension = getViewSize();
		var p:Point = new Point(viewSize.width-showSize.width, viewSize.height-showSize.height);
		if(p.x < 0) p.x = 0;
		if(p.y < 0) p.y = 0;
		return p;
	}

	public function addChangeListener(func : Function, obj : Object) : Object {
		return addEventListener(ON_STATE_CHANGED, func, obj);
	}

	public function getViewportPane() : Component {
		return this;
	}
	//------------------------Layout implementation---------------------
	

    /**
     * do nothing
     */
    public function addLayoutComponent(comp:Component, constraints:Object):Void{
    }

    /**
     * do nothing
     */
    public function removeLayoutComponent(comp:Component):Void{
    }
	
    public function preferredLayoutSize(target:Container):Dimension{
    	var viewSize:Dimension = getViewSize();
    	var rowCount:Number = getVisibleRowCount();
    	if(rowCount > 0){
	    	var rowHeight:Number = 20;
	    	if(getCellFactory().isAllCellHasSameHeight()){
	    		rowHeight = getCellFactory().getCellHeight();
	    	}
    		viewSize.height = rowCount * rowHeight;
    	}
    	var cellWidth:Number = getVisibleCellWidth();
    	if(cellWidth > 0){
    		viewSize.width = cellWidth;
    	}
    	return getInsets().getOutsideSize(viewSize);
    }

    public function minimumLayoutSize(target:Container):Dimension{
    	return getInsets().getOutsideSize();
    }
	
    public function maximumLayoutSize(target:Container):Dimension{
    	return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
    }
    
    /**
     * position and fill cells here
     */
    public function layoutContainer(target:Container):Void{
    	var factory:ListCellFactory = getCellFactory();
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
    
    private function layoutWhenShareCells():Void{
    	checkCreateCellsWhenShareCells();
    	
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		var ir:Rectangle = getInsets().getInsideBounds(getSize().getBounds());
    	var cellWidth:Number = ir.width;
    	
    	restrictionViewPos(viewPosition);
		var x:Number = Math.round(viewPosition.x);//avoid float to make cell graphics clean
		var y:Number = Math.round(viewPosition.y);
		var ih:Number = factory.getCellHeight();
		var startIndex:Number = Math.floor(y/ih);
		var startY:Number = startIndex*ih - y;
		var listSize:Number = m.getSize();
		
		var cx:Number = ir.x - x;
		var cy:Number = ir.y + startY;
		var maxY:Number = ir.y + ir.height;
		for(var i:Number = 0; i<cells.getSize(); i++){
			var cell:ListCell = ListCell(cells.get(i));
			var ldIndex:Number = startIndex + i;
			var cellCom:Component = cell.getCellComponent();
			if(ldIndex < listSize){
				cell.setCellValue(m.getElementAt(ldIndex));
				cellCom.setVisible(true);
				cellCom.setBoundsImmediately(cx, cy, cellWidth, ih);
				if(cy < maxY){
					lastVisibleIndex = ldIndex;
				}
				cy += ih;
				cell.setListCellStatus(this, isSelectedIndex(ldIndex), ldIndex);
			}else{
				cellCom.setVisible(false);
			}
		}
		firstVisibleIndex = startIndex;
    }
    
    private function layoutWhenNotShareCellsAndSameHeight():Void{
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		var ir:Rectangle = getInsets().getInsideBounds(getSize().getBounds());
    	var cellWidth:Number = Math.max(ir.width, viewWidth);
    	
    	restrictionViewPos(viewPosition);
		var x:Number = Math.round(viewPosition.x);//avoid float to make cell graphics clean
		var y:Number = Math.round(viewPosition.y);
		var ih:Number = factory.getCellHeight();
		var startIndex:Number = Math.floor(y/ih);
		var listSize:Number = m.getSize();
		var endIndex:Number = startIndex + Math.floor(ir.height / ih) + 2 - 1;
		if(endIndex >= listSize){
			endIndex = listSize - 1;
		}
		var startY:Number = startIndex*ih - y;
		
		var cx:Number = ir.x - x;
		var cy:Number = ir.y + startY;
		var maxY:Number = ir.y + ir.height;
		//invisible last viewed
		for(var i:Number=firstVisibleIndex+firstVisibleIndexOffset; i<startIndex; i++){
			ListCell(cells.get(i)).getCellComponent().setVisible(false);
		}
		var rlvi:Number = lastVisibleIndex + lastVisibleIndexOffset;
		for(var i:Number=endIndex+1; i<=rlvi; i++){
			ListCell(cells.get(i)).getCellComponent().setVisible(false);
		}
		
		//visible current needed
		for(var i:Number=startIndex; i<=endIndex; i++){
			var cell:ListCell = ListCell(cells.get(i));
			var cellCom:Component = cell.getCellComponent();
			cellCom.setVisible(true);
			var s:Dimension = getCachedCellPreferSize(cell);
			if(s == null){
				s = cell.getCellComponent().getPreferredSize();
				trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
			}
			var finalWidth:Number = isTracksWidth() ? ir.width : Math.max(cellWidth, s.width);
			cellCom.setBoundsImmediately(cx, cy, finalWidth, ih);
			if(cy < maxY){
				lastVisibleIndex = i;
			}
			cy += ih;
			cell.setListCellStatus(this, isSelectedIndex(i), i);
		}
		firstVisibleIndex = startIndex;
		firstVisibleIndexOffset = lastVisibleIndexOffset = 0;
    }
    
    private function getCachedCellPreferSize(cell:ListCell):Dimension{
    	return Dimension(cellPrefferSizes.get(cell.getCellComponent().getID()));
    }
    
    private function layoutWhenNotShareCellsAndNotSameHeight():Void{
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		var ir:Rectangle = getInsets().getInsideBounds(getSize().getBounds());
    	var cellWidth:Number = Math.max(ir.width, viewWidth);
    	
    	restrictionViewPos(viewPosition);
		var x:Number = Math.round(viewPosition.x);//avoid float to make cell graphics clean
		var y:Number = Math.round(viewPosition.y);
		
		var startIndex:Number = 0;
		var cellsCount:Number = cells.getSize();
		
		var tryY:Number = 0;
		var startY:Number = 0;
		for(var i:Number=0; i<cellsCount; i++){
			var cell:ListCell = ListCell(cells.get(i));
			var s:Dimension = getCachedCellPreferSize(cell);
			if(s == null){
				s = cell.getCellComponent().getPreferredSize();
				trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
			}
			tryY += s.height;
			if(tryY >= y){
				startIndex = i;
				startY = -(s.height - (tryY - y));
				break;
			}
		}
		
		var listSize:Number = m.getSize();
		var cx:Number = ir.x - x;
		var cy:Number = ir.y + startY;
		var maxY:Number = ir.y + ir.height;
		var tempLastVisibleIndex:Number = -1;
		//visible current needed
		var endIndex:Number = startIndex;
		for(var i:Number=startIndex; i<cellsCount; i++){
			var cell:ListCell = ListCell(cells.get(i));
			var cellCom:Component = cell.getCellComponent();
			var s:Dimension = getCachedCellPreferSize(cell);
			if(s == null){
				s = cell.getCellComponent().getPreferredSize();
				trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
			}
			cell.setListCellStatus(this, isSelectedIndex(i), i);
			cellCom.setVisible(true);
			var finalWidth:Number = isTracksWidth() ? ir.width : Math.max(cellWidth, s.width);
			cellCom.setBoundsImmediately(cx, cy, finalWidth, s.height);
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
		for(var i:Number=firstVisibleIndex+firstVisibleIndexOffset; i<startIndex; i++){
			ListCell(cells.get(i)).getCellComponent().setVisible(false);
		}
		var rlvi:Number = lastVisibleIndex + lastVisibleIndexOffset;
		for(var i:Number=endIndex+1; i<=rlvi; i++){
			ListCell(cells.get(i)).getCellComponent().setVisible(false);
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

    public function invalidateLayout(target:Container):Void{
    }
	
	//------------------------ListMode Listener Methods-----------------
	
	/**
	 * data in list has changed, update JList if needed.
	 */
    public function intervalAdded(e:ListDataEvent):Void{
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		
		var w:Number = viewWidth;
		var h:Number = viewHeight;
		var sameHeight:Boolean = factory.isAllCellHasSameHeight();
		
		var i0:Number = e.getIndex0();
		var i1:Number = e.getIndex1();
		i0 = Math.min(i0, i1);
		i1 = Math.max(i0, i1);
		
		if(factory.isShareCells()){
			w = getPreferredCellWidthWhenNoCount();
			h = m.getSize()*factory.getCellHeight();
		}else{
			for(var i:Number=i0; i<=i1; i++){
				var cell:ListCell = createNewCell();
				cells.append(cell, i);
				cell.setCellValue(m.getElementAt(i));
				addCellToContainer(cell);
				var s:Dimension = cell.getCellComponent().getPreferredSize();
				cell.getCellComponent().setVisible(false);
				cellPrefferSizes.put(cell.getCellComponent().getID(), s);
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
				lastVisibleIndexOffset += i1 - i0 + 1;
			}else if(i0 < firstVisibleIndex + firstVisibleIndexOffset){
				firstVisibleIndexOffset += lastVisibleIndexOffset = i1 - i0 + 1;
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
    public function intervalRemoved(e:ListDataEvent):Void{
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		
		var w:Number = viewWidth;
		var h:Number = viewHeight;
		var sameHeight:Boolean = factory.isAllCellHasSameHeight();
		
		var i0:Number = e.getIndex0();
		var i1:Number = e.getIndex1();
		i0 = Math.min(i0, i1);
		i1 = Math.max(i0, i1);
		
		if(factory.isShareCells()){
			w = getPreferredCellWidthWhenNoCount();
			h = m.getSize()*factory.getCellHeight();
		}else{
			var needRecountWidth:Boolean = false;
			for(var i:Number=i0; i<=i1; i++){
				var cell:ListCell = ListCell(cells.get(i));
				if(cell == maxWidthCell){
					needRecountWidth = true;
				}
				if(!sameHeight){
					var s:Dimension = getCachedCellPreferSize(cell);
					if(s == null){
						s = cell.getCellComponent().getPreferredSize();
						trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
					}
					h -= s.height;
				}
				removeCellFromeContainer(cell);
				cellPrefferSizes.remove(cell.getCellComponent().getID());
			}
			cells.removeRange(i0, i1);
			if(sameHeight){
				h = m.getSize()*factory.getCellHeight();
			}
			if(needRecountWidth){
				w = 0;
				for(var i:Number=cells.getSize()-1; i>=0; i--){
					var cell:ListCell = ListCell(cells.get(i));
					var s:Dimension = getCachedCellPreferSize(cell);
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
				lastVisibleIndexOffset -= i1 - i0 + 1;
			}else if(i0 < firstVisibleIndex + firstVisibleIndexOffset){
				firstVisibleIndexOffset -= lastVisibleIndexOffset = i1 - i0 + 1;
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
    public function contentsChanged(e:ListDataEvent):Void{
    	var factory:ListCellFactory = getCellFactory();
		var m:ListModel = getModel();
		
		var w:Number = viewWidth;
		var h:Number = viewHeight;
		var sameHeight:Boolean = factory.isAllCellHasSameHeight();
		
		var i0:Number = e.getIndex0();
		var i1:Number = e.getIndex1();
		i0 = Math.min(i0, i1);
		i1 = Math.max(i0, i1);
		
		if(factory.isShareCells()){
			w = getPreferredCellWidthWhenNoCount();
			h = m.getSize()*factory.getCellHeight();
		}else{
			var needRecountWidth:Boolean = false;
			for(var i:Number=i0; i<=i1; i++){
				var newValue:Object = m.getElementAt(i);
				var cell:ListCell = ListCell(cells.get(i));
				var s:Dimension = getCachedCellPreferSize(cell);
				if(s == null){
					s = cell.getCellComponent().getPreferredSize();
					trace("Warnning : cell size not cached index = " + i + ", value = " + cell.getCellValue());
				}
				if(cell == maxWidthCell){
					h -= s.height;
					cell.setCellValue(newValue);
					var ns:Dimension = cell.getCellComponent().getPreferredSize();
					cellPrefferSizes.put(cell.getCellComponent().getID(), ns);
					if(ns.width < s.width){
						needRecountWidth = true;
					}else{
						w = ns.width;
					}
					h += ns.height;
				}else{
					h -= s.height;
					cell.setCellValue(newValue);
					var ns:Dimension = cell.getCellComponent().getPreferredSize();
					cellPrefferSizes.put(cell.getCellComponent().getID(), ns);
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
				for(var i:Number=cells.getSize()-1; i>=0; i--){
					var cell:ListCell = ListCell(cells.get(i));
					var s:Dimension = getCachedCellPreferSize(cell);
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
        
    private function __selectionListener(source:ListSelectionModel, firstIndex:Number, lastIndex:Number):Void{
    	dispatchEvent(createEventObj(ON_SELECTION_CHANGED, firstIndex, lastIndex));
    	revalidate();
    }
    //-------------------------------Event Listener For All Items----------------
    
	private function initItemHandler():Void{
		itemHandler = new Object();
		itemHandler[Component.ON_PRESS] = Delegate.create(this, ____onItemPress);
		itemHandler[Component.ON_RELEASE] = Delegate.create(this, ____onItemRelease);
		itemHandler[Component.ON_RELEASEOUTSIDE] = Delegate.create(this, ____onItemReleaseOutside);
		itemHandler[Component.ON_ROLLOVER] = Delegate.create(this, ____onItemRollOver);
		itemHandler[Component.ON_ROLLOUT] = Delegate.create(this, ____onItemRollOut);
		itemHandler[Component.ON_CLICKED] = Delegate.create(this, ____onItemClicked);
	}
	
	private function createItemEventObj(source:Component, type:String):Event{
		var cell:ListCell = getCellByCellComponent(source);
		var event:Event = createEventObj(type, cell.getCellValue(), cell);
		return event;
	}
	
	private function getItemIndexByCellComponent(item:Component):Number{
		var cell:ListCell = comToCellMap.get(item.getID());
		return getItemIndexByCell(cell);
	}
	
	/**
	 * Returns the index of the cell.
	 */
	public function getItemIndexByCell(cell:ListCell):Number{
		if(getCellFactory().isShareCells()){
			return firstVisibleIndex + cells.indexOf(cell);
		}else{
			return cells.indexOf(cell);
		}
	}
	
	private function getCellByCellComponent(item:Component):ListCell{
		return ListCell(comToCellMap.get(item.getID()));
	}
	
	/**
	 * Returns the cell of the specified index
	 */
	public function getCellByIndex(index:Number):ListCell{
		if(getCellFactory().isShareCells()){
			return ListCell(cells.get(index - firstVisibleIndex));
		}else{
			return ListCell(cells.get(index));
		}
	}
	
	
    /**
     * Event Listener For All Items
     */
	private function __onItemPress(source:Component):Void{
		dispatchEvent(createItemEventObj(source, ON_ITEM_PRESS));
		parent.__onChildPressed(this);
		requestFocus();
	}
		
    /**
     * Event Listener For All Items
     */	
	private function __onItemRelease(source:Component):Void{
		dispatchEvent(createItemEventObj(source, ON_ITEM_RELEASE));
	}
	
    /**
     * Event Listener For All Items
     */	
	private function __onItemReleaseOutside(source:Component):Void{
		dispatchEvent(createItemEventObj(source, ON_ITEM_RELEASEOUTSIDE));
	}
	
    /**
     * Event Listener For All Items
     */	
	private function __onItemRollOver(source:Component):Void{
		dispatchEvent(createItemEventObj(source, ON_ITEM_ROLLOVER));
	}
	
    /**
     * Event Listener For All Items
     */	
	private function __onItemRollOut(source:Component):Void{
		dispatchEvent(createItemEventObj(source, ON_ITEM_ROLLOUT));
	}
	
    /**
     * Event Listener For All Items
     */	
	private function __onItemClicked(source:Component, clickCount:Number):Void{
		var cell:ListCell = getCellByCellComponent(source);
		var event:Event = createEventObj(ON_ITEM_CLICKED, cell.getCellValue(), cell, clickCount);
		dispatchEvent(event);
	}
	

    /**
     * Don't override this, you can override __onItemPress instead
     */
	private function ____onItemPress(source:Component):Void{
		__onItemPress(source);
	}
	
    /**
     * Don't override this, you can override __onItemRelease instead
     */	
	private function ____onItemRelease(source:Component):Void{
		__onItemRelease(source);
	}
	
    /**
     * Don't override this, you can override __onItemReleaseOutside instead
     */	
	private function ____onItemReleaseOutside(source:Component):Void{
		__onItemReleaseOutside(source);
	}
	
    /**
     * Don't override this, you can override __onItemRollOver instead
     */	
	private function ____onItemRollOver(source:Component):Void{
		__onItemRollOver(source);
	}
	
    /**
     * Don't override this, you can override __onItemRollOut instead
     */	
	private function ____onItemRollOut(source:Component):Void{
		__onItemRollOut(source);
	}
	
    /**
     * Don't override this, you can override __onItemClicked instead
     */	
	private function ____onItemClicked(source:Component, clickCount:Number):Void{
		__onItemClicked(source, clickCount);
	}	
	
	//-------------------------------Drag and Drop---------------------------------

	private var dndAutoScrollTimer:Timer;	
	private var dnd_line_mc:MovieClip;
	
	private function __onDragRecognized(dragInitiator:Component, touchedChild:Component):Void{
		if(isAutoDnDInitiatorAllown()){
			var data:Array = getSelectedIndices();
			var sourceData:ListSourceData = new ListSourceData("ListSourceData", data);
			
			var firstIndex:Number = getFirstVisibleIndex();
			var lastIndex:Number = getLastVisibleIndex();
			var mp:Point = getMousePosition();
			var ib:Rectangle = new Rectangle();
			var offsetY:Number = mp.y;
			for(var i:Number=firstIndex; i<=lastIndex; i++){
				ib = getCellByIndex(i).getCellComponent().getBounds(ib);
				if(mp.y < ib.y + ib.height){
					offsetY = ib.y;
					break;
				}
			}
			
			DragManager.startDrag(this, sourceData, new ListDragImage(this, offsetY));
		}
	}
	private function __onDragEnter(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		dndInsertPosition = -1;
		if(!(isAcceptableListSourceData(dragInitiator, sourceData) && isAutoDnDDropTargetAllown())){
			DragManager.getCurrentDragImage().switchToRejectImage();
		}else{
			DragManager.getCurrentDragImage().switchToAcceptImage();
			checkStartDnDAutoScroll();
		}
	}
	private function __onDragOverring(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		if(isAcceptableListSourceData(dragInitiator, sourceData) && isAutoDnDDropTargetAllown()){
			checkStartDnDAutoScroll();
			drawInsertLine();
		}
	}
	private function __onDragExit(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		checkStopDnDAutoScroll();
	}
	private function __onDragDrop(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		checkStopDnDAutoScroll();
		if(isAcceptableListSourceData(dragInitiator, sourceData) && isAutoDnDDropTargetAllown()){
			if(dndInsertPosition >= 0){
				var indices:Array = (ListSourceData(sourceData)).getItemIndices();
				if(indices == null || indices.length == null || indices.length <= 0){
					return;
				}
				var initiator:JList = JList(dragInitiator);
				var items:Array = new Array(indices.length);
				for(var i:Number=0; i<indices.length; i++){
					items[i] = initiator.getModel().getElementAt(indices[i]);
				}
				var insertOffset:Number = 0;
				if(initiator.getAutoDragAndDropType() == DND_MOVE){
					var imm:MutableListModel = MutableListModel(initiator.getModel());
					var sameModel:Boolean = (imm == getModel());
					for(var i:Number=0; i<indices.length; i++){
						var rindex:Number = indices[i];
						imm.removeElementAt(rindex-i);
						if(sameModel && rindex<dndInsertPosition){
							insertOffset ++;
						}
					}
				}
				var index:Number = dndInsertPosition - insertOffset;
				var mm:MutableListModel = MutableListModel(getModel());
				for(var i:Number=0; i<items.length; i++){
					mm.insertElementAt(items[i], index);
					index++;
				}
				return;
			}
		}
		DragManager.setDropMotion(DragManager.DEFAULT_REJECT_DROP_MOTION);
	}
	
	/**
	 * Returns is the source data is acceptale to drop in this list as build-in support
	 */
	public function isAcceptableListSourceData(dragInitiator:Component, sd:SourceData):Boolean{
		return (sd instanceof ListSourceData) && isDragAcceptableInitiator(dragInitiator);
	}
	/**
	 * Returns is the model is mutable
	 */
	public function isMutableModel():Boolean{
		return getModel() instanceof MutableListModel;
	}
	
	private function checkStartDnDAutoScroll():Void{
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
	private function checkStopDnDAutoScroll():Void{
		if(dndAutoScrollTimer != null){
			dndAutoScrollTimer.stop();
		}
		if(dnd_line_mc != null){
			dnd_line_mc.removeMovieClip();
			dnd_line_mc = null;
		}
	}
	
	private function __dndAutoScroll():Void{
		var lastCellBounds:Rectangle = getCellByIndex(getLastVisibleIndex()).getCellComponent().getBounds();
		var firstCellBounds:Rectangle = getCellByIndex(getFirstVisibleIndex()).getCellComponent().getBounds();
		var vp:Point = getViewPosition();
		var mp:Point = getMousePosition();
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
	
	private var dndInsertPosition:Number;
	private function drawInsertLine():Void{
		var firstIndex:Number = getFirstVisibleIndex();
		var lastIndex:Number = getLastVisibleIndex();
		
		var mp:Point = getMousePosition();
		var ib:Rectangle = new Rectangle();
		var insertIndex:Number = -1;
		var insertY:Number;
		for(var i:Number=firstIndex; i<=lastIndex; i++){
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
	
	
	private function ____onDragRecognized(dragInitiator:Component, touchedChild:Component):Void{
		__onDragRecognized(dragInitiator, touchedChild);
	}
	private function ____onDragEnter(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		__onDragEnter(source, dragInitiator, sourceData, mousePos);
	}
	private function ____onDragOverring(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		__onDragOverring(source, dragInitiator, sourceData, mousePos);
	}
	private function ____onDragExit(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		__onDragExit(source, dragInitiator, sourceData, mousePos);
	}
	private function ____onDragDrop(source:Component, dragInitiator:Component, sourceData:SourceData, mousePos:Point):Void{
		__onDragDrop(source, dragInitiator, sourceData, mousePos);
	}	
}
