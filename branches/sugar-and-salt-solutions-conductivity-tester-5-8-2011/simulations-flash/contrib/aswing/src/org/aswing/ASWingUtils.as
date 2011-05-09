/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.ASTextExtent;
import org.aswing.ASTextFormat;
import org.aswing.ASWingConstants;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.Icon;
import org.aswing.JPanel;
import org.aswing.overflow.JPopup;
import org.aswing.JWindow;
import org.aswing.LayoutManager;
import org.aswing.MCPanel;
import org.aswing.util.HashMap;

class org.aswing.ASWingUtils{
    /**
     * A fast access to ASWingConstants Constant
     * @see org.aswing.ASWingConstants
     */
    public static var CENTER:Number  = ASWingConstants.CENTER;
    /**
     * A fast access to ASWingConstants Constant
     * @see org.aswing.ASWingConstants
     */
    public static var TOP:Number     = ASWingConstants.TOP;
    /**
     * A fast access to ASWingConstants Constant
     * @see org.aswing.ASWingConstants
     */
    public static var LEFT:Number    = ASWingConstants.LEFT;
    /**
     * A fast access to ASWingConstants Constant
     * @see org.aswing.ASWingConstants
     */
    public static var BOTTOM:Number  = ASWingConstants.BOTTOM;
    /**
     * A fast access to ASWingConstants Constant
     * @see org.aswing.ASWingConstants
     */
    public static var RIGHT:Number   = ASWingConstants.RIGHT;
    /**
     * A fast access to ASWingConstants Constant
     * @see org.aswing.ASWingConstants
     */        
    public static var HORIZONTAL:Number = ASWingConstants.HORIZONTAL;
    /**
     * A fast access to ASWingConstants Constant
     * @see org.aswing.ASWingConstants
     */
    public static var VERTICAL:Number   = ASWingConstants.VERTICAL;
    
    


    private static var ROOT:MovieClip;
    private static var displayableComponents:HashMap;
    
    private static var initialStageWidth:Number = Stage.width;
    private static var initialStageHeight:Number = Stage.height;
    
    /**
     * Returns the center position in the stage.
     */
    public static function getScreenCenterPosition():Point{
    	var r:Rectangle = getVisibleMaximizedBounds();
    	return new Point(r.x + r.width/2, r.y + r.height/2);
    }
    
    /**
     * getVisibleMaximizedBounds(mc:MovieClip)<br>
     * getVisibleMaximizedBounds() default to return the visible bounds in stage. 
     * Returns the currently visible maximized bounds in a MovieClip(viewable the stage area).
     */
    public static function getVisibleMaximizedBounds(mc:MovieClip):Rectangle{
        var sw:Number = Stage.width;
        var sh:Number = Stage.height;
        var sa:String = Stage.align;
        
        var dw:Number = sw - initialStageWidth;
        var dh:Number = sh - initialStageHeight;
        
        var b:Rectangle = new Rectangle(0, 0, sw, sh);
        if(mc != undefined){
            mc.globalToLocal(b);
        }
        switch(sa){
            case "T":
                b.x -= dw/2;
                break;
            case "B":
                b.x -= dw/2;
                b.y -= dh;
                break;
            case "L":
                b.y -= dh/2;
                break;
            case "R":
                b.x -= dw;
                b.y -= dh/2;
                break;
            case "LT":
            case "TL":
                break;
            case "RT":
            case "TR":
                b.x -= dw;
                break;
            case "LB":
            case "BL":
                b.y -= dh;
                break;
            case "RB":
            case "BR":
                b.x -= dw;
                b.y -= dh;
                break;
            default:
                b.x -= dw/2;
                b.y -= dh/2;
                break;
        }
        b.x = Math.ceil(b.x);
        b.y = Math.ceil(b.y);
        b.width = Math.floor(b.width);
        b.height = Math.floor(b.height);
        return b;
    }
    
    /**
     * Registers a displayable component.<br>
     * You should not call this method at your application programs.
     * @param com the displayable component
     */
    public static function addDisplayableComponent(com:Component):Void{
        if(displayableComponents == undefined){
            displayableComponents = new HashMap();
        }
        displayableComponents.put(com.getID(), com);
    }
    
    /**
     * Removes a registered displayable component when it became not displayable.<br>
     * You should not call this method at your application programs generally with AsWing standard component, 
     * But if you created your custom component extension, you should note that you must call the destroy method 
     * when it was destroied, then it will be automaticlly removed here, but if you did not call destroy or 
     * overrided destroy method, you must call this method manually when it become undisplayable.
     * @param com the de-displayableed component
     * @see Component#destroy()
     */
    public static function removeDisplayableComponent(com:Component):Void{
        displayableComponents.remove(com.getID(), com);
    }
    
    /**
     * Gets the displayable component for it's id.
     * @param id the id of the component
     * @return the displayable component owned the id
     * @see Component#getID()
     */
    public static function getDisplayableComponent(id):Component{
        return displayableComponents.get(id);
    }   
    
    /**
     * Sets the root movieclip for components base on. 
     * Default is _root.
     */
    public static function setRootMovieClip(root:MovieClip):Void{
        ROOT = root;
    } 
    
    /**
     * Returns the root movieclip which components base on. or symbol libraray located in.
     * @return _root if you have not set it or you set it with null/undefined, the mc you'v set before.
     * @see #setRootMovieClip()
     */ 
    public static function getRootMovieClip():MovieClip{
        if(ROOT == undefined){
            return _root;
        }
        return ROOT;
    }   
    
    /**
     * Creates and return a pane to hold the component with specified layout manager and constraints.
     */
    public static function createPaneToHold(com:Component, layout:LayoutManager, constraints:Object):Container{
        var p:JPanel = new JPanel(layout);
        p.setOpaque(false);
        p.append(com, constraints);
        return p;
    }
    
    /**
     * Returns the first Window ancestor of c, or null if component is not contained inside a window
     * @return the first Window ancestor of c, or null if component is not contained inside a window
     */
    public static function getWindowAncestor(c:Component):JWindow{
        while(c != null){
            if(c instanceof JWindow){
                return JWindow(c);
            }
            c = c.getParent();
        }
        return null;
    }
    
    /**
     * Returns the first Popup ancestor of c, or null if component is not contained inside a popup
     * @return the first Popup ancestor of c, or null if component is not contained inside a popup
     */
    public static function getPopupAncestor(c:Component):JPopup{
        while(c != null){
            if(c instanceof JPopup){
                return JPopup(c);
            }
            c = c.getParent();
        }
        return null;
    }
    
    /**
     * Returns the first popup ancestor or movieclip root of c, or null if can't find the ancestor
     * @return the first popup ancestor or movieclip root of c, or null if can't find the ancestor
     */
    public static function getOwnerAncestor(c:Component){
    	var popup:JPopup = getPopupAncestor(c);
    	if(popup == null){
    		return c.getComponentRootAncestorMovieClip();
    	}
    	return popup;
    }
    
    /**
     * Returns the MCPanel ancestor of c, or null if it is not contained inside a mcpanel yet
     * @return the first MCPanel ancestor of c, or null.
     */
	public static function getAncestorComponent(c:Component):Container{
        while(c != null){
            if(c instanceof MCPanel){
                return Container(c);
            }
            c = c.getParent();
        }
        return null;
	}
    
    /**
     * When call <code>setLookAndFeel</code> it will not change the UIs at created components.
     * Call this method to update all existing component's UIs.
     * @see #updateComponentTreeUI()
     * @see org.aswing.Component#updateUI()
     */
    public static function updateAllComponentUI():Void{
        var mcps:Array = MCPanel.getMCPanels();
        var n:Number = mcps.length;
        for(var i:Number=0; i<n; i++){
            updateComponentTreeUI(MCPanel(mcps[i]));
        }
    }
    
    /**
     * A simple minded look and feel change: ask each node in the tree to updateUI() -- that is, 
     * to initialize its UI property with the current look and feel. 
     * @see org.aswing.Component#updateUI()
     */
    public static function updateComponentTreeUI(com:Component):Void{
        var rootc:Component = getRoot(com);
        updateChildrenUI(rootc);
    }
    
    private static function updateChildrenUI(c:Component):Void{
        c.updateUI();
        //trace("UI updated : " + c);
        if(c instanceof Container){
            var con:Container = Container(c);
            for(var i:Number = con.getComponentCount()-1; i>=0; i--){
                updateChildrenUI(con.getComponent(i));
            }
        }
    }
    
    /**
     * Returns the root component for the current component tree.
     * @return the first ancestor of c that's ordinaryly a MCPanel or a JPopup(or JWindow, JFrame).
     */
    public static function getRoot(c:Component):Component{
        var maxLoop:Number = 10000; //max search depth 1000
        while(c.getParent() != null && maxLoop>0){
            c = c.getParent();
            maxLoop--;
        }
        return c;
    }
    
    //Don't call this method currently, it has some problem cause text shake
    public static function boundsTextField(tf:TextField, r:Rectangle):Void{
        tf._x = r.x;
        tf._y = r.y;
        tf._width = r.width;
        tf._height = r.height;
    }
    
    /**
     * Apply the font and color to the textfield.
     * @param text
     * @param font
     * @param color
     */
    public static function applyTextFontAndColor(text:TextField, font:ASFont, color:ASColor):Void{
        var tf:ASTextFormat = font.getASTextFormat();
        applyTextFormatAndColor(text, tf, color);
    }
    
    public static function applyTextFont(text:TextField, font:ASFont):Void{
    	font.getASTextFormat().applyToTextCurrentAndNew(text);
    }
    
    public static function applyTextFormat(text:TextField, textFormat:ASTextFormat):Void{
    	textFormat.applyToTextCurrentAndNew(text);
    }    
    
    public static function applyTextColor(text:TextField, color:ASColor):Void{
        if(text.textColor != color.getRGB()){
        	text.textColor = color.getRGB();
        }
        if(text._alpha != color.getAlpha()){
        	text._alpha = color.getAlpha();
        }
    }
    
    /**
     * Apply the textformat and color to the textfield.
     * @param text
     * @param textFormat
     * @param color
     */
    public static function applyTextFormatAndColor(text:TextField, textFormat:ASTextFormat, color:ASColor):Void{
        applyTextFormat(text, textFormat);
        applyTextColor(text, color);
    }
    
    /**
     * Compute and return the location of the icons origin, the
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewR rectangle.
     */
    public static function layoutCompoundLabel(
        f:ASFont,
        text:String,
        icon:Icon,
        verticalAlignment:Number,
        horizontalAlignment:Number,
        verticalTextPosition:Number,
        horizontalTextPosition:Number,
        viewR:Rectangle,
        iconR:Rectangle,
        textR:Rectangle,
        textIconGap:Number):String
    {
        
        if (icon != null) {
            iconR.width = icon.getIconWidth();
            iconR.height = icon.getIconHeight();
        }else {
            iconR.width = iconR.height = 0;
        }
        
        var tf:ASTextFormat = f.getASTextFormat();
        
        var textIsEmpty:Boolean = (text==null || text=="");
        if(textIsEmpty){
            textR.width = textR.height = 0;
        }else{
            var ts:ASTextExtent = tf.getTextExtent(text);
            textR.width = Math.ceil(ts.getTextFieldWidth());
            textR.height = Math.ceil(ts.getTextFieldHeight());
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
                text = layoutTextWidth(text, textR, availTextWidth, tf);
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
    
    private static function charWidth(atf:ASTextFormat, ch:String):Number{
        return atf.getTextExtent(ch).getWidth();
    }
    
    public static function computeStringWidth(atf:ASTextFormat, str:String):Number{
        return atf.getTextExtent(str).getTextFieldWidth();
    }
    public static function computeStringHeight(atf:ASTextFormat, str:String):Number{
        return atf.getTextExtent(str).getTextFieldHeight();
    }    
    
    /**
     * before call this method textR.width must be filled with correct value of whole text.
     */
    public static function layoutTextWidth(text:String, textR:Rectangle, availTextWidth:Number, tf:ASTextFormat):String{
        if (textR.width <= availTextWidth) {
            return text;
        }
        var clipString:String = "...";
        var totalWidth:Number = Math.round(computeStringWidth(tf, clipString));
        if(totalWidth > availTextWidth){
            totalWidth = Math.round(computeStringWidth(tf, ".."));
            if(totalWidth > availTextWidth){
                text = ".";
                textR.width = Math.round(computeStringWidth(tf, "."));
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
            var nChars:Number;
            var lastWidth:Number = totalWidth;
            
            
            //begin binary search
            var num:Number = text.length;
            var li:Number = 0; //binary search of left index 
            var ri:Number = num; //binary search of right index
            
            while(li<ri){
                var i:Number = li + Math.floor((ri - li)/2);
                var subText:String = text.substring(0, i);
                var length:Number = Math.ceil(lastWidth + computeStringWidth(tf, subText));
                
                if((li == i - 1) && li>0){
                    if(length > availTextWidth){
                        subText = text.substring(0, li);
                        textR.width = Math.ceil(lastWidth + computeStringWidth(tf, text.substring(0, li)));
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
            
            /*
            //the old arithmetic
            var mCharWidth:Number = charWidth(tf, "i");
            for(nChars = 0; nChars < text.length; nChars++) {
                lastWidth = totalWidth;
                totalWidth += charWidth(tf, text.charAt(nChars));
                if (totalWidth > availTextWidth) {
                    break;
                }
            }
            if(nChars > 0){
                text = text.substring(0, nChars) + clipString;
            }else{
                text = clipString;
            }
            textR.width = lastWidth;
            return text;
            */
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
        viewR:Rectangle,
        textR:Rectangle):String
    {        
        var tf:ASTextFormat = f.getASTextFormat();
        
        var textIsEmpty:Boolean = (text==null || text=="");
        if(textIsEmpty){
            textR.width = textR.height = 0;
        }else{
            var ts:ASTextExtent = tf.getTextExtent(text);
            textR.width = Math.ceil(ts.getTextFieldWidth());
            textR.height = Math.ceil(ts.getTextFieldHeight());
        }        
        
        if(!textIsEmpty){
            
            /* If the label text string is too wide to fit within the available
             * space "..." and as many characters as will fit will be
             * displayed instead.
             */

            var availTextWidth:Number = viewR.width;
            if (textR.width > availTextWidth) {
                text = layoutTextWidth(text, textR, availTextWidth, tf);
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
     * Fixes inheritance chain for Container's childs to make AsWing MMC-compatible 
     */
    public static function fixContainerInheritance(instance:Container):Void {
        var proto:Object = instance;
        while (proto.__proto__.__proto__ != null) {
            if (proto.__proto__ instanceof Container) return;
            proto = proto.__proto__;
        }   
        proto.__proto__ = Container.prototype;
    }
    
}
