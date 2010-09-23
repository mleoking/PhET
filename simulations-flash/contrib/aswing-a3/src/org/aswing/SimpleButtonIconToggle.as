package org.aswing{

import flash.display.DisplayObject;
import org.aswing.graphics.Graphics2D;
import flash.display.SimpleButton;
import flash.filters.ColorMatrixFilter;

public class SimpleButtonIconToggle implements Icon{
	
	private var asset:SimpleButton;
	private var upState:DisplayObject;
	private var overState:DisplayObject;
	private var downState:DisplayObject;
	private var width:int;
	private var height:int;
	
	private static var disabledFilters:Array;
	private static var eabledFilters:Array = [];
	
	public function SimpleButtonIconToggle(asset:SimpleButton){
		this.asset = asset;
		width = Math.ceil(asset.width);
		height = Math.ceil(asset.height);
		asset.mouseEnabled = false;
		upState = asset.upState;
		overState = asset.overState;
		downState = asset.downState;
		
		if(disabledFilters == null){
			var cmatrix:Array = [0.3, 0.59, 0.11, 0, 0, 0.3, 0.59, 0.11, 0, 0, 0.3, 0.59, 0.11, 0, 0, 0, 0, 0, 1, 0];
			disabledFilters = [new ColorMatrixFilter(cmatrix)];			
		}
	}
	
	public function getDisplay(c:Component):DisplayObject{
		return asset;
	}
	
	public function getIconWidth(c:Component):int{
		return width;
	}
	
	public function getIconHeight(c:Component):int{
		return height;
	}
	
	public function updateIcon(c:Component, g:Graphics2D, x:int, y:int):void{
		asset.x = x;
		asset.y = y;
		var b:AbstractButton = c as AbstractButton;
		var state:DisplayObject = null;
		if(b){
			var model:ButtonModel = b.getModel();
			if(model.isPressed() && model.isArmed() || model.isSelected()) {
				state = downState;
			}else if(b.isRollOverEnabled() && model.isRollOver()) {
				state = overState;
			}else{
				state = upState;
			}
			asset.upState = state;
			asset.filters = model.isEnabled() ? eabledFilters : disabledFilters;
		}
	}
	
}
}