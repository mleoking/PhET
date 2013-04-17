/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.ext{

import org.aswing.JPanel;
import org.aswing.LayoutManager;
import org.aswing.util.ArrayList;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.AsWingConstants;
import org.aswing.geom.IntDimension;
import org.aswing.event.ContainerEvent;

/**
 * FormRow is a row in the Form.<br/>
 * A row include column children, each child sit a row, null child make a column blank, 
 * also a child can sit a continuous serveral columns.<br/>
 * For the 3 case, they are:
 * <p>
 * <ul>
 * 
 * <li>
 * [ --child1-- ][ --child2-- ][ --child3-- ]<br/>
 * 3 children sit 3 columns, one by one: <br/>
 * <code>setColumnChildren(child1, child2, child3);</code>
 * </li>
 * 
 * <li>
 * [ ---------- ][ --child1-- ][ --child2-- ]<br/>
 * First blank, and then 2 children sit 2 columns: <br/>
 * <code>setColumnChildren(null, child1, child2);</code>
 * </li>
 * 
 * <li>
 * [ ----------child1-------- ][ --child2-- ]<br/>
 * child1 sit first two column2, child2 sit last column: <br/>
 * <code>setColumnChildren(child1, child1, child2);</code>
 * </li>
 * 
 * </ul>
 * </p>
 * <p>
 * Use <code>setColumnChildren</code> and <code>setColumnChild</code> to set the columns 
 * instead of <code>append/remove</code> method of <code>Container</code>.
 * </p>
 * 
 * @author iiley
 */
public class FormRow extends JPanel implements LayoutManager{
	
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
	public static const CENTER:int  = AsWingConstants.CENTER;
	
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
	public static const TOP:int     = AsWingConstants.TOP;
	
	/**
	 * A fast access to AsWingConstants Constant
	 * @see org.aswing.AsWingConstants
	 */
    public static const BOTTOM:int  = AsWingConstants.BOTTOM;
        
    private var verticalAlignment:int;
    private var columns:ArrayList;
    private var widthes:Array;
	private var columnVerticalAlignments:Array;
	private var gap:int;
	private var columnChildrenIndecis:String;
    
    /**
     * Create a form row with specified column children.
     * @see #setColumnChildren()
     */
	public function FormRow(...columnChildren){
		super();
		layout = this;
		verticalAlignment = AsWingConstants.CENTER;
		columns = new ArrayList();
		columns.appendAll(columnChildren);
		appendChildren(columnChildren);
		columnVerticalAlignments = new Array();
		gap = 0;
		addEventListener(ContainerEvent.COM_ADDED, __childrenChangedResetColumns);
		addEventListener(ContainerEvent.COM_REMOVED, __childrenChangedResetColumns);
	}
	
	protected function appendChildren(arr:Array):void{
		for(var i:int=0; i<arr.length; i++){
			var com:Component = arr[i] as Component;
			if(com != null){
				append(com);
			}
		}
	}
	
	internal function setGap(gap:int):void{
		this.gap = gap;
	}
	
	internal function getGap():int{
		return gap;
	}
	
	/**
	 * Sets the column children.
	 * @param columnChildren the children list.
	 */
	public function setColumnChildren(columnChildren:Array):void{
		removeAll();
		columns = new ArrayList();
		columns.appendAll(columnChildren);
		appendChildren(columnChildren);
	}
	
	/**
	 * This is used for GuiBuilder.
	 * If a columnChildrenIndecis is set, when children changed, it will reset the 
	 * columns value with columnChildrenIndecis. Default is null
	 * <br>
	 * Set it null to not automatical reset column when children changed.
	 * <p>
	 * For example: 
	 * "-1,0,0,1" means [ ---------- ][ --child0 sit two column-- ][ --child1-- ]
	 * </p>
	 * @param indices the indices of children to be the columns, null to disable this automatic column set.
	 */
	public function setColumnChildrenIndecis(indices:String):void{
		if(indices == null){
			columnChildrenIndecis = null;
			return;
		}
		columnChildrenIndecis = indices;
		var childIndecis:Array = indices.split(",");
		columns = new ArrayList();
		for(var i:int=0; i<childIndecis.length; i++){
			var index:int = parseInt(childIndecis[i]);
			if(isNaN(index)) index = -1;
			if(index >= 0 && index < getComponentCount()){
				columns.append(getComponent(index));
			}else{
				columns.append(null);
			}
		}
		revalidate();
	}
	
	private function __childrenChangedResetColumns(e:ContainerEvent):void{
		if(columnChildrenIndecis != null){
			setColumnChildrenIndecis(columnChildrenIndecis);
		}
	}
	
	/**
	 * Sets the specified column position child.
	 * @param column the column position.
	 * @param child the child.
	 */
	public function setColumnChild(column:int, child:Component):Component{
		if(column < 0 || column > getColumnCount()){
			throw new RangeError("Out of column bounds!");
			return;
		}
		var old:Component = null;
		if(column < getColumnCount()){
			columns.get(column);
			columns.setElementAt(column, child);
		}else{
			columns.append(child);
		}
		if(old != null){
			if(!columns.contains(old)){
				remove(old);
			}
		}
		if(child != null){
			append(child);
		}
		return old;
	}
	
	/**
	 * Returns the child of column.
	 * @return a component, or null.
	 */
	public function getColumnChild(column:int):Component{
		return columns.get(column);
	}
	
	/**
	 * Returns the column count.
	 * @return column count.
	 */
	public function getColumnCount():int{
		return columns.size();
	}
	
	/**
	 * Returns the preferred size of specified column.
	 */
	public function getColumnPreferredWidth(column:int):int{
		var child:Component = getColumnChild(column);
		if(child == null){
			return 0;
		}
		return Math.ceil(child.getPreferredWidth()/getContinuousCount(column));
	}
	
    /**
     * Returns the default vertical alignment of the children in the row.
     *
     * @return the <code>verticalAlignment</code> property, one of the
     *		following values: 
     * <ul>
     * <li>AsWingConstants.CENTER (the default)
     * <li>AsWingConstants.TOP
     * <li>AsWingConstants.BOTTOM
     * </ul>
     */
    public function getVerticalAlignment():int {
        return verticalAlignment;
    }
    
    /**
     * Sets the default vertical alignment of the children in the row.
     * @param alignment  one of the following values:
     * <ul>
     * <li>AsWingConstants.CENTER (the default)
     * <li>AsWingConstants.TOP
     * <li>AsWingConstants.BOTTOM
     * </ul>
     */
    public function setVerticalAlignment(alignment:int):void {
        if (alignment == verticalAlignment){
        	return;
        }else{
        	verticalAlignment = alignment;
        	revalidate();
        }
    }

    /**
     * Returns the vertical alignment of a row.
     *
     * @return the <code>verticalAlignment</code> property, one of the
     *		following values: 
     * <ul>
     * <li>AsWingConstants.CENTER (the default)
     * <li>AsWingConstants.TOP
     * <li>AsWingConstants.BOTTOM
     * </ul>
     */
    public function getColumnVerticalAlignment(column:int):int {
    	if(columnVerticalAlignments[column] == null){
        	return verticalAlignment;
     	}else{
     		return columnVerticalAlignments[column];
     	}
    }
    
    /**
     * Sets the vertical alignment of a row.
     * @param alignment  one of the following values:
     * <ul>
     * <li>AsWingConstants.CENTER (the default)
     * <li>AsWingConstants.TOP
     * <li>AsWingConstants.BOTTOM
     * </ul>
     */
    public function setColumnVerticalAlignment(column:int, alignment:int):void {
        if (alignment == columnVerticalAlignments[column]){
        	return;
        }else{
        	columnVerticalAlignments[column] = alignment;
        	revalidate();
        }
    }    
	
	override public function setLayout(layout:LayoutManager):void{
		if(!(layout is FormRow)){
			throw new ArgumentError("layout must be FormRow instance!");
			return;
		}
		super.setLayout(layout);
	}
	
	protected function getContinuousCount(column:int):int{
		var child:Component = columns.get(column);
		var total:int = getColumnCount();
		var count:int = 1;
		var i:int;
		for(i=column+1; i<total; i++){
			if(getColumnChild(i) != child){
				break;
			}
			count++;
		}
		for(i=column-1; i>=0; i--){
			if(getColumnChild(i) != child){
				break;
			}
			count++;
		}
		return count;
	}
	
	/**
	 * Sets the width of all columns.
	 * @param widthes a array that contains all width of columns.
	 */
	public function setColumnWidthes(widthes:Array):void{
		this.widthes = widthes.concat();
	}
	
	/**
	 * Returns the width of column.
	 * @return the width of column.
	 */
	public function getColumnWidth(column:int):int{
		if(widthes == null) return 0;
		if(widthes[column] == null) return 0;
		return widthes[column];
	}
	
	//___________________________layout manager_________________________________
	
	public function addLayoutComponent(comp:Component, constraints:Object):void{
	}
	
	public function invalidateLayout(target:Container):void{
	}
	
	public function minimumLayoutSize(target:Container):IntDimension{
		return getInsets().getOutsideSize(new IntDimension(0, 0));
	}
	
	public function preferredLayoutSize(target:Container):IntDimension{
		var h:int = 0;
		var w:int = 0;
		var i:int;
		var n:int;
		for(i=getComponentCount()-1; i>=0; i--){
			var ih:int = getComponent(i).getPreferredHeight();
			if(ih > h) h = ih;
		}
		n = getColumnCount();
		for(i=0; i<n; i++){
			w += getColumnPreferredWidth(i);
		}
		if(n > 1){
			w += (n-1)*gap;
		}
		return getInsets().getOutsideSize(new IntDimension(w, h));
	}
	
	public function maximumLayoutSize(target:Container):IntDimension{
		return IntDimension.createBigDimension();
	}
	
	public function layoutContainer(target:Container):void{
		var c:Component;
		var i:int;
		var n:int;
		n = getColumnCount();
		var sx:int = getInsets().left;
		var sy:int = getInsets().top;
		var h:int = getHeight() - getInsets().getMarginHeight();
		for(i=0; i<n; i++){
			c = getColumnChild(i);
			var ccount:int = getContinuousCount(i);
			var va:int = getColumnVerticalAlignment(i);
			var ph:int = 0;
			if(c){
				ph = c.getPreferredHeight();
			}
			var pw:int = getColumnWidth(i)*ccount;
			var py:int = sy;
			if(va == TOP){
			}else if(va == BOTTOM){
				py = sy + h - ph;
			}else{
				py = sy + (h - ph)/2;
			}
			if(ccount > 1){
				i += (ccount-1);
				pw += (ccount-1)*gap;
			}
			if(c){
				c.setComBoundsXYWH(sx, sy, pw, ph);
			}
			sx += (pw + gap);
		}
	}
	
	public function removeLayoutComponent(comp:Component):void{
	}
	
	public function getLayoutAlignmentY(target:Container):Number{
		return getAlignmentY();
	}
	
	public function getLayoutAlignmentX(target:Container):Number{
		return getAlignmentX();
	}
	
}
}