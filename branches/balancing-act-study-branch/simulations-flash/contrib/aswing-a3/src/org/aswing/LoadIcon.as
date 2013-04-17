/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.graphics.Graphics2D;
import flash.display.DisplayObject;
import flash.display.Loader;
import flash.events.*;
import flash.net.URLRequest;
import flash.display.Sprite;
import flash.system.LoaderContext;

/**
 * LoadIcon allow you load extenal image/animation to be the icon content.
 * @author senkay
 */
public class LoadIcon extends AssetIcon{
	
	protected var loader:Loader;
	protected var owner:Component;
	protected var urlRequest:URLRequest;
	protected var context:LoaderContext;
	protected var needCountSize:Boolean;
	
	/**
	 * Creates a LoadIcon with specified url/URLRequest, width, height.
	 * @param url the url/URLRequest for a asset location.
	 * @param width (optional)the width of this icon.(miss this param mean use image width)
	 * @param height (optional)the height of this icon.(miss this param mean use image height)
	 * @param scale (optional)whether scale the extenal image/anim to fit the size 
	 * 		specified by front two params, default is false
	 */
	public function LoadIcon(url:*, width:Number=-1, height:Number=-1, scale:Boolean=false, context:LoaderContext=null){
		super(getLoader(), width, height, false);
		this.scale = scale;
		if(url is URLRequest){
			urlRequest = url;
		}else{
			urlRequest = new URLRequest(url);
		}
		this.context = context;
		needCountSize = (width == -1 || height == -1);
		getLoader().load(urlRequest, context);
	}
	
	/**
	 * Return the loader
	 * @return this loader
	 */
	public function getLoader():Loader{
		if (loader == null){
			loader = new Loader();
			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, __onComplete);
			loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, __onLoadError);
		}
		return loader;
	}
	
	/**
	 * when the loader init updateUI
	 */
	private function __onComplete(e:Event):void{
		if(needCountSize){
			setWidth(loader.width);
			setHeight(loader.height);
		}
		if(scale){
			loader.width = width;
			loader.height = height;
		}
		if(owner){
			owner.repaint();
			owner.revalidate();	
		}
	}
	
	private function __onLoadError(e:IOErrorEvent):void{
		//do nothing
	}
	

	override public function updateIcon(c:Component, g:Graphics2D, x:int, y:int):void{
		super.updateIcon(c, g, x, y);
		owner = c;
	}
	
	override public function getIconHeight(c:Component):int{
		owner = c;
		return super.getIconHeight(c);
	}
	
	override public function getIconWidth(c:Component):int{
		owner = c;
		return super.getIconWidth(c);
	}
	
	override public function getDisplay(c:Component):DisplayObject{
		owner = c;
		return super.getDisplay(c);
	}
	
	public function clone():LoadIcon{
		return new LoadIcon(urlRequest, needCountSize ? -1 : width, needCountSize ? -1 : height, scale, context);
	}
}
}