/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.overflow.ComponentDecorator;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
import org.aswing.util.Delegate;
import org.aswing.util.HashMap;
import org.aswing.util.MCUtils;

/**
 * LoadIcon allow you load extenal image/animation to be the icon content.
 * @author Tomato
 */
class org.aswing.LoadIcon extends ComponentDecorator implements Icon{
	private var url:String;
	private var width:Number;
	private var height:Number;
	private var sizeInited:Boolean;
	private var movieClipLoader:MovieClipLoader;
	private var servicedComs:HashMap;
	private var needLoadInits:HashMap;
	private var scale:Boolean;
	
	/**
	 * LoadIcon(url:String, width:Number, height:Number, scale:Boolean)<br>
	 * LoadIcon(url:String, width:Number, height:Number) default scale to false<br>
	 * LoadIcon(url:String) the width and height will be count when content loaded
	 * <p>
	 * Creates a LoadIcon with specified url, width, height.
	 * @param url the url of the extenal image/animation file
	 * @param width the width of this icon.(miss this param mean use image width)
	 * @param height the height of this icon.(miss this param mean use image height)
	 * @param scale (optional)whether scale the extenal image/anim to fit the size 
	 * specified by front two params, default is false
	 */
	public function LoadIcon(url:String, width:Number, height:Number, scale:Boolean){
		this.url = url;
		this.scale = (scale == true) ? true:false;
		sizeInited = true;
		if(width == undefined){
			width = 0;
			height = 0;
			sizeInited = false;
		}
		this.width = width;
		this.height = height;
		
		servicedComs = new HashMap();
		needLoadInits = new HashMap();
		
		movieClipLoader = new MovieClipLoader();
		var lis:Object = new Object();
		lis["onLoadInit"] = Delegate.create(this, __onMCLoaded);
		movieClipLoader.addListener(lis);
	}
	
	private function __onMCLoaded(target:MovieClip) : Void {
		if(!sizeInited){
			width = target._width;
			height = target._height;
			sizeInited = true;
		}
		
		var com:Component = servicedComs.get(target._name);
		if(needLoadInits.get(com.getID())){
			com.revalidate();
			com.repaint();
			needLoadInits.remove(com.getID());
		}
			
		var maskMC:MovieClip = MovieClip(target._parent._parent["mask"]);
		if(target._width == width && target._height == height){
			maskMC._visible = false;
			target._parent.setMask(null);
		}else if(scale){
			target._width = width;
			target._height = height;
			maskMC._visible = false;
			target._parent.setMask(null);
		}else{
			maskMC._visible = true;
			var mg:Graphics = new Graphics(maskMC);
			mg.fillRectangle(new SolidBrush(0), 0, 0, width, height);
			//avoid flash8 loaded swf mask bug
			target._parent.setMask(maskMC);
		}
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
		var needLoad:Boolean = true;
		if(getDecorateMC(com) != null){
			needLoad = false;
		}
		var mc:MovieClip = getCreateDecorateMC(com);
		mc._x = x;
		mc._y = y;
		if(needLoad && MCUtils.isMovieClipExist(mc)){
			if(!sizeInited){
				needLoadInits.put(com.getID(), true);
			}
			var imageMC:MovieClip = creater.createMCWithName(mc, "image");
			var maskMC:MovieClip = creater.createMCWithName(mc, "mask");
			var tname:String = "inner"+com.getID();
			servicedComs.put(tname, com);
			//avoid flash8 loaded swf mask bug
			movieClipLoader.loadClip(url, creater.createMCWithName(imageMC, tname));
		}
	}
	
	public function uninstallIcon(com:Component):Void{
		uninstallDecorate(com);
		servicedComs.remove(com.getID());
		needLoadInits.remove(com.getID());
	}
	
	public function toString():String{
		return "LoadIcon[Load URL:" + this.url+ "]";
	}
}
