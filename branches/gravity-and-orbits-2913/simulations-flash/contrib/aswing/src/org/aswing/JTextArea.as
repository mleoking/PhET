/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.Component;
import org.aswing.FocusManager;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.JTextComponent;
import org.aswing.plaf.TextUI;
import org.aswing.UIManager;
import org.aswing.Viewportable;

/**
 * A JTextArea is a multi-line area that displays text.
 * <p>
 * With JScrollPane, it's easy to be a scrollable text area, for example:
 * <pre>
 * var ta:JTextArea = new JTextArea();
 * 
 * var sp:JScrollPane = new JScrollPane(ta); 
 * //or 
 * //var sp:JScrollPane = new JScrollPane(); 
 * //sp.setView(ta);
 * </pre>
 * @author Tomato, iiley
 * @see org.aswing.JScrollPane
 */
class org.aswing.JTextArea extends JTextComponent implements Viewportable {
	/**
	 * When the JTextArea Viewportable state changed.
	 *<br>
	 * onStateChanged(source:JTextArea)
	 */	
	public static var ON_STATE_CHANGED:String = "onStateChanged";//Component.ON_STATE_CHANGED; 
	
	private var columns:Number;
	private var rows:Number;
	
	private var viewPos:Point;
	
	private var wordWrap:Boolean;
	private var multiline:Boolean;
	private var viewportSizeTesting:Boolean;
	
	private var verticalUnitIncrement:Number;
	private var verticalBlockIncrement:Number;
	private var horizontalUnitIncrement:Number;
	private var horizontalBlockIncrement:Number;
	
	/**
	 * JTextArea(text:String, rows:Number, columns:Number)<br>
	 * JTextArea(text:String, rows:Number) columns default to 0<br> 
	 * JTextArea(text:String) rows and columns default to 0<br>
	 * JTextArea() text default to 0, rows and columns default to 0<br>
	 * <p>
	 * @see #setRows()
	 * @see #setColumns()
	 */
	public function JTextArea(text:String, rows:Number, columns:Number) {
		super();
		setName("JTextArea");
		this.text = (text == undefined ? "" : text);
		
		viewPos = new Point();
		setRows(rows);
		setColumns(columns);
		wordWrap = false;
		multiline = true;
		viewportSizeTesting = false;
		updateUI();
	}
	
    public function updateUI():Void{
    	setUI(TextUI(UIManager.getUI(this)));
    }	
	
	public function getUIClassID():String{
		return "TextAreaUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicTextAreaUI;
    }
	
	public function setWordWrap(wrap:Boolean):Void{
		if(wordWrap != wrap){
			wordWrap = wrap;
			if(getTextField()!= null){
				getTextField().wordWrap = wrap;
			}else{
				repaint();
			}
			invalidateTextFieldAutoSizeToCountPrefferedSize();
			revalidate();
		}
	}
	
	public function isWordWrap():Boolean{
		return wordWrap;
	}
		
	/**
	 * Sets the number of columns in this JTextArea, if it changed then call parent to do layout. 
	 * @param columns the number of columns to use to calculate the preferred width;
	 * if columns is set to zero or min than zero, the preferred width will be matched just to view all of the text.
	 * default value is zero if missed this param.
	 */
	public function setColumns(columns:Number):Void{
		if(columns == undefined) columns = 0;
		if(columns < 0) columns = 0;
		if(this.columns != columns){
			this.columns = columns;
			if(isWordWrap()){
				invalidateTextFieldAutoSizeToCountPrefferedSize();
			}
			if(displayable){
				revalidate();
			}
		}
	}
	
	/**
	 * @see #setColumns
	 */
	public function getColumns():Number{
		return columns;
	}
	
	/**
	 * Sets the number of rows in this JTextArea, if it changed then call parent to do layout. 
	 * @param rows the number of rows to use to calculate the preferred height;
	 * if rows is set to zero or min than zero, the preferred height will be matched just to view all of the text.
	 * default value is zero if missed this param.
	 */
	public function setRows(rows:Number):Void{
		if(rows == undefined) rows = 0;
		if(rows < 0) rows = 0;
		if(this.rows != rows){
			this.rows = rows;
			if(isWordWrap()){
				invalidateTextFieldAutoSizeToCountPrefferedSize();
			}
			if(displayable){
				revalidate();
			}
		}
	}
	
	/**
	 * @see #setRows
	 */
	public function getRows():Number{
		return rows;
	}
	
	public function size():Void{
		super.size();
		if(isWordWrap()){
			//invalidateTextFieldAutoSizeToCountPrefferedSize();
		}
		if(getTextField() != null){
			//trace("_|||||||__  Sized!!");
			applyPropertiesToText(getTextField(), false);
			applyBoundsToText(getTextField(), getPaintBounds());
	    	//call this first to validate current textfield scroll properties
	    	var t:TextField = getTextField();
	    	if(isHtml()){
	    		t.styleSheet = getCSS();
	    		t.htmlText = text;
	    	}else{
	    		t.text = text;
	    	}
	    	t.background = false;
		}
	}
	
	private function isAutoSize():Boolean{
		return columns == 0 || rows == 0;
	}	
				
	public function countPreferredSize():Dimension{
		var size:Dimension;
		if(columns > 0 && rows > 0){
			var width:Number = getColumnWidth() * columns + getWidthMargin();
			var height:Number = getRowHeight() * rows + getHeightMargin();
			size = new Dimension(width, height);
		}else if(rows <=0 && columns <=0 ){
			size = getTextFieldAutoSizedSize();
		}else if(rows > 0){ // columns must <= 0
			size = getTextFieldAutoSizedSize();
			size.height = getRowHeight() * rows + getHeightMargin();
		}else{ //must be columns > 0 and rows <= 0
			size = getTextFieldAutoSizedSize();
		}
		return getInsets().getOutsideSize(size);
	}
		
    private function getWordWrapWidth():Number{
    	if(columns > 0){
    		return getColumnWidth() * columns + getWidthMargin();
    	}else{
    		return getPaintBounds().width;
    	}
    }
    
    /**
     * Sets if the text is multiline, by default it is true.
     * @param b true to support multiline, otherwise false.
     */
    public function setMultiline(b:Boolean):Void{
    	if(multiline != b){
    		multiline = b;
    		if(getTextField() != null){
    			getTextField().multiline = b;
    		}else{
    			repaint();
    		}
    		revalidate();
    	}
    }
    
    /**
     * Returns is the text support multiline.
     */
    public function isMultiline():Boolean{
    	return multiline;
    }
    
    //-------------------------------text listeners ---------------------
    private var needResumeSelection:Boolean;
	private function __onFocusGainedExtraFix():Void{
		if(FocusManager.getCurrentManager().receiveFocus(getInternalFocusObj()) != 0){
			needResumeSelection = true;
		}else{
			needResumeSelection = false;
		}
	}
	
	private function __uiTextSetFocus(oldFocus:Object):Void{
    	super.__uiTextSetFocus(oldFocus);
    	if(needResumeSelection == true){
    		startWaitToResumeTheSelectionNextTime();
    		needResumeSelection = false;
    	}
    }
	
	private function __uiTextChanged():Void{
    	if(viewportSizeTesting){
    		return;
    	}
		super.__uiTextChanged();
		revalidate();
	}
	
    private function __uiTextScrolled():Void{
    	if(viewportSizeTesting){
    		return;
    	}
    	var t:TextField = getTextField();
		var newViewPos:Point = new Point(t.hscroll, t.scroll-1);
		var isDiff:Boolean = Math.abs(viewPos.y-newViewPos.y)>=1 || Math.abs(viewPos.x - newViewPos.x) >= 1;
		if(isDiff){
			viewPos.setLocation(newViewPos);
			//notify scroll bar to syn
			fireStateChanged();
		}
    	super.__uiTextScrolled();
    }
    
	//---------------------------implementation of Viewportable---------------------------------
	
	/**
	 * the <code>Viewportable</code> state change listener adding method implementation.
	 */
	public function addChangeListener(func:Function, obj:Object):Object{
		return addEventListener(ON_STATE_CHANGED, func, obj);
	}
		
	/**
	 * Returns the unit value for the Vertical scrolling.
	 */
    public function getVerticalUnitIncrement():Number{
    	if(verticalUnitIncrement == undefined){
    		return 1;
    	}else{
    		return verticalUnitIncrement;
    	}
    }
    
    /**
     * Return the block value for the Vertical scrolling.
     */
    public function getVerticalBlockIncrement():Number{
    	if(verticalBlockIncrement == undefined){
    		return 10;
    	}else{
    		return verticalBlockIncrement;
    	}
    }
    
	/**
	 * Returns the unit value for the Horizontal scrolling.
	 */
    public function getHorizontalUnitIncrement():Number{
    	if(horizontalUnitIncrement == undefined){
    		return getColumnWidth();
    	}else{
    		return horizontalUnitIncrement;
    	}
    }
    
    /**
     * Return the block value for the Horizontal scrolling.
     */
    public function getHorizontalBlockIncrement():Number{
    	if(horizontalBlockIncrement == undefined){
    		return getColumnWidth()*10;
    	}else{
    		return horizontalBlockIncrement;
    	}
    }
    
	/**
	 * Sets the unit value for the Vertical scrolling.
	 */
    public function setVerticalUnitIncrement(increment:Number):Void{
    	if(verticalUnitIncrement != increment){
    		verticalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    /**
     * Sets the block value for the Vertical scrolling.
     */
    public function setVerticalBlockIncrement(increment:Number):Void{
    	if(verticalBlockIncrement != increment){
    		verticalBlockIncrement = increment;
			fireStateChanged();
    	}
    }
    
	/**
	 * Sets the unit value for the Horizontal scrolling.
	 */
    public function setHorizontalUnitIncrement(increment:Number):Void{
    	if(horizontalUnitIncrement != increment){
    		horizontalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    /**
     * Sets the block value for the Horizontal scrolling.
     */
    public function setHorizontalBlockIncrement(increment:Number):Void{
    	if(horizontalBlockIncrement != increment){
    		horizontalBlockIncrement = increment;
			fireStateChanged();
    	}
    }
    
    public function setViewportTestSize(s:Dimension):Void{
    	viewportSizeTesting = true;
    	setSize(s);
    	validateScroll();
    	viewportSizeTesting = false;
    }
   
    
    /**
     * Returns the size of the visible part of the view in view logic coordinates.
     *
     * @return a <code>Dimension</code> object giving the size of the view
     */
    public function getExtentSize():Dimension{    
    	var t:TextField = getTextField();
    	if(t == null){
    		return new Dimension(1, 1);//max than getViewSize
    	}
    	var wSize:Number, hSize:Number;
    	wSize = t._width;
    	var bottomscroll:Number = t.bottomScroll;
		var scroll:Number = t.scroll;
		var extent:Number = bottomscroll - scroll + 1;
		hSize = extent;
    	return new Dimension(wSize, hSize);
    }
        
    /**
     * Returns the viewportable view's amount size if view all content in view logic coordinates.
     * @return the view's size.
     */
    public function getViewSize():Dimension{
    	var t:TextField = getTextField();
    	if(t == null){
    		return new Dimension(0, 0);
    	}
    	var wRange:Number, hRange:Number;
    	if(isWordWrap()){
    		wRange = t._width;
    		t.hscroll = 0;
    	}else{
	    	if(t.textWidth + 5 > t._width){
    			wRange = t._width + t.maxhscroll;
	    	}else{
    			wRange = t._width;
    			t.hscroll = 0;
    			t.background = false;
	    	}
    	}
    	var bottomscroll:Number = t.bottomScroll;
		var maxscroll:Number = t.maxscroll;
		var scroll:Number = t.scroll;
		var extent:Number = bottomscroll - scroll + 1;
		var maxValue:Number = maxscroll + extent;
		var minValue:Number = 1;
    	hRange = maxValue - minValue;
    	return new Dimension(wRange, hRange);
    }
        
    /**
     * Returns the view coordinates that appear in the upper left
     * hand corner of the viewport, or 0,0 if there's no view. in view logic coordinates.
     *
     * @return a <code>Point</code> object giving the upper left coordinates
     */
    public function getViewPosition():Point{
    	return new Point(viewPos);
    }
    
    /**
     * Sets the view coordinates that appear in the upper left
     * hand corner of the viewport. in view logic coordinates.
     *
     * @param p  a <code>Point</code> object giving the upper left coordinates
     */
    public function setViewPosition(p:Point):Void{
		restrictionViewPos(p);
		if(!viewPos.equals(p)){
			viewPos.setLocation(p);
			validateScroll();
			fireStateChanged();
		}
    }
        
    private function validateScroll():Void{
		var xS:Number = Math.round(viewPos.x);
		var yS:Number = Math.round(viewPos.y)+1;
    	var t:TextField = getTextField();
		if(t.hscroll != xS){
			t.hscroll = xS;
		}
		if(t.scroll != yS){
			t.scroll = yS;
		}
		t.background = false; //avoid TextField background lose effect bug
    }
    
    /**
     * Returns visible row count.
     * @return how many rows can see
     */
    public function getVisibleRows():Number{
    	if(getTextField() != null){
    		return getExtentSize().height;
    	}else{
    		return Math.floor(getHeight()/getRowHeight());
    	}
    }
    
    /**
     * Scrolls the view so that <code>Rectangle</code>
     * within the view becomes visible. in view logic coordinates.
     * <p>
     * Note that this method will not scroll outside of the
     * valid viewport; for example, if <code>contentRect</code> is larger
     * than the viewport, scrolling will be confined to the viewport's
     * bounds.
     * @param contentRect the <code>Rectangle</code> to display
     */
	public function scrollRectToVisible(contentRect : Rectangle) : Void {
		setViewPosition(new Point(contentRect.x, contentRect.y));
	}
	
	/**
	 * Scrolls to view bottom left content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */
	public function scrollToBottomLeft():Void{
		setViewPosition(new Point(0, Number.MAX_VALUE));
	}
	/**
	 * Scrolls to view bottom right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToBottomRight():Void{
		setViewPosition(new Point(Number.MAX_VALUE, Number.MAX_VALUE));
	}
	/**
	 * Scrolls to view top left content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopLeft():Void{
		setViewPosition(new Point(0, 0));
	}
	/**
	 * Scrolls to view to right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopRight():Void{
		setViewPosition(new Point(Number.MAX_VALUE, 0));
	}	
	
	private function restrictionViewPos(p:Point):Point{
		var maxPos:Point = getViewMaxPos();
		p.x = Math.max(0, Math.min(maxPos.x, p.x));
		p.y = Math.max(0, Math.min(maxPos.y, p.y));
		return p;
	}
	
	private function getViewMaxPos():Point{
		var showSize:Dimension = getExtentSize();
		var viewSize:Dimension = getViewSize();
		var p:Point = new Point(viewSize.width-showSize.width, viewSize.height-showSize.height);
		if(p.x < 0) p.x = 0;
		if(p.y < 0) p.y = 0;
		return p;
	}
    
    /**
     * Return the component of the viewportable's pane which would added to displayed on the stage.
     * @return the component of the viewportable pane.
     */
    public function getViewportPane():Component{
    	return this;
    }	
}
