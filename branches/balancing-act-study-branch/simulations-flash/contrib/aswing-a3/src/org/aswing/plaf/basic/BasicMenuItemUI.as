/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{

import flash.display.*;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.text.*;

import org.aswing.*;
import org.aswing.event.AWEvent;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.*;

/**
 * @private
 * @author iiley
 */
public class BasicMenuItemUI extends BaseComponentUI implements MenuElementUI{
		
	/* Client Property keys for text and accelerator text widths */
	public static var MAX_TEXT_WIDTH:String =  "maxTextWidth";
	public static var MAX_ACC_WIDTH:String  =  "maxAccWidth";
	
	protected var menuItem:JMenuItem;
	
	protected var selectionBackground:ASColor;
	protected var selectionForeground:ASColor;	
	protected var disabledForeground:ASColor;
	protected var acceleratorForeground:ASColor;
	protected var acceleratorSelectionForeground:ASColor;
	
	protected var defaultTextIconGap:int;
	protected var acceleratorFont:ASFont;
	protected var acceleratorFontValidated:Boolean;

	protected var arrowIcon:Icon;
	protected var checkIcon:Icon;
	
	protected var textField:TextField;
	protected var accelTextField:TextField;
	
	protected var menuItemLis:Object;
	
	public function BasicMenuItemUI() {
		super();
	}
	
	override public function installUI(c:Component):void {
		menuItem = JMenuItem(c);
		installDefaults();
		installComponents();
		installListeners();
	}

	override public function uninstallUI(c:Component):void {
		menuItem = JMenuItem(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
	}

	protected function getPropertyPrefix():String {
		return "MenuItem.";
	}	

	protected function installDefaults():void {
		menuItem.setHorizontalAlignment(AsWingConstants.LEFT);
		menuItem.setVerticalAlignment(AsWingConstants.CENTER);
		var pp:String = getPropertyPrefix();
        LookAndFeel.installColorsAndFont(menuItem, pp);
        LookAndFeel.installBorderAndBFDecorators(menuItem, pp);
        LookAndFeel.installBasicProperties(menuItem, pp);
		
		selectionBackground = getColor(pp + "selectionBackground");
		selectionForeground = getColor(pp + "selectionForeground");
		disabledForeground = getColor(pp + "disabledForeground");
		acceleratorForeground = getColor(pp + "acceleratorForeground");
		acceleratorSelectionForeground = getColor(pp + "acceleratorSelectionForeground");
		acceleratorFont = getFont(pp + "acceleratorFont");
		acceleratorFontValidated = false;
		
		if(menuItem.getMargin() is UIResource) {
			menuItem.setMargin(getInsets(pp + "margin"));
		}
		
		arrowIcon = getIcon(pp + "arrowIcon");
		checkIcon = getIcon(pp + "checkIcon");
		installIcon(arrowIcon);
		installIcon(checkIcon);
		
		//TODO get this from UI defaults
		defaultTextIconGap = 4;
	}
	
	protected function installIcon(icon:Icon):void{
		if(icon && icon.getDisplay(menuItem)){
			menuItem.addChild(icon.getDisplay(menuItem));
		}
	}
	
	protected function uninstallIcon(icon:Icon):void{
		if(icon && icon.getDisplay(menuItem)){
			menuItem.removeChild(icon.getDisplay(menuItem));
		}
	}
	
	protected function installComponents():void{
 		textField = AsWingUtils.createLabel(menuItem, "label");
 		accelTextField = AsWingUtils.createLabel(menuItem, "accLabel");
 		menuItem.setFontValidated(false);
	}
	
	protected function installListeners():void{
		menuItem.addEventListener(MouseEvent.ROLL_OVER, ____menuItemRollOver);
		menuItem.addEventListener(MouseEvent.ROLL_OUT, ____menuItemRollOut);
		menuItem.addActionListener(____menuItemAct);
		menuItem.addStateListener(__menuStateChanged);
	}

	protected function uninstallDefaults():void {
 		LookAndFeel.uninstallBorderAndBFDecorators(menuItem);
		uninstallIcon(arrowIcon);
		uninstallIcon(checkIcon);
	}
	
	protected function uninstallComponents():void{
		menuItem.removeChild(textField);
		menuItem.removeChild(accelTextField);
	}
	
	protected function uninstallListeners():void{
		menuItem.removeEventListener(MouseEvent.ROLL_OVER, ____menuItemRollOver);
		menuItem.removeEventListener(MouseEvent.ROLL_OUT, ____menuItemRollOut);
		menuItem.removeActionListener(____menuItemAct);
		menuItem.removeStateListener(__menuStateChanged);
	}
	
	//---------------
	
	public function processKeyEvent(code:uint) : void {
		var manager:MenuSelectionManager = MenuSelectionManager.defaultManager();
		var path:Array = manager.getSelectedPath();
		if(path[path.length-1] != menuItem){
			return;
		}
		if(manager.isEnterKey(code)){
			menuItem.doClick();
			return;
		}
		if(path.length > 1 && path[path.length-1] == menuItem){
			if(manager.isPageNavKey(code)){
				path.pop();
				manager.setSelectedPath(menuItem.stage, path, false);
				MenuElement(path[path.length-1]).processKeyEvent(code);
			}else if(manager.isItemNavKey(code)){
				path.pop();
				if(manager.isPrevItemKey(code)){
					path.push(manager.prevSubElement(MenuElement(path[path.length-1]), menuItem));
				}else{
					path.push(manager.nextSubElement(MenuElement(path[path.length-1]), menuItem));
				}
				manager.setSelectedPath(menuItem.stage, path, false);
			}
		}
	}
	
	protected function __menuItemRollOver(e:MouseEvent):void{
		MenuSelectionManager.defaultManager().setSelectedPath(menuItem.stage, getPath(), false);
		menuItem.repaint();
	}
	
	protected function __menuItemRollOut(e:MouseEvent):void{
		var path:Array = MenuSelectionManager.defaultManager().getSelectedPath();
		if(path.length > 1 && path[path.length-1] == menuItem){
			path.pop();
			MenuSelectionManager.defaultManager().setSelectedPath(menuItem.stage, path, false);
		}
		menuItem.repaint();
	}
	
	protected function __menuItemAct(e:AWEvent):void{
		MenuSelectionManager.defaultManager().clearSelectedPath(false);
		menuItem.repaint();
	}
    
    protected function __menuStateChanged(e:Event):void{
    	menuItem.repaint();
    }

	private function ____menuItemRollOver(e:MouseEvent):void{
		__menuItemRollOver(e);
	}
	private function ____menuItemRollOut(e:MouseEvent):void{
		__menuItemRollOut(e);
	}
	private function ____menuItemAct(e:AWEvent):void{
		__menuItemAct(e);
	}    
	
	//---------------
	
	/**
	 * SubUI override this to do different
	 */
	protected function isMenu():Boolean{
		return false;
	}
	
	/**
	 * SubUI override this to do different
	 */
	protected function isTopMenu():Boolean{
		return false;
	}
	
	/**
	 * SubUI override this to do different
	 */
	protected function shouldPaintSelected():Boolean{
		return menuItem.getModel().isRollOver();
	}
	
    public function getPath():Array { //MenuElement[]
        var m:MenuSelectionManager = MenuSelectionManager.defaultManager();
        var oldPath:Array = m.getSelectedPath();
        var newPath:Array;
        var i:int = oldPath.length;
        if (i == 0){
            return [];
        }
        var parent:Component = menuItem.getParent();
        if (MenuElement(oldPath[i-1]).getMenuComponent() == parent) {
            // The parent popup menu is the last so far
            newPath = oldPath.concat();
            newPath.push(menuItem);
        } else {
            // A sibling menuitem is the current selection
            // 
            //  This probably needs to handle 'exit submenu into 
            // a menu item.  Search backwards along the current
            // selection until you find the parent popup menu,
            // then copy up to that and add yourself...
            var j:int;
            for (j = oldPath.length-1; j >= 0; j--) {
                if (MenuElement(oldPath[j]).getMenuComponent() == parent){
                    break;
                }
            }
            newPath = oldPath.slice(0, j+1);
            newPath.push(menuItem);
        }
        return newPath;
    }	
    
	override public function paint(c:Component, g:Graphics2D, b:IntRectangle):void{
		var mi:JMenuItem = JMenuItem(c);
		paintMenuItem(mi, g, b, checkIcon, arrowIcon,
					  selectionBackground, selectionForeground,
					  defaultTextIconGap);
	}
	
	protected function paintMenuItem(b:JMenuItem, g:Graphics2D, r:IntRectangle, checkIcon:Icon, arrowIcon:Icon, 
		background:ASColor, foreground:ASColor, defaultTextIconGap:int):void{
		
		var model:ButtonModel = b.getModel();
		resetRects();
		viewRect.setRect( r );

		var font:ASFont = b.getFont();

		var acceleratorText:String = getAcceleratorText(b);
		
		// layout the text and icon
		var text:String = layoutMenuItem(
			font, b.getDisplayText(), acceleratorFont, acceleratorText, b.getIcon(),
			checkIcon, arrowIcon,
			b.getVerticalAlignment(), b.getHorizontalAlignment(),
			b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
			viewRect, iconRect, textRect, acceleratorRect, 
			checkIconRect, arrowIconRect,
			b.getDisplayText() == null ? 0 : defaultTextIconGap,
			defaultTextIconGap
		);
		
		// Paint background
		paintMenuBackground(b, g, r, background);
		
		var isSelected:Boolean = shouldPaintSelected();
		
		// Paint the Check
		paintCheckIcon(b, useCheckAndArrow(), 
			g, checkIconRect.x, checkIconRect.y);

		var icon:Icon = null;
		// Paint the Icon
		if(b.getIcon() != null) { 
			if(!model.isEnabled()) {
				icon = b.getDisabledIcon();
			} else if(model.isPressed() && model.isArmed()) {
				icon = b.getPressedIcon();
				if(icon == null) {
					// Use default icon
					icon = b.getIcon();
				} 
			} else {
				icon = b.getIcon();
			}
		}
		paintIcon(b, icon, g, iconRect.x, iconRect.y);
		var tc:ASColor;
		// Draw the Text
		if(text != null && text != "") {
			tc = b.getForeground();
			if(isSelected){
				tc = selectionForeground;
			}
			if(!b.isEnabled()){
				if(disabledForeground != null){
					tc = disabledForeground;
				}else{
					tc = BasicGraphicsUtils.getDisabledColor(b);
				}
			}
			textField.visible = true;
			paintTextField(b, textRect, textField, text, font, tc, !b.isFontValidated());
			b.setFontValidated(true);
		}else{
			textField.visible = false;
		}
	
		// Draw the Accelerator Text
		if(acceleratorText != null && acceleratorText !="") {
			//Get the maxAccWidth from the parent to calculate the offset.
			var accOffset:int = 0;
			var parent:Container = menuItem.getParent();
			if (parent != null) {
				var p:Container = parent;
				var maxValueInt:int;
				if(p.getClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH) == undefined){
					maxValueInt = acceleratorRect.width;
				}else{
					maxValueInt = p.getClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH);
				}
				
				//Calculate the offset, with which the accelerator texts will be drawn with.
				accOffset = maxValueInt - acceleratorRect.width;
			}
	  		var accTextFieldRect:IntRectangle = new IntRectangle();
			accTextFieldRect.x = acceleratorRect.x - accOffset;
			accTextFieldRect.y = acceleratorRect.y;
			tc = acceleratorForeground;
			if(!model.isEnabled()) {
				if(disabledForeground != null){
					tc = disabledForeground;
				}else{
					tc = BasicGraphicsUtils.getDisabledColor(b);
				}
			} else if (isSelected) {
				tc = acceleratorSelectionForeground;
			}
			accelTextField.visible = true;
			paintTextField(b, accTextFieldRect, accelTextField, acceleratorText, acceleratorFont, tc, !acceleratorFontValidated);
			acceleratorFontValidated = true;
		}else{
			accelTextField.visible = false;
		}

		// Paint the Arrow
		paintArrowIcon(b, useCheckAndArrow(), 
			g, arrowIconRect.x, arrowIconRect.y);
	 }
	
	protected function paintCheckIcon(b:JMenuItem, isPaint:Boolean, g:Graphics2D, x:int, y:int):void{
		if(!checkIcon) return;
		
		if(!isPaint){
			setIconVisible(checkIcon, false);
		}else{
			setIconVisible(checkIcon, true);
			checkIcon.updateIcon(b, g, x, y);
		}
	}
	
	protected function paintArrowIcon(b:JMenuItem, isPaint:Boolean, g:Graphics2D, x:int, y:int):void{
		if(!arrowIcon) return;
		
		if(!isPaint){
			setIconVisible(arrowIcon, false);
		}else{
			setIconVisible(arrowIcon, true);
			arrowIcon.updateIcon(b, g, x, y);
		}	
	}
	
	protected function paintIcon(b:JMenuItem, icon:Icon, g:Graphics2D, x:int, y:int):void{
        var icons:Array = getIcons();
        for(var i:int=0; i<icons.length; i++){
        	var ico:Icon = icons[i];
			setIconVisible(ico, false);
        }
        if(icon != null){
        	setIconVisible(icon, true);
        	icon.updateIcon(b, g, x, y);
        }	
	}
	
    protected function setIconVisible(icon:Icon, visible:Boolean):void{
    	if(icon.getDisplay(menuItem) != null){
    		icon.getDisplay(menuItem).visible = visible;
    	}
    }
    
    protected function getIcons():Array{
    	var arr:Array = new Array();
    	var button:AbstractButton = menuItem;
    	if(button.getIcon() != null){
    		arr.push(button.getIcon());
    	}
    	if(button.getDisabledIcon() != null){
    		arr.push(button.getDisabledIcon());
    	}
    	if(button.getSelectedIcon() != null){
    		arr.push(button.getSelectedIcon());
    	}
    	if(button.getDisabledSelectedIcon() != null){
    		arr.push(button.getDisabledSelectedIcon());
    	}
    	if(button.getRollOverIcon() != null){
    		arr.push(button.getRollOverIcon());
    	}
    	if(button.getRollOverSelectedIcon() != null){
    		arr.push(button.getRollOverSelectedIcon());
    	}
    	if(button.getPressedIcon() != null){
    		arr.push(button.getPressedIcon());
    	}
    	return arr;
    }	
	
	protected function paintMenuBackground(menuItem:JMenuItem, g:Graphics2D, r:IntRectangle, bgColor:ASColor):void {
		var color:ASColor = bgColor;
		if(menuItem.isOpaque()) {
			if (!shouldPaintSelected()) {
				color = menuItem.getBackground();
			}
			g.fillRectangle(new SolidBrush(color), r.x, r.y, r.width, r.height);
		}else if(shouldPaintSelected() && (menuItem.getBackgroundDecorator() == null || menuItem.getBackgroundDecorator() == DefaultEmptyDecoraterResource.INSTANCE)){
			g.fillRectangle(new SolidBrush(color), r.x, r.y, r.width, r.height);
		}
	}	
	

	protected function paintTextField(b:JMenuItem, tRect:IntRectangle, textField:TextField, text:String, font:ASFont, color:ASColor, validateFont:Boolean):void{
		if(textField.text != text){
			textField.text = text;
		}
		if(validateFont){
			AsWingUtils.applyTextFont(textField, font);
		}
		AsWingUtils.applyTextColor(textField, color);
		textField.x = tRect.x;
		textField.y = tRect.y;
		if(b.getMnemonicIndex() >= 0){
			textField.setTextFormat(
				new TextFormat(null, null, null, null, null, true), 
				b.getMnemonicIndex());
		}
	}	
	

	// these rects are used for painting and preferredsize calculations.
	// they used to be regenerated constantly.  Now they are reused.
	protected static var zeroRect:IntRectangle = new IntRectangle();
	protected static var iconRect:IntRectangle = new IntRectangle();
	protected static var textRect:IntRectangle = new IntRectangle();
	protected static var acceleratorRect:IntRectangle = new IntRectangle();
	protected static var checkIconRect:IntRectangle = new IntRectangle();
	protected static var arrowIconRect:IntRectangle = new IntRectangle();
	protected static var viewRect:IntRectangle = new IntRectangle();
	protected static var r:IntRectangle = new IntRectangle();

	protected function resetRects():void {
		iconRect.setRect(zeroRect);
		textRect.setRect(zeroRect);
		acceleratorRect.setRect(zeroRect);
		checkIconRect.setRect(zeroRect);
		arrowIconRect.setRect(zeroRect);
		viewRect.setRectXYWH(0, 0, 100000, 100000);
		r.setRect(zeroRect);
	}	

	/**
	 * Returns the a menu item's preferred size with specified icon and text.
	 */
	protected function getPreferredMenuItemSize(b:JMenuItem, 
													 checkIcon:Icon,
													 arrowIcon:Icon,
													 defaultTextIconGap:int):IntDimension{
		var icon:Icon = b.getIcon(); 
		var text:String = b.getDisplayText();
		var acceleratorText:String = getAcceleratorText(b);

		var font:ASFont = b.getFont();

		resetRects();
		
		layoutMenuItem(
				  font, text, acceleratorFont, acceleratorText, icon, checkIcon, arrowIcon,
				  b.getVerticalAlignment(), b.getHorizontalAlignment(),
				  b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
				  viewRect, iconRect, textRect, acceleratorRect, checkIconRect, arrowIconRect,
				  text == null ? 0 : defaultTextIconGap,
				  defaultTextIconGap
				  );
		// find the union of the icon and text rects
		r = textRect.union(iconRect);
		
		// To make the accelerator texts appear in a column, find the widest MenuItem text
		// and the widest accelerator text.

		//Get the parent, which stores the information.
		var parent:Container = menuItem.getParent();
	
		//Check the parent, and see that it is not a top-level menu.
		if (parent != null &&  !isTopMenu()) {
			var p:Container = parent;
			//Get widest text so far from parent, if no one exists null is returned.
			var maxTextValue:int = p.getClientProperty(BasicMenuItemUI.MAX_TEXT_WIDTH);
			var maxAccValue:int = p.getClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH);
						
			//Compare the text widths, and adjust the r.width to the widest.
			if (r.width < maxTextValue) {
				r.width = maxTextValue;
			} else {
				p.putClientProperty(BasicMenuItemUI.MAX_TEXT_WIDTH, r.width);
			}
			
		  //Compare the accelarator widths.
			if (acceleratorRect.width > maxAccValue) {
				maxAccValue = acceleratorRect.width;
				p.putClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH, acceleratorRect.width);
			}
			
			//Add on the widest accelerator 
			r.width += maxAccValue;
			r.width += defaultTextIconGap;
		}
	
		if(useCheckAndArrow()) {
			// Add in the checkIcon
			r.width += checkIconRect.width;
			r.width += defaultTextIconGap;
			
			// Add in the arrowIcon
			r.width += defaultTextIconGap;
			r.width += arrowIconRect.width;
		}	

		r.width += 2*defaultTextIconGap;

		var insets:Insets = b.getInsets();
		if(insets != null) {
			r.width += insets.left + insets.right;
			r.height += insets.top + insets.bottom;
		}
		r.width = Math.ceil(r.width);
		r.height = Math.ceil(r.height);
		// if the width is even, bump it up one. This is critical
		// for the focus dash line to draw properly
		if(r.width%2 == 0) {
			r.width++;
		}

		// if the height is even, bump it up one. This is critical
		// for the text to center properly
		if(r.height%2 == 0) {
			r.height++;
		}
		return r.getSize();
	}
	
	protected function getAcceleratorText(b:JMenuItem):String{
		if(b.getAccelerator() == null){
			return "";
		}else{
			return b.getAccelerator().getDescription();
		}
	}
	
	/** 
	 * Compute and return the location of the icons origin, the 
	 * location of origin of the text baseline, and a possibly clipped
	 * version of the compound labels string.  Locations are computed
	 * relative to the viewRect rectangle. 
	 */
	protected function layoutMenuItem(
		font:ASFont, 
		text:String, 
		accelFont:ASFont, 
		acceleratorText:String, 
		icon:Icon, 
		checkIcon:Icon, 
		arrowIcon:Icon, 
		verticalAlignment:int, 
		horizontalAlignment:int, 
		verticalTextPosition:int, 
		horizontalTextPosition:int, 
		viewRect:IntRectangle, 
		iconRect:IntRectangle, 
		textRect:IntRectangle, 
		acceleratorRect:IntRectangle, 
		checkIconRect:IntRectangle, 
		arrowIconRect:IntRectangle, 
		textIconGap:int, 
		menuItemGap:int
		):String
	{

		AsWingUtils.layoutCompoundLabel(menuItem, font, text, icon, verticalAlignment, 
							horizontalAlignment, verticalTextPosition, 
							horizontalTextPosition, viewRect, iconRect, textRect, 
							textIconGap);
										
		/* Initialize the acceelratorText bounds rectangle textRect.  If a null 
		 * or and empty String was specified we substitute "" here 
		 * and use 0,0,0,0 for acceleratorTextRect.
		 */
		if(acceleratorText == null || acceleratorText == "") {
			acceleratorRect.width = acceleratorRect.height = 0;
			acceleratorText = "";
		}else {
			var td:IntDimension = accelFont.computeTextSize(acceleratorText);
			acceleratorRect.width = td.width;
			acceleratorRect.height = td.height;
		}

		/* Initialize the checkIcon bounds rectangle's width & height.
		 */
		if( useCheckAndArrow()) {
			if (checkIcon != null) {
				checkIconRect.width = checkIcon.getIconWidth(menuItem);
				checkIconRect.height = checkIcon.getIconHeight(menuItem);
			} else {
				checkIconRect.width = checkIconRect.height = 0;
			}
			/* Initialize the arrowIcon bounds rectangle width & height.
			 */
			if (arrowIcon != null) {
				arrowIconRect.width = arrowIcon.getIconWidth(menuItem);
				arrowIconRect.height = arrowIcon.getIconHeight(menuItem);
			} else {
				arrowIconRect.width = arrowIconRect.height = 0;
			}
		}

		var labelRect:IntRectangle = iconRect.union(textRect);
		textRect.x += menuItemGap;
		iconRect.x += menuItemGap;

		// Position the Accelerator text rect
		acceleratorRect.x = viewRect.x + viewRect.width - arrowIconRect.width - menuItemGap*2 - acceleratorRect.width;
		
		// Position the Check and Arrow Icons 
		if (useCheckAndArrow()) {
			checkIconRect.x = viewRect.x + menuItemGap;
			textRect.x += menuItemGap + checkIconRect.width;
			iconRect.x += menuItemGap + checkIconRect.width;
			arrowIconRect.x = viewRect.x + viewRect.width - menuItemGap - arrowIconRect.width;
		}

		// Align the accelertor text and the check and arrow icons vertically
		// with the center of the label rect.  
		acceleratorRect.y = labelRect.y + Math.floor(labelRect.height/2) - Math.floor(acceleratorRect.height/2);
		if( useCheckAndArrow() ) {
			arrowIconRect.y = labelRect.y + Math.floor(labelRect.height/2) - Math.floor(arrowIconRect.height/2);
			checkIconRect.y = labelRect.y + Math.floor(labelRect.height/2) - Math.floor(checkIconRect.height/2);
		}

		return text;
	}	
	
	protected function useCheckAndArrow():Boolean{
		return !isTopMenu();
	}
	
	override public function getPreferredSize(c:Component):IntDimension{
		var b:JMenuItem = JMenuItem(c);
		return getPreferredMenuItemSize(b, checkIcon, arrowIcon, defaultTextIconGap);
	}

	override public function getMinimumSize(c:Component):IntDimension{
		var size:IntDimension = menuItem.getInsets().getOutsideSize();
		if(menuItem.getMargin() != null){
			size = menuItem.getMargin().getOutsideSize(size);
		}
		return size;
	}

	override public function getMaximumSize(c:Component):IntDimension{
		return IntDimension.createBigDimension();
	}	
}
}