package org.aswing{
	
import org.aswing.graphics.*;
import flash.display.*;
import org.aswing.error.*;

/**
 * Abstract class for A icon with a decorative displayObject.
 * @see org.aswing.AttachIcon
 * @see org.aswing.LoadIcon
 * @author senkay
 * @author iiley
 */	
public class AssetIcon implements Icon{
	
	protected var width:int;
	protected var height:int;
	protected var scale:Boolean; 
	protected var asset:DisplayObject;
	protected var assetContainer:DisplayObjectContainer;
	protected var maskShape:Shape;
	
	/**
	 * Creates a AssetIcon with a path to load external content.
	 * @param path the path of the external content.
	 * @param width (optional)if you specifiled the width of the Icon, and scale is true,
	 * 		the mc will be scale to this width when paint. If you do not specified the with, it will use 
	 * 		asset.width.
	 * @param height (optional)if you specifiled the height of the Icon, and scale is true, 
	 * 		the mc will be scale to this height when paint. If you do not specified the height, it will use 
	 * 		asset.height.
	 * @param scale (optional)whether scale MC to fix the width and height specified. Default is true
	 */
	public function AssetIcon(asset:DisplayObject=null, width:int=-1, height:int=-1, scale:Boolean=false){
		this.asset = asset;
		this.scale = scale;
		
		if (width==-1 && height==-1){
			if (asset){
				this.width = asset.width;
				this.height = asset.height;				
			}else{
				this.width = 0;
				this.height = 0;
			}
		}else{
			this.width = width;
			this.height = height;
			assetContainer = AsWingUtils.createSprite(null, "assetContainer");
			maskShape = AsWingUtils.createShape(assetContainer, "maskShape");
			maskShape.graphics.beginFill(0xFF0000);
			maskShape.graphics.drawRect(0, 0, width, height);
			maskShape.graphics.endFill();
			if(asset){
				assetContainer.addChild(asset);
				asset.mask = maskShape;
				if(scale){
					asset.width = width;
					asset.height = height;
				}
			}
		}
	}
	
	public function getAsset():DisplayObject{
		return asset;
	}
	
	protected function setWidth(width:int):void{
		this.width = width;
	}
	
	protected function setHeight(height:int):void{
		this.height = height;
	}
	
	public function updateIcon(c:Component, g:Graphics2D, x:int, y:int):void{
		var floor:DisplayObject = getDisplay(c);
		if(floor){
			floor.x = x;
			floor.y = y;
		}
	}
	
	public function getIconHeight(c:Component):int{
		return height;
	}
	
	public function getIconWidth(c:Component):int{
		return width;
	}
	
	public function getDisplay(c:Component):DisplayObject{
		if(assetContainer){
			return assetContainer;
		}else{
			return asset;
		}
	}
	
}
}