/*
 Copyright aswing.org, see the LICENCE.txt.
*/
	
package org.aswing { 
	
import flash.display.DisplayObject;
import flash.display.MovieClip;
import flash.utils.*;

import org.aswing.event.AttachEvent;
import org.aswing.geom.IntDimension;
import flash.display.Loader;
import flash.display.Sprite;
import flash.events.*;
import flash.net.URLRequest;
import flash.system.ApplicationDomain;

/**
 * Dispatched when when the symbol was attached.
 * @eventType org.aswing.event.AttachEvent.ATTACHED
 */
[Event(name="attached", type="org.aswing.event.AttachEvent")]

/**
 * JAttachPane, a container attach flash symbol in library to be its floor.
 * @see org.aswing.JLoadPane
 * @author iiley
 */
public class JAttachPane extends AssetPane{
	
	private var className:String;
	private var applicationDomain:ApplicationDomain;
	
	/**
	 * Creates a JAttachPane with a path to attach a symbol from library.
	 * 
	 * @param assetClassName the class name of the symbol in library
	 * @param prefferSizeStrategy the prefferedSize count strategy. Must be one of below:
	 * <ul>
	 * <li>{@link org.aswing.AssetPane#PREFER_SIZE_BOTH}
	 * <li>{@link org.aswing.AssetPane#PREFER_SIZE_IMAGE}
	 * <li>{@link org.aswing.AssetPane#PREFER_SIZE_LAYOUT}
	 * </ul>
	 * Default is PREFER_SIZE_IMAGE.
	 * @param applicationDomain the applicationDomain for the class placed in. default is null means current domain.
	 * @see #setPath()
	 */
	public function JAttachPane(assetClassName:String=null, prefferSizeStrategy:int=1, applicationDomain:ApplicationDomain=null) {
		super(null, prefferSizeStrategy);		
		setName("JAttachPane");
		this.className = assetClassName;
		this.applicationDomain = (applicationDomain == null ? ApplicationDomain.currentDomain : applicationDomain);
		setAsset(createAsset());
	}
	
	/**
	 * Sets the class name of the asset.
	 * @param assetClassName the asset class name.
	 */
	public function setAssetClassName(assetClassName:String):void{
		if(className != assetClassName){
			className = assetClassName;
			setAsset(createAsset());
		}
	}
	
	/**
	 * Sets the applicationDomain.
	 * @param ad the applicationDomain.
	 */
	public function setApplicationDomain(ad:ApplicationDomain):void{
		if(applicationDomain != ad){
			applicationDomain = ad;
			setAsset(createAsset());
		}
	}
	
	public function getAssetClassName():String{
		return className;
	}
	
	public function getApplicationDomain():ApplicationDomain{
		return applicationDomain;
	}
	
	/**
	 * Sets the path to attach displayObject from library of loader.
	 * @param assetClassName the linkageID of a displayObject.
	 * @param loader the loader that its contentLoaderInfo.appliactionDomain to be used.
	 */
	public function setAssetClassNameAndLoader(assetClassName:String, loader:Loader):void{
		if(className != assetClassName 
			|| applicationDomain != loader.contentLoaderInfo.applicationDomain){
			className = assetClassName;
			applicationDomain = loader.contentLoaderInfo.applicationDomain;
			setAsset(createAsset());
		}
	}
	
	/**
	 * unload the asset of the pane
	 */ 
	override public function unloadAsset():void{
		className = null;
		super.unloadAsset();
	}
	
	/**
	 * return the class name of the asset.
	 * @return the class name.
	 */ 
	public function getClassName():String{
		return className;
	}
	
	private function createAsset():DisplayObject{
		if(className == null){
			return null;
		}
		var classReference:Class;
		if (applicationDomain == null){
			classReference = getDefinitionByName(className) as Class;
		}else{
			classReference = applicationDomain.getDefinition(className) as Class;
		}
		if(classReference == null){
			return null;
		}
		var attachMC:DisplayObject = new classReference() as DisplayObject;
		if(attachMC == null){
			return null;
		}
		setAssetOriginalSize(new IntDimension(attachMC.width, attachMC.height));
		dispatchEvent(new AttachEvent(AttachEvent.ATTACHED));
		return attachMC;
	}
}
}