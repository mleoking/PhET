package org.aswing.plaf.basic.tabbedpane{

import flash.display.*;
import org.aswing.graphics.Graphics2D;
import org.aswing.plaf.UIResource;
import org.aswing.Component;
import org.aswing.Icon;
import org.aswing.ASColor;
import org.aswing.graphics.Pen;

/**
 * Close Icon for tab.
 */
public class CloseIcon implements Icon, UIResource{

	protected var width:int;
	protected var height:int;
	protected var shape:Shape;
	private var color:ASColor;
	
	public function CloseIcon(){
		width = 12;
		height = width;
		shape = new Shape();
	}
	
	public function getColor():ASColor{
		return color;
	}
	
	public function updateIcon(c:Component, g:Graphics2D, x:int, y:int):void{
		if(color == null){
			color = c.getUI().getColor("ClosableTabbedPane.darkShadow");
		}
		shape.graphics.clear();
		if(!c.isEnabled()){
			return; //do not paint X when not enabled
		}
		var w:Number = width/2;
		g.drawLine(
			new Pen(getColor(), w/3), 
			x+(width-w)/2, y+(width-w)/2,
			x+(width+w)/2, y+(width+w)/2);
		g.drawLine(
			new Pen(getColor(), w/3), 
			x+(width-w)/2, y+(width+w)/2,
			x+(width+w)/2, y+(width-w)/2);		
	}
		
	public function getIconHeight(c:Component):int
	{
		return width;
	}
	
	public function getIconWidth(c:Component):int
	{
		return height;
	}
	
	public function getDisplay(c:Component):DisplayObject
	{
		return shape;
	}
	
}
}