/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASFont;
import org.aswing.ASTextExtent;
import org.aswing.ASTextFormat;
import org.aswing.ASWingUtils;
import org.aswing.Component;
import org.aswing.FocusManager;
import org.aswing.geom.Dimension;
import org.aswing.geom.Rectangle;
import org.aswing.JOptionPane;
import org.aswing.plaf.TextUI;
import org.aswing.RepaintManager;
import org.aswing.util.Delegate;

/**
 * JTextComponent is the base class for text components. 
 * @author Tomato, iiley
 */
class org.aswing.JTextComponent extends Component {
	/**
	 * When the text content was scrolled.
	 *<br>
	 * onTextScroll(source:JTextComponent)
	 */	
	public static var ON_TEXT_SCROLLED :String = "onTextScrolled";
	/**
	 * When the text content was changed.
	 *<br>
	 * onTextChanged(source:JTextComponent)
	 */	
	public static var ON_TEXT_CHANGED:String = "onTextChanged";	
	
	private static var defaultRegainTextFocusEnabled:Boolean = true;
	private var regainTextFocusEnabled:Boolean;
	
	private var text:String;
	private var htmlText:String;
	private var textFormat:ASTextFormat;
	private var html:Boolean;
	private var password:Boolean;
	private var maxChars:Number;
	private var restrict:String;
	private var editable:Boolean;
	private var condenseWhite:Boolean;
    private var css:TextField.StyleSheet;
	
	private var textField:TextField;
	private var textListener:Object;
	
	//dont access these properties directly in subclass, use get method.
	private var columnWidth:Number;
	private var rowHeight:Number;
	private var widthMargin:Number;
	private var heightMargin:Number;
	
	private var columnRowCounted:Boolean;
	private var autoSizedToCountPrefferedSize:Boolean;
	private var autoSizedSize:Dimension;
	
	private var lastBeginIndex:Number;
	private var lastEndIndex:Number;
	private var lastCaretIndex:Number;
	private var keyRecordListener:Object;
	private var isTextRollOver:Boolean;
	private var mouseMoveListenerForRolls:Object;
	private static var mouseRecordListener:Object;
		
	private function JTextComponent() {
		super();
		setName("JTextComponent");
		textFormat = ASTextFormat.getASTextFormatWithASFont(font);
		text = "";
		columnRowCounted = false;
		autoSizedToCountPrefferedSize = false;
		html = false;
		password = false;
		maxChars = null;
		restrict = null;
		editable = true;
		condenseWhite = false;
		regainTextFocusEnabled = null;
		isTextRollOver = false;
		
		lastBeginIndex = 0;
		lastEndIndex = 0;
		lastCaretIndex = 0;
		
		keyRecordListener = new Object();
		keyRecordListener[ON_KEY_DOWN] = Delegate.create(this, __recordSelectionWhenKeyDown);
		addEventListener(keyRecordListener);
		
		if(mouseRecordListener == undefined){
			mouseRecordListener = new Object();
			mouseRecordListener.onMouseDown = Delegate.create(JTextComponent, __recordSelectionWhenMouseDown);
			Mouse.addListener(mouseRecordListener);
		}
				
		textListener = new Object();
		textListener.onChanged   = Delegate.create(this, ____uiTextChanged);
		textListener.onScroller  = Delegate.create(this, ____uiTextScrolled);
	}
	private function __recordSelectionWhenKeyDown():Void{
		recordSelection();
	}
	private static function __recordSelectionWhenMouseDown():Void{
		var focusOwner:Component = FocusManager.getCurrentManager().getFocusOwner();
		if(focusOwner instanceof JTextComponent){
			var ftc:JTextComponent = JTextComponent(focusOwner);
			ftc.recordSelection();
		}
	}
	private function recordSelection():Void{
		lastBeginIndex = Selection.getBeginIndex();
		lastEndIndex   = Selection.getEndIndex();
		lastCaretIndex = Selection.getCaretIndex();
	}
    /**
     * Returns the L&F object that renders this component.
     * @return the TextUI object
     * @see #setUI()
     */
    public function getUI():TextUI {
        return TextUI(ui);
    }
    
    /**
     * Sets the L&F object that renders this component.
     * @param ui the <code>TextUI</code> L&F object
     * @see #getUI()
     */
    public function setUI(ui:TextUI):Void {
        super.setUI(ui);
    }
    
    /**
     * Resets the UI property to a value from the current look
     * and feel.  Subtypes of <code>JTextComponent</code>
     * should override this to update the UI. For
     * example, <code>JTextField</code> might do the following:
     * <pre>
     *      setUI(TextUI(UIManager.getUI(this)));
     * </pre>
     */
    public function updateUI():Void{
    }
    
	/**
	 * Shortcut to and ON_TEXT_CHANGED listener.
	 * <p>
	 * addChangeListener(func:Function)<br>
	 * addChangeListener(func:Function, obj:Object)<br>
	 * @param func the function which want to handler the event.
	 * @param obj context in which to run the function of param func.
	 * @see #ON_TEXT_CHANGED
	 */	
    public function addChangeListener(func:Function, obj:Object):Object{
    	return addEventListener(ON_TEXT_CHANGED, func, obj);
    }
    
	public function addEventListener(eventTypeOrLis:Object, func:Function, obj:Object):Object{
		var lis:Object = super.addEventListener(eventTypeOrLis, func, obj);
		checkRolls();
		return lis;
	}
	
	public function removeEventListener(listener:Object):Void{
		super.removeEventListener(listener);
		checkRolls();
	}
    
	/**
	 * Set the text of this text field.(it the textfield enabled html, it will be htmlText)
	 * @param t the text of this text field, if it is null or undefined, it will be set to "";
	 * @see #isHtml()
	 */
	public function setText(t:String):Void{
		if(t == null) t = "";
		if(t != text){
			text = t;
			invalidateTextFieldAutoSizeToCountPrefferedSize();
			if(getTextField()!= null){
				if(isHtml()){
					getTextField().styleSheet = getCSS();
					getTextField().htmlText = text;
				}else{
					getTextField().text = text;
				}
			}else{
				repaint();
				dispatchEvent(ON_TEXT_CHANGED, createEventObj(ON_TEXT_CHANGED));
			}
			if(isAutoSize()){
				revalidate();
			}
		}
	}
	
	/**
	 * Returns the text contained in this TextComponent.
	 * @return the text
	 */
	public function getText():String{
		return text;
	}
	
	/**
	 * Sets the caret index -  the blinking insertion point (caret) position 
	 * for the text. This is a equivalent to <code>setSelection(caretIndex, caretIndex)</code>
	 * @see #setSelection()
	 * @see #getCaretIndex()
	 * @param caretIndex the caret index to set.
	 */
	public function setCaretIndex(caretIndex:Number):Void{
		lastCaretIndex = caretIndex;
		setSelection(caretIndex, caretIndex);
	}
	
	/**
	 * Sets the selection range of the text.
	 * @see #getSelectionStart();
	 * @see #getSelectionEnd()
	 * @param startIndex the selection start index.
	 * @param endIndex   the selection end index.
	 */
	public function setSelection(startIndex:Number, endIndex:Number):Void{
		lastBeginIndex = startIndex;
		lastEndIndex   = endIndex;
		if(isTextFieldFocused()){
    		Selection.setSelection(lastBeginIndex, lastEndIndex);
		}
	}
	
	/**
	 * Select the all text.
	 */
	public function selectAll():Void{
		lastCaretIndex = getText().length;
		setSelection(0, getText().length);
		getTextField().hscroll = getTextField().maxhscroll;
	}
	
	/**
	 * Returns the index of the last time blinking insertion point (caret) position.
	 * Selection span indexes are zero-based (for example, the first position is 0, the second position is 1, and so on).
	 * @return the caret position
	 */
	public function getCaretIndex():Number{
		return lastCaretIndex;
	}
	
	/**
	 * Returns the index at the beginning of the last time selection span.
	 * Selection span indexes are zero-based 
	 * (for example, the first position is 0, the second position is 1, and so on).
	 * @return the index at the beginning of the selection span
	 */
	public function getSelectionStart():Number{
		return lastBeginIndex;
	}
	
	/**
	 * Returns the ending index of the last time selection span. 
	 * span, the method returns -1. Selection span indexes are zero-based 
	 * (for example, the first position is 0, the second position is 1, and so on).
	 * @return the ending index of the currently focused selection span
	 */
	public function getSelectionEnd():Number{
		return lastEndIndex;
		
	}
	
	/**
	 * Appends a text to the text's end.
	 * @param t the text to append.
	 */
	public function appendText(t:String):Void{
		setText(text + t);
	}
	
	/**
	 * Inserts a text to the specified position of current text.
	 * @param t the text to insert
	 * @param position the position to insert, if it less or equals than 0 means the begin of current text.
	 */
	public function insertText(t:String, position:Number):Void{
		if(position <= 0){
			setText(t + text);
		}else{
			setText(text.substr(0, position) + t + text.substr(position));
		}
	}
	
	/**
	 * Sets whether use html style. 
	 * @see #getHtmlText()
	 */
	public function setHtml(enabled:Boolean):Void{
		if(html != enabled){
			html = enabled;
			invalidateTextFieldAutoSizeToCountPrefferedSize();
			if(getTextField()!= null){
				applyPropertiesToText(getTextField(), false);
				applyBoundsToText(getTextField(), getPaintBounds());
			}else{
				repaint();
			}
			revalidate();
		}
	}
	
	public function isHtml():Boolean{
		return html;
	}
	
	public function setPasswordField(p:Boolean):Void{
		if(password != p){
			password = p;
			if(getTextField() != null){
				getTextField().password = p;
			}
			repaint();
		}
	}
	
	public function isPasswordField():Boolean{
		return password;
	}
	
	public function setMaxChars(count:Number):Void{
		if(maxChars != count){
			maxChars = count;
			if(getTextField() != null){
				getTextField().maxChars = count;
			}
			repaint();
			revalidate();
		}
	}
	
	public function getMaxChars():Number{
		return maxChars;
	}
	
	public function setRestrict(r:String):Void{
		if(this.restrict !== r){
			this.restrict = r;
			if(getTextField() != null){
				getTextField().restrict = r;
			}
			repaint();
			revalidate();
		}
	}
	
	public function getRestrict():String{
		return restrict;
	}
	
	public function setCondenseWhite(b:Boolean):Void{
		if(condenseWhite != b){
			condenseWhite = b;
			if(getTextField() != null){
				getTextField().condenseWhite = b;
			}
			repaint();
			revalidate();
		}
	}
	
	public function isCondenseWhite():Boolean{
		return condenseWhite;
	}
	
	public function setEnabled(enabled:Boolean):Void{
		var old:Boolean = isEnabled();
		super.setEnabled(enabled);
		if(old != enabled){
			if(getTextField() != null){
				getTextField().selectable = enabled;
			}
			repaint();
		}
	}
	
	public function setEditable(editable:Boolean):Void{
		if(this.editable != editable){
			this.editable = editable;
			if(getTextField() != null){
				getTextField().type = editable ? "input" : "dynamic";
			}
			repaint();
		}
	}
	
	public function isEditable():Boolean{
		return editable;
	}
	
	/**
	 * Returns the chars of the text.
	 */
	public function getTextLength():Number{
		return getText().length;
	}
	
	/**
	 * Returns is the textField is focused from Selection judge.
	 */
	public function isTextFieldFocused():Boolean{
		return eval(Selection.getFocus()) == textField;
	}
	
	/**
	 * Returns the regain text focus ability.
	 * @see #setRegainTextFocusEnabled()
	 */
	public function isRegainTextFocusEnabled():Boolean {
		return (regainTextFocusEnabled != null) ? regainTextFocusEnabled : isDefaultRegainTextFocusEnabled();
	}
	
	/**
	 * Sets wether regain focus from Selection when a AsWing Focused TextComponent's TextField lost Selection focus.
	 * The TextField us Selection.setFocus() method to regain the Selection focus.
	 * @param b true to enable this function, false to disable.
	 */
	public function setRegainTextFocusEnabled(b:Boolean):Void {
		this.regainTextFocusEnabled = b;
	}
	
	/**
	 * Returns the default value for all TextComponent's <code>regainTextFocusEnabled</code> property.
	 */
	public static function isDefaultRegainTextFocusEnabled():Boolean {
		return defaultRegainTextFocusEnabled;
	}
	
	/**
	 * Sets the default value for all TextComponent's <code>regainTextFocusEnabled</code> property.
	 */
	public static function setDefaultRegainTextFocusEnabled(b:Boolean):Void {
		defaultRegainTextFocusEnabled = b;
	}	
	
	//-------------------------------------------------
	/**
	 * Sets the text format properties for the text component's text format.
	 * <p>The will casue the font changed.
	 */
	public function setTextFormat(tf:ASTextFormat):Void{
		var t:ASTextFormat = tf.clone();
		setFont(t.getASFont());
		invalidateColumnRowSize();
		textFormat = t;
	}
	
	public function setFont(f:ASFont):Void{
		if(getFont() != f){
			invalidateColumnRowSize();
			invalidateTextFieldAutoSizeToCountPrefferedSize();
			super.setFont(f);
			textFormat.setASFont(f);
		}
	}
	
	/**
	 * Returns a copy of the text format of this component.
	 */
	public function getTextFormat():ASTextFormat{
		return textFormat.clone();
	}
	
	//------------------------For UIs---------------------------	

    
    private function create():Void{
    	super.create();
    	textField = createTextField("textField");
    	textField.addListener(textListener);
		textField.onSetFocus  = Delegate.create(this, ____uiTextSetFocus);
		textField.onKillFocus = Delegate.create(this, ____uiTextKilledFocus);
    	
    	//call this first to validate current textfield scroll properties
		applyPropertiesToText(textField, false);
		applyBoundsToText(textField, getPaintBounds());
    	var t:TextField = textField;
    	if(isHtml()){
    		textField.styleSheet = getCSS();
    		textField.htmlText = text;
    	}else{
    		textField.text = text;
    	}
    	textField.background = false;
    }
    
    private function paint(b:Rectangle):Void{
    	super.paint(b);
    	var t:TextField = textField;
    	applyPropertiesToText(t, false);
    	applyBoundsToText(t, b);
    }
    
    private function applyPropertiesToText(t:TextField, autoSize:Boolean):Void{
    	applyPropertiesToTextExceptTextAndFormat(t, autoSize);
		if(isHtml()){
			if(t.htmlText != text){
				t.styleSheet = getCSS();
				t.htmlText = (text == null ? "" : text);
			}
		}else{
			if(t.text != text){
				t.text = (text == null ? "" : text);
			}
			ASWingUtils.applyTextFormatAndColor(t, textFormat, getForeground());
		}
    }
    
    private function applyPropertiesToTextExceptTextAndFormat(t:TextField, autoSize:Boolean):Void{
    	t.autoSize = autoSize;
    	t.wordWrap = isWordWrap();
    	if(isWordWrap()){
    		t._width = getWordWrapWidth();
    	}
		t.background = false;
		t.border = false;
		t.type = isEditable() ? "input" : "dynamic";
		t.selectable = isEnabled();
		t.maxChars = getMaxChars();
		t.html = isHtml();
		t.restrict = getRestrict();
		t.condenseWhite = isCondenseWhite();
		t.password = isPasswordField();
		t.multiline = isMultiline();
    }
    
    private function applyBoundsToText(t:TextField, b:Rectangle):Void{
		t._x = b.x;
		t._y = b.y;
		t._width = b.width;
		t._height = b.height;
    }
    
    /**
     * @return the textField if created, null if not.
     */
    public function getTextField():TextField{
    	if(isDisplayable()){
    		return textField;
    	}else{
    		return null;
    	}
    }
    
    /**
     * Alwasy return false.
     * Subclass override this to change Text's multiline ability
     */
    public function isMultiline():Boolean{
    	return false;
    }
    /**
     * Subclass override this to change Text's wordWrap ability
     */
    public function isWordWrap():Boolean{
    	return false;
    }
    /**
     * If the isWordWrap method returned true, this method will be called to 
     * get the wrap width.
     * @see #isWordWrap()
     */    
    private function getWordWrapWidth():Number{
    	return 0;
    }
	
	/**
	 * Returns the css <code>StyleSheet</code> the text used.
	 * @return the css <code>StyleSheet</code> used
	 */
	public function getCSS():TextField.StyleSheet {
		return css;
	}
	
	/**
	 * Sets the css <code>StyleSheet</code> to the text.
	 * @param css the css <code>StyleSheet</code> to use
	 */
	public function setCSS(css:TextField.StyleSheet):Void {
		this.css = css;
		if(isHtml()){
			repaint();
			revalidate();
		}
	}
	//----------------------------------------------------------
	
	private function getInternalFocusObj():Object{
		return getTextField();
	}
	
	private function retrieveTextFromTextField():Void{
    	if(isHtml()){
    		text = getTextField().htmlText;
    	}else{
    		text = getTextField().text;
    	}
	}
		
    private function __uiTextChanged():Void{
    	retrieveTextFromTextField();
    	if(!isMultiline()){ //fix the bug that fp in interenet browser single line TextField Ctrl+Enter will entered a newline bug
    		if(Key.getCode() == Key.CONTROL || Key.getCode() == Key.ENTER){
    			for(var i:Number=0; i<text.length; i++){
					if(text.charCodeAt(i) == 13){
						text = text.substring(0, i) + text.substring(i+1);
				    	if(isHtml()){
				    		getTextField().htmlText = text;
				    	}else{
				    		getTextField().text = text;
				    	}
						return;
					}
				}
    		}
    	}
    	invalidateTextFieldAutoSizeToCountPrefferedSize();
		dispatchEvent(ON_TEXT_CHANGED, createEventObj(ON_TEXT_CHANGED));
    }
    
    private function __uiTextScrolled():Void{
    	dispatchEvent(ON_TEXT_SCROLLED, createEventObj(ON_TEXT_SCROLLED));
    }
    
    private function __uiTextSetFocus(oldFocus:Object):Void{
    	if(!isFocusOwner()){
    		requestFocus();
    	}
    }
    
    private var moustUpListenerForRegainFocus:Object;
    private function __uiTextKilledFocus(newFocus:Object):Void{
    	if(isRegainTextFocusEnabled() && isFocusOwner()){
    		Mouse.removeListener(moustUpListenerForRegainFocus);
    		moustUpListenerForRegainFocus = {onMouseUp:Delegate.create(this, __nextMouseUped)};
    		Mouse.addListener(moustUpListenerForRegainFocus);
    	}
    }
    private function __nextMouseUped():Void{
    	Mouse.removeListener(moustUpListenerForRegainFocus);
    	if(isFocusOwner()){
    		startWaitToResumeTheSelectionNextTime();
    	}
    }
    
    private function startWaitToResumeTheSelectionNextTime():Void{
    	RepaintManager.getInstance().addCallAfterNextPaintTime(Delegate.create(this, __resumeTheSelectionAtNextTime));
    }
    
    private function __resumeTheSelectionAtNextTime():Void{
    	if(isFocusOwner()){
    		resumeTheSelection();
    	}
    }
    
    private function resumeTheSelection():Void{
//    	Selection.setFocus(getTextField());
//    	Selection.setSelection(lastBeginIndex, lastEndIndex);
    }
    	
    //cant override this method in sub class
    private function ____uiTextChanged():Void{
    	__uiTextChanged();
    }
    //cant override this method in sub class
    private function ____uiTextScrolled():Void{
    	__uiTextScrolled();
    }
    //cant override this method in sub class
    private function ____uiTextSetFocus(oldFocus:Object):Void{
    	__uiTextSetFocus(oldFocus);
    }
    //cant override this method in sub class
    private function ____uiTextKilledFocus(newFocus:Object):Void{
    	__uiTextKilledFocus(newFocus);
    }
    //-------------------------------------------------------------------
    		
	/**
	 * JTextComponent need count preferred size itself.
	 */
	private function countPreferredSize():Dimension{
		trace("Subclass of JTextComponent need implement this method : countPreferredSize!");
		throw new Error("Subclass of JTextComponent need implement this method : countPreferredSize!");
		return null;
	}
	
	/**
	 * Returns the column width. The meaning of what a column is can be considered a fairly weak notion for some fonts.
	 * This method is used to define the width of a column. 
	 * By default this is defined to be the width of the character m for the font used.
	 * if the font size changed, the invalidateColumnRowSize will be called,
	 * then next call get method about this will be counted first.
	 */
	private function getColumnWidth():Number{
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
	private function getRowHeight():Number{
		if(!columnRowCounted) countColumnRowSize();
		return rowHeight;
	}
	
	/**
	 * @see #getColumnWidth
	 */
	private function getWidthMargin():Number{
		if(!columnRowCounted) countColumnRowSize();
		return widthMargin;
	}
	
	/**
	 * @see #getRowHeight
	 */	
	private function getHeightMargin():Number{
		if(!columnRowCounted) countColumnRowSize();
		return heightMargin;
	}
	
	/**
	 * Subclass override this method to do right counting.
	 */
	private function isAutoSize():Boolean{
		return true;
	}
	
	private function countColumnRowSize():Void{
		var textExtent:ASTextExtent = textFormat.getTextExtent("mmmmm");
		columnWidth = textExtent.getWidth()/5;
		rowHeight = textExtent.getHeight();
		widthMargin = textExtent.getTextFieldWidth() - textExtent.getWidth();
		heightMargin = textExtent.getTextFieldHeight() - textExtent.getHeight();
		columnRowCounted = true;
	}
	
	public function getTextFieldAutoSizedSize():Dimension{
		if(!autoSizedToCountPrefferedSize){
			 countAutoSizedSize();
			 autoSizedToCountPrefferedSize = true;
		}
		return autoSizedSize;
	}
	private function countAutoSizedSize():Void{
		var t:TextField = creater.createTF(_root, "tempText");
		applyPropertiesToText(t, true);
		autoSizedSize = new Dimension(t._width, t._height);
		t.removeTextField();
		delete _root[t._name];
	}
	
	private function invalidateColumnRowSize():Void{
		columnRowCounted = false;
	}
	
	private function invalidateTextFieldAutoSizeToCountPrefferedSize():Void{
		autoSizedToCountPrefferedSize = false;
	}
	
	//___________________ Simulate Mouse Roll Over/Out____________________
	
	private function checkRolls():Void{
		for(var i:Number=0; i<listeners.length; i++){
			if(listeners[i][ON_ROLLOVER] != undefined || 
				listeners[i][ON_ROLLOUT] != undefined){
				setListenRolls(true);
				return;
			}
		}
		setListenRolls(false);
	}
    
    private function setListenRolls(b:Boolean):Void{
    	if(b && mouseMoveListenerForRolls == null){
			mouseMoveListenerForRolls = new Object();
			mouseMoveListenerForRolls["onMouseMove"] = Delegate.create(this, __mouseMoveForRolls);
			Mouse.addListener(mouseMoveListenerForRolls);
			trigger_mc.onRollOver = undefined;
			trigger_mc.onRollOut = undefined;
    	}else if(mouseMoveListenerForRolls != null && !b){
    		Mouse.removeListener(mouseMoveListenerForRolls);
			trigger_mc.onRollOver = Delegate.create(this, ____onRollOver);
			trigger_mc.onRollOut = Delegate.create(this, ____onRollOut);
    		mouseMoveListenerForRolls = null;
    	}
    }
    
    private function initActive():Void{
    	super.initActive();
    	if(mouseMoveListenerForRolls != null){
			trigger_mc.onRollOver = undefined;
			trigger_mc.onRollOut = undefined;
    	}
    }
    
    private function __mouseMoveForRolls():Void{
    	var tf:MovieClip = root_mc;
    	if(tf == null){
    		return;
    	}
    	var x:Number = tf._parent._xmouse;
        var y:Number = tf._parent._ymouse;
        
        if (x >= tf._x && x <= tf._x+tf._width && y >= tf._y && y <= tf._y+tf._height){
            if (!isTextRollOver){
                ____onRollOver();
                isTextRollOver = true;
            }
        }else{
			if (isTextRollOver){
		        ____onRollOut();
		        isTextRollOver = false;
			}
        }
    }
}
