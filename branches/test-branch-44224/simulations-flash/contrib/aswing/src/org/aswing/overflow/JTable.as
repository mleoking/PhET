/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.CellEditor;
import org.aswing.ChildrenMaskedContainer;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.DefaultCheckBoxCellEditor;
import org.aswing.DefaultListSelectionModel;
import org.aswing.DefaultNumberTextFieldCellEditor;
import org.aswing.DefaultTextFieldCellEditor;
import org.aswing.event.CellEditorListener;
import org.aswing.event.TableModelEvent;
import org.aswing.event.TableModelListener;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.Insets;
import org.aswing.LayoutManager;
import org.aswing.ListSelectionModel;
import org.aswing.plaf.TableUI;
import org.aswing.RepaintManager;
import org.aswing.table.DefaultTableColumnModel;
import org.aswing.table.DefaultTableModel;
import org.aswing.table.DefaultTextCell;
import org.aswing.table.GeneralTableCellFactoryUIResource;
import org.aswing.table.JTableHeader;
import org.aswing.table.Resizable2;
import org.aswing.table.Resizable2Imp1;
import org.aswing.table.Resizable3;
import org.aswing.table.Resizable3Imp1;
import org.aswing.table.Resizable3Imp2;
import org.aswing.table.TableCell;
import org.aswing.table.TableCellEditor;
import org.aswing.table.TableCellFactory;
import org.aswing.table.TableColumn;
import org.aswing.table.TableColumnModel;
import org.aswing.table.TableColumnModelEvent;
import org.aswing.table.TableColumnModelListener;
import org.aswing.table.TableModel;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.HashMap;
import org.aswing.Viewportable;

/**
 * The <code>JTable</code> is used to display and edit regular two-dimensional tables
 * of cells.
 * <p>
 * The <code>JTable</code> has many
 * facilities that make it possible to customize its rendering and editing
 * but provides defaults for these features so that simple tables can be
 * set up easily.  For example, to set up a table with 10 rows and 10
 * columns of numbers:
 * <p>
 * <pre>
 *      class MyTableModel extends AbstractTableModel{
 *          public function getColumnCount():Number { return 10; }
 *          public function getRowCount():Number { return 10;}
 *          public getValueAt(row:Number, col:Number) { return row*col; }
 *      };
 *      var dataModel:MyTableModel = new MyTableModel();
 *      var table:JTable = new JTable(dataModel);
 *      var scrollpane:JScrollPane = new JScrollPane(table);
 * </pre>
 * <p>
 * Note that if you wish to use a <code>JTable</code> in a standalone
 * view (outside of a <code>JScrollPane</code>) and want the header
 * displayed, you can get it using {@link #getTableHeader} and
 * display it separately.
 * <p>
 * When designing applications that use the <code>JTable</code> it is worth paying
 * close attention to the data structures that will represent the table's data.
 * The <code>DefaultTableModel</code> is a model implementation that
 * uses a <code>Vector</code> of <code>Vector</code>s of <code>Object</code>s to
 * store the cell values. As well as copying the data from an
 * application into the <code>DefaultTableModel</code>,
 * it is also possible to wrap the data in the methods of the
 * <code>TableModel</code> interface so that the data can be passed to the
 * <code>JTable</code> directly, as in the example above. This often results
 * in more efficient applications because the model is free to choose the
 * internal representation that best suits the data.
 * A good rule of thumb for deciding whether to use the <code>AbstractTableModel</code>
 * or the <code>DefaultTableModel</code> is to use the <code>AbstractTableModel</code>
 * as the base class for creating subclasses and the <code>DefaultTableModel</code>
 * when subclassing is not required.
 * <p>
 * The <code>JTable</code> uses integers exclusively to refer to both the rows and the columns
 * of the model that it displays. The <code>JTable</code> simply takes a tabular range of cells
 * and uses <code>getValueAt(int, int)</code> to retrieve the
 * values from the model during painting.
 * <p>
 * By default, columns may be rearranged in the <code>JTable</code> so that the
 * view's columns appear in a different order to the columns in the model.
 * This does not affect the implementation of the model at all: when the
 * columns are reordered, the <code>JTable</code> maintains the new order of the columns
 * internally and converts its column indices before querying the model.
 * <p>
 * To listen table row selection change event, by <code>addSelectionListener()</code>
 * To listen column selection see {@link org.aswing.table.TableColumnModel#addColumnModelListener} <br>
 * To listen other Table events see {@link org.aswing.table.TableModel#addTableModelListener}
 * @author iiley
 */
class org.aswing.overflow.JTable extends ChildrenMaskedContainer implements Viewportable, TableModelListener,
	TableColumnModelListener, CellEditorListener, LayoutManager{
	
	/**
	 * When the table row selection changed.<br>
	 * onSelectionChanged(source:JTable, firstIndex:Number, lastIndex:Number)<br>
	 * firstIndex the first selection changed index<br>
	 * lastIndex the last selection changed index
	 * <p>
	 * To listen other Table events see {@link org.aswing.table.TableModel#addTableModelListener}
	 * @see #ON_COLUMN_SELECTION_CHANGED
	 */	
	public static var ON_SELECTION_CHANGED:String = DefaultListSelectionModel.ON_SELECTION_CHANGED;
	
	/**
	 * When the table column selection changed.<br>
	 * onColumnSelectionChanged(source:JTable, firstIndex:Number, lastIndex:Number)<br>
	 * firstIndex the first selection changed index<br>
	 * lastIndex the last selection changed index
	 * <p>
	 * To listen other Table events see {@link org.aswing.table.TableModel#addTableModelListener}
	 * @see #ON_SELECTION_CHANGED
	 */	
	public static var ON_COLUMN_SELECTION_CHANGED:String = "onColumnSelectionChanged";
	
	/**
	 * When the table's state changed.
	 *<br>
	 * onStateChanged(source:JTable)
	 */	
	public static var ON_STATE_CHANGED:String = "onStateChanged";//Component.ON_STATE_CHANGED; 
	
	//-------------------------------------------------------------------------
	// Editing Events definition (Credit for Romain Ecarnot)
	//-------------------------------------------------------------------------
	
	/**
	 * When the current cell is edited.<br>
	 * onTableCellEditingStarted( source : JTable, cellRow : Number, cellColumn : Number, cellValue )<br>
	 * cellRow the current cell's row index<br>
	 * cellColumn the current cell's column index<br>
	 * cellValue the current cell value ( before edition )<br>
	 */	
	public static var ON_CELL_EDITING_STARTED : String = "onTableCellEditingStarted";
	
	/**
	 * When the current cell edition is canceled.<br>
	 * onTableCellEditingCanceled( source : JTable, cellRow : Number, cellColumn : Number )<br>
	 * cellRow the current cell's row index<br>
	 * cellColumn the current cell's column index<br>
	 */	
	public static var ON_CELL_EDITING_CANCELED : String = "onTableCellEditingCanceled";
	
	/**
	 * When the current cell edition is finished.<br>
	 * onTableCellEditingStopped( source : JTable, cellRow : Number, cellColumn : Number, newValue, oldValue )<br>
	 * cellRow the current cell's row index<br>
	 * cellColumn the current cell's column index<br>
	 * newValue the new cell value<br>
	 * oldValue the old cell value ( before edition )<br>
	 */	
	public static var ON_CELL_EDITING_STOPPED : String = "onTableCellEditingStopped";
	
	private static var uiClassID:String = "TableUI";
	
	/**
	 * Do not adjust column widths automatically; use a scrollbar. 
	 */
	public static var AUTO_RESIZE_OFF:Number = 0;
	/** 
	 * When a column is adjusted in the UI, adjust the next column the opposite way.
	 */
	public static var AUTO_RESIZE_NEXT_COLUMN:Number = 1;
	/**
	 * During UI adjustment, change subsequent columns to preserve the total width;
	 * this is the default behavior. 
	 */
	public static var AUTO_RESIZE_SUBSEQUENT_COLUMNS:Number = 2;
	/**
	 * During all resize operations, apply adjustments to the last column only.
	 */
	public static var AUTO_RESIZE_LAST_COLUMN:Number = 3;
	/**
	 * During all resize operations, proportionately resize all columns.
	 */
	public static var AUTO_RESIZE_ALL_COLUMNS:Number = 4;
		
	/**
	 * Only can select one most item at a time.
	 */
	public static var SINGLE_SELECTION:Number = 0;
	/**
	 * Can select any item at a time.
	 */
	public static var MULTIPLE_SELECTION:Number = 1;
	
	private var dataModel:TableModel;
	
	private var columnModel:TableColumnModel;
	private var selectionModel:ListSelectionModel;
	private var selectionModelListener:Object;
	
	private var tableHeader:JTableHeader;
	private var rowCells:Array;
	private var rowHeight:Number;
	private var rowMargin:Number;
	private var gridColor:ASColor;
	private var showHorizontalLines:Boolean;
	private var showVerticalLines:Boolean;
	private var autoResizeMode:Number;
	private var autoCreateColumnsFromModel:Boolean;
	private var preferredViewportSize:Dimension;
	private var rowSelectionAllowed:Boolean;
	private var cellSelectionEnabled:Boolean;
	private var cellEditor:TableCellEditor;
	private var editingColumn:Number;
	private var editingRow:Number;
	private var defaultRenderersByColumnClass:HashMap;
	private var defaultEditorsByColumnClass:HashMap;
	private var selectionForeground:ASColor;
	private var selectionBackground:ASColor;
	private var surrendersFocusOnKeystroke:Boolean;
	private var columnSelectionAdjusting:Boolean;
	private var rowSelectionAdjusting:Boolean;
	
	//viewport
	private var viewPosition:Point;
	private var verticalUnitIncrement:Number;
	private var verticalBlockIncrement:Number;
	private var horizontalUnitIncrement:Number;
	private var horizontalBlockIncrement:Number;
	/** Stored cell value before any edition. */
	private var _storedValue : Object;

	/**
	 * <pre>
	 * JTable()
	 * JTable(rowData:Array, columnNames:Array)
	 * JTable(numRows:Number, numColumns:Number)
	 * JTable(dm:TableModel, cm:TableColumnModel, sm:ListSelectionModel)
	 * JTable(dm:TableModel, cm:TableColumnModel)
	 * JTable(dm:TableModel)
	 * </pre>
	 * Constructs a default <code>JTable</code>.
	 * 
	 * @see #initWithEmptyParam()
	 * @see #initWithDataNames()
	 * @see #initWithRowColumn()
	 * @see #initWithModels()
	 */
	public function JTable() {
		super();
		setName("JTable");
		if(arguments[0] instanceof TableModel){
			initWithModels(arguments[0], arguments[1], arguments[2]);
		}else if(arguments[0] instanceof Number || typeof(arguments[0]) == "number"){
			if(arguments[1] != null){
				initWithRowColumn(arguments[0], arguments[1]);
			}else{
				trace("Counstruct JTable parameters warning : " + arguments);
				initWithEmptyParam();
			}
		}else if(arguments.length == 2){
			if(arguments[0] instanceof Array && arguments[1] instanceof Array){
				initWithDataNames(arguments[0], arguments[1]);
			}else{
				trace("Counstruct JTable parameters warning : " + arguments);
				initWithEmptyParam();
			}
		}else{
			initWithEmptyParam();
		}
	}

	/**
	 * Initializes with a default data model, a default column model, and a default selection
	 * model.
	 *
	 * @see #createDefaultDataModel()
	 * @see #createDefaultColumnModel()
	 * @see #createDefaultSelectionModel()
	 */	
	private function initWithEmptyParam():Void{
		initWithModels(null, null, null);
	}
	
	/**
	 * Initializes the <code>JTable</code> to display the values in the two dimensional array,
	 * <code>rowData</code>, with column names, <code>columnNames</code>.
	 * <code>rowData</code> is an array of rows, so the value of the cell at row 1,
	 * column 5 can be obtained with the following code:
	 * <p>
	 * <pre> rowData[1][5]; </pre>
	 * <p>
	 * All rows must be of the same length as <code>columnNames</code>.
	 * <p>
	 * @param rowData           the data for the new table
	 * @param columnNames       names of each column
	 */
	private function initWithDataNames(rowData:Array, columnNames:Array):Void{
		initWithModels((new DefaultTableModel()).initWithDataNames(rowData, columnNames));
	}
	
	/**
	 * Initializes the <code>JTable</code> with <code>numRows</code>
	 * and <code>numColumns</code> of empty cells using
	 * <code>DefaultTableModel</code>.  The columns will have
	 * names of the form "A", "B", "C", etc.
	 *
	 * @param numRows           the number of rows the table holds
	 * @param numColumns        the number of columns the table holds
	 * @see org.aswing.table.DefaultTableModel
	 */
	private function initWithRowColumn(numRows:Number, numColumns:Number):Void{
		initWithModels((new DefaultTableModel()).initWithRowcountColumncount(numRows, numColumns));
	}
	

	/**
	 * <pre>
	 * initWithModels(dm:TableModel, cm:TableColumnModel, sm:ListSelectionModel):JTable
	 * initWithModels(dm:TableModel, cm:TableColumnModel):JTable
	 * initWithModels(dm:TableModel):JTable
	 * initWithModels():JTable
	 * </pre>
	 * <p>
	 * Initializes the <code>JTable</code> with
	 * <code>dm</code> as the data model, <code>cm</code> as the
	 * column model, and <code>sm</code> as the selection model.
	 * If any of the parameters are <code>null/undefined</code> this method
	 * will initialize the table with the corresponding default model.
	 * The <code>autoCreateColumnsFromModel</code> flag is set to false
	 * if <code>cm</code> is non-null, otherwise it is set to true
	 * and the column model is populated with suitable
	 * <code>TableColumns</code> for the columns in <code>dm</code>.
	 *
	 * @param dm		the data model for the table
	 * @param cm		the column model for the table
	 * @param sm		the row selection model for the table
	 * @see #createDefaultDataModel()
	 * @see #createDefaultColumnModel()
	 * @see #createDefaultSelectionModel()
	 */	
	private function initWithModels(dm:TableModel, cm:TableColumnModel, sm:ListSelectionModel):Void{
		setLayout(this);
		
		rowCells = new Array();
		
		viewPosition = new Point();
		
		selectionModelListener = new Object();
		selectionModelListener[ON_SELECTION_CHANGED] = Delegate.create(this, __listSelectionChanged);
		
		if (cm == null){
			cm = createDefaultColumnModel();
			autoCreateColumnsFromModel = true;
		}
		//trace("cm = " + cm);
		setColumnModel(cm);
		if (sm == null){
			sm = createDefaultSelectionModel();
		}
		//trace("sm = " + sm);
		setSelectionModel(sm);
		if (dm == null){
			dm = createDefaultDataModel();
		}
		//trace("dm = " + dm);
		setModel(dm);
		initializeLocalVars();
		updateUI();
	}
	
	//****************************************
	//           Managing TableUI
	//****************************************
	
	/**
	 * Returns the L&F object that renders this component.
	 *
	 * @return the <code>TableUI</code> object that renders this component
	 */
	public function getUI():TableUI {
		return TableUI(ui);
	}

	/**
	 * Sets the L&F object that renders this component and repaints.
	 *
	 * @param ui  the TableUI L&F object
	 * @see UIDefaults#getUI()
	 */
	public function setUI(ui:TableUI):Void {
		if (this.ui != ui) {
			super.setUI(ui);
		    repaint();
		}
	}	
	


	private function updateSubComponentUI(componentShell):Void {
		if (componentShell == null) {
			return;
		}
		var component:Component = null;
		if (componentShell instanceof Component) {
			component = Component(componentShell);
			component.updateUI();
		}else if (componentShell instanceof CellEditor) {
		   ((CellEditor(componentShell))).updateUI();
		}
	}

	/**
	 * Notification from the <code>UIManager</code> that the L&F has changed.
	 * Replaces the current UI object with the latest version from the
	 * <code>UIManager</code>.
	 *
	 * @see org.aswing.Component#updateUI
	 */
	public function updateUI():Void {
		// Update the UIs of the cell editors
		var cm:TableColumnModel = getColumnModel();
		for(var column:Number = 0; column < cm.getColumnCount(); column++) {
			var aColumn:TableColumn = cm.getColumn(column);
			updateSubComponentUI(aColumn.getCellEditor());
		}
		//Update the cells ui
		for(var i:Number=0; i<rowCells.length; i++){
			for(var j:Number=0; j<rowCells[i].length; j++){
				var cell:TableCell = rowCells[i][j];
				cell.getCellComponent().updateUI();
			}
		}

		// Update the UIs of all the default editors.
		var defaultEditors:Array = defaultEditorsByColumnClass.values();
		for(var i:Number=0; i<defaultEditors.length; i++){
			updateSubComponentUI(defaultEditors[i]);
		}

		// Update the UI of the table header
		if (tableHeader != null && tableHeader.getParent() == null) {
			tableHeader.updateUI();
		}
		
		setUI(TableUI(UIManager.getUI(this)));
		resizeAndRepaint();
	}

	/**
	 * Returns the suffix used to construct the name of the L&F class used to
	 * render this component.
	 *
	 * @return the string "TableUI"
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 */
	public function getUIClassID():String {
		return uiClassID;
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicTableUI;
    }
	
	
	//*****************************************************
	//		   Table Attributes
	//*****************************************************

	/**
	 * Sets the <code>tableHeader</code> working with this <code>JTable</code> to <code>newHeader</code>.
	 * It is legal to have a <code>null</code> <code>tableHeader</code>.
	 *
	 * @param   tableHeader  new tableHeader
	 * @see	 #getTableHeader()
	 */
	public function setTableHeader(tableHeader:JTableHeader):Void{
		if (this.tableHeader != tableHeader){
			var old:JTableHeader = this.tableHeader;
			if (old != null){
				old.setTable(null);
				old.removeFromContainer();
			}
			this.tableHeader = tableHeader;
			if (tableHeader != null){
				tableHeader.setTable(this);
				append(tableHeader);
			}
			//firePropertyChange("tableHeader", old, tableHeader);
		}
	}
	
	/**
	 * Returns the <code>tableHeader</code> used by this <code>JTable</code>.
	 *
	 * @return  the <code>tableHeader</code> used by this table
	 * @see	 #setTableHeader()
	 */	
	public function getTableHeader():JTableHeader{
		return tableHeader;
	}
	
	/**
	 * Sets the height, in pixels, of all cells to <code>rowHeight</code>,
	 * revalidates, and repaints.
	 * The height of the cells will be equal to the row height minus
	 * the row margin.
	 *
	 * @param   rowHeight new row height
	 * @exception Error	  if <code>rowHeight</code> is less than 1
     * @see     #getAllRowHeight()
     */	
	public function setRowHeight(rowHeight:Number):Void{
		if (rowHeight < 1){
			trace("Error : New row height less than 1");
			throw new Error("New row height less than 1");
		}
		var old:Number = this.rowHeight;
		this.rowHeight = rowHeight;
		resizeAndRepaint();
		//firePropertyChange("rowHeight", old, rowHeight);
	}
	
	/**
	 * Returns the height of a table row, in pixels.
	 * The default row height is 20.0.
	 *
	 * @return  the height in pixels of a table row
	 * @see #setAllRowHeight()
	 */	
	public function getRowHeight():Number{
		return rowHeight;
	}
		
	/**
	 * Sets the amount of empty space between cells in adjacent rows.
	 *
	 * @param  rowMargin  the number of pixels between cells in a row
	 * @see	 #getRowMargin()
	 */	
	public function setRowMargin(rowMargin:Number):Void{
		var old:Number = this.rowMargin;
		this.rowMargin = rowMargin;
		resizeAndRepaint();
		//firePropertyChange("rowMargin", old, rowMargin);
	}
	
	/**
	 * Gets the amount of empty space, in pixels, between cells. Equivalent to:
	 * <code>getIntercellSpacing().height</code>.
	 * @return the number of pixels between cells in a row
	 *
	 * @see	 #setRowMargin()
	 */	
	public function getRowMargin():Number{
		return rowMargin;
	}
	
	/**
	 * Sets the <code>rowMargin</code> and the <code>columnMargin</code> --
	 * the height and width of the space between cells -- to
	 * <code>intercellSpacing</code>.
	 *
	 * @param   intercellSpacing		a <code>Dimension</code>
	 *					specifying the new width
	 *					and height between cells
	 * @see	 #getIntercellSpacing()
	 */	
	public function setIntercellSpacing(intercellSpacing:Dimension):Void{
		setRowMargin(intercellSpacing.height);
		getColumnModel().setColumnMargin(intercellSpacing.width);
		resizeAndRepaint();
	}
	
	/**
	 * Returns the horizontal and vertical space between cells.
	 * The default spacing is (1, 1), which provides room to draw the grid.
	 *
	 * @return  the horizontal and vertical spacing between cells
	 * @see	 #setIntercellSpacing()
	 */	
	public function getIntercellSpacing():Dimension{
		return new Dimension(getColumnModel().getColumnMargin(), rowMargin);
	}
	
	/**
	 * Sets the color used to draw grid lines to <code>gridColor</code> and redisplays.
	 * The default color is look and feel dependent.
	 *
	 * @param  gridColor the new color of the grid lines
	 * @see #getGridColor()
	 */	
	public function setGridColor(gridColor:ASColor):Void{
		if (gridColor == null){
			trace("New color is null, Ignored");
			return;
		}
		var old:ASColor = this.gridColor;
		this.gridColor = gridColor;
		//firePropertyChange("gridColor", old, gridColor);
		repaint();
	}
	
	/**
	 * Returns the color used to draw grid lines.
	 * The default color is look and feel dependent.
	 *
	 * @return  the color used to draw grid lines
	 * @see	 #setGridColor()
	 */	
	public function getGridColor():ASColor{
		return gridColor;
	}
	
	/**
	 *  Sets whether the table draws grid lines around cells.
	 *  If <code>showGrid</code> is true it does; if it is false it doesn't.
	 *  There is no <code>getShowGrid</code> method as this state is held
	 *  in two variables -- <code>showHorizontalLines</code> and <code>showVerticalLines</code> --
	 *  each of which can be queried independently.
	 *
	 * @param   showGrid true if table view should draw grid lines
	 *
	 * @see #setShowVerticalLines()
	 * @see #setShowHorizontalLines()
	 */	
	public function setShowGrid(showGrid:Boolean):Void{
		setShowHorizontalLines(showGrid);
		setShowVerticalLines(showGrid);
		repaint();
	}
	
	/**
	 *  Sets whether the table draws horizontal lines between cells.
	 *  If <code>showHorizontalLines</code> is true it does; if it is false it doesn't.
	 *
	 * @param   showHorizontalLines	  true if table view should draw horizontal lines
	 * @see	 #getShowHorizontalLines()
	 * @see	 #setShowGrid()
	 * @see	 #setShowVerticalLines()
	 */	
	public function setShowHorizontalLines(showHorizontalLines:Boolean):Void{
		var old:Boolean = this.showHorizontalLines;
		this.showHorizontalLines = showHorizontalLines;
		//firePropertyChange("showHorizontalLines", old, showHorizontalLines);
		if(old != showHorizontalLines){
			repaint();
		}
	}
	
	/**
	 * Sets whether the table draws vertical lines between cells.
	 * If <code>showVerticalLines</code> is true it does; if it is false it doesn't.
	 *
	 * @param   showVerticalLines true if table view should draw vertical lines
	 * @see     #getShowVerticalLines()
	 * @see     #setShowGrid()
	 * @see     #setShowHorizontalLines()
	 */	
	public function setShowVerticalLines(showVerticalLines:Boolean):Void{
		var old:Boolean = this.showVerticalLines;
		this.showVerticalLines = showVerticalLines;
		//firePropertyChange("showVerticalLines", old, showVerticalLines);
		if(old != showVerticalLines){
			repaint();
		}
	}
	
	/**
	 * Returns true if the table draws horizontal lines between cells, false if it
	 * doesn't. The default is true.
	 *
	 * @return  true if the table draws horizontal lines between cells, false if it
	 *          doesn't
	 * @see	 #setShowHorizontalLines()
	 */	
	public function getShowHorizontalLines():Boolean{
		return showHorizontalLines;
	}
	
	/**
	 * Returns true if the table draws vertical lines between cells, false if it
	 * doesn't. The default is true.
	 *
	 * @return  true if the table draws vertical lines between cells, false if it
	 *          doesn't
	 * @see	 #setShowVerticalLines()
	 */	
	public function getShowVerticalLines():Boolean{
		return showVerticalLines;
	}
	
	/**
	 * Sets the table's auto resize mode when the table is resized.
	 *
	 * @param   mode One of 5 legal values:
	 *                   AUTO_RESIZE_OFF,
	 *                   AUTO_RESIZE_NEXT_COLUMN,
	 *                   AUTO_RESIZE_SUBSEQUENT_COLUMNS,
	 *                   AUTO_RESIZE_LAST_COLUMN,
	 *                   AUTO_RESIZE_ALL_COLUMNS
	 *
	 * @see     #getAutoResizeMode()
	 * @see     #doLayout()
	 */	
	public function setAutoResizeMode(mode:Number):Void{
		if (((((mode == AUTO_RESIZE_OFF) 
		|| (mode == AUTO_RESIZE_NEXT_COLUMN)) 
		|| (mode == AUTO_RESIZE_SUBSEQUENT_COLUMNS)) 
		|| (mode == AUTO_RESIZE_LAST_COLUMN)) 
		|| (mode == AUTO_RESIZE_ALL_COLUMNS))
		{
			if(mode != autoResizeMode){
				var old:Number = autoResizeMode;
				autoResizeMode = mode;
				resizeAndRepaint();
				if (tableHeader != null){
					tableHeader.resizeAndRepaint();
				}
				//firePropertyChange("autoResizeMode", old, autoResizeMode);
			}
		}
	}
	
	/**
	 * Returns the auto resize mode of the table.  The default mode
	 * is AUTO_RESIZE_SUBSEQUENT_COLUMNS.
	 *
	 * @return  the autoResizeMode of the table
	 *
	 * @see	 #setAutoResizeMode()
	 * @see	 #doLayout()
	 */	
	public function getAutoResizeMode():Number{
		return autoResizeMode;
	}
	
	/**
	 * Sets this table's <code>autoCreateColumnsFromModel</code> flag.
	 * This method calls <code>createDefaultColumnsFromModel</code> if
	 * <code>autoCreateColumnsFromModel</code> changes from false to true.
	 *
	 * @param   autoCreateColumnsFromModel   true if <code>JTable</code> should automatically create columns
	 * @see #getAutoCreateColumnsFromModel()
	 * @see #createDefaultColumnsFromModel()
	 */	
	public function setAutoCreateColumnsFromModel(autoCreateColumnsFromModel:Boolean):Void{
		if (this.autoCreateColumnsFromModel != autoCreateColumnsFromModel){
			var old:Boolean = this.autoCreateColumnsFromModel;
			this.autoCreateColumnsFromModel = autoCreateColumnsFromModel;
			if (autoCreateColumnsFromModel){
				createDefaultColumnsFromModel();
			}
			//firePropertyChange("autoCreateColumnsFromModel", old, autoCreateColumnsFromModel);
		}
	}
	
	/**
	 * Determines whether the table will create default columns from the model.
	 * If true, <code>setModel</code> will clear any existing columns and
	 * create new columns from the new model.  Also, if the event in
	 * the <code>tableChanged</code> notification specifies that the
	 * entire table changed, then the columns will be rebuilt.
	 * The default is true.
	 *
	 * @return  the autoCreateColumnsFromModel of the table
	 * @see	 #setAutoCreateColumnsFromModel()
	 * @see	 #createDefaultColumnsFromModel()
	 */	
	public function getAutoCreateColumnsFromModel():Boolean{
		return autoCreateColumnsFromModel;
	}
	
	/**
	 * Creates default columns for the table from
	 * the data model using the <code>getColumnCount</code> method
	 * defined in the <code>TableModel</code> interface.
	 * <p>
	 * Clears any existing columns before creating the
	 * new columns based on information from the model.
	 *
	 * @see #getAutoCreateColumnsFromModel()
	 */	
	public function createDefaultColumnsFromModel():Void{
		var m:TableModel = getModel();
		if (m != null){
			var cm:TableColumnModel = getColumnModel();
			while ((cm.getColumnCount() > 0)){
				cm.removeColumn(cm.getColumn(0));
			}
			for (var i:Number = 0; i < m.getColumnCount(); i++){
				var newColumn:TableColumn = new TableColumn(i);
				addColumn(newColumn);
			}
		}
	}
	
	/**
	 * Sets a default cell factory to be used if no renderer has been set in
	 * a <code>TableColumn</code>. If renderer is <code>null</code>,
	 * removes the default factory for this column class.
	 *
	 * @param  columnClass     set the default cell factory for this columnClass
	 * @param  renderer        default cell factory to be used for this
	 *			       columnClass
	 * @see     #getDefaultRenderer()
	 * @see     #setDefaultEditor()
	 */	
	public function setDefaultCellFactory(columnClass:String, renderer:TableCellFactory):Void{
		if (renderer != null){
			defaultRenderersByColumnClass.put(columnClass, renderer);
		}else{
			defaultRenderersByColumnClass.remove(columnClass);
		}
	}
	
	/**
	 * Returns the cell factory to be used when no factory has been set in
	 * a <code>TableColumn</code>. During the rendering of cells the factory is fetched from
	 * a <code>Hashtable</code> of entries according to the class of the cells in the column. If
	 * there is no entry for this <code>columnClass</code> the method returns
	 * the entry for the most specific superclass. The <code>JTable</code> installs entries
	 * for <code>"Object"</code>, <code>"Number"</code>, and <code>"Boolean"</code>, all of which can be modified
	 * or replaced.
	 *
	 * @param   columnClass   return the default cell factory
	 *			      for this columnClass
	 * @return  the factory for this columnClass
	 * @see     #setDefaultRenderer()
	 * @see     #getColumnClass()
	 */	
	public function getDefaultCellFactory(columnClass:String):TableCellFactory{
		//trace("getDefaultRenderer of " + columnClass);
		if (columnClass == null){
			return null;
		}else{
			var renderer:Object = defaultRenderersByColumnClass.get(columnClass);
			//trace("defaultRenderersByColumnClass " + renderer);
			if (renderer != null){
				return TableCellFactory(renderer);
			}else{
				return getDefaultCellFactory("Object");
			}
		}
	}
	
	/**
	 * Sets a default cell editor to be used if no editor has been set in
	 * a <code>TableColumn</code>. If no editing is required in a table, or a
	 * particular column in a table, uses the <code>isCellEditable</code>
	 * method in the <code>TableModel</code> interface to ensure that this
	 * <code>JTable</code> will not start an editor in these columns.
	 * If editor is <code>null</code>, removes the default editor for this
	 * column class.
	 *
	 * @param  columnClass  set the default cell editor for this columnClass
	 * @param  editor   default cell editor to be used for this columnClass
	 * @see     TableModel#isCellEditable()
	 * @see     #getDefaultEditor()
	 * @see     #setDefaultRenderer()
	 */	
	public function setDefaultEditor(columnClass:String, editor:TableCellEditor):Void{
		if (editor != null){
			defaultEditorsByColumnClass.put(columnClass, editor);
		}else{
			defaultEditorsByColumnClass.remove(columnClass);
		}
	}
	
	/**
	 * Returns the editor to be used when no editor has been set in
	 * a <code>TableColumn</code>. During the editing of cells the editor is fetched from
	 * a <code>Hashtable</code> of entries according to the class of the cells in the column. If
	 * there is no entry for this <code>columnClass</code> the method returns
	 * the entry for the most specific superclass. The <code>JTable</code> installs entries
	 * for <code>"Object"</code>, <code>"Number"</code>, and <code>"Boolean"</code>, all of which can be modified
	 * or replaced.
	 *
	 * @param   columnClass  return the default cell editor for this columnClass
	 * @return the default cell editor to be used for this columnClass
	 * @see     #setDefaultEditor()
	 * @see     #getColumnClass()
	 */	
	public function getDefaultEditor(columnClass:String):TableCellEditor{
		if (columnClass == null){
			return null;
		}else{
			var editor:Object = defaultEditorsByColumnClass.get(columnClass);
			if (editor != null){
				return TableCellEditor(editor);
			}else{
				return getDefaultEditor("Object");
			}
		}
	}

	/**
	 * Sets the table's selection mode to allow only single selections, a single
	 * contiguous interval, or multiple intervals.
	 * <P>
	 * <bold>Note:</bold>
	 * <code>JTable</code> provides all the methods for handling
	 * column and row selection.  When setting states,
	 * such as <code>setSelectionMode</code>, it not only
	 * updates the mode for the row selection model but also sets similar
	 * values in the selection model of the <code>columnModel</code>.
	 * If you want to have the row and column selection models operating
	 * in different modes, set them both directly.
	 * <p>
	 * Both the row and column selection models for <code>JTable</code>
	 * default to using a <code>DefaultListSelectionModel</code>
	 * so that <code>JTable</code> works the same way as the
	 * <code>JList</code>. See the <code>setSelectionMode</code> method
	 * in <code>JList</code> for details about the modes.
	 *
	 * @see JList#setSelectionMode()
	 */		
	public function setSelectionMode(selectionMode:Number):Void{
		clearSelection();
		getSelectionModel().setSelectionMode(selectionMode);
		getColumnModel().getSelectionModel().setSelectionMode(selectionMode);
	}
	
	/**
	 * Sets whether the rows in this model can be selected.
	 *
	 * @param rowSelectionAllowed   true if this model will allow row selection
	 * @see #getRowSelectionAllowed()
	 */	
	public function setRowSelectionAllowed(rowSelectionAllowed:Boolean):Void{
		var old:Boolean = this.rowSelectionAllowed;
		this.rowSelectionAllowed = rowSelectionAllowed;
		if (old != rowSelectionAllowed){
			repaint();
		}
		//firePropertyChange("rowSelectionAllowed", old, rowSelectionAllowed);
	}
	
	/**
	 * Returns true if rows can be selected.
	 *
	 * @return true if rows can be selected, otherwise false
	 * @see #setRowSelectionAllowed()
	 */	
	public function getRowSelectionAllowed():Boolean{
		return rowSelectionAllowed;
	}
	
	/**
	 * Sets whether the columns in this model can be selected.
	 *
	 * @param columnSelectionAllowed   true if this model will allow column selection
	 * @see #getColumnSelectionAllowed()
	 */	
	public function setColumnSelectionAllowed(columnSelectionAllowed:Boolean):Void{
		var old:Boolean = columnModel.getColumnSelectionAllowed();
		columnModel.setColumnSelectionAllowed(columnSelectionAllowed);
		if (old != columnSelectionAllowed){
			repaint();
		}
		//firePropertyChange("columnSelectionAllowed", old, columnSelectionAllowed);
	}
	
	/**
	 * Returns true if columns can be selected.
	 *
	 * @return true if columns can be selected, otherwise false
	 * @see #setColumnSelectionAllowed()
	 */	
	public function getColumnSelectionAllowed():Boolean{
		return columnModel.getColumnSelectionAllowed();
	}
	
	/**
	 * Sets whether this table allows both a column selection and a
	 * row selection to exist simultaneously. When set,
	 * the table treats the intersection of the row and column selection
	 * models as the selected cells. Override <code>isCellSelected</code> to
	 * change this default behavior. This method is equivalent to setting
	 * both the <code>rowSelectionAllowed</code> property and
	 * <code>columnSelectionAllowed</code> property of the
	 * <code>columnModel</code> to the supplied value.
	 *
	 * @param  cellSelectionEnabled   	true if simultaneous row and column
	 *					selection is allowed
	 * @see #getCellSelectionEnabled()
	 * @see #isCellSelected()
	 */	
	public function setCellSelectionEnabled(cellSelectionEnabled:Boolean):Void{
		setRowSelectionAllowed(cellSelectionEnabled);
		setColumnSelectionAllowed(cellSelectionEnabled);
		var old:Boolean = this.cellSelectionEnabled;
		this.cellSelectionEnabled = cellSelectionEnabled;
		//firePropertyChange("cellSelectionEnabled", old, cellSelectionEnabled);
	}
	
	/**
	 * Returns true if both row and column selection models are enabled.
	 * Equivalent to <code>getRowSelectionAllowed() &&
	 * getColumnSelectionAllowed()</code>.
	 *
	 * @return true if both row and column selection models are enabled
	 *
	 * @see #setCellSelectionEnabled()
	 */	
	public function getCellSelectionEnabled():Boolean{
		return (getRowSelectionAllowed() && getColumnSelectionAllowed());
	}
	
	/**
	 *  Selects all rows, columns, and cells in the table.
	 */	
	public function selectAll():Void{
		if (isEditing()){
			removeEditor();
		}
		if ((getRowCount() > 0) && (getColumnCount() > 0)){
			var oldLead:Number;
			var oldAnchor:Number;
			var selModel:ListSelectionModel;
			selModel = selectionModel;
			//TODO adjusting needed?
			//selModel.setValueIsAdjusting(true);
			oldLead = selModel.getLeadSelectionIndex();
			oldAnchor = selModel.getAnchorSelectionIndex();
			setRowSelectionInterval(0, getRowCount() - 1);
			selModel.addSelectionInterval(oldAnchor, oldLead);
			//selModel.setValueIsAdjusting(false);
			selModel = columnModel.getSelectionModel();
			//selModel.setValueIsAdjusting(true);
			oldLead = selModel.getLeadSelectionIndex();
			oldAnchor = selModel.getAnchorSelectionIndex();
			setColumnSelectionInterval(0, getColumnCount() - 1);
			selModel.addSelectionInterval(oldAnchor, oldLead);
			//selModel.setValueIsAdjusting(false);
		}
	}
	
	/**
	 * Deselects all selected columns and rows.
	 */	
	public function clearSelection():Void{
		selectionModel.clearSelection();
		columnModel.getSelectionModel().clearSelection();
	}
	
	private function boundRow(row:Number):Number{
		if ((row < 0) || (row >= getRowCount())){
			trace("Error : Row index out of range");
			throw new Error("Row index out of range");
		}
		return row;
	}
	
	private function boundColumn(col:Number):Number{
		if ((col < 0) || (col >= getColumnCount())){
			trace("Error : Column index out of range");
			throw new Error("Column index out of range");
		}
		return col;
	}
	
	/**
	 * Selects the rows from <code>index0</code> to <code>index1</code>,
	 * inclusive.
	 *
	 * @exception Error      if <code>index0</code> or
	 *						<code>index1</code> lie outside
	 *                                          [0, <code>getRowCount()</code>-1]
	 * @param   index0 one end of the interval
	 * @param   index1 the other end of the interval
	 */	
	public function setRowSelectionInterval(index0:Number, index1:Number):Void{
		selectionModel.setSelectionInterval(boundRow(index0), boundRow(index1));
	}
	
	/**
	 * Selects the columns from <code>index0</code> to <code>index1</code>,
	 * inclusive.
	 *
	 * @exception Error      if <code>index0</code> or
	 *						<code>index1</code> lie outside
	 *                                          [0, <code>getColumnCount()</code>-1]
	 * @param   index0 one end of the interval
	 * @param   index1 the other end of the interval
	 */	
	public function setColumnSelectionInterval(index0:Number, index1:Number):Void{
		columnModel.getSelectionModel().setSelectionInterval(boundColumn(index0), boundColumn(index1));
	}
	
	/**
	 * Adds the rows from <code>index0</code> to <code>index1</code>, inclusive, to
	 * the current selection.
	 *
	 * @exception Error      if <code>index0</code> or <code>index1</code>
	 *                                          lie outside [0, <code>getRowCount()</code>-1]
	 * @param   index0 one end of the interval
	 * @param   index1 the other end of the interval
	 */	
	public function addRowSelectionInterval(index0:Number, index1:Number):Void{
		selectionModel.addSelectionInterval(boundRow(index0), boundRow(index1));
	}
	
	/**
	 * Adds the columns from <code>index0</code> to <code>index1</code>,
	 * inclusive, to the current selection.
	 *
	 * @exception Error      if <code>index0</code> or
	 *						<code>index1</code> lie outside
	 *                                          [0, <code>getColumnCount()</code>-1]
	 * @param   index0 one end of the interval
	 * @param   index1 the other end of the interval
	 */	
	public function addColumnSelectionInterval(index0:Number, index1:Number):Void{
		columnModel.getSelectionModel().addSelectionInterval(boundColumn(index0), boundColumn(index1));
	}
	
	/**
	 * Deselects the rows from <code>index0</code> to <code>index1</code>, inclusive.
	 *
	 * @exception Error      if <code>index0</code> or
	 *						<code>index1</code> lie outside
	 *                                          [0, <code>getRowCount()</code>-1]
	 * @param   index0 one end of the interval
	 * @param   index1 the other end of the interval
	 */	
	public function removeRowSelectionInterval(index0:Number, index1:Number):Void{
		selectionModel.removeSelectionInterval(boundRow(index0), boundRow(index1));
	}
	
	/**
	 * Deselects the columns from <code>index0</code> to <code>index1</code>, inclusive.
	 *
	 * @exception Error      if <code>index0</code> or
	 *						<code>index1</code> lie outside
	 *                                          [0, <code>getColumnCount()</code>-1]
	 * @param   index0 one end of the interval
	 * @param   index1 the other end of the interval
	 */	
	public function removeColumnSelectionInterval(index0:Number, index1:Number):Void{
		columnModel.getSelectionModel().removeSelectionInterval(boundColumn(index0), boundColumn(index1));
	}
	
	/**
	 * Returns the index of the first selected row, -1 if no row is selected.
	 * @return the index of the first selected row
	 */	
	public function getSelectedRow():Number{
		return selectionModel.getMinSelectionIndex();
	}
	
	/**
	 * Returns the index of the first selected column,
	 * -1 if no column is selected.
	 * @return the index of the first selected column
	 */	
	public function getSelectedColumn():Number{
		return columnModel.getSelectionModel().getMinSelectionIndex();
	}
	
	/**
	 * Returns the indices of all selected rows.
	 *
	 * @return an array of integers containing the indices of all selected rows,
	 *         or an empty array if no row is selected
	 * @see #getSelectedRow
	 */	
	public function getSelectedRows():Array{
		var iMin:Number = selectionModel.getMinSelectionIndex();
		var iMax:Number = selectionModel.getMaxSelectionIndex();
		if ((iMin == (- 1)) || (iMax == (- 1))){
			return new Array();
		}
		
		var rvTmp:Array = new Array();
		var n:Number = 0;
		for (var i:Number = iMin; i <= iMax; i++){
			if (selectionModel.isSelectedIndex(i)){
				rvTmp.push(i);
			}
		}
		return rvTmp;
	}
	
	/**
	 * Returns the indices of all selected columns.
	 *
	 * @return an array of integers containing the indices of all selected columns,
	 *         or an empty array if no column is selected
	 * @see #getSelectedColumn
	 */	
	public function getSelectedColumns():Array{
		return columnModel.getSelectedColumns();
	}
	
	/**
	 * Returns the number of selected rows.
	 *
	 * @return the number of selected rows, 0 if no rows are selected
	 */	
	public function getSelectedRowCount():Number{
		var iMin:Number = selectionModel.getMinSelectionIndex();
		var iMax:Number = selectionModel.getMaxSelectionIndex();
		var count:Number = 0;
		for (var i:Number = iMin; i <= iMax; i++){
			if (selectionModel.isSelectedIndex(i)){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Returns the number of selected columns.
	 *
	 * @return the number of selected columns, 0 if no columns are selected
	 */	
	public function getSelectedColumnCount():Number{
		return columnModel.getSelectedColumnCount();
	}
	
	/**
	 * Returns true if the specified index is in the valid range of rows,
	 * and the row at that index is selected.
	 *
	 * @return true if <code>row</code> is a valid index and the row at
	 *              that index is selected (where 0 is the first row)
	 */	
	public function isRowSelected(row:Number):Boolean{
		return selectionModel.isSelectedIndex(row);
	}
	
	/**
	 * Returns true if the specified index is in the valid range of columns,
	 * and the column at that index is selected.
	 *
	 * @param  column   the column in the column model
	 * @return true if <code>column</code> is a valid index and the column at
	 *              that index is selected (where 0 is the first column)
	 */	
	public function isColumnSelected(column:Number):Boolean{
		return columnModel.getSelectionModel().isSelectedIndex(column);
	}
	
	/**
	 * Returns true if the specified indices are in the valid range of rows
	 * and columns and the cell at the specified position is selected.
	 * @param row   the row being queried
	 * @param column  the column being queried
	 *
	 * @return true if <code>row</code> and <code>column</code> are valid indices
	 *              and the cell at index <code>(row, column)</code> is selected,
	 *              where the first row and first column are at index 0
	 */	
	public function isCellSelected(row:Number, column:Number):Boolean{
		if ((! getRowSelectionAllowed()) && (! getColumnSelectionAllowed())){
			return false;
		}
		return ((!getRowSelectionAllowed()) || isRowSelected(row)) 
				&& ((!getColumnSelectionAllowed()) || isColumnSelected(column));
	}
	
	/**
	 * Scrolls the JTable to make the specified cell completely visible.
	 * @param row the row index
	 * @param column the column index
	 */	
	public function ensureCellIsVisible(row:Number, column:Number):Void{
		var rect:Rectangle = getCellRect(row, column);
		rect.setLocation(getPixelLocationFromLogicLocation(rect.getLocation()));
		
		var insets:Insets = getInsets();
		var insetsX:Number = insets.left;
		var insetsY:Number = insets.top;
		var startX:Number = insetsX;
		var startY:Number = insetsY + getHeaderHeight();
		var endX:Number = getWidth() - insets.right;
		var endY:Number = getHeight() - insets.bottom;
		var moveX:Number = 0;
		var moveY:Number = 0;
		if(rect.x < startX){
			moveX = rect.x - startX;
		}else if(rect.x + rect.width > endX){
			moveX = rect.x + rect.width - endX;
		}
		if(rect.y < startY){
			moveY = rect.y - startY;
		}else if(rect.y + rect.height > endY){
			moveY = rect.y + rect.height - endY;
		}
		setViewPosition(getViewPosition().move(moveX, moveY));
	}
	
	private function changeSelectionModel(sm:ListSelectionModel, index:Number, toggle:Boolean, extend:Boolean, selected:Boolean):Void{
		if (extend){
			if (toggle){
				sm.setAnchorSelectionIndex(index);
			}else{
				sm.setSelectionInterval(sm.getAnchorSelectionIndex(), index);
			}
		}else{
			if (toggle){
				if (selected){
					sm.removeSelectionInterval(index, index);
				}else{
					sm.addSelectionInterval(index, index);
				}
			}else{
				sm.setSelectionInterval(index, index);
			}
		}
	}
	
	/**
	 * Updates the selection models of the table, depending on the state of the
	 * two flags: <code>toggle</code> and <code>extend</code>. Most changes
	 * to the selection that are the result of keyboard or mouse events received
	 * by the UI are channeled through this method so that the behavior may be
	 * overridden by a subclass. Some UIs may need more functionality than
	 * this method provides, such as when manipulating the lead for discontiguous
	 * selection, and may not call into this method for some selection changes.
	 * <p>
	 * This implementation uses the following conventions:
	 * <ul>
	 * <li> <code>toggle</code>: <em>false</em>, <code>extend</code>: <em>false</em>.
	 *      Clear the previous selection and ensure the new cell is selected.
	 * <li> <code>toggle</code>: <em>false</em>, <code>extend</code>: <em>true</em>.
	 *      Extend the previous selection from the anchor to the specified cell,
	 *      clearing all other selections.
	 * <li> <code>toggle</code>: <em>true</em>, <code>extend</code>: <em>false</em>.
	 *      If the specified cell is selected, deselect it. If it is not selected, select it.
	 * <li> <code>toggle</code>: <em>true</em>, <code>extend</code>: <em>true</em>.
	 *      Leave the selection state as it is, but move the anchor index to the specified location.
	 * </ul>
	 * @param  rowIndex   affects the selection at <code>row</code>
	 * @param  columnIndex  affects the selection at <code>column</code>
	 * @param  toggle  see description above
	 * @param  extend  if true, extend the current selection
	 */	
	public function changeSelection(rowIndex:Number, columnIndex:Number, toggle:Boolean, extend:Boolean):Void{
		var rsm:ListSelectionModel = getSelectionModel();
		var csm:ListSelectionModel = getColumnModel().getSelectionModel();
		// Check the selection here rather than in each selection model.
		// This is significant in cell selection mode if we are supposed
		// to be toggling the selection. In this case it is better to
		// ensure that the cell's selection state will indeed be changed.
		// If this were done in the code for the selection model it
		// might leave a cell in selection state if the row was
		// selected but the column was not - as it would toggle them both.		
		var selected:Boolean = isCellSelected(rowIndex, columnIndex);
		changeSelectionModel(csm, columnIndex, toggle, extend, selected);
		changeSelectionModel(rsm, rowIndex, toggle, extend, selected);

    	// Scroll after changing the selection as blit scrolling is immediate,
    	// so that if we cause the repaint after the scroll we end up painting
    	// everything!		
		if (false){//getAutoscrolls()){
			var cellRect:Rectangle = getCellRect(rowIndex, columnIndex, false);
			if (cellRect != null)
			{
				scrollRectToVisible(cellRect);
			}
		}
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
		//firePropertyChange("selectionForeground", old, selectionForeground);
		if (! selectionForeground.equals(old)){
			repaint();
			revalidate();
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
		//firePropertyChange("selectionBackground", old, selectionBackground);
		if (! selectionBackground.equals(old)){
			repaint();
			revalidate();
		}
	}
	
	/**
	 * Returns the <code>TableColumn</code> object for the column in the table
	 * whose identifier is equal to <code>identifier</code>, when compared using
	 * <code>equals</code>.
	 *
	 * @return  the <code>TableColumn</code> object that matches the identifier
	 * @param   identifier                      the identifier object
	 */	
	public function getColumn(identifier:Object):TableColumn{
		var cm:TableColumnModel = getColumnModel();
		var columnIndex:Number = cm.getColumnIndex(identifier);
		return cm.getColumn(columnIndex);
	}
	
	/**
	 * Returns the <code>TableColumn</code> object for the column at
	 * <code>columnIndex</code>.
	 *
	 * @param   columnIndex     the index of the desired column 
	 * @return  the <code>TableColumn</code> object for
	 *				the column at <code>columnIndex</code>
	 */	
	public function getColumnAt(columnIndex:Number):TableColumn{
		return getColumnModel().getColumn(columnIndex);
	}
	
	//*****************************************************
	// Informally implement the TableModel interface.
	//*****************************************************

	/**
	 * Maps the index of the column in the view at
	 * <code>viewColumnIndex</code> to the index of the column
	 * in the table model.  Returns the index of the corresponding
	 * column in the model.  If <code>viewColumnIndex</code>
	 * is less than zero, returns <code>viewColumnIndex</code>.
	 *
	 * @param   viewColumnIndex     the index of the column in the view
	 * @return  the index of the corresponding column in the model
	 *
	 * @see #convertColumnIndexToView
	 */	
	public function convertColumnIndexToModel(viewColumnIndex:Number):Number{
		if (viewColumnIndex < 0){
			return viewColumnIndex;
		}
		return getColumnModel().getColumn(viewColumnIndex).getModelIndex();
	}
	
	/**
	 * Maps the index of the column in the table model at
	 * <code>modelColumnIndex</code> to the index of the column
	 * in the view.  Returns the index of the
	 * corresponding column in the view; returns -1 if this column is not
	 * being displayed.  If <code>modelColumnIndex</code> is less than zero,
	 * returns <code>modelColumnIndex</code>.
	 *
	 * @param   modelColumnIndex     the index of the column in the model
	 * @return  the index of the corresponding column in the view
	 *
	 * @see #convertColumnIndexToModel()
	 */	
	public function convertColumnIndexToView(modelColumnIndex:Number):Number{
		if (modelColumnIndex < 0){
			return modelColumnIndex;
		}
		var cm:TableColumnModel = getColumnModel();
		for (var column:Number = 0; column < getColumnCount(); column++){
			if (cm.getColumn(column).getModelIndex() == modelColumnIndex){
				return column;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the number of rows in this table's model.
	 * @return the number of rows in this table's model
	 *
	 * @see #getColumnCount()
	 */	
	public function getRowCount():Number{
		return getModel().getRowCount();
	}
	
	/**
	 * Returns the number of columns in the column model. Note that this may
	 * be different from the number of columns in the table model.
	 *
	 * @return  the number of columns in the table
	 * @see #getRowCount()
	 * @see #removeColumn()
	 */	
	public function getColumnCount():Number{
		return getColumnModel().getColumnCount();
	}
	
	/**
	 * Returns the name of the column appearing in the view at
	 * column position <code>column</code>.
	 *
	 * @param  column    the column in the view being queried
	 * @return the name of the column at position <code>column</code>
	 *		in the view where the first column is column 0
	 */	
	public function getColumnName(column:Number):String{
		return getModel().getColumnName(convertColumnIndexToModel(column));
	}
	
	/**
	 * Returns the type of the column appearing in the view at
	 * column position <code>column</code>.
	 *
	 * @param   column   the column in the view being queried
	 * @return the type of the column at position <code>column</code>
	 * 		in the view where the first column is column 0
	 */	
	public function getColumnClass(column:Number):String{
		return getModel().getColumnClass(convertColumnIndexToModel(column));
	}
	
	/**
	 * Returns the cell value at <code>row</code> and <code>column</code>.
	 * <p>
	 * <b>Note</b>: The column is specified in the table view's display
	 *              order, and not in the <code>TableModel</code>'s column
	 *		    order.  This is an important distinction because as the
	 *		    user rearranges the columns in the table,
	 *		    the column at a given index in the view will change.
	 *              Meanwhile the user's actions never affect the model's
	 *              column ordering.
	 *
	 * @param   row             the row whose value is to be queried
	 * @param   column          the column whose value is to be queried
	 * @return  the Object at the specified cell
	 */	
	public function getValueAt(row:Number, column:Number):Object{
		return getModel().getValueAt(row, convertColumnIndexToModel(column));
	}
	
	/**
	 * Sets the value for the cell in the table model at <code>row</code>
	 * and <code>column</code>.
	 * <p>
	 * <b>Note</b>: The column is specified in the table view's display
	 *              order, and not in the <code>TableModel</code>'s column
	 *		    order.  This is an important distinction because as the
	 *		    user rearranges the columns in the table,
	 *		    the column at a given index in the view will change.
	 *              Meanwhile the user's actions never affect the model's
	 *              column ordering.
	 *
	 * <code>aValue</code> is the new value.
	 *
	 * @param   aValue          the new value
	 * @param   row             the row of the cell to be changed
	 * @param   column          the column of the cell to be changed
	 * @see #getValueAt()
	 */	
	public function setValueAt(aValue:Object, row:Number, column:Number):Void{
		getModel().setValueAt(aValue, row, convertColumnIndexToModel(column));
	}
	
	/**
	 * Returns true if the cell at <code>row</code> and <code>column</code>
	 * is editable.  Otherwise, invoking <code>setValueAt</code> on the cell
	 * will have no effect.
	 * <p>
	 * <b>Note</b>: The column is specified in the table view's display
	 *              order, and not in the <code>TableModel</code>'s column
	 *		    order.  This is an important distinction because as the
	 *		    user rearranges the columns in the table,
	 *		    the column at a given index in the view will change.
	 *              Meanwhile the user's actions never affect the model's
	 *              column ordering.
	 *
	 *
	 * @param   row      the row whose value is to be queried
	 * @param   column   the column whose value is to be queried
	 * @return  true if the cell is editable
	 * @see #setValueAt()
	 */	
	public function isCellEditable(row:Number, column:Number):Boolean{
		return getModel().isCellEditable(row, convertColumnIndexToModel(column));
	}
	
	/**
	 *  Appends <code>aColumn</code> to the end of the array of columns held by
	 *  this <code>JTable</code>'s column model.
	 *  If the column name of <code>aColumn</code> is <code>null</code>,
	 *  sets the column name of <code>aColumn</code> to the name
	 *  returned by <code>getModel().getColumnName()</code>.
	 *  <p>
	 *  To add a column to this <code>JTable</code> to display the
	 *  <code>modelColumn</code>'th column of data in the model with a
	 *  given <code>width</code>, <code>cellRenderer</code>,
	 *  and <code>cellEditor</code> you can use:
	 *  <pre>
	 *
	 *      addColumn(new TableColumn(modelColumn, width, cellRenderer, cellEditor));
	 *
	 *  </pre>
	 *  [Any of the <code>TableColumn</code> constructors can be used
	 *  instead of this one.]
	 *  The model column number is stored inside the <code>TableColumn</code>
	 *  and is used during rendering and editing to locate the appropriates
	 *  data values in the model. The model column number does not change
	 *  when columns are reordered in the view.
	 *
	 *  @param  aColumn         the <code>TableColumn</code> to be added
	 *  @see    #removeColumn
	 */	
	public function addColumn(aColumn:TableColumn):Void{
		if (aColumn.getHeaderValue() == null){
			var modelColumn:Number = aColumn.getModelIndex();
			var columnName:String = getModel().getColumnName(modelColumn);
			aColumn.setHeaderValue(columnName);
		}
		getColumnModel().addColumn(aColumn);
	}
	
	/**
	 *  Removes <code>aColumn</code> from this <code>JTable</code>'s
	 *  array of columns.  Note: this method does not remove the column
	 *  of data from the model; it just removes the <code>TableColumn</code>
	 *  that was responsible for displaying it.
	 *
	 *  @param  aColumn         the <code>TableColumn</code> to be removed
	 *  @see    #addColumn
	 */	
	public function removeColumn(aColumn:TableColumn):Void{
		getColumnModel().removeColumn(aColumn);
	}
	
	/**
	 * Moves the column <code>column</code> to the position currently
	 * occupied by the column <code>targetColumn</code> in the view.
	 * The old column at <code>targetColumn</code> is
	 * shifted left or right to make room.
	 *
	 * @param   column                  the index of column to be moved
	 * @param   targetColumn            the new index of the column
	 */	
	public function moveColumn(column:Number, targetColumn:Number):Void{
		getColumnModel().moveColumn(column, targetColumn);
	}
	
	/**
	 * Returns the index of the column that <code>point</code> lies in,
	 * or -1 if the result is not in the range
	 * [0, <code>getColumnCount()</code>-1].
	 *
	 * @param   point   the location of interest
	 * @return  the index of the column that <code>point</code> lies in,
	 *		or -1 if the result is not in the range
	 *		[0, <code>getColumnCount()</code>-1]
	 * @see     #rowAtPoint
	 */	
	public function columnAtPoint(point:Point):Number{
		var x:Number = point.x;
		return getColumnModel().getColumnIndexAtX(x);
	}
	
	/**
	 * Returns the index of the row that <code>point</code> lies in,
	 * or -1 if the result is not in the range
	 * [0, <code>getRowCount()</code>-1].
	 *
	 * @param   point   the location of interest
	 * @return  the index of the row that <code>point</code> lies in,
	 *          or -1 if the result is not in the range
	 *          [0, <code>getRowCount()</code>-1]
	 * @see     #columnAtPoint
	 */
	public function rowAtPoint(point:Point):Number{
		var y:Number = point.y;
		var result:Number = Math.floor(y / getRowHeight());
		if (result < 0){
			return (-1);
		}else if (result >= getRowCount()){
			return (-1);
		}else{
			return result;
		}
	}
	
	/**
	 * Returns a rectangle for the cell that lies at the intersection of
	 * <code>row</code> and <code>column</code>.
	 * If <code>includeSpacing</code> is true then the value returned
	 * has the full height and width of the row and column
	 * specified. If it is false, the returned rectangle is inset by the
	 * intercell spacing to return the true bounds of the rendering or
	 * editing component as it will be set during rendering.
	 * <p>
	 * If the column index is valid but the row index is less
	 * than zero the method returns a rectangle with the
	 * <code>y</code> and <code>height</code> values set appropriately
	 * and the <code>x</code> and <code>width</code> values both set
	 * to zero. In general, when either the row or column indices indicate a
	 * cell outside the appropriate range, the method returns a rectangle
	 * depicting the closest edge of the closest cell that is within
	 * the table's range. When both row and column indices are out
	 * of range the returned rectangle covers the closest
	 * point of the closest cell.
	 * <p>
	 * In all cases, calculations that use this method to calculate
	 * results along one axis will not fail because of anomalies in
	 * calculations along the other axis. When the cell is not valid
	 * the <code>includeSpacing</code> parameter is ignored.
	 *
	 * @param   row                   the row index where the desired cell
	 *                                is located
	 * @param   column                the column index where the desired cell
	 *                                is located in the display; this is not
	 *                                necessarily the same as the column index
	 *                                in the data model for the table; the
	 *                                {@link #convertColumnIndexToView(int)}
	 *                                method may be used to convert a data
	 *                                model column index to a display
	 *                                column index
	 * @param   includeSpacing        if false, return the true cell bounds -
	 *                                computed by subtracting the intercell
	 *				      spacing from the height and widths of
	 *				      the column and row models
	 *
	 * @return  the rectangle containing the cell at location
	 *          <code>row</code>,<code>column</code>
	 */	
	public function getCellRect(row:Number, column:Number, includeSpacing:Boolean):Rectangle{
		var r:Rectangle = new Rectangle();
		var insets:Insets = getInsets();
		var valid:Boolean = true;
		if (row < 0){
			valid = false;
		}else if (row >= getRowCount()){
			r.y = getViewSize().height - insets.getMarginHeight();
			valid = false;
		}else{
			r.height = getRowHeight();
			r.y = row * r.height;
		}
		if (column < 0){
			valid = false;
		}else if (column >= getColumnCount()){
			r.x = getLastTotalColumnWidth() - insets.getMarginWidth();
			valid = false;
		}else{
			var cm:TableColumnModel = getColumnModel();
			for (var i:Number = 0; i < column; i++)
			{
				r.x += cm.getColumn(i).getWidth();
			}
			r.width = cm.getColumn(column).getWidth();
		}
		if (valid && (!includeSpacing))
		{
			var rm:Number = getRowMargin();
			var cm:Number = getColumnModel().getColumnMargin();
			r.setRect(r.x + (cm / 2), r.y + (rm / 2), r.width - cm, r.height - rm);
		}
		return r;
	}
	
	/**
	 * Returns the location in the JTable view area of the logic location.
	 */
	public function getPixelLocationFromLogicLocation(p:Point):Point{
		var pp:Point = new Point(p);
		var startP:Point = getViewStartPoint();
		pp.move(startP.x, startP.y);
		return pp;
	}
	
	/**
	 * Returns the location in the JTable view area of the logic location.
	 */
	public function getLogicLocationFromPixelLocation(p:Point):Point{
		var pp:Point = new Point(p);
		var startP:Point = getViewStartPoint();
		pp.move(-startP.x, -startP.y);
		return pp;
	}
	
	private function getViewStartPoint():Point{
		var viewPos:Point = getViewPosition();
		var insets:Insets = getInsets();
		var insetsX:Number = insets.left;
		var insetsY:Number = insets.top;
		var startX:Number = insetsX - viewPos.x;
		var startY:Number = insetsY + getHeaderHeight() - viewPos.y;
		return new Point(startX, startY);
	}
	
	/**
	 * Returns the header height.
	 */
	public function getHeaderHeight():Number{
		if(getTableHeader() == null){
			return 0;
		}else{
			return getTableHeader().getHeight();
		}
	}
	
	private function viewIndexForColumn(aColumn:TableColumn):Number{
		var cm:TableColumnModel = getColumnModel();
		for (var column:Number = 0; column < cm.getColumnCount(); column++)
		{
			if (cm.getColumn(column) == aColumn)
			{
				return column;
			}
		}
		return -1;
	}
	
	private function getResizingColumn():TableColumn{
		return (((tableHeader == null) ? null : tableHeader.getResizingColumn()));
	}
	
//	public @Deprecated function sizeColumnsToFit(lastColumnOnly:Boolean):Void{
//		var oldAutoResizeMode:Number = autoResizeMode;
//		setAutoResizeMode((lastColumnOnly ? AUTO_RESIZE_LAST_COLUMN : AUTO_RESIZE_ALL_COLUMNS));
//		sizeColumnsToFit(- 1);
//		setAutoResizeMode(oldAutoResizeMode);
//	}
//
//	public function sizeColumnsToFit(resizingColumn:Number):Void{
//		if (resizingColumn == (- 1)){
//			setWidthsFromPreferredWidths(false);
//		}else{
//			if (autoResizeMode == AUTO_RESIZE_OFF){
//				var aColumn:TableColumn = getColumnModel().getColumn(resizingColumn);
//				aColumn.setPreferredWidth(aColumn.getWidth());
//			}else{
//				var delta:Number = (getWidth() - getColumnModel().getTotalColumnWidth());
//				accommodateDelta(resizingColumn, delta);
//				setWidthsFromPreferredWidths(true);
//			}
//		}
//	}
	
	private function setWidthsFromPreferredWidths(inverse:Boolean):Void{
		var insets:Insets = getInsets();
		var totalWidth:Number = (autoResizeMode == AUTO_RESIZE_OFF ? getLastTotalColumnWidth() : (getWidth() - insets.getMarginWidth()));
		var totalPreferred:Number = getPreferredSize().width - insets.getMarginWidth();
		var target:Number = ((!inverse) ? totalWidth : totalPreferred);
		var cm:TableColumnModel = columnModel;
		var r:Resizable3 = new Resizable3Imp1(cm, inverse);
		adjustSizes3(target, r, inverse);
	}
	
	// Distribute delta over columns, as indicated by the autoresize mode.
	private function accommodateDelta(resizingColumnIndex:Number, delta:Number):Void{
		var columnCount:Number = getColumnCount();
		var from:Number = resizingColumnIndex;
		var to:Number = columnCount;
		// Use the mode to determine how to absorb the changes.
		switch (autoResizeMode) {
			case AUTO_RESIZE_NEXT_COLUMN: 
				from = (from + 1);
				to = Math.min(from + 1, columnCount);
				break;
			case AUTO_RESIZE_SUBSEQUENT_COLUMNS: 
				from = (from + 1);
				to = columnCount;
				break;
			case AUTO_RESIZE_LAST_COLUMN: 
				from = (columnCount - 1);
				to = (from + 1);
				break;
			case AUTO_RESIZE_ALL_COLUMNS: 
				from = 0;
				to = columnCount;
				break;
			default: 
				return ;
		}
		var start:Number = from;
		var end:Number = to;
		var cm:TableColumnModel = columnModel;
		var r:Resizable3 = new Resizable3Imp2(cm, start, end);
		var totalWidth:Number = 0;
		for (var i:Number = from; i < to; i++)
		{
			var aColumn:TableColumn = columnModel.getColumn(i);
			var input:Number = aColumn.getWidth();
			totalWidth = (totalWidth + input);
		}
		adjustSizes3(totalWidth + delta, r, false);
		return ;
	}
	
	private function adjustSizes3(target:Number, r:Resizable3, inverse:Boolean):Void{
		var N:Number = r.getElementCount();
		var totalPreferred:Number = 0;
		for (var i:Number = 0; i < N; i++){
			totalPreferred += r.getMidPointAt(i);
		}
		var s:Resizable2 = new Resizable2Imp1(r, (target < totalPreferred) == (!inverse));
		adjustSizes2(target, s, !inverse);
	}
	
	private function adjustSizes2(target:Number, r:Resizable2, limitToRange:Boolean):Void{
		var totalLowerBound:Number = 0;
		var totalUpperBound:Number = 0;
		for (var i:Number = 0; i < r.getElementCount(); i++){
			totalLowerBound += r.getLowerBoundAt(i);
			totalUpperBound += r.getUpperBoundAt(i);
		}
		if (limitToRange){
			target = Math.min(Math.max(totalLowerBound, target), totalUpperBound);
		}
		for (var i:Number = 0; i < r.getElementCount(); i++){
			var lowerBound:Number = r.getLowerBoundAt(i);
			var upperBound:Number = r.getUpperBoundAt(i);

			// Check for zero. This happens when the distribution of the delta
			// finishes early due to a series of "fixed" entries at the end.
			// In this case, lowerBound == upperBound, for all subsequent terms.			
			var newSize:Number;
			if (totalLowerBound == totalUpperBound){
				newSize = lowerBound;
			}else{
				var f:Number = ((target - totalLowerBound)) / (totalUpperBound - totalLowerBound);
				newSize = Math.round(lowerBound + (f * (upperBound - lowerBound)));
			}
			r.setSizeAt(newSize, i);
			target -= newSize;
			totalLowerBound -= lowerBound;
			totalUpperBound -= upperBound;
		}
	}
	
	/**
	 * editCellAt(row:Number, column:Number, clickCount:Number):Boolean<br>
	 * editCellAt(row:Number, column:Number):Boolean
	 * <p>
	 * Programmatically starts editing the cell at <code>row</code> and
	 * <code>column</code>, if those indices are in the valid range, and
	 * the cell at those indices is editable.
	 *
	 * @param   row                             the row to be edited
	 * @param   column                          the column to be edited
	 * @param	clickCount						the click count, if force to edite, pass undefined to this param.
	 * @return  false if for any reason the cell cannot be edited,
	 *                or if the indices are invalid
	 */	
	public function editCellAt(row:Number, column:Number, clickCount:Number):Boolean{
		if ((cellEditor != null) && (! cellEditor.stopCellEditing())){
			return false;
		}
		if ((((row < 0) || (row >= getRowCount())) || (column < 0)) || (column >= getColumnCount())){
			return false;
		}
		if (!isCellEditable(row, column)){
			return false;
		}
		if(cellEditor != null){
			removeEditor();
		}
		var editor:TableCellEditor = getCellEditorOfRowColumn(row, column);
		if ((editor != null) && (clickCount == undefined || editor.isCellEditable(clickCount))){
			var cb:Rectangle = getCellRect(row, column, true);
			cb.setLocation(getPixelLocationFromLogicLocation(cb.getLocation()));
			editor.startCellEditing(this, getValueAt(row, column), cb);
			setCellEditor(editor);
			setEditingRow(row);
			setEditingColumn(column);
			editor.removeCellEditorListener(this);
			editor.addCellEditorListener(this);
			
			_storedValue = getValueAt(row, column);
			dispatchEvent(createEventObj( ON_CELL_EDITING_STARTED, row, column, _storedValue ));
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if a cell is being edited.
	 *
	 * @return  true if the table is editing a cell
	 * @see     #getEditingColumn()
	 * @see     #getEditingRow()
	 */	
	public function isEditing():Boolean{
		return (((cellEditor == null) ? false : true));
	}
	
	/**
	 * Returns the index of the column that contains the cell currently
	 * being edited.  If nothing is being edited, returns -1.
	 *
	 * @return  the index of the column that contains the cell currently
	 *		being edited; returns -1 if nothing being edited
	 * @see #getEditingRow()
	 */	
	public function getEditingColumn():Number{
		return editingColumn;
	}
	
	/**
	 * Returns the index of the row that contains the cell currently
	 * being edited.  If nothing is being edited, returns -1.
	 *
	 * @return  the index of the row that contains the cell currently
	 *		being edited; returns -1 if nothing being edited
	 * @see #getEditingColumn()
	 */	
	public function getEditingRow():Number{
		return editingRow;
	}    
	
	//*****************************************
	//          Managing models
	//*****************************************
	
	/**
	 * Sets the data model for this table to <code>newModel</code> and registers
	 * with it for listener notifications from the new data model.
	 *
	 * @param   dataModel        the new data source for this table
	 * @see     #getModel()
	 */	
	public function setModel(dataModel:TableModel):Void{
		if (dataModel == null){
			trace("Can't set null TableModel to JTable, Ignored");
			return;
		}
		if (this.dataModel != dataModel){
			var old:TableModel = this.dataModel;
			if (old != null)
			{
				old.removeTableModelListener(this);
			}
			this.dataModel = dataModel;
			dataModel.addTableModelListener(this);
			tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));
			//firePropertyChange("model", old, dataModel);
		}
	}
	
	/**
	 * Returns the <code>TableModel</code> that provides the data displayed by this
	 * <code>JTable</code>.
	 *
	 * @return  the <code>TableModel</code> that provides the data displayed by this <code>JTable</code>
	 * @see     #setModel()
	 */	
	public function getModel():TableModel{
		return dataModel;
	}
	
	/**
	 * Sets the column model for this table to <code>newModel</code> and registers
	 * for listener notifications from the new column model. Also sets
	 * the column model of the <code>JTableHeader</code> to <code>columnModel</code>.
	 *
	 * @param   columnModel        the new data source for this table
	 * @see     #getColumnModel()
	 */	
	public function setColumnModel(columnModel:TableColumnModel):Void{
		if (columnModel == null){
			trace("Cannot set a null ColumnModel to JTable, Ignored");
			return;
		}
		var old:TableColumnModel = this.columnModel;
		if (columnModel != old){
			if (old != null)
			{
				old.removeColumnModelListener(this);
			}
			this.columnModel = columnModel;
			columnModel.addColumnModelListener(this);
			if (tableHeader != null)
			{
				tableHeader.setColumnModel(columnModel);
			}
			//firePropertyChange("columnModel", old, columnModel);
			resizeAndRepaint();
		}
	}
	
	/**
	 * Returns the <code>TableColumnModel</code> that contains all column information
	 * of this table.
	 *
	 * @return  the object that provides the column state of the table
	 * @see     #setColumnModel()
	 */	
	public function getColumnModel():TableColumnModel{
		return columnModel;
	}
	
	/**
	 * Sets the row selection model for this table to <code>newModel</code>
	 * and registers for listener notifications from the new selection model.
	 *
	 * @param   newModel        the new selection model, if it is null, nothing change.
	 * @see     #getSelectionModel()
	 */	
	public function setSelectionModel(newModel:ListSelectionModel):Void{
		if (newModel == null){
			trace("Cannot set a null SelectionModel to JTable, Ignored");
			return;
		}
		var oldModel:ListSelectionModel = selectionModel;
		if (newModel != oldModel){
			if (oldModel != null){
				oldModel.removeEventListener(selectionModelListener);
			}
			selectionModel = newModel;
			newModel.addEventListener(selectionModelListener);
			//firePropertyChange("selectionModel", oldModel, newModel);
			repaint();
			checkLeadAnchor();
		}
	}
	
	/**
	 * Returns the <code>ListSelectionModel</code> that is used to maintain row
	 * selection state.
	 *
	 * @return  the object that provides row selection state, <code>null</code>
	 *          if row selection is not allowed
	 * @see     #setSelectionModel()
	 */	
	public function getSelectionModel():ListSelectionModel{
		return selectionModel;
	}
	
	private function checkLeadAnchor():Void{
		var model:TableModel = getModel();
		if (model == null){
			return ;
		}
		var lead:Number = selectionModel.getLeadSelectionIndex();
		var count:Number = model.getRowCount();
		if (count == 0){
			if (lead != (- 1)){
				//TODO adjusting
				//selectionModel.setValueIsAdjusting(true);
				selectionModel.setAnchorSelectionIndex(- 1);
				selectionModel.setLeadSelectionIndex(- 1);
				//selectionModel.setValueIsAdjusting(false);
			}
		}else{
			if (lead == (- 1)){
				if (selectionModel.isSelectedIndex(0)){
					selectionModel.addSelectionInterval(0, 0);
				}else{
					selectionModel.removeSelectionInterval(0, 0);
				}
			}
		}
	}	
	
	//*********************************************
	// Implementing TableModelListener interface
	//*********************************************
	
	/**
	 * Invoked when this table's <code>TableModel</code> generates
	 * a <code>TableModelEvent</code>.
	 * The <code>TableModelEvent</code> should be constructed in the
	 * coordinate system of the model; the appropriate mapping to the
	 * view coordinate system is performed by this <code>JTable</code>
	 * when it receives the event.
	 * <p>
	 * Application code will not use these methods explicitly, they
	 * are used internally by <code>JTable</code>.
	 */	
	public function tableChanged(e:TableModelEvent):Void{
		if ((e == null) || (e.getFirstRow() == TableModelEvent.HEADER_ROW)){
			clearSelection();
			checkLeadAnchor();
			if (getAutoCreateColumnsFromModel()){
				createDefaultColumnsFromModel();
				return ;
			}
			resizeAndRepaint();
			return ;
		}
		if (e.getType() == TableModelEvent.INSERT){
			tableRowsInserted(e);
			return ;
		}
		if (e.getType() == TableModelEvent.DELETE){
			tableRowsDeleted(e);
			return ;
		}
		var modelColumn:Number = e.getColumn();
		var end:Number = e.getLastRow();
		if (end != Number.MAX_VALUE){
			revalidate();
		}else{
			clearSelection();
			resizeAndRepaint();
		}
	}
	private function tableRowsInserted(e:TableModelEvent):Void{
		var start:Number = e.getFirstRow();
		var end:Number = e.getLastRow();
		if (start < 0){
			start = 0;
		}
		if (end < 0){
			end = (getRowCount() - 1);
		}
		var length:Number = ((end - start) + 1);
		selectionModel.insertIndexInterval(start, length, true);
		checkLeadAnchor();
		resizeAndRepaint();
	}
	private function tableRowsDeleted(e:TableModelEvent):Void{
		var start:Number = e.getFirstRow();
		var end:Number = e.getLastRow();
		if (start < 0){
			start = 0;
		}
		if (end < 0){
			end = (getRowCount() - 1);
		}
		var deletedCount:Number = ((end - start) + 1);
		var previousRowCount:Number = (getRowCount() + deletedCount);
		selectionModel.removeIndexInterval(start, end);
		checkLeadAnchor();
		//var rh:Number = getAllRowHeight();
		//var drawRect:Rectangle = new Rectangle(0, (start * rh), getColumnModel().getTotalColumnWidth(), ((previousRowCount - start) * rh));
		resizeAndRepaint();
	}
	
	//**************************************************
	// Implementing TableColumnModelListener interface
	//**************************************************
	/**
	 * Invoked when a column is added to the table column model.
	 * <p>
	 * Application code will not use these methods explicitly, they
	 * are used internally by JTable.
	 *
	 * @see TableColumnModelListener
	 */		
	public function columnAdded(e:TableColumnModelEvent):Void{
		if (isEditing()){
			removeEditor();
		}
		resizeAndRepaint();
	}
	/**
	 * Invoked when a column is removed from the table column model.
	 * <p>
	 * Application code will not use these methods explicitly, they
	 * are used internally by JTable.
	 *
	 * @see TableColumnModelListener
	 */	
	public function columnRemoved(e:TableColumnModelEvent):Void{
		if (isEditing()){
			removeEditor();
		}
		resizeAndRepaint();
	}
	/**
	 * Invoked when a column is repositioned. If a cell is being
	 * edited, then editing is stopped and the cell is redrawn.
	 * <p>
	 * Application code will not use these methods explicitly, they
	 * are used internally by JTable.
	 *
	 * @param e   the event received
	 * @see TableColumnModelListener
	 */
	public function columnMoved(e:TableColumnModelEvent):Void{
		if (isEditing()){
			removeEditor();
		}
		resizeAndRepaint();
	}
	/**
	 * Invoked when a column is moved due to a margin change.
	 * If a cell is being edited, then editing is stopped and the cell
	 * is redrawn.
	 * <p>
	 * Application code will not use these methods explicitly, they
	 * are used internally by JTable.
	 *
	 * @param  e    the event received
	 * @see TableColumnModelListener
	 */
	public function columnMarginChanged(source:TableColumnModel):Void{
		if (isEditing()){
			removeEditor();
		}
		var resizingColumn:TableColumn = getResizingColumn();
		if ((resizingColumn != null) && (autoResizeMode == AUTO_RESIZE_OFF)){
			resizingColumn.setPreferredWidth(resizingColumn.getWidth());
		}
		resizeAndRepaint();
	}
	private function limit(i:Number, a:Number, b:Number):Number{
		return Math.min(b, Math.max(i, a));
	}
	/**
	 * Invoked when the selection model of the <code>TableColumnModel</code>
	 * is changed.
	 * <p>
	 * Application code will not use these methods explicitly, they
	 * are used internally by JTable.
	 *
	 * @param  e  the event received
	 * @see TableColumnModelListener
	 */
	public function columnSelectionChanged(source:TableColumnModel, firstIndex:Number, lastIndex:Number):Void{
		dispatchEvent(createEventObj(ON_COLUMN_SELECTION_CHANGED, firstIndex, lastIndex));
		
		var isAdjusting:Boolean = false;//e.getValueIsAdjusting();
		if (columnSelectionAdjusting && (! isAdjusting)){
			columnSelectionAdjusting = false;
			return ;
		}
		columnSelectionAdjusting = isAdjusting;
		if ((getRowCount() <= 0) || (getColumnCount() <= 0)){
			return ;
		}
		resizeAndRepaint();
	}
	
	//************************************************
	// Implementing ListSelectionListener interface
	//************************************************
	
	/**
	 * Invoked when the row selection changes -- repaints to show the new
	 * selection.
	 * <p>
	 * Application code will not use these methods explicitly, they
	 * are used internally by JTable.
	 *
	 * @param e   the event received
	 */
	public function __listSelectionChanged(source:ListSelectionModel, firstIndex:Number, lastIndex:Number):Void{
		var isAdjusting:Boolean = false;//e.getValueIsAdjusting();
		if (rowSelectionAdjusting && (!isAdjusting))
		{
			rowSelectionAdjusting = false;
			return ;
		}
		rowSelectionAdjusting = isAdjusting;
		
		dispatchEvent(createEventObj(ON_SELECTION_CHANGED, firstIndex, lastIndex));
		resizeAndRepaint();
	}
	
	//************************************************
	// Implementing CellEditorListener interface
	//************************************************
	
	/**
	 * Invoked when editing is finished. The changes are saved and the
	 * editor is discarded.
	 * <p>
	 * Application code will not use these methods explicitly, they
	 * are used internally by JTable.
	 *
	 * @param  e  the event received
	 * @see CellEditorListener
	 */
	public function editingStopped(source:CellEditor):Void{
		var editor:TableCellEditor = getCellEditor();
		if (editor != null){
			var value:Object = editor.getCellEditorValue();
			setValueAt(value, editingRow, editingColumn);
			dispatchEvent( createEventObj( ON_CELL_EDITING_STOPPED, editingRow, editingColumn, value, _storedValue ));
			removeEditor();
		}
	}
	
	/**
	 * Invoked when editing is canceled. The editor object is discarded
	 * and the cell is rendered once again.
	 * <p>
	 * Application code will not use these methods explicitly, they
	 * are used internally by JTable.
	 *
	 * @param  e  the event received
	 * @see CellEditorListener
	 */
	public function editingCanceled(source:CellEditor):Void{
		dispatchEvent(createEventObj( ON_CELL_EDITING_CANCELED, editingRow, editingColumn ) );
		removeEditor();
	}
	
	/**
	 * Sets the preferred size of the viewport for this table.
	 *
	 * @param size  a <code>Dimension</code> object specifying the <code>preferredSize</code> of a
	 *              <code>JViewport</code> whose view is this table
	 */	
	public function setPreferredScrollableViewportSize(size:Dimension):Void{
		preferredViewportSize = size;
	}
	
	/**
	 * Returns the preferred size of the viewport for this table.
	 *
	 * @return a <code>Dimension</code> object containing the <code>preferredSize</code> of the <code>JViewport</code>
	 *         which displays this table
	 */
	public function getPreferredScrollableViewportSize():Dimension{
		return preferredViewportSize;
	}	
	
	//*********************************************************
	//         Initialize Defaults
	//*********************************************************

	private function initializeLocalVars():Void{
		setOpaque(false);
		createDefaultCellFactories();
		createDefaultEditors();
		setTableHeader(createDefaultTableHeader());
		setShowGrid(true);
		setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		setRowHeight(20);
		setRowMargin(1);
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);
		setCellEditor(null);
		setEditingColumn(-1);
		setEditingRow(-1);
		//setSurrendersFocusOnKeystroke(false);
		setPreferredScrollableViewportSize(new Dimension(450, 400));
		//var toolTipManager:ToolTipManager = ToolTipManager.sharedInstance();
		//toolTipManager.registerComponent(this);
		//setAutoscrolls(true);
	}
	public function createDefaultDataModel():TableModel{
		return (new DefaultTableModel()).initWithRowcountColumncount(0, 0);
	}
	public function createDefaultColumnModel():TableColumnModel{
		return new DefaultTableColumnModel();
	}
	public function createDefaultSelectionModel():ListSelectionModel{
		return new DefaultListSelectionModel();
	}
	public function createDefaultTableHeader():JTableHeader{
		return new JTableHeader(columnModel);
	}
	public function createDefaultCellFactories():Void{
		defaultRenderersByColumnClass = new HashMap();
		defaultRenderersByColumnClass.put("Object", new GeneralTableCellFactoryUIResource(DefaultTextCell));
	}
	public function createDefaultEditors():Void{
		defaultEditorsByColumnClass = new HashMap();
		defaultEditorsByColumnClass.put("Number", new DefaultNumberTextFieldCellEditor());
		defaultEditorsByColumnClass.put("Boolean", new DefaultCheckBoxCellEditor());
		defaultEditorsByColumnClass.put("Object", new DefaultTextFieldCellEditor());
	}
	
	public function resizeAndRepaint():Void{
		revalidate();
		repaint();
	}
	
	public function getCellEditor():TableCellEditor{
		return cellEditor;
	}
	
	public function setCellEditor(anEditor:TableCellEditor):Void{
		var oldEditor:TableCellEditor = cellEditor;
		cellEditor = anEditor;
		//firePropertyChange("tableCellEditor", oldEditor, anEditor);
	}
	
	public function setEditingColumn(aColumn:Number):Void{
		editingColumn = aColumn;
	}
	
	public function setEditingRow(aRow:Number):Void{
		editingRow = aRow;
	}
	
	public function getCellFactory(row:Number, column:Number):TableCellFactory{
		//trace("getCellFactory...");
		var tableColumn:TableColumn = getColumnModel().getColumn(column);
		//trace("tableColumn = " + tableColumn);
		var renderer:TableCellFactory = tableColumn.getCellFactory();
		//trace("renderer = " + renderer);
		if (renderer == null)
		{
			renderer = getDefaultCellFactory(getColumnClass(column));
			//trace("renderer = " + renderer);
		}
		return renderer;
	}
	
	public function getCellEditorOfRowColumn(row:Number, column:Number):TableCellEditor{
		var tableColumn:TableColumn = getColumnModel().getColumn(column);
		var editor:TableCellEditor = tableColumn.getCellEditor();
		if (editor == null){
			editor = getDefaultEditor(getColumnClass(column));
		}
		return editor;
	}
	
	public function removeEditor():Void{
//		KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", editorRemover);
//		editorRemover = null;
		var editor:TableCellEditor = getCellEditor();
		if (editor != null){
			editor.removeCellEditorListener(this);
			setCellEditor(null);
			setEditingColumn(- 1);
			setEditingRow(- 1);
			requestFocus();
		}
	}	
		
	//*****************************************************
	//           Layout Implementation
	//*****************************************************
	
	/**
	 * Causes this table to lay out its rows and columns.  Overridden so
	 * that columns can be resized to accomodate a change in the size of
	 * a containing parent.
	 * Resizes one or more of the columns in the table
	 * so that the total width of all of this <code>JTable</code>'s
	 * columns is equal to the width of the table.
	 * <p>
	 * Before the layout begins the method gets the
	 * <code>resizingColumn</code> of the <code>tableHeader</code>.
	 * When the method is called as a result of the resizing of an enclosing window,
	 * the <code>resizingColumn</code> is <code>null</code>. This means that resizing
	 * has taken place "outside" the <code>JTable</code> and the change -
	 * or "delta" - should be distributed to all of the columns regardless
	 * of this <code>JTable</code>'s automatic resize mode.
	 * <p>
	 * If the <code>resizingColumn</code> is not <code>null</code>, it is one of
	 * the columns in the table that has changed size rather than
	 * the table itself. In this case the auto-resize modes govern
	 * the way the extra (or deficit) space is distributed
	 * amongst the available columns.
	 * <p>
	 * The modes are:
	 * <ul>
	 * <li>  AUTO_RESIZE_OFF: Don't automatically adjust the column's
	 * widths at all. Use a horizontal scrollbar to accomodate the
	 * columns when their sum exceeds the width of the
	 * <code>Viewport</code>.  If the <code>JTable</code> is not
	 * enclosed in a <code>JScrollPane</code> this may
	 * leave parts of the table invisible.
	 * <li>  AUTO_RESIZE_NEXT_COLUMN: Use just the column after the
	 * resizing column. This results in the "boundary" or divider
	 * between adjacent cells being independently adjustable.
	 * <li>  AUTO_RESIZE_SUBSEQUENT_COLUMNS: Use all columns after the
	 * one being adjusted to absorb the changes.  This is the
	 * default behavior.
	 * <li>  AUTO_RESIZE_LAST_COLUMN: Automatically adjust the
	 * size of the last column only. If the bounds of the last column
	 * prevent the desired size from being allocated, set the
	 * width of the last column to the appropriate limit and make
	 * no further adjustments.
	 * <li>  AUTO_RESIZE_ALL_COLUMNS: Spread the delta amongst all the columns
	 * in the <code>JTable</code>, including the one that is being
	 * adjusted.
	 * </ul>
	 * <p>
	 * <bold>Note:</bold> When a <code>JTable</code> makes adjustments
	 *   to the widths of the columns it respects their minimum and
	 *   maximum values absolutely.  It is therefore possible that,
	 *   even after this method is called, the total width of the columns
	 *   is still not equal to the width of the table. When this happens
	 *   the <code>JTable</code> does not put itself
	 *   in AUTO_RESIZE_OFF mode to bring up a scroll bar, or break other
	 *   commitments of its current auto-resize mode -- instead it
	 *   allows its bounds to be set larger (or smaller) than the total of the
	 *   column minimum or maximum, meaning, either that there
	 *   will not be enough room to display all of the columns, or that the
	 *   columns will not fill the <code>JTable</code>'s bounds.
	 *   These respectively, result in the clipping of some columns
	 *   or an area being painted in the <code>JTable</code>'s
	 *   background color during painting.
	 * <p>
	 *   The mechanism for distributing the delta amongst the available
	 *   columns is provided in a private method in the <code>JTable</code>
	 *   class:
	 * <pre>
	 *   adjustSizes(long targetSize, final Resizable3 r, boolean inverse)
	 * </pre>
	 *   an explanation of which is provided in the following section.
	 *   <code>Resizable3</code> is a private
	 *   interface that allows any data structure containing a collection
	 *   of elements with a size, preferred size, maximum size and minimum size
	 *   to have its elements manipulated by the algorithm.
	 * <p>
	 * <H3> Distributing the delta </H3>
	 * <p>
	 * <H4> Overview </H4>
	 * <P>
	 * Call "DELTA" the difference between the target size and the
	 * sum of the preferred sizes of the elements in r. The individual
	 * sizes are calculated by taking the original preferred
	 * sizes and adding a share of the DELTA - that share being based on
	 * how far each preferred size is from its limiting bound (minimum or
	 * maximum).
	 * <p>
	 * <H4>Definition</H4>
	 * <P>
	 * Call the individual constraints min[i], max[i], and pref[i].
	 * <p>
	 * Call their respective sums: MIN, MAX, and PREF.
	 * <p>
	 * Each new size will be calculated using:
	 * <p>
	 * <pre>
	 *          size[i] = pref[i] + delta[i]
	 * </pre>
	 * where each individual delta[i] is calculated according to:
	 * <p>
	 * If (DELTA < 0) we are in shrink mode where:
	 * <p>
	 * <PRE>
	 *                        DELTA
	 *          delta[i] = ------------ * (pref[i] - min[i])
	 *                     (PREF - MIN)
	 * </PRE>
	 * If (DELTA > 0) we are in expand mode where:
	 * <p>
	 * <PRE>
	 *                        DELTA
	 *          delta[i] = ------------ * (max[i] - pref[i])
	 *                      (MAX - PREF)
	 * </PRE>
	 * <P>
	 * The overall effect is that the total size moves that same percentage,
	 * k, towards the total minimum or maximum and that percentage guarantees
	 * accomodation of the required space, DELTA.
	 *
	 * <H4>Details</H4>
	 * <P>
	 * Naive evaluation of the formulae presented here would be subject to
	 * the aggregated rounding errors caused by doing this operation in finite
	 * precision (using ints). To deal with this, the multiplying factor above,
	 * is constantly recalculated and this takes account of the rounding
	 * errors in the previous iterations. The result is an algorithm that
	 * produces a set of integers whose values exactly sum to the supplied
	 * <code>targetSize</code>, and does so by spreading the rounding
	 * errors evenly over the given elements.
	 *
	 * <H4>When the MAX and MIN bounds are hit</H4>
	 * <P>
	 * When <code>targetSize</code> is outside the [MIN, MAX] range,
	 * the algorithm sets all sizes to their appropriate limiting value
	 * (maximum or minimum).
	 *
	 */	
	public function doLayout():Void{	
		//trace("doLayout");	
		var resizingColumn:TableColumn = getResizingColumn();
		if (resizingColumn == null){
			setWidthsFromPreferredWidths(false);
		}else{
			var w:Number = getLastTotalColumnWidth();
			var columnIndex:Number = viewIndexForColumn(resizingColumn);
			var delta:Number = (w - getColumnModel().getTotalColumnWidth());
			accommodateDelta(columnIndex, delta);
			delta = (w - getColumnModel().getTotalColumnWidth());
        	// If the delta cannot be completely accomodated, then the
        	// resizing column will have to take any remainder. This means
        	// that the column is not being allowed to take the requested
        	// width. This happens under many circumstances: For example,
        	// AUTO_RESIZE_NEXT_COLUMN specifies that any delta be distributed
        	// to the column after the resizing column. If one were to attempt
        	// to resize the last column of the table, there would be no
        	// columns after it, and hence nowhere to distribute the delta.
        	// It would then be given entirely back to the resizing column,
        	// preventing it from changing size.			
			if (delta != 0 && (autoResizeMode != AUTO_RESIZE_OFF)){
				resizingColumn.setWidth(resizingColumn.getWidth() + delta);
			}
			setWidthsFromPreferredWidths(true);
		}
		lastTotalColumnWidth = getColumnModel().getTotalColumnWidth();
		super.doLayout();
	}
	
	private function getLastTotalColumnWidth():Number{
		if(lastTotalColumnWidth == undefined){
			if(autoResizeMode == AUTO_RESIZE_OFF){
				lastTotalColumnWidth = getPreferredSize().width - getInsets().getMarginWidth();
			}else{
				lastTotalColumnWidth = getWidth() - getInsets().getMarginWidth();
			}
		}
		return lastTotalColumnWidth;
	}
	
	private var lastTotalColumnWidth:Number;
	
	private function layoutCells():Void{
		//trace("layoutCells");
		// table viewport bounds
		//var time:Number = getTimer();
		var insets:Insets = getInsets();
		var insetsX:Number = insets.left;
		var insetsY:Number = insets.top;
		var startX:Number = Math.round(insetsX - viewPosition.x);
		
		//layout table header
		getTableHeader().setLocation(startX, insetsY);
		getTableHeader().setSize(getLastTotalColumnWidth(), getTableHeader().getPreferredHeight());
		getTableHeader().bringToTopDepth();
		getTableHeader().validate();
		getTableHeader().paintImmediately();
		
		var b:Rectangle = new Rectangle();
		b.setSize(getExtentSize());
		b.setLocation(viewPosition);

		if (getRowCount() <= 0 || getColumnCount() <= 0) {
			return;
		}

		var upperLeft:Point = b.getLocation();
		var lowerRight:Point = b.rightBottom();
		var rMin:Number = rowAtPoint(upperLeft);
		var rMax:Number = rowAtPoint(lowerRight);
		var columnCount:Number = getColumnCount();
		// This should never happen (as long as our bounds intersect the clip,
		// which is why we bail above if that is the case).
		if (rMin == -1) {
			rMin = 0;
		}
		// If the table does not have enough rows to fill the view we'll get -1.
		// (We could also get -1 if our bounds don't intersect the clip,
		// which is why we bail above if that is the case).
		// Replace this with the index of the last row.
		if (rMax == -1) {
			rMax = getRowCount() - 1;
		}
		var cMin:Number = columnAtPoint(upperLeft);
		var cMax:Number = columnAtPoint(lowerRight);
		// This should never happen.
		if (cMin == -1) {
			cMin = 0;
		}
		// If the table does not have enough columns to fill the view we'll get -1.
		// Replace this with the index of the last column.
		if (cMax == -1) {
			cMax = columnCount - 1;
		}	
		
		//layout each eyeable cells
		var header:JTableHeader = getTableHeader();

		var cm:TableColumnModel = getColumnModel();
		var columnMargin:Number = cm.getColumnMargin();
		var rowMargin:Number = getRowMargin();

		var cellRect:Rectangle;
		var tempRect:Rectangle = new Rectangle();
		var aColumn:TableColumn;
		var columnWidth:Number;
		var cr:Number = 0; //row in visible cell table
		var cc:Number = 0; //column in visible cell table
		
		var startY:Number = Math.round(insetsY - viewPosition.y + getTableHeader().getHeight());
		
//		trace(" header, use time : " + (getTimer() - time));
//		time = getTimer();
		var row:Number = rMin-1;
		var showHL:Boolean = getShowHorizontalLines();
		var showVL:Boolean = getShowVerticalLines();
		while((++row) <= rMax){
			if(cr >= rowCells.length){
				break;
			}
			cellRect = getCellRect(row, cMin, false);
			if(showHL && row == (getRowCount() - 1)){
				cellRect.height -= rowMargin;
			}
			var column:Number = cMin-1;
			while((++column) <= cMax){
				cc = column;
				if(cc >= rowCells[cr].length){
					break;
				}
				aColumn = cm.getColumn(column);
				columnWidth = aColumn.getWidth();
				cellRect.width = columnWidth - columnMargin;
				tempRect.setRect(cellRect.x + startX, cellRect.y + startY, cellRect.width, cellRect.height);
				if(showVL && column == (columnCount - 1)){
					tempRect.width -= columnMargin;
				}
				layoutCell(row, column, tempRect, cr, cc);
				cellRect.x += columnWidth;
			}
			//invisible others columns
			var cellColumnCount:Number = rowCells[0].length;
			for(cc=0; cc<cMin; cc++){
				var cell:TableCell = rowCells[cr][cc];
				cell.getCellComponent().setVisible(false);
			}
			for(cc=cMax+1; cc < cellColumnCount; cc++){
				var cell:TableCell = rowCells[cr][cc];
				cell.getCellComponent().setVisible(false);
			}
			cr++;
		}
		//invisible others rows
		for(var i:Number=cr; i<rowCells.length; i++){
			for(var c:Number=columnCount-1; c>=0; c--){
				var cell:TableCell = rowCells[i][c];
				cell.getCellComponent().setVisible(false);
			}
		}
		//trace((rMax-rMin)*(cMax-cMin)+ " cells, use time : " + (getTimer() - time));
	}
	
	private function layoutCell(row:Number, column:Number, cellRect:Rectangle, cr:Number, cc:Number):Void{
        var value = getValueAt(row, column);

//        var isSelected:Boolean = false;
//        var hasFocus:Boolean = false;
//
//        isSelected = isCellSelected(row, column);
//
//        var rowIsLead:Boolean = (selectionModel.getLeadSelectionIndex() == row);
//        var colIsLead:Boolean = (columnModel.getSelectionModel().getLeadSelectionIndex() == column);
//
//        hasFocus = (rowIsLead && colIsLead) && isFocusOwner();
        
        var cell:TableCell = rowCells[cr][cc];
        if(cell == undefined){
        	trace("Logic Error : rowCells[" + cr + "][" + cc + "] = undefined");
        	trace("rowCells.length = " + rowCells.length);
        }
        cell.setCellValue(value);
        cell.setTableCellStatus(this, isCellSelected(row, column), row, column);
        cell.getCellComponent().setBoundsImmediately(cellRect);
        cell.getCellComponent().setVisible(true);
        cell.getCellComponent().validate();
    	//cell.getCellComponent().paintImmediately();
	}
	
	private var lastColumnCellFactories:Array;
	//track if the cell factory changed, if changed, recreate all cells
	private function synCellClasses():Void{
		if(lastColumnCellFactories.length != getColumnCount()){
			clearCells();
			return;
		}
		var isSame:Boolean = true;
		for(var i:Number = lastColumnCellFactories.length-1; i>=0; i--){
			if(lastColumnCellFactories[i] != this.getCellFactory(0, i)){
				clearCells();
				return;
			}
		}
	}
	
	private function clearCells():Void{
		//trace("Clear cells!!!!!!!!!!!!");
		removeCells(rowCells);
		rowCells = new Array();
	}
	
	private function synCreateCellInstances():Void{
		synCellClasses();
		var ih:Number = getRowHeight();
		var needNum:Number = Math.floor(getExtentSize().height/ih) + 2;
		
		if(rowCells.length == needNum/* || !displayable*/){
			return;
		}
		var columnCount:Number = getColumnCount();
		lastColumnCellFactories = new Array();
		for(var i:Number=0; i<columnCount; i++){
			lastColumnCellFactories.push(getCellFactory(0, i));
		}
		
		//create needed
		if(rowCells.length < needNum){
			var addNum:Number = needNum - rowCells.length;
			//trace("----create need rows-----" + needNum);
			//trace("----current rows-----" + rowCells.length);
			for(var i:Number=0; i<addNum; i++){
				var columnCells:Array = new Array();
				for(var c:Number=0; c<columnCount; c++){
					var cell:TableCell = TableCellFactory(lastColumnCellFactories[c]).createNewCell();
					columnCells.push(cell);
					addCellToContainer(cell);
				}
				rowCells.push(columnCells);
			}
		}else if(rowCells.length > needNum){ //remove mored
			var removeIndex:Number = needNum;
			var removed:Array = rowCells.splice(removeIndex, rowCells.length-1);
			removeCells(removed);
		}
	}
	
	private function removeCells(removed:Array):Void{
		for(var i:Number=0; i<removed.length; i++){
			var columnCells:Array = removed[i];
			for(var c:Number=0; c<columnCells.length; c++){
				var cell:TableCell = TableCell(columnCells[c]);
				removeCellFromeContainer(cell);
			}
		}
	}
	
	private function addCellToContainer(cell:TableCell):Void{
		var cellCom:Component = cell.getCellComponent();
		setCellComponentProperties(cellCom);
		append(cellCom);
	}
	
	private static function setCellComponentProperties(com:Component):Void{
		com.setFocusable(false);
		com.setTriggerEnabled(false);
		if(com instanceof Container){
			var con:Container = Container(com);
			for(var i:Number=0; i<con.getComponentCount(); i++){
				setCellComponentProperties(con.getComponent(i));
			}
		}
	}
	
	private function removeCellFromeContainer(cell:TableCell):Void{
		remove(cell.getCellComponent());
	}	
	
	//*****************************************************
	//                  Events Handling
	//*****************************************************	
	private function __onPress():Void{
		super.__onPress();
	}
	private function __onRelease():Void{
		super.__onRelease();
	}
	private function __onReleaseOutside():Void{
		super.__onReleaseOutside();
	}
	private function __onRollOver():Void{
		super.__onRollOver();
	}
	private function __onRollOut():Void{
		super.__onRollOut();
	}
	private function __onDragOver():Void{
		super.__onDragOver();
	}
	private function __onDragOut():Void{
		super.__onDragOut();
	}
	
	private function __onClick():Void{
		super.__onClick();
		//clickCount
	}
	
	
	//*****************************************************
	//           Viewportable Implementation
	//*****************************************************
	
	public function setSize():Void{
		super.setSize(arguments[0], arguments[1]);
		if(!testingSize){
			setViewPosition(getViewPosition());
		}
	}
	
    public function getVerticalUnitIncrement():Number{
    	if(verticalUnitIncrement == undefined){
    		return getRowHeight();
    	}else{
    		return verticalUnitIncrement;
    	}
    }
    
    public function getVerticalBlockIncrement():Number{
    	if(verticalBlockIncrement == undefined){
    		return getRowHeight()*5;
    	}else{
    		return verticalBlockIncrement;
    	}
    }
    
    public function getHorizontalUnitIncrement():Number{
    	if(horizontalUnitIncrement == undefined){
    		return 1;
    	}else{
    		return horizontalUnitIncrement;
    	}
    }
    
    public function getHorizontalBlockIncrement():Number{
    	if(horizontalBlockIncrement == undefined){
    		return 10;
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
    
    private var testingSize:Boolean;
    public function setViewportTestSize(s:Dimension):Void{
    	testingSize = true;
    	setSize(s);
    	testingSize = false;
    }
    
    public function getExtentSize():Dimension{
    	var d:Dimension = getInsets().getInsideSize(getSize());
    	d.height -= getTableHeader().getHeight();
    	return d;
    }
    
    public function getViewSize():Dimension{
    	if(getUI() == null){
    		return getInsets().getOutsideSize();
    	}
    	return getUI().getViewSize(this);
    }
    
    public function getViewPosition():Point{
		return new Point(viewPosition.x, viewPosition.y);
    }
    
    public function setViewPosition(p:Point):Void{
		restrictionViewPos(p);
		if(!viewPosition.equals(p)){
			viewPosition.setLocation(p);
			fireStateChanged();
			//revalidate();
			valid = false;
			RepaintManager.getInstance().addInvalidRootComponent(this);
			repaint();
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
    
	/**
	 * Adds listener to row selection changed.
	 * @see #ON_SELECTION_CHANGED
	 */	
	public function addSelectionListener(func:Function, obj:Object):Object{
		return addEventListener(ON_SELECTION_CHANGED, func, obj);
	}
	
	/**
	 * Adds listener to column selection changed.
	 * @see #ON_COLUMN_SELECTION_CHANGED
	 */	
	public function addColumnSelectionListener(func:Function, obj:Object):Object{
		return addEventListener(ON_COLUMN_SELECTION_CHANGED, func, obj);
	}
    
	/**
	 * Adds listener to viewportable view position changed.
	 */
	public function addChangeListener(func:Function, obj:Object):Object{
		return addEventListener(ON_STATE_CHANGED, func, obj);
	}
    
    public function getViewportPane():Component{
    	return this;
    }
    
	//******************************************************************
	//------------------------Layout implementation---------------------
	//******************************************************************
	
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
    	return getViewSize();
    }

    public function minimumLayoutSize(target:Container):Dimension{
    	return getInsets().getOutsideBounds().getSize();
    }
	
    public function maximumLayoutSize(target:Container):Dimension{
    	return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
    }
    
	/**
	 * position and fill cells here
	 */
    public function layoutContainer(target:Container):Void{
		synCreateCellInstances();
		layoutCells();
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
}