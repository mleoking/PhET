/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
/**
 * @author iiley
 */
class org.aswing.ASTextExtent {
	private var width:Number;
	private var height:Number;
	private var ascent:Number;
	private var descent:Number;
	private var textFieldWidth:Number;
	private var textFieldHeight:Number;
	
	public function ASTextExtent(width:Number, height:Number, ascent:Number, descent:Number,
	 							textFieldWidth:Number, textFieldHeight:Number){
	 	this.width = width;
	 	this.height = height;
	 	this.ascent = ascent;
	 	this.descent = descent;
	 	this.textFieldWidth = textFieldWidth;
	 	this.textFieldHeight = textFieldHeight;
	}
	
	public function getWidth():Number{
		return width;
	}
	public function getHeight():Number{
		return height;
	}
	public function getAscent():Number{
		return ascent;
	}
	public function getDescent():Number{
		return descent;
	}
	public function getTextFieldWidth():Number{
		return textFieldWidth;
	}
	public function getTextFieldHeight():Number{
		return textFieldHeight;
	}
	
	public function toString():String{
		var str:String = "ASTextExtent["
			+ "width:" + width
			+ ", height:" + height
			+ ", ascent:" + ascent
			+ ", descent:" + descent
			+ ", textFieldWidth:" + textFieldWidth
			+ ", textFieldHeight:" + textFieldHeight
			+ "]";
		return str;
	}
}
