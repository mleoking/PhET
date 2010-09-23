package org.aswing{

import flash.display.DisplayObject;
import org.aswing.graphics.Graphics2D;

/**
 * EmptyIcon is just for sit a place.
 * @author iiley
 */
public class EmptyIcon implements Icon{
	
	private var width:int;
	private var height:int;
	
	public function EmptyIcon(width:int, height:int){
		this.width = width;
		this.height = height;
	}
	
	public function getDisplay(c:Component):DisplayObject{
		return null;
	}
	
	public function getIconWidth(c:Component):int
	{
		return width;
	}
	
	public function getIconHeight(c:Component):int
	{
		return height;
	}
	
	public function updateIcon(c:Component, g:Graphics2D, x:int, y:int):void{
	}
	
}
}