/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.ASTextExtent;
import org.aswing.ASWingUtils;
import org.aswing.ButtonModel;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
import org.aswing.Insets;
import org.aswing.overflow.JMenuBar;
import org.aswing.overflow.JMenuItem;
import org.aswing.LookAndFeel;
import org.aswing.MenuElement;
import org.aswing.MenuSelectionManager;
import org.aswing.plaf.MenuItemUI;
import org.aswing.plaf.UIResource;
import org.aswing.UIManager;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicMenuItemUI extends MenuItemUI {
	
	/* Client Property keys for text and accelerator text widths */
	public static var MAX_TEXT_WIDTH:String =  "maxTextWidth";
	public static var MAX_ACC_WIDTH:String  =  "maxAccWidth";
	
	private var menuItem:JMenuItem;
	
	private var selectionBackground:ASColor;
	private var selectionForeground:ASColor;	
	private var disabledForeground:ASColor;
	private var acceleratorForeground:ASColor;
	private var acceleratorSelectionForeground:ASColor;
	
	private var defaultTextIconGap:Number;
	private var acceleratorFont:ASFont;

	private var arrowIcon:Icon;
	private var checkIcon:Icon;
	
	private var textField:TextField;
	private var accelTextField:TextField;
	
	private var menuItemLis:Object;
	
	public function BasicMenuItemUI() {
		super();
	}
	
	public function installUI(c:Component):Void {
		menuItem = JMenuItem(c);
		installDefaults();
		installListeners();
	}

	public function uninstallUI(c:Component):Void {
		menuItem = JMenuItem(c);
		uninstallDefaults();
		uninstallListeners();
		textField.removeTextField();
		accelTextField.removeTextField();
		textField = null;
		accelTextField = null;
	}

	private function getPropertyPrefix():String {
		return "MenuItem";
	}	

	private function installDefaults():Void {
		menuItem.setHorizontalAlignment(JMenuItem.LEFT);
		menuItem.setVerticalAlignment(JMenuItem.CENTER);
		var pp:String = getPropertyPrefix() + ".";
		LookAndFeel.installColorsAndFont(menuItem, pp + "background", pp + "foreground", pp + "font");
		LookAndFeel.installBorder(menuItem, pp+"border");
		LookAndFeel.installBasicProperties(menuItem, pp);
		
		selectionBackground = UIManager.getColor(pp + "selectionBackground");
		selectionForeground = UIManager.getColor(pp + "selectionForeground");
		disabledForeground = UIManager.getColor(pp + "disabledForeground");
		acceleratorForeground = UIManager.getColor(pp + "acceleratorForeground");
		acceleratorSelectionForeground = UIManager.getColor(pp + "acceleratorSelectionForeground");
		acceleratorFont = UIManager.getFont(pp + "acceleratorFont");
		
		if(menuItem.getMargin() === undefined || (menuItem.getMargin() instanceof UIResource)) {
			menuItem.setMargin(UIManager.getInsets(pp + "margin"));
		}
		
		arrowIcon = UIManager.getIcon(pp + "arrowIcon");
		checkIcon = UIManager.getIcon(pp + "checkIcon");
		
		//TODO get this from UI defaults
		defaultTextIconGap = 4;
	}
	
	private function installListeners():Void{
		menuItemLis = new Object();
		menuItemLis[JMenuItem.ON_ROLLOVER] = Delegate.create(this, ____menuItemRollOver);
		menuItemLis[JMenuItem.ON_ROLLOUT] = Delegate.create(this, ____menuItemRollOut);
		menuItemLis[JMenuItem.ON_RELEASEOUTSIDE] = menuItemLis[JMenuItem.ON_ROLLOUT];
		menuItemLis[JMenuItem.ON_ACT] = Delegate.create(this, ____menuItemAct);
		menuItemLis[JMenuItem.ON_STATE_CHANGED] = Delegate.create(this, __menuStateChanged);
		menuItem.addEventListener(menuItemLis);
	}

	private function uninstallDefaults():Void {
		LookAndFeel.uninstallBorder(menuItem);
	}
	
	private function uninstallListeners():Void{
		menuItem.removeEventListener(menuItemLis);
		menuItemLis = null;
	}
	
	//---------------
	
	public function processKeyEvent(code : Number) : Void {
		var manager:MenuSelectionManager = MenuSelectionManager.defaultManager();
		var path:Array = manager.getSelectedPath();
		if(path[path.length-1] != menuItem){
			return;
		}
		if(manager.isEnterKey(code)){
			menuItem.click();
			return;
		}
		if(path.length > 1 && path[path.length-1] == menuItem){
			if(manager.isPageNavKey(code)){
				path.pop();
				manager.setSelectedPath(path);
				MenuElement(path[path.length-1]).processKeyEvent(code);
			}else if(manager.isItemNavKey(code)){
				path.pop();
				if(manager.isPrevItemKey(code)){
					path.push(manager.prevSubElement(MenuElement(path[path.length-1]), menuItem));
				}else{
					path.push(manager.nextSubElement(MenuElement(path[path.length-1]), menuItem));
				}
				manager.setSelectedPath(path);
			}
		}
	}
	
	private function __menuItemRollOver():Void{
		MenuSelectionManager.defaultManager().setSelectedPath(getPath());
		menuItem.repaint();
	}
	
	private function __menuItemRollOut():Void{
		var path:Array = MenuSelectionManager.defaultManager().getSelectedPath();
		if(path.length > 1 && path[path.length-1] == menuItem){
			path.pop();
			MenuSelectionManager.defaultManager().setSelectedPath(path);
		}
		menuItem.repaint();
	}
	
	private function __menuItemAct():Void{
		MenuSelectionManager.defaultManager().clearSelectedPath();
		menuItem.repaint();
	}
    
    private function __menuStateChanged():Void{
    	menuItem.repaint();
    }

	private function ____menuItemRollOver():Void{
		__menuItemRollOver();
	}
	private function ____menuItemRollOut():Void{
		__menuItemRollOut();
	}
	private function ____menuItemAct():Void{
		__menuItemAct();
	}    
	
	//---------------
	
	/**
	 * SubUI override this to do different
	 */
	private function isMenu():Boolean{
		return false;
	}
	
	/**
	 * SubUI override this to do different
	 */
	private function isTopMenu():Boolean{
		return false;
	}
	
	/**
	 * SubUI override this to do different
	 */
	private function shouldPaintSelected():Boolean{
		return menuItem.getModel().isRollOver();
	}
	
    public function getPath():Array { //MenuElement[]
        var m:MenuSelectionManager = MenuSelectionManager.defaultManager();
        var oldPath:Array = m.getSelectedPath();
        var newPath:Array;
        var i:Number = oldPath.length;
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
            var j:Number;
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
    
	public function create(c:Component):Void{
		textField = c.createTextField("txt");
		accelTextField = c.createTextField("acctxt");
		c.setFontValidated(false);
	}	
	
	public function paint(c:Component, g:Graphics, b:Rectangle):Void{
		var mi:JMenuItem = JMenuItem(c);
		paintMenuItem(mi, g, b, checkIcon, arrowIcon,
					  selectionBackground, selectionForeground,
					  defaultTextIconGap);
	}
	
	private function paintMenuItem(b:JMenuItem, g:Graphics, r:Rectangle, checkIcon:Icon, arrowIcon, 
		background:ASColor, foreground:ASColor, defaultTextIconGap:Number):Void{
		
		var model:ButtonModel = b.getModel();
		resetRects();
		viewRect.setRect( r );

		var font:ASFont = b.getFont();

		var acceleratorText:String = getAcceleratorText(b);
		
		// layout the text and icon
		var text:String = layoutMenuItem(
			font, b.getText(), acceleratorFont, acceleratorText, b.getIcon(),
			checkIcon, arrowIcon,
			b.getVerticalAlignment(), b.getHorizontalAlignment(),
			b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
			viewRect, iconRect, textRect, acceleratorRect, 
			checkIconRect, arrowIconRect,
			b.getText() == null ? 0 : defaultTextIconGap,
			defaultTextIconGap
		);
		
		// Paint background
		paintMenuBackground(b, g, r, background);
		
		var isSelected:Boolean = shouldPaintSelected();
		
		// Paint the Check
		paintCheckIcon(b, useCheckAndArrow() ? checkIcon : null, 
			g, checkIconRect.x, checkIconRect.y);

		var icon:Icon = null;
		// Paint the Icon
		if(b.getIcon() != null) { 
			if(!model.isEnabled()) {
				icon = b.getDisabledIcon();
			} else if(model.isPressed() && model.isRollOver()) {
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

		// Draw the Text
		if(text != null && text != "") {
			var tc:ASColor = b.getForeground();
			if(isSelected){
				tc = selectionForeground;
			}
			if(!b.isEnabled()){
				if(disabledForeground != null){
					tc = disabledForeground;
				}else{
					tc = b.getBackground().brighter();
				}
			}
			textField._visible = true;
			paintTextField(b, textRect, textField, text, font, tc, true);
		}else{
			textField._visible = false;
		}
	
		// Draw the Accelerator Text
		if(acceleratorText != null && acceleratorText !="") {
			//Get the maxAccWidth from the parent to calculate the offset.
			var accOffset:Number = 0;
			var parent:Container = menuItem.getParent();
			if (parent != null) {
				var p:Container = parent;
				var maxValueInt:Number = p.getClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH);
				if(maxValueInt == null) maxValueInt = acceleratorRect.width;
				
				//Calculate the offset, with which the accelerator texts will be drawn with.
				accOffset = maxValueInt - acceleratorRect.width;
			}
	  		var accTextFieldRect:Rectangle = new Rectangle();
			accTextFieldRect.x = acceleratorRect.x - accOffset;
			accTextFieldRect.y = acceleratorRect.y;
			var tc:ASColor = acceleratorForeground;
			if(!model.isEnabled()) {
				if(disabledForeground != null){
					tc = disabledForeground;
				}else{
					tc = b.getBackground().brighter();
				}
			} else if (isSelected) {
				tc = acceleratorSelectionForeground;
			}
			accelTextField._visible = true;
			paintTextField(b, accTextFieldRect, accelTextField, acceleratorText, acceleratorFont, tc, false);
		}else{
			accelTextField._visible = false;
		}

		// Paint the Arrow
		paintArrowIcon(b, useCheckAndArrow() ? arrowIcon : null, 
			g, arrowIconRect.x, arrowIconRect.y);
	 }
	
	private var lastPaintedCheckIcon:Icon;
	private function paintCheckIcon(b:JMenuItem, icon:Icon, g:Graphics, x:Number, y:Number):Void{
		if(lastPaintedCheckIcon != icon){
			if(lastPaintedCheckIcon != null){
				lastPaintedCheckIcon.uninstallIcon(b);
			}
			lastPaintedCheckIcon = icon;
		}
		icon.paintIcon(b, g, x, y);
	}
	
	private var lastPaintedArrowIcon:Icon;
	private function paintArrowIcon(b:JMenuItem, icon:Icon, g:Graphics, x:Number, y:Number):Void{
		if(lastPaintedArrowIcon != icon){
			if(lastPaintedArrowIcon != null){
				lastPaintedArrowIcon.uninstallIcon(b);
			}
			lastPaintedArrowIcon = icon;
		}
		icon.paintIcon(b, g, x, y);		
	}
	
	private var lastPaintedIcon:Icon;
	private function paintIcon(b:JMenuItem, icon:Icon, g:Graphics, x:Number, y:Number):Void{
		if(lastPaintedIcon != icon){
			if(lastPaintedIcon != null){
				lastPaintedIcon.uninstallIcon(b);
			}
			lastPaintedIcon = icon;
		}
		icon.paintIcon(b, g, x, y);		
	}
	
	private function paintMenuBackground(menuItem:JMenuItem, g:Graphics, r:Rectangle, bgColor:ASColor):Void {
		var model:ButtonModel = menuItem.getModel();
		var color:ASColor = bgColor;
		if(menuItem.isOpaque()) {
			if (!shouldPaintSelected()) {
				color = menuItem.getBackground();
			}
			g.fillRectangle(new SolidBrush(color), r.x, r.y, r.width, r.height);
		}else if(shouldPaintSelected()){
			g.fillRectangle(new SolidBrush(color), r.x, r.y, r.width, r.height);
		}
	}	
	

	private function paintTextField(b:JMenuItem, tRect:Rectangle, textField:TextField, text:String, font:ASFont, color:ASColor, validateFont:Boolean):Void{
		if(textField.text != text){
			textField.text = text;
		}
		if(validateFont && !b.isFontValidated()){
			ASWingUtils.applyTextFont(textField, font);
			b.setFontValidated(true);
		}
		ASWingUtils.applyTextColor(textField, color);
		textField._x = tRect.x;
		textField._y = tRect.y;
	}	
	

	// these rects are used for painting and preferredsize calculations.
	// they used to be regenerated constantly.  Now they are reused.
	private static var zeroRect:Rectangle;
	private static var iconRect:Rectangle;
	private static var textRect:Rectangle;
	private static var acceleratorRect:Rectangle;
	private static var checkIconRect:Rectangle;
	private static var arrowIconRect:Rectangle;
	private static var viewRect:Rectangle;
	private static var r:Rectangle;

	private function resetRects():Void {
		if(zeroRect == null){
			zeroRect = new Rectangle(0, 0, 0, 0);
			iconRect = new Rectangle();
			textRect = new Rectangle();
			acceleratorRect = new Rectangle();
			checkIconRect = new Rectangle();
			arrowIconRect = new Rectangle();
			viewRect = new Rectangle(0, 0, Number.MAX_VALUE, Number.MAX_VALUE);
			r = new Rectangle();
		}
		iconRect.setRect(zeroRect);
		textRect.setRect(zeroRect);
		acceleratorRect.setRect(zeroRect);
		checkIconRect.setRect(zeroRect);
		arrowIconRect.setRect(zeroRect);
		viewRect.setRect(0, 0, 100000, 100000);
		r.setRect(zeroRect);
	}	

	/**
	 * Returns the a menu item's preferred size with specified icon and text.
	 */
	private function getPreferredMenuItemSize(b:JMenuItem, 
													 checkIcon:Icon,
													 arrowIcon:Icon,
													 defaultTextIconGap:Number):Dimension{
		var icon:Icon = b.getIcon(); 
		var text:String = b.getText();
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
			var maxTextWidth:Number = p.getClientProperty(BasicMenuItemUI.MAX_TEXT_WIDTH);
			var maxAccWidth:Number = p.getClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH);
			
			var maxTextValue:Number = maxTextWidth!=null ? maxTextWidth : 0;
			var maxAccValue:Number = maxAccWidth!=null ? maxAccWidth : 0;
			
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
	
	private function getAcceleratorText(b:JMenuItem):String{
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
	private function layoutMenuItem(
		font:ASFont, 
		text:String, 
		accelFont:ASFont, 
		acceleratorText:String, 
		icon:Icon, 
		checkIcon:Icon, 
		arrowIcon:Icon, 
		verticalAlignment:Number, 
		horizontalAlignment:Number, 
		verticalTextPosition:Number, 
		horizontalTextPosition:Number, 
		viewRect:Rectangle, 
		iconRect:Rectangle, 
		textRect:Rectangle, 
		acceleratorRect:Rectangle, 
		checkIconRect:Rectangle, 
		arrowIconRect:Rectangle, 
		textIconGap:Number, 
		menuItemGap:Number
		):String
	{

		ASWingUtils.layoutCompoundLabel(font, text, icon, verticalAlignment, 
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
			var tf:ASTextExtent = accelFont.getASTextFormat().getTextExtent(acceleratorText);
			acceleratorRect.width = Math.ceil(tf.getTextFieldWidth());
			acceleratorRect.height = Math.ceil(tf.getTextFieldHeight());
		}

		/* Initialize the checkIcon bounds rectangle's width & height.
		 */
		if( useCheckAndArrow()) {
			if (checkIcon != null) {
				checkIconRect.width = Math.ceil(checkIcon.getIconWidth());
				checkIconRect.height = Math.ceil(checkIcon.getIconHeight());
			} else {
				checkIconRect.width = checkIconRect.height = 0;
			}
			/* Initialize the arrowIcon bounds rectangle width & height.
			 */
			if (arrowIcon != null) {
				arrowIconRect.width = Math.ceil(arrowIcon.getIconWidth());
				arrowIconRect.height = Math.ceil(arrowIcon.getIconHeight());
			} else {
				arrowIconRect.width = arrowIconRect.height = 0;
			}
		}

		var labelRect:Rectangle = iconRect.union(textRect);
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
	
	private function useCheckAndArrow():Boolean{
		return !isTopMenu();
	}
	
	public function getPreferredSize(c:Component):Dimension{
		var b:JMenuItem = JMenuItem(c);
		return getPreferredMenuItemSize(b, checkIcon, arrowIcon, defaultTextIconGap);
	}

	public function getMinimumSize(c:Component):Dimension{
		var size:Dimension = menuItem.getInsets().getOutsideSize();
		if(menuItem.getMargin() != null){
			size = menuItem.getMargin().getOutsideSize(size);
		}
		return size;
	}

	public function getMaximumSize(c:Component):Dimension{
		return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
	}
}