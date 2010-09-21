/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASWingUtils;
import org.aswing.Component;
import org.aswing.overflow.ComponentDecorator;
import org.aswing.graphics.Graphics;
import org.aswing.Icon;

/**
 * Attach a mc from library with the linkage name to be a icon.
 * @author iiley
 */
class org.aswing.overflow.AttachIcon extends ComponentDecorator implements Icon{
	private var linkage:String;
	private var width:Number;
	private var height:Number;
	private var scale:Boolean;
	private var noWHInited:Boolean;
	
	/**
	 * AttachIcon(linkage:String, width:Number, height:Number, scale:Boolean)<br>
	 * AttachIcon(linkage:String, width:Number, height:Number)<br>
	 * AttachIcon(linkage:String)<br>
	 * <p>
	 * Attach a mc from library to be a icon.<br>
	 * If speciaficed the width and height, the mc will be scale to be this size if scale setted true.
	 * else the width and height will be the symbol's _width and _height.
	 * @param linkage the linkageID of the symbol to attach
	 * @param width (optional)if you specifiled the width of the Icon, and scale is true, 
	 * the mc will be scale to this width when paint.
	 * @param height (optional)if you specifiled the height of the Icon, and scale is true, 
	 * the mc will be scale to this height when paint.
	 * @param scale (optional)whether scale MC to fix the width and height specified. Default is true
	 */
	public function AttachIcon(linkage:String, width:Number, height:Number, scale:Boolean){
		this.linkage = linkage;
		noWHInited = false;
		this.scale = (scale == undefined ? true : scale);
		if(width == undefined){
			var iconMC:MovieClip = creater.attachMC(ASWingUtils.getRootMovieClip(), linkage);
			if(iconMC == null){
				width = 0;
				height = 0;
			}else{
				width = iconMC._width;
				height = iconMC._height;
			}
			iconMC.removeMovieClip();
			noWHInited = true;
		}
		this.width = width;
		this.height = height;
	}

	/**
	 * Return the icon's width.
	 */
	public function getIconWidth():Number{
		return width;
	}
	
	/**
	 * Return the icon's height.
	 */
	public function getIconHeight():Number{
		return height;
	}
	
	/**
	 * Draw the icon at the specified location.
	 */
	public function paintIcon(com:Component, g:Graphics, x:Number, y:Number):Void{
		var mc:MovieClip = getAttachDecorateMC(com, linkage);
		mc._x = x;
		mc._y = y;
		if(scale && !noWHInited){
			mc._width = width;
			mc._height = height;
		}
	}
	
	public function uninstallIcon(com:Component):Void{
		uninstallDecorate(com);
	}
	
	public function toString():String{
		return "AttachIcon[linkage:" + this.linkage + "]";
	}
}
