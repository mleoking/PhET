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
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.Insets;
import org.aswing.UIManager;
import org.aswing.util.HashMap;

/**
 * A poor Title Border.
 * @author iiley
 */
class org.aswing.border.SimpleTitledBorder extends DecorateBorder{
	
	public static var TOP:Number = ASWingConstants.TOP;
	public static var BOTTOM:Number = ASWingConstants.BOTTOM;
	
	public static var CENTER:Number = ASWingConstants.CENTER;
	public static var LEFT:Number = ASWingConstants.LEFT;
	public static var RIGHT:Number = ASWingConstants.RIGHT;
	

    // Space between the border and the component's edge
    public static var EDGE_SPACING:Number = 0;	
	
	private var title:String;
	private var position:Number;
	private var align:Number;
	private var offset:Number;
	private var font:ASFont;
	private var color:ASColor;
	
	private var textFields:HashMap;
	private var textFieldExtent:ASTextExtent;
	private var colorFontValid:Boolean;
	
	/**
	 * SimpleTitledBorder(interior:Border, title:String, position:Number, align:Number, offset:Number, font:ASFont, color:ASColor)<br>
	 * SimpleTitledBorder(interior:Border, title:String, position:Number, align:Number, offset:Number, font:ASFont)<br>
	 * SimpleTitledBorder(interior:Border, title:String, position:Number, align:Number, offset:Number)<br>
	 * SimpleTitledBorder(interior:Border, title:String, position:Number, align:Number,)<br>
	 * SimpleTitledBorder(interior:Border, title:String, position:Number)<br>
	 * SimpleTitledBorder(interior:Border, title:String)<br>
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
	public function SimpleTitledBorder(interior:Border, title:String, position:Number, align:Number, offset:Number, font:ASFont, color:ASColor){
		super(interior);
		this.title = title;
		this.position = (position==undefined ? TOP : position);
		this.align = (align==undefined ? LEFT : align);
		this.offset = (offset==undefined ? 0 : offset);
		this.font = (font==undefined ? UIManager.getFont("systemFont") : font);
		this.color = (color==undefined ? ASColor.BLACK : color);
		textFields = new HashMap();
		colorFontValid = false;
	}
	
	
	//------------get set-------------
	
		
	public function getPosition():Number {
		return position;
	}

	public function setPosition(position:Number):Void {
		this.position = position;
	}

	public function getColor():ASColor {
		return color;
	}

	public function setColor(color:ASColor):Void {
		this.color = color;
		this.invalidateColorFont();
	}

	public function getFont():ASFont {
		return font;
	}

	public function setFont(font:ASFont):Void {
		this.font = font;
		invalidateColorFont();
		invalidateExtent();
	}

	public function getAlign():Number {
		return align;
	}

	public function setAlign(align:Number):Void {
		this.align = align;
	}

	public function getTitle():String {
		return title;
	}

	public function setTitle(title:String):Void {
		this.title = title;
		this.invalidateExtent();
		this.invalidateColorFont();
	}

	public function getOffset():Number {
		return offset;
	}

	public function setOffset(offset:Number):Void {
		this.offset = offset;
	}
	
	private function invalidateExtent():Void{
		textFieldExtent = null;
	}
	private function invalidateColorFont():Void{
		colorFontValid = false;
	}
	
	public function paintBorderImp(c:Component, g:Graphics, bounds:Rectangle):Void{
    	var text_field:TextField = TextField(textFields.get(c.getID()));
    	if(text_field == null){
	    	text_field = c.createTextField("stitleBorder");
	    	if(text_field != null){
    			text_field.text = title;
    			textFields.put(c.getID(), text_field);
	    	}
    	}
    	if(text_field == null){
    		return;
    	}
    	if(!colorFontValid){
    		ASWingUtils.applyTextFontAndColor(text_field, font, color);
    		colorFontValid = true;
    	}
    	
    	var width:Number = Math.ceil(text_field._width);
    	var height:Number = Math.ceil(text_field._height);
    	var x:Number = offset;
    	if(align == LEFT){
    		x += bounds.x;
    	}else if(position == RIGHT){
    		x += (bounds.x + bounds.width - width);
    	}else{
    		x += (bounds.x + bounds.width/2 - width/2);
    	}
    	var y:Number = bounds.y + EDGE_SPACING;
    	if(position == BOTTOM){
    		y = bounds.y + bounds.height - height + EDGE_SPACING;
    	}
    	text_field._x = x;
    	text_field._y = y;
    }
    	   
    public function getBorderInsetsImp(c:Component, bounds:Rectangle):Insets{
    	var insets:Insets = new Insets();
    	var cs:Dimension = bounds.getSize();
    	if(textFieldExtent == null){
			textFieldExtent = font.getASTextFormat().getTextExtent(title);
    	}
		if(cs.width < textFieldExtent.getTextFieldWidth()){
			var delta:Number = Math.ceil(textFieldExtent.getTextFieldWidth()) - cs.width;
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
    		insets.bottom = EDGE_SPACING*2 + Math.ceil(textFieldExtent.getTextFieldHeight());
    	}else{
    		insets.top = EDGE_SPACING*2 + Math.ceil(textFieldExtent.getTextFieldHeight());
    	}
    	return insets;
    }
    	
	public function uninstallBorderImp(com:Component):Void{
		var text_field:TextField = TextField(textFields.remove(com.getID()));
		text_field.removeTextField();
	}
}
