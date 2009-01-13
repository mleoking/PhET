/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.EventDispatcher;
import org.aswing.table.DefaultTextCell;
import org.aswing.table.GeneralTableCellFactoryUIResource;
import org.aswing.table.TableCell;
import org.aswing.table.TableCellEditor;
import org.aswing.table.TableCellFactory;

/**
 *  A <code>TableColumn</code> represents all the attributes of a column in a
 *  <code>JTable</code>, such as width, resizibility, minimum and maximum width.
 *  In addition, the <code>TableColumn</code> provides slots for a renderer and 
 *  an editor that can be used to display and edit the values in this column. 
 *  <p>
 *  It is also possible to specify renderers and editors on a per type basis
 *  rather than a per column basis - see the 
 *  <code>setDefaultRenderer</code> method in the <code>JTable</code> class.
 *  This default mechanism is only used when the renderer (or
 *  editor) in the <code>TableColumn</code> is <code>null</code>.
 * <p>
 *  The <code>TableColumn</code> stores the link between the columns in the
 *  <code>JTable</code> and the columns in the <code>TableModel</code>.
 *  The <code>modelIndex</code> is the column in the
 *  <code>TableModel</code>, which will be queried for the data values for the
 *  cells in this column. As the column moves around in the view this
 *  <code>modelIndex</code> does not change.
 *  <p>
 * <b>Note:</b> Some implementations may assume that all 
 *    <code>TableColumnModel</code>s are unique, therefore we would 
 *    recommend that the same <code>TableColumn</code> instance
 *    not be added more than once to a <code>TableColumnModel</code>.
 *    To show <code>TableColumn</code>s with the same column of
 *    data from the model, create a new instance with the same
 *    <code>modelIndex</code>.
 *  <p>
 * @author iiley
 */
class org.aswing.table.TableColumn extends EventDispatcher{
	
	public static var COLUMN_WIDTH_PROPERTY:String = "columWidth";
	public static var HEADER_VALUE_PROPERTY:String = "headerValue";
	public static var HEADER_RENDERER_PROPERTY:String = "headerRenderer";
	public static var CELL_RENDERER_PROPERTY:String = "cellRenderer";
	
	public static var ON_PROPERTY_CHANGED:String = "onPropertyChanged";
	
	private var modelIndex:Number;
	private var identifier:Object;
	private var width:Number;
	private var minWidth:Number;
	private var preferredWidth:Number;
	private var maxWidth:Number;
	private var headerRenderer:TableCellFactory;
	private var headerValue:Object;
	private var cellRenderer:TableCellFactory;
	private var cellEditor:TableCellEditor;
	private var isResizable:Boolean;
	
	/**
	 * Create a TableColumn.
	 * @param modelIndex modelIndex
	 * @param width the    (optional)width of the column, default to 75
	 * @param cellRenderer (optional)the cell renderer for this column cells
	 * @param cellEditor   (optional)the cell editor for this column cells
	 */
	public function TableColumn(modelIndex:Number, width:Number, cellRenderer:TableCellFactory, cellEditor:TableCellEditor){
		if(modelIndex == undefined) modelIndex = 0;
		this.modelIndex = modelIndex;
		if(width == undefined) width = 75;
		this.width = width;
		this.preferredWidth = width;
		this.cellRenderer = cellRenderer;
		this.cellEditor = cellEditor;
		minWidth = 17;
		maxWidth = 10000000; //default max width
		isResizable = true;
		//resizedPostingDisableCount = 0;
		headerValue = null;
	}
	
	private function firePropertyChange(propertyName:String, oldValue, newValue):Void{
		if(oldValue != newValue){
			dispatchEvent(ON_PROPERTY_CHANGED, this.createEventObj(ON_PROPERTY_CHANGED, propertyName, oldValue, newValue));
		}
	}
	
	public function setModelIndex(modelIndex:Number):Void{
		var old:Number = this.modelIndex;
		this.modelIndex = modelIndex;
		firePropertyChange("modelIndex", old, modelIndex);
	}
	
	public function getModelIndex():Number{
		return modelIndex;
	}
	
	public function setIdentifier(identifier:Object):Void{
		var old:Object = this.identifier;
		this.identifier = identifier;
		firePropertyChange("identifier", old, identifier);
	}
	
	public function getIdentifier():Object{
		return (((identifier != null) ? identifier : getHeaderValue()));
	}
	
	public function setHeaderValue(headerValue:Object):Void{
		var old:Object = this.headerValue;
		this.headerValue = headerValue;
		firePropertyChange("headerValue", old, headerValue);
	}
	
	public function getHeaderValue():Object{
		return headerValue;
	}
	
	public function setHeaderCellFactory(headerRenderer:TableCellFactory):Void{
		var old:TableCellFactory = this.headerRenderer;
		this.headerRenderer = headerRenderer;
		firePropertyChange("headerRenderer", old, headerRenderer);
	}
	
	public function getHeaderCellFactory():TableCellFactory{
		return headerRenderer;
	}
	
	public function setCellFactory(cellRenderer:TableCellFactory):Void{
		var old:TableCellFactory = this.cellRenderer;
		this.cellRenderer = cellRenderer;
		firePropertyChange("cellRenderer", old, cellRenderer);
	}
	
	public function getCellFactory():TableCellFactory{
		return cellRenderer;
	}
	
	public function setCellEditor(cellEditor:TableCellEditor):Void{
		var old:TableCellEditor = this.cellEditor;
		this.cellEditor = cellEditor;
		firePropertyChange("cellEditor", old, cellEditor);
	}
	
	public function getCellEditor():TableCellEditor{ 
		return cellEditor;
	}
	
	public function setWidth(width:Number):Void{
		var old:Number = this.width;
		this.width = Math.min(Math.max(width, minWidth), maxWidth);
		
		firePropertyChange("width", old, this.width);
	}
	
	public function getWidth():Number{
		return width;
	}
	
	public function setPreferredWidth(preferredWidth:Number):Void{
		var old:Number = this.preferredWidth;
		this.preferredWidth = Math.min(Math.max(preferredWidth, minWidth), maxWidth);
		firePropertyChange("preferredWidth", old, this.preferredWidth);
	}
	
	public function getPreferredWidth():Number{
		return preferredWidth;
	}
	
	public function setMinWidth(minWidth:Number):Void{
		var old:Number = this.minWidth;
		this.minWidth = Math.max(minWidth, 0);
		if (width < minWidth){
			setWidth(minWidth);
		}
		if (preferredWidth < minWidth){
			setPreferredWidth(minWidth);
		}
		firePropertyChange("minWidth", old, this.minWidth);
	}
	
	public function getMinWidth():Number{
		return minWidth;
	}
	
	public function setMaxWidth(maxWidth:Number):Void{
		var old:Number = this.maxWidth;
		this.maxWidth = Math.max(minWidth, maxWidth);
		if (width > maxWidth)
		{
			setWidth(maxWidth);
		}
		if (preferredWidth > maxWidth)
		{
			setPreferredWidth(maxWidth);
		}
		firePropertyChange("maxWidth", old, this.maxWidth);
	}
	
	public function getMaxWidth():Number{
		return maxWidth;
	}
	
	public function setResizable(isResizable:Boolean):Void{
		var old:Boolean = this.isResizable;
		this.isResizable = isResizable;
		firePropertyChange("isResizable", old, this.isResizable);
	}
	
	public function getResizable():Boolean{
		return isResizable;
	}
	
    /**
     * Resizes the <code>TableColumn</code> to fit the width of its header cell.
     * This method does nothing if the header renderer is <code>null</code>
     * (the default case). Otherwise, it sets the minimum, maximum and preferred 
     * widths of this column to the widths of the minimum, maximum and preferred 
     * sizes of the Component delivered by the header renderer. 
     * The transient "width" property of this TableColumn is also set to the 
     * preferred width. Note this method is not used internally by the table 
     * package. 
     *
     * @see	#setPreferredWidth()
     */	
	public function sizeWidthToFit():Void{
		if (headerRenderer == null){
			return;
		}
		var cell:TableCell = headerRenderer.createNewCell(true);
		cell.setCellValue(getHeaderValue());
		var c:Component = cell.getCellComponent();
		setMinWidth(c.getMinimumSize().width);
		setMaxWidth(c.getMaximumSize().width);
		setPreferredWidth(c.getPreferredSize().width);
		setWidth(getPreferredWidth());
	}
	
    /**
     * Adds a property change listener to the listener list.
     * The listener is registered for all properties.
     */
	public function addPropertyChangeListener(func:Function, obj:Object):Object{
		return addEventListener(ON_PROPERTY_CHANGED, func, obj);
	}
		
	public function getPropertyChangeListeners():Array{
		return listeners.concat();
	}
	
	public function createDefaultHeaderRenderer():TableCellFactory{
		var factory:TableCellFactory = new GeneralTableCellFactoryUIResource(DefaultTextCell);
		//TODO header cell
		return factory;
	}
}