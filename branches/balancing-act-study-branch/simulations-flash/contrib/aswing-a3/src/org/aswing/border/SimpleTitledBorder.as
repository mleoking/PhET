/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.border
{
	
/**
 * A poor Title Border.
 * @author iiley
 */
import flash.display.*;
import flash.text.*;

import org.aswing.*;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.util.HashMap;

public class SimpleTitledBorder extends DecorateBorder
{
	
	public static const TOP:int = AsWingConstants.TOP;
	public static const BOTTOM:int = AsWingConstants.BOTTOM;
	
	public static const CENTER:int = AsWingConstants.CENTER;
	public static const LEFT:int = AsWingConstants.LEFT;
	public static const RIGHT:int = AsWingConstants.RIGHT;
	

    // Space between the border and the component's edge
    public static var EDGE_SPACING:int = 0;	
	
	private var title:String;
	private var position:int;
	private var align:int;
	private var offset:int;
	private var font:ASFont;
	private var color:ASColor;
	
	private var textField:TextField;
	private var textFieldSize:IntDimension;
	private var colorFontValid:Boolean;
	
	/**
	 * Create a simple titled border.
	 * @param title the title text string.
	 * @param position the position of the title(TOP or BOTTOM), default is TOP
	 * @see #TOP
	 * @see #BOTTOM
	 * @param align the align of the title(CENTER or LEFT or RIGHT), default is CENTER
	 * @see #CENTER
	 * @see #LEFT
	 * @see #RIGHT
	 * @param offset the addition of title text's x position, default is 0
	 * @param font the title text's ASFont
	 * @param color the color of the title text
	 * @see org.aswing.border.TitledBorder
	 */
	public function SimpleTitledBorder(interior:Border=null, title:String="", position:int=AsWingConstants.TOP, align:int=CENTER, offset:int=0, font:ASFont=null, color:ASColor=null){
		super(interior);
		this.title = title;
		this.position = position;
		this.align = align;
		this.offset = offset;
		this.font = (font==null ? UIManager.getFont("systemFont") : font);
		this.color = (color==null ? ASColor.BLACK : color);
		textField = null;
		colorFontValid = false;
		textFieldSize = null;
	}
	
	
	//------------get set-------------
	
		
	public function getPosition():int {
		return position;
	}

	public function setPosition(position:int):void {
		this.position = position;
	}

	public function getColor():ASColor {
		return color;
	}

	public function setColor(color:ASColor):void {
		this.color = color;
		this.invalidateColorFont();
	}

	public function getFont():ASFont {
		return font;
	}

	public function setFont(font:ASFont):void {
		this.font = font;
		invalidateColorFont();
		invalidateExtent();
	}

	public function getAlign():int {
		return align;
	}

	public function setAlign(align:int):void {
		this.align = align;
	}

	public function getTitle():String {
		return title;
	}

	public function setTitle(title:String):void {
		this.title = title;
		this.invalidateExtent();
		this.invalidateColorFont();
	}

	public function getOffset():int {
		return offset;
	}

	public function setOffset(offset:int):void {
		this.offset = offset;
	}
	
	private function invalidateExtent():void{
		textFieldSize = null;
	}
	private function invalidateColorFont():void{
		colorFontValid = false;
	}
	
	private function getTextFieldSize():IntDimension{
    	if (textFieldSize == null){
	    	var tf:TextFormat = getFont().getTextFormat();
			textFieldSize = AsWingUtils.computeStringSize(tf, title);   	
    	}
    	return textFieldSize;
	}
	
	private function getTextField():TextField{
    	if(textField == null){
	    	textField = new TextField();
	    	textField.selectable = false;
	    	textField.autoSize = TextFieldAutoSize.CENTER;	    	
    	}
    	return textField;
	}
	
	override public function updateBorderImp(c:Component, g:Graphics2D, bounds:IntRectangle):void{
    	if(!colorFontValid){
    		textField.text = title;
    		AsWingUtils.applyTextFontAndColor(textField, font, color);
    		colorFontValid = true;
    	}
    	
    	var width:int = Math.ceil(textField.width);
    	var height:int = Math.ceil(textField.height);
    	var x:int = offset;
    	if(align == LEFT){
    		x += bounds.x;
    	}else if(align == RIGHT){
    		x += (bounds.x + bounds.width - width);
    	}else{
    		x += (bounds.x + bounds.width/2 - width/2);
    	}
    	var y:int = bounds.y + EDGE_SPACING;
    	if(position == BOTTOM){
    		y = bounds.y + bounds.height - height + EDGE_SPACING;
    	}
    	textField.x = x;
    	textField.y = y;
    }
    	   
    override public function getBorderInsetsImp(c:Component, bounds:IntRectangle):Insets{
    	var insets:Insets = new Insets();
    	var cs:IntDimension = bounds.getSize();
		if(cs.width < getTextFieldSize().width){
			var delta:int = Math.ceil(getTextFieldSize().width) - cs.width;
			if(align == RIGHT){
				insets.left = delta;
			}else if(align == CENTER){
				insets.left = delta/2;
				insets.right = delta/2;
			}else{
				insets.right = delta;
			}
		}
    	if(position == BOTTOM){
    		insets.bottom = EDGE_SPACING*2 + Math.ceil(getTextFieldSize().height);
    	}else{
    		insets.top = EDGE_SPACING*2 + Math.ceil(getTextFieldSize().height);
    	}
    	return insets;
    }
	
	override public function getDisplayImp():DisplayObject
	{
		return getTextField();
	}	
}
}