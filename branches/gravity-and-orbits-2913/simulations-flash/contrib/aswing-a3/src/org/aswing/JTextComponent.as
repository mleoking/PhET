/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{
	
import flash.display.InteractiveObject;
import flash.events.*;
import flash.text.*;
import flash.ui.Keyboard;

import org.aswing.geom.*;

/**
 * JTextComponent is the base class for text components. 
 * <p>
 * <code>JTextComponent</code> can be formated by <code>ASFont</code>, 
 * but some times you need complex format,then <code>ASFont</code> is 
 * not enough, so you can set a <code>EmptyFont</code> instance to the 
 * <code>JTextComponent</code>, it will do nothing for the format, then 
 * you can call <code>setTextFormat</code>, <code>setDefaultTextFormat</code> 
 * to format the text with <code>TextFormat</code> instances. And don't forgot 
 * to call <code>revalidate</code> if you think the component size should be 
 * change after that. Because these method will not call <code>revalidate</code>
 * automatically.
 * </p>
 * 
 * @author iiley
 * @see #setTextFormat()
 * @see EmptyFont
 * @see JTextField
 * @see JTextArea
 */
public class JTextComponent extends Component implements EditableComponent{
	
	private var textField:TextField;
	private var editable:Boolean;
	
	protected var columnWidth:int;
	protected var rowHeight:int;
	protected var widthMargin:int;
	protected var heightMargin:int;
	protected var columnRowCounted:Boolean;	
	
	public function JTextComponent(){
		super();
		textField = new TextField();
		textField.type = TextFieldType.INPUT;
		textField.autoSize = TextFieldAutoSize.NONE;
		textField.background = false;
		editable = true;
		columnRowCounted = false;
		addChild(textField);
		textField.addEventListener(TextEvent.TEXT_INPUT, __onTextComponentTextInput);
	}
	
	private function __onTextComponentTextInput(e:TextEvent):void{
    	if(!getTextField().multiline){ //fix the bug that fp in interenet browser single line TextField Ctrl+Enter will entered a newline bug
    		var text:String = e.text;
    		var km:KeyboardManager = getKeyboardManager();
    		if(km){
	    		if(km.isKeyDown(Keyboard.CONTROL) && km.isKeyDown(Keyboard.ENTER)){
					if(text.length == 1 && text.charCodeAt(0) == 10){
						e.preventDefault();
					}
	    		}
    		}
    	}
	}
	
	/**
	 * Returns the internal <code>TextField</code> instance.
	 * @return the internal <code>TextField</code> instance.
	 */
	public function getTextField():TextField{
		return textField;
	}
	
	/**
	 * Subclass override this method to do right counting.
	 */
	protected function isAutoSize():Boolean{
		return false;
	}
	
	override public function setEnabled(b:Boolean):void{
		super.setEnabled(b);
		getTextField().selectable = b;
		getTextField().mouseEnabled = b;
	}
	
	public function setEditable(b:Boolean):void{
		if(b != editable){
			editable = b;
			if(b){
				getTextField().type = TextFieldType.INPUT;
			}else{
				getTextField().type = TextFieldType.DYNAMIC;
			}
			invalidate();
			invalidateColumnRowSize();
			repaint();
		}
	}
	
	public function isEditable():Boolean{
		return editable;
	}
	
	/**
	 * Sets the font to the text component.
	 * @param f the font.
	 * @see EmptyFont
	 */
	override public function setFont(f:ASFont):void{
		super.setFont(f);
		setFontValidated(true);
		if(getFont() != null){
			getFont().apply(getTextField());
			invalidateColumnRowSize();
		}
	}
	
	override public function setForeground(c:ASColor):void{
		super.setForeground(c);
		if(getForeground() != null){
    		getTextField().textColor = getForeground().getRGB();
    		getTextField().alpha = getForeground().getAlpha();
  		}
	}
	
	public function setText(text:String):void{
		if(getTextField().text != text){
			getTextField().text = text;
			if(isAutoSize()){
				revalidate();
			}
		}
	}
	
	public function getText():String{
		return getTextField().text;
	}
	
	public function setHtmlText(ht:String):void{
		getTextField().htmlText = ht;
		if(isAutoSize()){
			revalidate();
		}
	}
	
	public function getHtmlText():String{
		return getTextField().htmlText;
	}
	
	public function appendText(newText:String):void{
		getTextField().appendText(newText);
		if(isAutoSize()){
			revalidate();
		}
	}
	
	/**
	 * Append text implemented by <code>replaceText</code> to avoid the 
	 * <code>appendText()</code> method bug(the bug will make the text not be append at 
	 * the end of the text, some times it appends to a middle position).
	 * @param newText the text to be append to the end of the text field.
	 */
	public function appendByReplace(newText:String):void{
		var n:int = getLength();
		getTextField().replaceText(n, n, newText);
	}
	
	public function replaceSelectedText(value:String):void{
		getTextField().replaceSelectedText(value);
	}
	
	public function replaceText(beginIndex:int, endIndex:int, newText:String):void{
		getTextField().replaceText(beginIndex, endIndex, newText);
	}
	
	public function setSelection(beginIndex:int, endIndex:int):void{
		getTextField().setSelection(beginIndex, endIndex);
	}
	
	public function selectAll():void{
		getTextField().setSelection(0, getTextField().length);
	}
	
	public function setCondenseWhite(b:Boolean):void{
		if(getTextField().condenseWhite != b){
			getTextField().condenseWhite = b;
			revalidate();
		}
	}
	
	public function isCondenseWhite():Boolean{
		return getTextField().condenseWhite;
	}
	
	/**
	 * Sets the default textFormat to the text.
	 * <p>
	 * You should set a <code>EmptyFont</code> instance to be the component 
	 * font before this call to make sure the textFormat will be effective.
	 * </p>
	 * @param dtf the default textformat.
	 * @see #setFont()
	 */
	public function setDefaultTextFormat(dtf:TextFormat):void{
		getTextField().defaultTextFormat = dtf;
	}
	
	public function getDefaultTextFormat():TextFormat{
		return getTextField().defaultTextFormat;
	}
	
	/**
	 * Sets the textFormat to the specified range.
	 * <p>
	 * You should set a <code>EmptyFont</code> instance to be the component 
	 * font before this call to make sure the textFormat will be effective.
	 * </p>
	 * @param tf the default textformat.
	 * @param beginIndex the begin index.
	 * @param endIndex the end index.
	 * @see #setFont()
	 */	
	public function setTextFormat(tf:TextFormat, beginIndex:int = -1, endIndex:int = -1):void{
		getTextField().setTextFormat(tf, beginIndex, endIndex);
	}
	
	public function getTextFormat(beginIndex:int = -1, endIndex:int = -1):TextFormat{
		return getTextField().getTextFormat(beginIndex, endIndex);
	}
	
	public function setDisplayAsPassword(b:Boolean):void{
		getTextField().displayAsPassword = b;
	}
	
	public function isDisplayAsPassword():Boolean{
		return getTextField().displayAsPassword;
	}
	
	public function getLength():int{
		return getTextField().length;
	}
	
	public function setMaxChars(n:int):void{
		getTextField().maxChars = n;
	}
	
	public function getMaxChars():int{
		return getTextField().maxChars;
	}
	
	public function setRestrict(res:String):void{
		getTextField().restrict = res;
	} 
	
	public function getRestrict():String{
		return getTextField().restrict;
	}
	
	public function getSelectionBeginIndex():int{
		return getTextField().selectionBeginIndex;
	}
	
	public function getSelectionEndIndex():int{
		return getTextField().selectionEndIndex;
	}
	
	public function setCSS(css:StyleSheet):void{
		getTextField().styleSheet = css;
		if(isAutoSize()){
			revalidate();
		}
	}
	
	public function getCSS():StyleSheet{
		return getTextField().styleSheet;
	}
	
	public function setWordWrap(b:Boolean):void{
		getTextField().wordWrap = b;
		if(isAutoSize()){
			revalidate();
		}
	}
	
	public function isWordWrap():Boolean{
		return getTextField().wordWrap;
	}
	
	public function setUseRichTextClipboard(b:Boolean):void{
		getTextField().useRichTextClipboard = b;
	}
	
	public function isUseRichTextClipboard():Boolean{
		return getTextField().useRichTextClipboard;
	}
	
	//-------------------------------------------------------------
	
	/**
	 * JTextComponent need count preferred size itself.
	 */
	override protected function countPreferredSize():IntDimension{
		throw new Error("Subclass of JTextComponent need implement this method : countPreferredSize!");
		return null;
	}
	
	/**
	 * Invalidate the column and row size, make it will be recount when need it next time.
	 */
	protected function invalidateColumnRowSize():void{
		columnRowCounted = false;
	}	
	
	/**
	 * Returns the column width. The meaning of what a column is can be considered a fairly weak notion for some fonts.
	 * This method is used to define the width of a column. 
	 * By default this is defined to be the width of the character m for the font used.
	 * if the font size changed, the invalidateColumnRowSize will be called,
	 * then next call get method about this will be counted first.
	 */
	protected function getColumnWidth():int{
		if(!columnRowCounted) countColumnRowSize();
		return columnWidth;
	}
	
	/**
	 * Returns the row height. The meaning of what a column is can be considered a fairly weak notion for some fonts.
	 * This method is used to define the height of a row. 
	 * By default this is defined to be the height of the character m for the font used.
	 * if the font size changed, the invalidateColumnRowSize will be called,
	 * then next call get method about this will be counted first.
	 */
	protected function getRowHeight():int{
		if(!columnRowCounted) countColumnRowSize();
		return rowHeight;
	}
	
	/**
	 * @see #getColumnWidth()
	 */
	protected function getWidthMargin():int{
		if(!columnRowCounted) countColumnRowSize();
		return widthMargin;
	}
	
	/**
	 * @see #getRowHeight()
	 */	
	protected function getHeightMargin():int{
		if(!columnRowCounted) countColumnRowSize();
		return heightMargin;
	}
	
	protected function getTextFieldAutoSizedSize(forceWidth:int=0, forceHeight:int=0):IntDimension{
		var tf:TextField = getTextField();
		var oldSize:IntDimension = new IntDimension(tf.width, tf.height);
		var old:String = tf.autoSize;
		if(forceWidth != 0){
			tf.width = forceWidth;
		}
		if(forceHeight != 0){
			tf.height = forceHeight;
		}
		tf.autoSize = TextFieldAutoSize.LEFT;
		var size:IntDimension = new IntDimension(tf.width, tf.height);
		tf.autoSize = old;
		tf.width = oldSize.width;
		tf.height = oldSize.height;
		if(forceWidth != 0){
			size.width = forceWidth;
		}
		if(forceHeight != 0){
			size.height = forceHeight;
		}
		return size;
	}
	
	protected function countColumnRowSize():void{
		var str:String = "mmmmm";
		var tf:TextFormat = getFont().getTextFormat();
		var textFieldSize:IntDimension = AsWingUtils.computeStringSize(tf, str, true, getTextField());
		var textSize:IntDimension = AsWingUtils.computeStringSize(tf, str, false, getTextField());
		
		columnWidth = textSize.width/5;
		rowHeight = textSize.height;
		widthMargin = textFieldSize.width - textSize.width;
		heightMargin = textFieldSize.height - textSize.height;
		columnRowCounted = true;
	}
	
    /**
     * Returns the text field to receive the focus for this component.
     * @return the object to receive the focus.
     */
    override public function getInternalFocusObject():InteractiveObject{
    	return getTextField();
    }
	
	override protected function paint(b:IntRectangle):void{
		super.paint(b);
		applyBoundsToText(b);
	}
	
    protected function applyBoundsToText(b:IntRectangle):void{
		var t:TextField = getTextField();
		t.x = b.x;
		t.y = b.y;
		t.width = b.width;
		t.height = b.height;
    }	
}
}