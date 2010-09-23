/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.border{
	
import flash.display.*;
import flash.text.*;

import org.aswing.*;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.util.HashMap;

/**
 * TitledBorder, a border with a line rectangle and a title text.
 * @author iiley
 */	
public class TitledBorder extends DecorateBorder{
		
	public static function get DEFAULT_FONT():ASFont{
		return UIManager.getFont("systemFont");
	}
	public static function get DEFAULT_COLOR():ASColor{
		return ASColor.BLACK;
	}
	public static function get DEFAULT_LINE_COLOR():ASColor{
		return ASColor.GRAY;
	}
	public static function get DEFAULT_LINE_LIGHT_COLOR():ASColor{
		return ASColor.WHITE;
	}
	public static const DEFAULT_LINE_THICKNESS:int = 1;
		
	public static const TOP:int = AsWingConstants.TOP;
	public static const BOTTOM:int = AsWingConstants.BOTTOM;
	
	public static const CENTER:int = AsWingConstants.CENTER;
	public static const LEFT:int = AsWingConstants.LEFT;
	public static const RIGHT:int = AsWingConstants.RIGHT;
	

    // Space between the text and the line end
    public static var GAP:int = 1;	
	
	private var title:String;
	private var position:int;
	private var align:int;
	private var edge:Number;
	private var round:Number;
	private var font:ASFont;
	private var color:ASColor;
	private var lineColor:ASColor;
	private var lineLightColor:ASColor;
	private var lineThickness:Number;
	private var beveled:Boolean;
	private var textField:TextField;
	private var textFieldSize:IntDimension;
	
	/**
	 * Create a titled border.
	 * @param title the title text string.
	 * @param position the position of the title(TOP or BOTTOM), default is TOP
	 * @param align the align of the title(CENTER or LEFT or RIGHT), default is CENTER
	 * @param edge the edge space of title position, defaut is 0.
	 * @param round round rect radius, default is 0 means normal rectangle, not rect.
	 * @see org.aswing.border.SimpleTitledBorder
	 * @see #setColor()
	 * @see #setLineColor()
	 * @see #setFont()
	 * @see #setLineThickness()
	 * @see #setBeveled()
	 */
	public function TitledBorder(interior:Border=null, title:String="", position:int=AsWingConstants.TOP, align:int=CENTER, edge:Number=0, round:Number=0){
		super(interior);
		this.title = title;
		this.position = position;
		this.align = align;;
		this.edge = edge;
		this.round = round;
		
		font = DEFAULT_FONT;
		color = DEFAULT_COLOR;
		lineColor = DEFAULT_LINE_COLOR;
		lineLightColor = DEFAULT_LINE_LIGHT_COLOR;
		lineThickness = DEFAULT_LINE_THICKNESS;
		beveled = true;
		textField = null;
		textFieldSize = null;
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
    	var textHeight:Number = Math.ceil(getTextFieldSize().height);
    	var x1:Number = bounds.x + lineThickness*0.5;
    	var y1:Number = bounds.y + lineThickness*0.5;
    	if(position == TOP){
    		y1 += textHeight/2;
    	}
    	var w:Number = bounds.width - lineThickness;
    	var h:Number = bounds.height - lineThickness - textHeight/2;
    	if(beveled){
    		w -= lineThickness;
    		h -= lineThickness;
    	}
    	var x2:Number = x1 + w;
    	var y2:Number = y1 + h;
    	
    	var textR:IntRectangle = new IntRectangle();
    	var viewR:IntRectangle = new IntRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
    	var text:String = title;
        var verticalAlignment:Number = position;
        var horizontalAlignment:Number = align;
    	
    	var pen:Pen = new Pen(lineColor, lineThickness);
    	if(round <= 0){
    		if(bounds.width <= edge*2){
    			g.drawRectangle(pen, x1, y1, w, h);
    			if(beveled){
    				pen.setColor(lineLightColor);
    				g.beginDraw(pen);
    				g.moveTo(x1+lineThickness, y2-lineThickness);
    				g.lineTo(x1+lineThickness, y1+lineThickness);
    				g.lineTo(x2-lineThickness, y1+lineThickness);
    				g.moveTo(x2+lineThickness, y1);
    				g.lineTo(x2+lineThickness, y2+lineThickness);
    				g.lineTo(x1, y2+lineThickness);
    			}
    			textField.text="";
    		}else{
    			viewR.x += edge;
    			viewR.width -= edge*2;
    			text = AsWingUtils.layoutText(font, text, verticalAlignment, horizontalAlignment, viewR, textR);
    			//draw dark rect
    			g.beginDraw(pen);
    			if(position == TOP){
	    			g.moveTo(textR.x - GAP, y1);
	    			g.lineTo(x1, y1);
	    			g.lineTo(x1, y2);
	    			g.lineTo(x2, y2);
	    			g.lineTo(x2, y1);
	    			g.lineTo(textR.x + textR.width+GAP, y1);
	    				    			
    			}else{
	    			g.moveTo(textR.x - GAP, y2);
	    			g.lineTo(x1, y2);
	    			g.lineTo(x1, y1);
	    			g.lineTo(x2, y1);
	    			g.lineTo(x2, y2);
	    			g.lineTo(textR.x + textR.width+GAP, y2);
    			}
    			g.endDraw();
    			if(beveled){
	    			//draw hightlight
	    			pen.setColor(lineLightColor);
	    			g.beginDraw(pen);
	    			if(position == TOP){
		    			g.moveTo(textR.x - GAP, y1+lineThickness);
		    			g.lineTo(x1+lineThickness, y1+lineThickness);
		    			g.lineTo(x1+lineThickness, y2-lineThickness);
		    			g.moveTo(x1, y2+lineThickness);
		    			g.lineTo(x2+lineThickness, y2+lineThickness);
		    			g.lineTo(x2+lineThickness, y1);
		    			g.moveTo(x2-lineThickness, y1+lineThickness);
		    			g.lineTo(textR.x + textR.width+GAP, y1+lineThickness);
		    				    			
	    			}else{
		    			g.moveTo(textR.x - GAP, y2+lineThickness);
		    			g.lineTo(x1, y2+lineThickness);
		    			g.moveTo(x1+lineThickness, y2-lineThickness);
		    			g.lineTo(x1+lineThickness, y1+lineThickness);
		    			g.lineTo(x2-lineThickness, y1+lineThickness);
		    			g.moveTo(x2+lineThickness, y1);
		    			g.lineTo(x2+lineThickness, y2+lineThickness);
		    			g.lineTo(textR.x + textR.width+GAP, y2+lineThickness);
	    			}
	    			g.endDraw();
    			}
    		}
    	}else{
    		if(bounds.width <= (edge*2 + round*2)){
    			if(beveled){
    				g.drawRoundRect(new Pen(lineLightColor, lineThickness), 
    							x1+lineThickness, y1+lineThickness, w, h, 
    							Math.min(round, Math.min(w/2, h/2)));
    			}
    			g.drawRoundRect(pen, x1, y1, w, h, 
    							Math.min(round, Math.min(w/2, h/2)));
    			textField.text="";
    		}else{
    			viewR.x += (edge+round);
    			viewR.width -= (edge+round)*2;
    			text = AsWingUtils.layoutText(font, text, verticalAlignment, horizontalAlignment, viewR, textR);
				var r:Number = round;

    			if(beveled){
    				pen.setColor(lineLightColor);
	    			g.beginDraw(pen);
	    			var t:Number = lineThickness;
    				x1+=t;
    				x2+=t;
    				y1+=t;
    				y2+=t;
	    			if(position == TOP){
			    		g.moveTo(textR.x - GAP, y1);
						//Top left
						g.lineTo (x1+r, y1);
						g.curveTo(x1, y1, x1, y1+r);
						//Bottom left
						g.lineTo (x1, y2-r );
						g.curveTo(x1, y2, x1+r, y2);
						//bottom right
						g.lineTo(x2-r, y2);
						g.curveTo(x2, y2, x2, y2-r);
						//Top right
						g.lineTo (x2, y1+r);
						g.curveTo(x2, y1, x2-r, y1);
						g.lineTo(textR.x + textR.width+GAP, y1);
	    			}else{
			    		g.moveTo(textR.x + textR.width+GAP, y2);
						//bottom right
						g.lineTo(x2-r, y2);
						g.curveTo(x2, y2, x2, y2-r);
						//Top right
						g.lineTo (x2, y1+r);
						g.curveTo(x2, y1, x2-r, y1);
						//Top left
						g.lineTo (x1+r, y1);
						g.curveTo(x1, y1, x1, y1+r);
						//Bottom left
						g.lineTo (x1, y2-r );
						g.curveTo(x1, y2, x1+r, y2);
						g.lineTo(textR.x - GAP, y2);
	    			}
	    			g.endDraw();  
    				x1-=t;
    				x2-=t;
    				y1-=t;
    				y2-=t;  				
    			}		
    			pen.setColor(lineColor);		
    			g.beginDraw(pen);
    			if(position == TOP){
		    		g.moveTo(textR.x - GAP, y1);
					//Top left
					g.lineTo (x1+r, y1);
					g.curveTo(x1, y1, x1, y1+r);
					//Bottom left
					g.lineTo (x1, y2-r );
					g.curveTo(x1, y2, x1+r, y2);
					//bottom right
					g.lineTo(x2-r, y2);
					g.curveTo(x2, y2, x2, y2-r);
					//Top right
					g.lineTo (x2, y1+r);
					g.curveTo(x2, y1, x2-r, y1);
					g.lineTo(textR.x + textR.width+GAP, y1);
    			}else{
		    		g.moveTo(textR.x + textR.width+GAP, y2);
					//bottom right
					g.lineTo(x2-r, y2);
					g.curveTo(x2, y2, x2, y2-r);
					//Top right
					g.lineTo (x2, y1+r);
					g.curveTo(x2, y1, x2-r, y1);
					//Top left
					g.lineTo (x1+r, y1);
					g.curveTo(x1, y1, x1, y1+r);
					//Bottom left
					g.lineTo (x1, y2-r );
					g.curveTo(x1, y2, x1+r, y2);
					g.lineTo(textR.x - GAP, y2);
    			}
    			g.endDraw();
    		}
    	}
    	textField.text = text;
		AsWingUtils.applyTextFontAndColor(textField, font, color);
    	textField.x = textR.x;
    	textField.y = textR.y;   	
    }
    	   
   override public function getBorderInsetsImp(c:Component, bounds:IntRectangle):Insets{
    	var cornerW:Number = Math.ceil(lineThickness*2 + round - round*0.707106781186547);
    	var insets:Insets = new Insets(cornerW, cornerW, cornerW, cornerW);
    	if(position == BOTTOM){
    		insets.bottom += Math.ceil(getTextFieldSize().height);
    	}else{
    		insets.top += Math.ceil(getTextFieldSize().height);
    	}
    	return insets;
    }
	
	override public function getDisplayImp():DisplayObject
	{
		return getTextField();
	}		
	
	//-----------------------------------------------------------------

	public function getFont():ASFont {
		return font;
	}

	public function setFont(font:ASFont):void {
		if(this.font != font){
			if(font == null) font = DEFAULT_FONT;
			this.font = font;
			textFieldSize == null;
		}
	}

	public function getLineColor():ASColor {
		return lineColor;
	}

	public function setLineColor(lineColor:ASColor):void {
		if (lineColor != null){
			this.lineColor = lineColor;
		}
	}
	
	public function getLineLightColor():ASColor{
		return lineLightColor;
	}
	
	public function setLineLightColor(lineLightColor:ASColor):void{
		if (lineLightColor != null){
			this.lineLightColor = lineLightColor;
		}
	}
	
	public function isBeveled():Boolean{
		return beveled;
	}
	
	public function setBeveled(b:Boolean):void{
		beveled = b;
	}

	public function getEdge():Number {
		return edge;
	}

	public function setEdge(edge:Number):void {
		this.edge = edge;
	}

	public function getTitle():String {
		return title;
	}

	public function setTitle(title:String):void {
		if(this.title != title){
			this.title = title;
			textFieldSize == null;
		}
	}

	public function getRound():Number {
		return round;
	}

	public function setRound(round:Number):void {
		this.round = round;
	}

	public function getColor():ASColor {
		return color;
	}

	public function setColor(color:ASColor):void {
		this.color = color;
	}

	public function getAlign():int {
		return align;
	}
	
	/**
	 * Sets the align of title text.
	 * @see #CENTER
	 * @see #LEFT
	 * @see #RIGHT
	 */
	public function setAlign(align:int):void {
		this.align = align;
	}

	public function getPosition():int {
		return position;
	}
	
	/**
	 * Sets the position of title text.
	 * @see #TOP
	 * @see #BOTTOM
	 */
	public function setPosition(position:int):void {
		this.position = position;
	}	
	
	public function getLineThickness():int {
		return lineThickness;
	}

	public function setLineThickness(lineThickness:Number):void {
		this.lineThickness = lineThickness;
	}
		
	private function getTextFieldSize():IntDimension{
    	if (textFieldSize == null){
			textFieldSize = getFont().computeTextSize(title);  	
    	}
    	return textFieldSize;
	}
}
}