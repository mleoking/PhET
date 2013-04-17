/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.icon{

import flash.display.DisplayObject;
import flash.display.Shape;

import org.aswing.*;
import org.aswing.graphics.Graphics2D;
import org.aswing.plaf.UIResource;

/**
 * Frame title bar icon base.
 * @author iiley
 * @private
 */
public class FrameIcon implements Icon, UIResource{
		
	private static const DEFAULT_ICON_WIDTH:int = 13;
	
	protected var width:int;
	protected var height:int;
	protected var shape:Shape;
	
	private var color:ASColor;
	private var disabledColor:ASColor;
		
	/**
	 * @param width the width of the icon square.
	 */	
	public function FrameIcon(width:int=DEFAULT_ICON_WIDTH){
		this.width = width;
		height = width;
		shape = new Shape();
	}
	
	public function getColor(c:Component):ASColor{
		if(c.isEnabled()){
			return color;
		}else{
			return disabledColor;
		}
	}
	
	public function updateIcon(c:Component, g:Graphics2D, x:int, y:int):void
	{
		if(color == null){
			color = c.getUI().getColor("Frame.activeCaptionText");
			disabledColor = new ASColor(color.getRGB(), 0.5);
		}
		shape.graphics.clear();
		updateIconImp(c, new Graphics2D(shape.graphics), x, y);
	}
	
	public function updateIconImp(c:Component, g:Graphics2D, x:int, y:int):void{}
	
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