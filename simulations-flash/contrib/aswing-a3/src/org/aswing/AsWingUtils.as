/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import flash.display.*;
import flash.geom.Point;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFieldType;
import flash.text.TextFormat;
import flash.utils.Dictionary;

import org.aswing.geom.*;	

/**
 * A collection of utility methods for AsWing.
 * @author iiley
 */
public class AsWingUtils{

    /**
     * A fast access to ASWingConstants Constant
     * @see org.aswing.ASWingConstants
     */
    public static var CENTER:Number  = AsWingConstants.CENTER;
    /**
     * A fast access to AsWingConstants Constant
     * @see org.aswing.AsWingConstants
     */
    public static var TOP:Number     = AsWingConstants.TOP;
    /**
     * A fast access to AsWingConstants Constant
     * @see org.aswing.AsWingConstants
     */
    public static var LEFT:Number    = AsWingConstants.LEFT;
    /**
     * A fast access to AsWingConstants Constant
     * @see org.aswing.AsWingConstants
     */
    public static var BOTTOM:Number  = AsWingConstants.BOTTOM;
    /**
     * A fast access to AsWingConstants Constant
     * @see org.aswing.AsWingConstants
     */
    public static var RIGHT:Number   = AsWingConstants.RIGHT;
    /**
     * A fast access to AsWingConstants Constant
     * @see org.aswing.AsWingConstants
     */        
    public static var HORIZONTAL:Number = AsWingConstants.HORIZONTAL;
    /**
     * A fast access to AsWingConstants Constant
     * @see org.aswing.AsWingConstants
     */
    public static var VERTICAL:Number   = AsWingConstants.VERTICAL;
    
    /**
     * Shared text field to count the text size
     */
    private static var TEXT_FIELD:TextField = new TextField();
    private static var TEXT_FONT:ASFont = null;
    {
    	TEXT_FIELD.autoSize = TextFieldAutoSize.LEFT;
    	TEXT_FIELD.type = TextFieldType.DYNAMIC;
    }
    
    private static var weakComponentDic:Dictionary = new Dictionary(true);
    
    /**
     * Create a sprite at specified parent with specified name.
     * The created sprite default property is mouseEnabled=false.
     * @return the sprite
     */ 
    public static function createSprite(parent:DisplayObjectContainer=null, name:String=null):Sprite{
    	var sp:Sprite = new Sprite();
		sp.focusRect = false;
    	if(name != null){
    		sp.name = name;
    	}
    	sp.mouseEnabled = false;
    	if(parent != null){
    		parent.addChild(sp);
    	}
    	return sp;
    }
    
    /**
     * Create a disabled TextField at specified parent with specified name.
     * The created sprite default property is mouseEnabled=false, selecteable=false, editable=false 
     * TextFieldAutoSize.LEFT etc.
     * @return the textfield
     */ 
    public static function createLabel(parent:DisplayObjectContainer=null, name:String=null):TextField{
    	var textField:TextField = new TextField();
    	textField.focusRect = false;
    	if(name != null){
    		textField.name = name;
    	}
 		textField.selectable = false;
 		textField.mouseEnabled = false;
 		textField.mouseWheelEnabled = false;
 		textField.autoSize = TextFieldAutoSize.LEFT;
 		textField.tabEnabled = false;
    	if(parent != null){
    		parent.addChild(textField);
    	}
    	return textField;
    }    
    
    /**
     * Create a shape. 
     * @return the sprite
    */
    public static function createShape(parent:DisplayObjectContainer=null, name:String=null):Shape{
    	var sp:Shape = new Shape();
    	if(name != null){
    		sp.name = name;
    	}
    	if(parent != null){
    		parent.addChild(sp);
    	}
    	return sp;
    }
    
    /**
     * Returns whethor or not the display object is showing, which means that 
     * it is visible and it's ancestors(parent, parent's parent ...) is visible and on stage too. 
     * @return trun if showing, not then false.
     */
    public static function isDisplayObjectShowing(dis:DisplayObject):Boolean{
    	if(dis == null || dis.stage == null){
    		return false;
    	}
    	while(dis != null && dis.visible == true){
    		if(dis == dis.stage){
    			return true;
    		}
    		dis = dis.parent;
    	}
    	return false;
    }
    
    /**
     * Returns whether or not the ancestor is the child's display ancestor.
     * @return whether or not the ancestor is the child's display ancestor.
     */
    public static function isAncestor(ancestor:Component, child:Component):Boolean{
    	if(ancestor == null || child == null) 
    		return false;
    		
    	var pa:DisplayObjectContainer = child.parent;
    	while(pa != null){
    		if(pa == ancestor){
    			return true;
    		}
    		pa = pa.parent;
    	}
    	return false;
    }
    
    /**
     * Returns whether or not the ancestor is the child's component ancestor.
     * @return whether or not the ancestor is the child's component ancestor.
     */    
    public static function isAncestorComponent(ancestor:Component, child:Component):Boolean{
    	if(ancestor == null || child == null || !(ancestor is Container)) 
    		return false;
    		
    	var pa:Container = child.getParent();
    	while(pa != null){
    		if(pa == ancestor){
    			return true;
    		}
    		pa = pa.getParent();
    	}
    	return false;
    }
    
    /**
     * Returns whether or not the ancestor is the child's ancestor.
     * @return whether or not the ancestor is the child's ancestor.
     */
    public static function isAncestorDisplayObject(ancestor:DisplayObjectContainer, child:DisplayObject):Boolean{
    	if(ancestor == null || child == null) 
    		return false;
    		
    	var pa:DisplayObjectContainer = child.parent;
    	while(pa != null){
    		if(pa == ancestor){
    			return true;
    		}
    		pa = pa.parent;
    	}
    	return false;
    }
    
    public static function getStageMousePosition(stage:Stage=null):IntPoint{
    	if(stage == null) stage = AsWingManager.getStage();
    	return new IntPoint(stage.mouseX, stage.mouseY);
    }
    
    /**
     * Returns the center position in the stage.
     */
    public static function getScreenCenterPosition():IntPoint{
    	var r:IntRectangle = getVisibleMaximizedBounds();
    	return new IntPoint(r.x + r.width/2, r.y + r.height/2);
    }
    
    /**
     * Locate the popup at center of the stage.
     * @param popup the popup to be located.
     */
    public static function centerLocate(popup:JPopup):void{
    	var p:IntPoint = getScreenCenterPosition();
		p.x = Math.round(p.x - popup.getWidth()/2);
		p.y = Math.round(p.y - popup.getHeight()/2);
		popup.setLocation(p);
    }
    
    /**
     * Returns the currently visible maximized bounds in a display object(viewable the stage area).
     * <p>
     * Note : your stage must be StageAlign.TOP_LEFT align unless this returned value may not be right.
     * </>
     * @param dis the display object, default is stage
     */
    public static function getVisibleMaximizedBounds(dis:DisplayObject=null):IntRectangle{
    	var stage:Stage = dis == null ? null : dis.stage;
    	if(stage == null){
    		stage = AsWingManager.getStage();
    	}
    	if(stage == null){
    		return new IntRectangle(200, 200);//just return a value here
    	}
    	if(stage.scaleMode != StageScaleMode.NO_SCALE){
    		return new IntRectangle(0, 0, stage.stageWidth, stage.stageHeight);
    	}
    	var sw:Number = stage.stageWidth;
        var sh:Number = stage.stageHeight;
        var b:IntRectangle = new IntRectangle(0, 0, sw, sh);
        if(dis != null){
        	var p:Point = dis.globalToLocal(new Point(0, 0));
        	b.setLocation(new IntPoint(p.x, p.y));
        }
        return b;
        /*
        var sa:String = stage.align;
        var initStageSize:IntDimension = AsWingManager.getInitialStageSize();
        var dw:Number = sw - initStageSize.width;
        var dh:Number = sh - initStageSize.height;
        
        //TODO imp when stage resized
        var b:IntRectangle = new IntRectangle(0, 0, sw, sh);
        if(dis != null){
        	var p:Point = dis.globalToLocal(new Point(0, 0));
        	b.setLocation(new IntPoint(p.x, p.y));
        }
        
        switch(sa){
            case StageAlign.TOP:
                b.x -= dw/2;
                break;
            case StageAlign.BOTTOM:
                b.x -= dw/2;
                b.y -= dh;
                break;
            case StageAlign.LEFT:
                b.y -= dh/2;
                break;
            case StageAlign.RIGHT:
                b.x -= dw;
                b.y -= dh/2;
                break;
            case StageAlign.TOP_LEFT:
                break;
            case StageAlign.TOP_RIGHT:
                b.x -= dw;
                break;
            case StageAlign.BOTTOM_LEFT:
                b.y -= dh;
                break;
            case StageAlign.BOTTOM_RIGHT:
                b.x -= dw;
                b.y -= dh;
                break;
            default:
                b.x -= dw/2;
                b.y -= dh/2;
                break;
        }        
        return b;*/
    }
    
    /**
     * Apply the font and color to the textfield.
     * @param text
     * @param font
     * @param color
     */
    public static function applyTextFontAndColor(text:TextField, font:ASFont, color:ASColor):void{
        applyTextFont(text, font);
        applyTextColor(text, color);
    }
    
    /**
     * 
     */
    public static function applyTextFont(text:TextField, font:ASFont):void{
    	font.apply(text);
    }
    
    /**
     * 
     */
    public static function applyTextFormat(text:TextField, textFormat:TextFormat):void{
    	text.setTextFormat(textFormat);
    }    
    
    /**
     * 
     */
    public static function applyTextColor(text:TextField, color:ASColor):void{
        if(text.textColor !== color.getRGB()){
        	text.textColor = color.getRGB();
        }
        if(text.alpha !== color.getAlpha()){
        	text.alpha = color.getAlpha();
        }
    }    
    
    /**
     * Compute and return the location of the icons origin, the
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewR rectangle.
     */
    public static function layoutCompoundLabel(
    	c:Component, 
    	f:ASFont, 
        text:String,
        icon:Icon,
        verticalAlignment:int,
        horizontalAlignment:int,
        verticalTextPosition:int,
        horizontalTextPosition:int,
        viewR:IntRectangle,
        iconR:IntRectangle,
        textR:IntRectangle,
        textIconGap:int):String
    {
        if (icon != null) {
            iconR.width = icon.getIconWidth(c);
            iconR.height = icon.getIconHeight(c);
        }else {
            iconR.width = iconR.height = 0;
        }
                
        var textIsEmpty:Boolean = (text==null || text=="");
        if(textIsEmpty){
            textR.width = textR.height = 0;
        }else{
        	var textS:IntDimension = inter_computeStringSize(f, text);
            textR.width = textS.width;
            textR.height = textS.height;
        }
        
         /* Unless both text and icon are non-null, we effectively ignore
         * the value of textIconGap.  The code that follows uses the
         * value of gap instead of textIconGap.
         */

        var gap:Number = (textIsEmpty || (icon == null)) ? 0 : textIconGap;
        
        if(!textIsEmpty){
            
            /* If the label text string is too wide to fit within the available
             * space "..." and as many characters as will fit will be
             * displayed instead.
             */
            var availTextWidth:Number;

            if (horizontalTextPosition == CENTER) {
                availTextWidth = viewR.width;
            }else {
                availTextWidth = viewR.width - (iconR.width + gap);
            }
            
            if (textR.width > availTextWidth) {
                text = layoutTextWidth(text, textR, availTextWidth, f);
            }
        }

        /* Compute textR.x,y given the verticalTextPosition and
         * horizontalTextPosition properties
         */

        if (verticalTextPosition == TOP) {
            if (horizontalTextPosition != CENTER) {
                textR.y = 0;
            }else {
                textR.y = -(textR.height + gap);
            }
        }else if (verticalTextPosition == CENTER) {
            textR.y = (iconR.height / 2) - (textR.height / 2);
        }else { // (verticalTextPosition == BOTTOM)
            if (horizontalTextPosition != CENTER) {
                textR.y = iconR.height - textR.height;
            }else {
                textR.y = (iconR.height + gap);
            }
        }

        if (horizontalTextPosition == LEFT) {
            textR.x = -(textR.width + gap);
        }else if (horizontalTextPosition == CENTER) {
            textR.x = (iconR.width / 2) - (textR.width / 2);
        }else { // (horizontalTextPosition == RIGHT)
            textR.x = (iconR.width + gap);
        }
        

        //trace("textR : " + textR);
        //trace("iconR : " + iconR);    
        //trace("viewR : " + viewR);    

        /* labelR is the rectangle that contains iconR and textR.
         * Move it to its proper position given the labelAlignment
         * properties.
         *
         * To avoid actually allocating a Rectangle, Rectangle.union
         * has been inlined below.
         */
        var labelR_x:Number = Math.min(iconR.x, textR.x);
        var labelR_width:Number = Math.max(iconR.x + iconR.width, textR.x + textR.width) - labelR_x;
        var labelR_y:Number = Math.min(iconR.y, textR.y);
        var labelR_height:Number = Math.max(iconR.y + iconR.height, textR.y + textR.height) - labelR_y;
        
        //trace("labelR_x : " + labelR_x);
        //trace("labelR_width : " + labelR_width);  
        //trace("labelR_y : " + labelR_y);
        //trace("labelR_height : " + labelR_height);        
        
        var dx:Number = 0;
        var dy:Number = 0;

        if (verticalAlignment == TOP) {
            dy = viewR.y - labelR_y;
        }
        else if (verticalAlignment == CENTER) {
            dy = (viewR.y + (viewR.height/2)) - (labelR_y + (labelR_height/2));
        }
        else { // (verticalAlignment == BOTTOM)
            dy = (viewR.y + viewR.height) - (labelR_y + labelR_height);
        }

        if (horizontalAlignment == LEFT) {
            dx = viewR.x - labelR_x;
        }
        else if (horizontalAlignment == RIGHT) {
            dx = (viewR.x + viewR.width) - (labelR_x + labelR_width);
        }
        else { // (horizontalAlignment == CENTER)
            dx = (viewR.x + (viewR.width/2)) - (labelR_x + (labelR_width/2));
        }

        /* Translate textR and glypyR by dx,dy.
         */

        //trace("dx : " + dx);
        //trace("dy : " + dy);  
        
        textR.x += dx;
        textR.y += dy;

        iconR.x += dx;
        iconR.y += dy;
        
        //trace("tf = " + tf);

        //trace("textR : " + textR);
        //trace("iconR : " + iconR);        
        
        return text;
    }
        
    /**
     * Not include the gutters
     */
    /*private static function inter_stringWidth(font:ASFont, ch:String):Number{
    	TEXT_FIELD.text = ch;
    	if(TEXT_FONT != font){
    		font.apply(TEXT_FIELD);
    		TEXT_FONT = font;
    	}
        return TEXT_FIELD.textWidth;
    }*/
    
    private static function inter_computeStringSize(font:ASFont, str:String):IntDimension{
    	TEXT_FIELD.text = str;
    	if(TEXT_FONT != font){
    		font.apply(TEXT_FIELD);
    		TEXT_FONT = font;
    	}
        return new IntDimension(Math.ceil(TEXT_FIELD.width), Math.ceil(TEXT_FIELD.height));
    }
        
    private static function inter_computeStringWidth(font:ASFont, str:String):Number{
    	TEXT_FIELD.text = str;
    	if(TEXT_FONT != font){
    		font.apply(TEXT_FIELD);
    		TEXT_FONT = font;
    	}
        return TEXT_FIELD.width;
    }
    
    
    private static var TEXT_FIELD_EXT:TextField = new TextField();
    {
    	TEXT_FIELD_EXT.autoSize = TextFieldAutoSize.LEFT;
    	TEXT_FIELD_EXT.type = TextFieldType.DYNAMIC;
    }
    
    /**
     * Computes the text size of specified textFormat, text, and textfield.
     * @param tf the textformat of the text
     * @param str the text to be computes
     * @param includeGutters whether or not include the 2-pixels gutters in the result
     * @param textField if a textField is specifed, the embedFonts, antiAliasType, gridFitType, sharpness, 
     * 			and thickness properties of this textField will take effects.
	 * @return the computed size of the text
     */
    public static function computeStringSize(tf:TextFormat, str:String, includeGutters:Boolean=true, 
    	textField:TextField=null):IntDimension{
    	if(textField){
    		TEXT_FIELD_EXT.embedFonts = textField.embedFonts;
    		TEXT_FIELD_EXT.antiAliasType = textField.antiAliasType;
    		TEXT_FIELD_EXT.gridFitType = textField.gridFitType;
    		TEXT_FIELD_EXT.sharpness = textField.sharpness;
    		TEXT_FIELD_EXT.thickness = textField.thickness;
    	}
    	TEXT_FIELD_EXT.text = str;
    	TEXT_FIELD_EXT.setTextFormat(tf);
    	if(includeGutters){
    		return new IntDimension(Math.ceil(TEXT_FIELD_EXT.width), Math.ceil(TEXT_FIELD_EXT.height));
    	}else{
    		return new IntDimension(Math.ceil(TEXT_FIELD_EXT.textWidth), Math.ceil(TEXT_FIELD_EXT.textHeight));
    	}
    }
    
    /**
     * Computes the text size of specified font, text.
     * @param tf the font of the text
     * @param str the text to be computes
     * @param includeGutters whether or not include the 2-pixels gutters in the result
	 * @return the computed size of the text
     */
    public static function computeStringSizeWithFont(font:ASFont, str:String, includeGutters:Boolean=true):IntDimension{
    	TEXT_FIELD_EXT.text = str;
    	font.apply(TEXT_FIELD_EXT);
    	if(includeGutters){
    		return new IntDimension(Math.ceil(TEXT_FIELD_EXT.width), Math.ceil(TEXT_FIELD_EXT.height));
    	}else{
    		return new IntDimension(Math.ceil(TEXT_FIELD_EXT.textWidth), Math.ceil(TEXT_FIELD_EXT.textHeight));
    	}
    }    
    
    /**
     * before call this method textR.width must be filled with correct value of whole text.
     */
    private static function layoutTextWidth(text:String, textR:IntRectangle, availTextWidth:Number, font:ASFont):String{
        if (textR.width <= availTextWidth) {
            return text;
        }
        var clipString:String = "...";
        var totalWidth:int = Math.round(inter_computeStringWidth(font, clipString));
        if(totalWidth > availTextWidth){
            totalWidth = Math.round(inter_computeStringWidth(font, ".."));
            if(totalWidth > availTextWidth){
                text = ".";
                textR.width = Math.round(inter_computeStringWidth(font, "."));
                if(textR.width > availTextWidth){
                    textR.width = 0;
                    text = "";
                }
            }else{
                text = "..";
                textR.width = totalWidth;
            }
            return text;
        }else{
            var lastWidth:Number = totalWidth;
            
            
            //begin binary search
            var num:int = text.length;
            var li:int = 0; //binary search of left index 
            var ri:int = num; //binary search of right index
            
            while(li<ri){
                var i:int = li + (ri - li)/2;
                var subText:String = text.substring(0, i);
                var length:int = Math.ceil(lastWidth + inter_computeStringWidth(font, subText));
                
                if((li == i - 1) && li>0){
                    if(length > availTextWidth){
                        subText = text.substring(0, li);
                        textR.width = Math.ceil(lastWidth + inter_computeStringWidth(font, text.substring(0, li)));
                    }else{
                        textR.width = length;
                    }
                    return subText + clipString;
                }else if(i <= 1){
                    if(length <= availTextWidth){
                        textR.width = length;
                        return subText + clipString;
                    }else{
                        textR.width = lastWidth;
                        return clipString;
                    }
                }
                
                if(length < availTextWidth){
                    li = i;
                }else if(length > availTextWidth){
                    ri = i;
                }else{
                    text = subText + clipString;
                    textR.width = length;
                    return text;
                }
            }
            //end binary search
            textR.width = lastWidth;
            return "";
        }
    } 
    
    
    /**
     * Compute and return the location of origin of the text baseline, and a possibly clipped
     * version of the text string.  Locations are computed
     * relative to the viewR rectangle.
     */
    public static function layoutText(
        f:ASFont,
        text:String,
        verticalAlignment:Number,
        horizontalAlignment:Number,
        viewR:IntRectangle,
        textR:IntRectangle):String
    {
		var	textFieldSize:IntDimension = inter_computeStringSize(f, text);
        var textIsEmpty:Boolean = (text==null || text=="");
        if(textIsEmpty){
            textR.width = textR.height = 0;
        }else{
            textR.width = Math.ceil(textFieldSize.width);
            textR.height = Math.ceil(textFieldSize.height);
        }        
        
        if(!textIsEmpty){
            
            /* If the label text string is too wide to fit within the available
             * space "..." and as many characters as will fit will be
             * displayed instead.
             */

            var availTextWidth:Number = viewR.width;
            if (textR.width > availTextWidth) {
                text = layoutTextWidth(text, textR, availTextWidth, f);
            }
        }
        if(horizontalAlignment == CENTER){
            textR.x = viewR.x + (viewR.width - textR.width)/2;
        }else if(horizontalAlignment == RIGHT){
            textR.x = viewR.x + (viewR.width - textR.width);
        }else{
            textR.x = viewR.x;
        }
        if(verticalAlignment == CENTER){
            textR.y = viewR.y + (viewR.height - textR.height)/2;
        }else if(verticalAlignment == BOTTOM){
            textR.y = viewR.y + (viewR.height - textR.height);
        }else{
            textR.y = viewR.y;
        }
        return text;
    }
       
    /**
     * Creates and return a pane to hold the component with specified layout manager and constraints.
     */
    public static function createPaneToHold(com:Component, layout:LayoutManager, constraints:Object=null):Container{
        var p:JPanel = new JPanel(layout);
        p.setOpaque(false);
        p.append(com, constraints);
        return p;
    }   
     
    /**
     * Returns the MCPanel ancestor of c, or null if it is not contained inside a mcpanel yet
     * @return the first MCPanel ancestor of c, or null.
     */
	/*public static function getAncestorComponent(c:Component):Container{
        while(c != null){
            if(c is Container){
                return Container(c);
            }
            c = c.getParent();
        }
        return null;
	}*/
	
    /**
     * Returns the first Popup ancestor of c, or null if component is not contained inside a popup
     * @return the first Popup ancestor of c, or null if component is not contained inside a popup
     */
    public static function getPopupAncestor(c:Component):JPopup{
        while(c != null){
            if(c is JPopup){
                return JPopup(c);
            }
            c = c.getParent();
        }
        return null;
    }
    
    /**
     * Returns the first popup ancestor or display object root of c, or null if can't find the ancestor
     * @return the first popup ancestor or display object root of c, or null if can't find the ancestor
     */
    public static function getOwnerAncestor(c:Component):DisplayObjectContainer{
		if(c == null){
			return null;
		}
    	var popup:JPopup = getPopupAncestor(c);
    	if(popup == null){
    		return c.root as DisplayObjectContainer;
    	}
    	return popup;
    }
    
    /**
     * Returns the component owner of specified obj.
     * @return the component owner of specified obj.
     */
    public static function getOwnerComponent(dis:DisplayObject):Component{
    	while(dis != null && !(dis is Component)){
    		dis = dis.parent;
    	}
    	return dis as Component;
    }
    
    /**
     * All component will be registered here.
     */
    internal static function weakRegisterComponent(c:Component):void{
    	weakComponentDic[c] = null;
    }
    
    /**
     * When call <code>setLookAndFeel</code> it will not change the UIs at created components.
     * Call this method to update all UIs of all components in memory whether it is displayable or not.
     * Take care to call this method, because there's may many component in memory since the garbage collector 
     * may have not collected some useless components, so it many take a long time to complete updating.
     * @see #updateAllComponentUI()
     * @see org.aswing.Component#updateUI()
     */
    public static function updateAllComponentUIInMemory():void{
    	for(var c:* in weakComponentDic){
    		if(!c.isUIElement()){
    			c.updateUI();
    		}
    	}
    }
    
    /**
     * When call <code>setLookAndFeel</code> it will not change the UIs at created components.
     * Call this method to update all UIs of components that is on display list or popups.
     * @see #updateAllComponentUIInMemory()
     * @see #updateChildrenUI()
     * @see #updateComponentTreeUI()
     * @see org.aswing.Component#updateUI()
     */
    public static function updateAllComponentUI(stage:Stage=null):void{
    	if(stage == null){
    		stage = AsWingManager.getStage();
    	}
		if(AsWingManager.isStageInited()){
			updateChildrenUI(stage);
		}
    }
    
    /**
     * A simple minded look and feel change: ask each node in the tree to updateUI() -- that is, 
     * to initialize its UI property with the current look and feel. 
     * @param c the component used to search its owner ancestor
     * @see org.aswing.Component#updateUI()
     * @see #updateChildrenUI()
     */
    public static function updateComponentTreeUI(c:Component):void{
        updateChildrenUI(getOwnerAncestor(c));
    }
    
    /**
     * Asks every component that is not a ui element containsed in the display object to updateUI().
     * This function will search all components contained in the specified object.
     * @param dis the display object
     * @see org.aswing.Component#isUIElement()
     */
    public static function updateChildrenUI(dis:DisplayObject):void{
    	if(dis == null) return;
    	var c:Component = dis as Component;
    	if(c){
    		if(c.isUIElement()){
    			return;
    		}
        	c.updateUI();
     	}
        //trace("UI updated : " + c);
        if(dis is DisplayObjectContainer){
            var con:DisplayObjectContainer = DisplayObjectContainer(dis);
            for(var i:int = con.numChildren-1; i>=0; i--){
                updateChildrenUI(con.getChildAt(i));
            }
        }
    }
}

}