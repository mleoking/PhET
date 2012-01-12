/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.ext{

import org.aswing.*;
import org.aswing.geom.*;

public class GridListLayout extends EmptyLayout{

    private var hgap:int;

    private var vgap:int;

    private var rows:int;

    private var cols:int;
    
    private var tileWidth:int;
    private var tileHeight:int;

    /**
     * @param     rows   the rows, with the value zero meaning 
     *                   any number of rows
     * @param     cols   the columns, with the value zero meaning 
     *                   any number of columns
     * @param     hgap   (optional)the horizontal gap, default 0
     * @param     vgap   (optional)the vertical gap, default 0
     * @throws ArgumentError  if the value of both
     *			<code>rows</code> and <code>cols</code> is 
     *			set to zero
     */
    public function GridListLayout(rows:int=1, cols:int=0, hgap:int=0, vgap:int=0) {
		if ((rows == 0) && (cols == 0)) {
	     	throw new ArgumentError("rows and cols cannot both be zero");
	 	}
	    
		this.rows = rows;
		this.cols = cols;
		this.hgap = hgap;
		this.vgap = vgap;
    }
    
    public function getRows():int {
		return rows;
    }
    
    public function setRows(rows:int):void {
		this.rows = rows;
    }

    public function getColumns():int {
		return cols;
    }
    
    public function setColumns(cols:int):void {
		this.cols = cols;
    }

    public function getHgap():int {
		return hgap;
    }
    
    public function setHgap(hgap:int):void {
		this.hgap = hgap;
    }
    
    public function getVgap():int {
		return vgap;
    }
    
    public function setVgap(vgap:int):void {
		this.vgap = vgap;
    }
	
	public function getTileWidth():int{
		return tileWidth;
	}
	
	public function getTileHeight():int{
		return tileHeight;
	}
	
	public function setTileWidth(w:int):void{
		tileWidth = w;
	}
	
	public function setTileHeight(h:int):void{
		tileHeight = h;
	}	
	
    override public function preferredLayoutSize(target:Container):IntDimension{
		if ((cols == 0) && (this.rows == 0)) {
	    	throw new Error("rows and cols cannot both be zero");
		}
		var insets:Insets = target.getInsets();
		var ncomponents:int = target.getComponentCount();
		var nrows:int = rows;
		var ncols:int = cols;
		if (nrows > 0){
			ncols = Math.floor(((ncomponents + nrows) - 1) / nrows);
		}else{
			nrows = Math.floor(((ncomponents + ncols) - 1) / ncols);
		}
		var w:int = tileWidth;
		var h:int = tileHeight;
		return new IntDimension((((insets.left + insets.right) + (ncols * w)) + ((ncols - 1) * hgap)), (((insets.top + insets.bottom) + (nrows * h)) + ((nrows - 1) * vgap))); 	
    }
    
    public function getViewSize(target:GridCellHolder):IntDimension{
		if ((cols == 0) && (this.rows == 0)) {
	    	throw new Error("rows and cols cannot both be zero");
		}
		var insets:Insets = target.getInsets();
		var ncomponents:int = target.getComponentCount();
		var nrows:int = rows;
		var ncols:int = cols;
		var list:GridList = target.getList();
		var bounds:IntDimension = list.getExtentSize();
		if(list.isTracksWidth() || list.isTracksHeight()){
			if(list.isTracksHeight()){
				nrows = Math.floor((bounds.height+vgap)/(tileHeight+vgap));
				ncols = Math.floor(((ncomponents + nrows) - 1) / nrows);
			}else{
				ncols = Math.floor((bounds.width+hgap)/(tileWidth+hgap));
				nrows = Math.floor(((ncomponents + ncols) - 1) / ncols);
			}
		}else{
			if(nrows > 0){
				ncols = Math.floor(((ncomponents + nrows) - 1) / nrows);
			}else{
				nrows = Math.floor(((ncomponents + ncols) - 1) / ncols);
			}
		}
		var w:int = tileWidth;
		var h:int = tileHeight;
		return new IntDimension((((insets.left + insets.right) + (ncols * w)) + ((ncols - 1) * hgap)), (((insets.top + insets.bottom) + (nrows * h)) + ((nrows - 1) * vgap)));    	
    }

    override public function minimumLayoutSize(target:Container):IntDimension{
		return target.getInsets().getOutsideSize();
    }
	
	/**
	 * return new IntDimension(1000000, 1000000);
	 */
    override public function maximumLayoutSize(target:Container):IntDimension{
    	return new IntDimension(1000000, 1000000);
    }
    
    override public function layoutContainer(target:Container):void{
		if ((cols == 0) && (this.rows == 0)) {
	    	throw new Error("rows and cols cannot both be zero");
		}
		var insets:Insets = target.getInsets();
		var ncomponents:int = target.getComponentCount();
		var nrows:int = rows;
		var ncols:int = cols;
		if (ncomponents == 0){
			return ;
		}
		var bounds:IntRectangle = insets.getInsideBounds(target.getSize().getBounds());
		var list:GridList = GridCellHolder(target).getList();
		if(list.isTracksWidth() || list.isTracksHeight()){
			if(list.isTracksHeight()){
				nrows = Math.floor((bounds.height+vgap)/(tileHeight+vgap));
				ncols = Math.floor(((ncomponents + nrows) - 1) / nrows);
			}else{
				ncols = Math.floor((bounds.width+hgap)/(tileWidth+hgap));
				nrows = Math.floor(((ncomponents + ncols) - 1) / ncols);
			}
		}else{
			if(nrows > 0){
				ncols = Math.floor(((ncomponents + nrows) - 1) / nrows);
			}else{
				nrows = Math.floor(((ncomponents + ncols) - 1) / ncols);
			}
		}
		var w:int = getTileWidth();
		var h:int = getTileHeight();
		var x:int = insets.left;
		var y:int = insets.top;
		for (var r:int = 0; r < nrows; r++){
			x = insets.left;
			for (var c:int = 0; c < ncols; c++){
				var i:int = ((r * ncols) + c);
				if (i < ncomponents){
					target.getComponent(i).setBounds(new IntRectangle(x, y, w, h));
					list.getCellByIndex(i).setGridListCellStatus(list, list.isSelectedIndex(i), i);
				}
				x += (w + hgap);
			}
			y += (h + vgap);
		}
	}
}
}