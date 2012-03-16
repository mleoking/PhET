/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.ASTextExtent;
import org.aswing.ASWingConstants;
import org.aswing.ASWingUtils;
import org.aswing.border.Border;
import org.aswing.border.DecorateBorder;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.Insets;
import org.aswing.UIManager;
import org.aswing.util.HashMap;

/**
 * TitledBorder, a border with a line rectangle and a title text.
 * @author iiley
 */
class org.aswing.border.TitledBorder extends DecorateBorder {
		
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
	public static var DEFAULT_LINE_THICKNESS:Number = 1;
		
	public static var TOP:Number = ASWingConstants.TOP;
	public static var BOTTOM:Number = ASWingConstants.BOTTOM;
	
	public static var CENTER:Number = ASWingConstants.CENTER;
	public static var LEFT:Number = ASWingConstants.LEFT;
	public static var RIGHT:Number = ASWingConstants.RIGHT;
	

    // Space between the text and the line end
    public static var GAP:Number = 1;	
	
	private var title:String;
	private var position:Number;
	private var align:Number;
	private var edge:Number;
	private var round:Number;
	private var font:ASFont;
	private var color:ASColor;
	private var lineColor:ASColor;
	private var lineLightColor:ASColor;
	private var lineThickness:Number;
	private var beveled:Boolean;
	private var textFields:HashMap;
	var textFieldExtent:ASTextExtent;
	
	/**
	 * TitledBorder(interior:Border, title:String, position:Number, align:Number, edge:Number, round:Number)<br>
	 * TitledBorder(interior:Border, title:String, position:Number, align:Number, edge:Number)<br>
	 * TitledBorder(interior:Border, title:String, position:Number, align:Number,)<br>
	 * TitledBorder(interior:Border, title:String, position:Number)<br>
	 * TitledBorder(interior:Border, title:String)<br>
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
	public function TitledBorder(interior:Border, title:String, position:Number, align:Number, edge:Number, round:Number){
		super(interior);
		this.title = title;
		this.position = (position==undefined ? TOP : position);
		this.align = (align==undefined ? CENTER : align);
		this.edge = (edge==undefined ? 0 : edge);
		this.round = (round==undefined ? 0 : round);
		
		font = DEFAULT_FONT;
		color = DEFAULT_COLOR;
		lineColor = DEFAULT_LINE_COLOR;
		lineLightColor = DEFAULT_LINE_LIGHT_COLOR;
		lineThickness = DEFAULT_LINE_THICKNESS;
		beveled = true;
		textFields = new HashMap();
		textFieldExtent = font.getASTextFormat().getTextExtent(title);
	}
	
	
	public function paintBorderImp(c:Component, g:Graphics, bounds:Rectangle):Void{
    	var text_field:TextField = TextField(textFields.get(c.getID()));
    	if(text_field == null){
	    	text_field = c.createTextField("titleBorder");
	    	if(text_field != null){
    			textFields.put(c.getID(), text_field);
	    	}
    	}
    	if(text_field == null){
    		return;
    	}
    	
    	var textHeight:Number = Math.ceil(textFieldExtent.getTextFieldHeight());
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
    	
    	var textR:Rectangle = new Rectangle();
    	var viewR:Rectangle = new Rectangle(bounds);
    	var text:String = title;
        var verticalAlignment:Number = position;
        var horizontalAlignment:Number = align;
    	
    	var pen:Pen = new Pen(lineColor, lineThickness);
    	if(round <= 0){
    		if(bounds.width <= edge*2){
    			g.drawRectangle(pen, x1, y1, w, h);
    			if(beveled){
    				pen.setASColor(lineLightColor);
    				g.beginDraw(pen);
    				g.moveTo(x1+lineThickness, y2-lineThickness);
    				g.lineTo(x1+lineThickness, y1+lineThickness);
    				g.lineTo(x2-lineThickness, y1+lineThickness);
    				g.moveTo(x2+lineThickness, y1);
    				g.lineTo(x2+lineThickness, y2+lineThickness);
    				g.lineTo(x1, y2+lineThickness);
    			}
    			text_field.text="";
    		}else{
    			viewR.x += edge;
    			viewR.width -= edge*2;
    			text = ASWingUtils.layoutText(font, text, verticalAlignment, horizontalAlignment, viewR, textR);
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
	    			pen.setASColor(lineLightColor);
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
    			text_field.text="";
    		}else{
    			viewR.x += (edge+round);
    			viewR.width -= (edge+round)*2;
    			text = ASWingUtils.layoutText(font, text, verticalAlignment, horizontalAlignment, viewR, textR);
				var r:Number = round;

    			if(beveled){
    				pen.setASColor(lineLightColor);
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
    			pen.setASColor(lineColor);		
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
    	text_field.text = text;
		ASWingUtils.applyTextFontAndColor(text_field, font, color);
    	text_field._x = textR.x;
    	text_field._y = textR.y;
    }
    	   
    public function getBorderInsetsImp(c:Component, bounds:Rectangle):Insets{
    	var cornerW:Number = Math.ceil(lineThickness*2 + round - round*0.707106781186547);
    	var insets:Insets = new Insets(cornerW, cornerW, cornerW, cornerW);
    	if(position == BOTTOM){
    		insets.bottom += Math.ceil(textFieldExtent.getTextFieldHeight());
    	}else{
    		insets.top += Math.ceil(textFieldExtent.getTextFieldHeight());
    	}
    	return insets;
    }
    	
	public function uninstallBorderImp(com:Component):Void{
		var text_field:TextField = TextField(textFields.remove(com.getID()));
		text_field.removeTextField();
	}
	
	//-----------------------------------------------------------------

	public function getFont():ASFont {
		return font;
	}

	public function setFont(font:ASFont):Void {
		if(this.font != font){
			if(font == null) font = DEFAULT_FONT;
			this.font = font;
			textFieldExtent = font.getASTextFormat().getTextExtent(title);
		}
	}

	public function getLineColor():ASColor {
		return lineColor;
	}

	public function setLineColor(lineColor:ASColor):Void {
		this.lineColor = lineColor;
	}
	
	public function getLineLightColor():ASColor{
		return lineLightColor;
	}
	
	public function setLineLightColor(lineLightColor:ASColor):Void{
		this.lineLightColor = lineLightColor;
	}
	
	public function isBeveled():Boolean{
		return beveled;
	}
	
	public function setBeveled(b:Boolean):Void{
		beveled = b;
	}

	public function getEdge():Number {
		return edge;
	}

	public function setEdge(edge:Number):Void {
		this.edge = edge;
	}

	public function getTitle():String {
		return title;
	}

	public function setTitle(title:String):Void {
		if(this.title != title){
			this.title = title;
			textFieldExtent = font.getASTextFormat().getTextExtent(title);
		}
	}

	public function getRound():Number {
		return round;
	}

	public function setRound(round:Number):Void {
		this.round = round;
	}

	public function getColor():ASColor {
		return color;
	}

	public function setColor(color:ASColor):Void {
		this.color = color;
	}

	public function getAlign():Number {
		return align;
	}
	
	/**
	 * Sets the align of title text.
	 * @see #CENTER
	 * @see #LEFT
	 * @see #RIGHT
	 */
	public function setAlign(align:Number):Void {
		this.align = align;
	}

	public function getPosition():Number {
		return position;
	}
	
	/**
	 * Sets the position of title text.
	 * @see #TOP
	 * @see #BOTTOM
	 */
	public function setPosition(position:Number):Void {
		this.position = position;
	}	
	
	public function getLineThickness():Number {
		return lineThickness;
	}

	public function setLineThickness(lineThickness:Number):Void {
		this.lineThickness = lineThickness;
	}	

}
