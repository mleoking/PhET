/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.geom.Dimension;
import org.aswing.Insets;

/**
 * @author feynixs(Cai Rong)
 * @author iiley
 */
class org.aswing.GridLayout extends EmptyLayout{
	/**
     * This is the horizontal gap (in pixels) which specifies the space
     * between columns.  They can be changed at any time.
     * This should be a non-negative integer.
     *
     * @see #getHgap()
     * @see #setHgap(hgap:Number)
     */
    private var hgap:Number;
    /**
     * This is the vertical gap (in pixels) which specifies the space
     * between rows.  They can be changed at any time.
     * This should be a non negative integer.
     *
     * @see #getVgap()
     * @see #setVgap(vgap:Number)
     */
    private var vgap:Number;
    /**
     * This is the number of rows specified for the grid.  The number
     * of rows can be changed at any time.
     * This should be a non negative integer, where '0' means
     * 'any number' meaning that the number of Rows in that
     * dimension depends on the other dimension.
     *
     * @see #getRows()
     * @see #setRows(rows:Number)
     */
    private var rows:Number;
    /**
     * This is the number of columns specified for the grid.  The number
     * of columns can be changed at any time.
     * This should be a non negative integer, where '0' means
     * 'any number' meaning that the number of Columns in that
     * dimension depends on the other dimension.
     *
     * @see #getColumns()
     * @see #setColumns(cols:Number)
     */
    private var cols:Number;



    /**
     * GridLayout(rows:Number, cols:Number)<br>
     * GridLayout(rows:Number, cols:Number, hgap:Number)<br>
     * GridLayout(rows:Number, cols:Number, hgap:Number, vgap:Number)
     * <p>
     * Creates a grid layout with the specified number of rows and 
     * columns. All components in the layout are given equal size. 
     * <p>
     * In addition, the horizontal and vertical gaps are set to the 
     * specified values. Horizontal gaps are placed between each
     * of the columns. Vertical gaps are placed between each of
     * the rows. 
     * <p>
     * One, but not both, of <code>rows</code> and <code>cols</code> can 
     * be zero, which means that any number of objects can be placed in a 
     * row or in a column. 
     * <p>
     * All <code>GridLayout</code> constructors defer to this one.
     * @param     rows   the rows, with the value zero meaning 
     *                   any number of rows
     * @param     cols   the columns, with the value zero meaning 
     *                   any number of columns
     * @param     hgap   (optional)the horizontal gap, default 0
     * @param     vgap   (optional)the vertical gap, default 0
     * @throws Error  if the value of both
     *			<code>rows</code> and <code>cols</code> is 
     *			set to zero
     */
    public function GridLayout(rows:Number, cols:Number, hgap:Number, vgap:Number) {
		if ((rows == 0) && (cols == 0)) {
	     	trace("rows and cols cannot both be zero");
	     	throw new Error("rows and cols cannot both be zero");
	    }
	    
	    if(rows == undefined) rows = 1;
	    if(cols == undefined) cols = 0;
	    if(hgap == undefined) hgap = 0;
	    if(vgap == undefined) vgap = 0;
	    
		this.rows = rows;
		this.cols = cols;
		this.hgap = hgap;
		this.vgap = vgap;
    }

    /**
     * Gets the number of rows in this layout.
     * @return    the number of rows in this layout
     * 
     */
    public function getRows():Number {
		return rows;
    }

    /**
     * Sets the number of rows in this layout to the specified value.
     * @param        rows   the number of rows in this layout
     * @throws    Error  if the value of both 
     *               <code>rows</code> and <code>cols</code> is set to zero
     */
    public function setRows(rows:Number):Void {
		if ((rows == 0) && (this.cols == 0)) {
	    	trace("rows and cols cannot both be zero");
	    	throw new Error("rows and cols cannot both be zero");
		}
		this.rows = rows;
    }

    /**
     * Gets the number of columns in this layout.
     * @return  the number of columns in this layout
     * 
     */
    public function getColumns():Number {
		return cols;
    }

    /**
     * Sets the number of columns in this layout to the specified value. 
     * Setting the number of columns has no affect on the layout 
     * if the number of rows specified by a constructor or by 
     * the <tt>setRows</tt> method is non-zero. In that case, the number 
     * of columns displayed in the layout is determined by the total 
     * number of components and the number of rows specified.
     * @param        cols   the number of columns in this layout
     * @throws    Error  if the value of both 
     *               <code>rows</code> and <code>cols</code> is set to zero
     * 
     */
    public function setColumns(cols:Number):Void {
		if ((cols == 0) && (this.rows == 0)) {
	    	trace("rows and cols cannot both be zero");
	    	throw new Error("rows and cols cannot both be zero");
		}
		this.cols = cols;
    }

    /**
     * Gets the horizontal gap between components.
     * @return       the horizontal gap between components
     * 
     */
    public function getHgap():Number {
		return hgap;
    }
    
    /**
     * Sets the horizontal gap between components to the specified value.
     * @param    hgap   the horizontal gap between components
     *
     */
    public function setHgap(hgap:Number):Void {
		this.hgap = hgap;
    }
    
    /**
     * Gets the vertical gap between components.
     * @return       the vertical gap between components
     * 
     */
    public function getVgap():Number {
		return vgap;
    }
    
    /**
     * Sets the vertical gap between components to the specified value.
     * @param         vgap  the vertical gap between components
     * 
     */
    public function setVgap(vgap:Number):Void {
		this.vgap = vgap;
    }
	
    public function preferredLayoutSize(target:Container):Dimension{
		var insets:Insets = target.getInsets();
		var ncomponents:Number = target.getComponentCount();
		var nrows:Number = rows;
		var ncols:Number = cols;
		if (nrows > 0){
			ncols = Math.floor(((ncomponents + nrows) - 1) / nrows);
		}else{
			nrows = Math.floor(((ncomponents + ncols) - 1) / ncols);
		}
		var w:Number = 0;
		var h:Number = 0;
		for (var i:Number = 0; i < ncomponents; i++){
			var comp:Component = target.getComponent(i);
			var d:Dimension = comp.getPreferredSize();
			if (w < d.width){
				w = d.width;
			}
			if (h < d.height){
				h = d.height;
			}
		}
		return new Dimension((((insets.left + insets.right) + (ncols * w)) + ((ncols - 1) * hgap)), (((insets.top + insets.bottom) + (nrows * h)) + ((nrows - 1) * vgap))); 	
    }

    public function minimumLayoutSize(target:Container):Dimension{
		return target.getInsets().getOutsideSize();
    }
	
	/**
	 * return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
	 */
    public function maximumLayoutSize(target:Container):Dimension{
    	return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
    }
    
    public function layoutContainer(target:Container):Void{
		var insets:Insets = target.getInsets();
		var ncomponents:Number = target.getComponentCount();
		var nrows:Number = rows;
		var ncols:Number = cols;
		if (ncomponents == 0){
			return ;
		}
		if (nrows > 0){
			ncols = Math.floor(((ncomponents + nrows) - 1) / nrows);
		}else{
			nrows = Math.floor(((ncomponents + ncols) - 1) / ncols);
		}
		var w:Number = (target.getWidth() - (insets.left + insets.right));
		var h:Number = (target.getHeight() - (insets.top + insets.bottom));
		w = Math.floor((w - ((ncols - 1) * hgap)) / ncols);
		h = Math.floor((h - ((nrows - 1) * vgap)) / nrows);
		var x:Number = insets.left;
		var y:Number = insets.top;
		for (var c:Number = 0; c < ncols; c++){
			y = insets.top;
			for (var r:Number = 0; r < nrows; r++){
				var i:Number = ((r * ncols) + c);
				if (i < ncomponents){
					target.getComponent(i).setBounds(x, y, w, h);
				}
				y += (h + vgap);
			}
			x += (w + hgap);
		}
	}
	public function toString():String{
		return ((((((((("GridLayout[hgap=") + hgap) + ",vgap=") + vgap) + ",rows=") + rows) + ",cols=") + cols) + "]");
	}
    
	/**
	 * return 0.5
	 */
    public function getLayoutAlignmentX(target:Container):Number{
    	return 0.5;
    }

	/**
	 * return 0.5
	 */
    public function getLayoutAlignmentY(target:Container):Number{
    	return 0.5;
    }
}
