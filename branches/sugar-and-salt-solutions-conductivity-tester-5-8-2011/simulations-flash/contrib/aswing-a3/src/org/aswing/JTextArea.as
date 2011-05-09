/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import flash.events.Event;
import flash.text.*;

import org.aswing.event.InteractiveEvent;
import org.aswing.geom.IntDimension;
import org.aswing.geom.IntPoint;
import org.aswing.geom.IntRectangle;
import org.aswing.plaf.basic.BasicTextAreaUI;

/**
 * Dispatched when the viewport's state changed. the state is all about:
 * <ul>
 * <li>view position</li>
 * <li>verticalUnitIncrement</li>
 * <li>verticalBlockIncrement</li>
 * <li>horizontalUnitIncrement</li>
 * <li>horizontalBlockIncrement</li>
 * </ul>
 * </p>
 * 
 * @eventType org.aswing.event.InteractiveEvent.STATE_CHANGED
 */
[Event(name="stateChanged", type="org.aswing.event.InteractiveEvent")]

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
 * @author iiley
 * @see org.aswing.JScrollPane
 */
public class JTextArea extends JTextComponent implements Viewportable{
	 	
 	/**
 	 * The default unit/block increment, it means auto count a value.
 	 */
 	public static const AUTO_INCREMENT:int = int.MIN_VALUE;
 	
	private static var defaultMaxChars:int = 0;
 	
	private var columns:int;
	private var rows:int;
	
	private var viewPos:IntPoint;
	private var viewportSizeTesting:Boolean;
	private var lastMaxScrollV:int;
	private var lastMaxScrollH:int;
		
	private var verticalUnitIncrement:int;
	private var verticalBlockIncrement:int;
	private var horizontalUnitIncrement:int;
	private var horizontalBlockIncrement:int;
	
	public function JTextArea(text:String="", rows:int=0, columns:int=0){
		super();
		setName("JTextField");
		getTextField().multiline = true;
		getTextField().text = text;
		setMaxChars(defaultMaxChars);
		this.rows = rows;
		this.columns = columns;
		viewPos = new IntPoint();
		viewportSizeTesting = false;			
		lastMaxScrollV = getTextField().maxScrollV;
		lastMaxScrollH = getTextField().maxScrollH;
		
		verticalUnitIncrement = AUTO_INCREMENT;
		verticalBlockIncrement = AUTO_INCREMENT;
		horizontalUnitIncrement = AUTO_INCREMENT;
		horizontalBlockIncrement = AUTO_INCREMENT;
		
		getTextField().addEventListener(Event.CHANGE, __onTextAreaTextChange);
		getTextField().addEventListener(Event.SCROLL, __onTextAreaTextScroll);
		
		updateUI();
	}
	
	override public function updateUI():void{
		setUI(UIManager.getUI(this));
	}
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicTextAreaUI;
    }
	
	override public function getUIClassID():String{
		return "TextAreaUI";
	}
	
	/**
	 * Sets the maxChars property for default value when <code>JTextArea</code> be created.
	 * By default it is 0, you can change it by this method.
	 * @param n the default maxChars to set
	 */
	public static function setDefaultMaxChars(n:int):void{
		defaultMaxChars = n;
	}
	
	/**
	 * Returns the maxChars property for default value when <code>JTextArea</code> be created.
	 * @return the default maxChars value.
	 */
	public static function getDefaultMaxChars():int{
		return defaultMaxChars;
	}	
	
	/**
	 * Sets the number of columns in this JTextArea, if it changed then call parent to do layout. 
	 * 
	 * @param columns the number of columns to use to calculate the preferred width;
	 * 			if columns is set to zero or min than zero, the preferred width will be matched just to view all of the text.
	 */
	public function setColumns(columns:int):void{
		if(columns < 0) columns = 0;
		if(this.columns != columns){
			this.columns = columns;
			if(isWordWrap()){
				//invalidateTextFieldAutoSizeToCountPrefferedSize();
			}
			revalidate();
		}
	}
	
	/**
	 * @see #setColumns
	 */
	public function getColumns():int{
		return columns;
	}
	
	/**
	 * Sets the number of rows in this JTextArea, if it changed then call parent to do layout. 
	 * 
	 * @param rows the number of rows to use to calculate the preferred height;
	 * 			if rows is set to zero or min than zero, the preferred height will be matched just to view all of the text.
	 */
	public function setRows(rows:int):void{
		if(rows < 0) rows = 0;
		if(this.rows != rows){
			this.rows = rows;
			if(isWordWrap()){
				//invalidateTextFieldAutoSizeToCountPrefferedSize();
			}
			revalidate();
		}
	}
	
	/**
	 * @see #setRows
	 */
	public function getRows():int{
		return rows;
	}
	
	override protected function isAutoSize():Boolean{
		return columns == 0 || rows == 0;
	}
	
	override protected function countPreferredSize():IntDimension{
		var size:IntDimension;
		if(columns > 0 && rows > 0){
			var width:int = getColumnWidth() * columns + getWidthMargin();
			var height:int = getRowHeight() * rows + getHeightMargin();
			size = new IntDimension(width, height);
		}else if(rows <=0 && columns <=0 ){
			size = getTextFieldAutoSizedSize();
		}else if(rows > 0){ // columns must <= 0
			var forceHeight:int = getRowHeight() * rows + getHeightMargin();
			size = getTextFieldAutoSizedSize(0, forceHeight);
		}else{ //must be columns > 0 and rows <= 0
			var forceWidth:int = getColumnWidth() * columns + getWidthMargin();
			size = getTextFieldAutoSizedSize(forceWidth, 0);
		}
		return getInsets().getOutsideSize(size);
	}	
	
	protected function fireStateChanged(programmatic:Boolean=true):void{
		dispatchEvent(new InteractiveEvent(InteractiveEvent.STATE_CHANGED, programmatic));
	}
	
	override protected function size():void{
		super.size();
		applyBoundsToText(getPaintBounds());
	}

	
	private function __onTextAreaTextChange(e:Event):void{
    	if(viewportSizeTesting){
    		return;
    	}
		//do not need call revalidate here in fact
		//because if the scroll changed with text change, the 
		//scroll event will be fired see below handler
	}
	
	private function __onTextAreaTextScroll(e:Event):void{
    	if(viewportSizeTesting){
    		return;
    	}
    	var t:TextField = getTextField();
    	if(focusScrolling){//avoid scroll change when make focus
    		var vp:IntPoint = getViewPosition();
    		t.scrollH = vp.x;
    		t.scrollV = vp.y + 1;
    		return;
    	}
		var newViewPos:IntPoint = new IntPoint(t.scrollH, t.scrollV-1);
		if(!getViewPosition().equals(newViewPos)){
			viewPos.setLocation(newViewPos);
			//notify scroll bar to syn
			fireStateChanged(true);
		}
		if(lastMaxScrollV != t.maxScrollV || lastMaxScrollH != t.maxScrollH){
			lastMaxScrollV = t.maxScrollV;
			lastMaxScrollH = t.maxScrollH;
			revalidate();
		}
	}
	
	private var focusScrolling:Boolean = false;
	override public function makeFocus():void{
		if(getFocusTransmit() == null){
			focusScrolling = true;
			super.makeFocus();
			focusScrolling = false;
		}
	}
	
	//------------------------------------------------------------
	//                    Viewportable Imp
	//------------------------------------------------------------
	
	/**
	 * Add a listener to listen the viewpoat state change event.
	 * <p>
	 * When the viewpoat's state changed, the state is all about:
	 * <ul>
	 * <li>viewPosition</li>
	 * <li>verticalUnitIncrement</li>
	 * <li>verticalBlockIncrement</li>
	 * <li>horizontalUnitIncrement</li>
	 * <li>horizontalBlockIncrement</li>
	 * </ul>
	 * @param listener the listener
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
	 * @see org.aswing.event.InteractiveEvent#STATE_CHANGED
	 */
	public function addStateListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
		addEventListener(InteractiveEvent.STATE_CHANGED, listener, false, priority);
	}	
	
	/**
	 * Removes a state listener.
	 * @param listener the listener to be removed.
	 * @see org.aswing.event.AWEvent#STATE_CHANGED
	 */	
	public function removeStateListener(listener:Function):void{
		removeEventListener(InteractiveEvent.STATE_CHANGED, listener);
	}
	
	/**
	 * Returns the unit value for the Vertical scrolling.
	 */
    public function getVerticalUnitIncrement():int{
    	if(verticalUnitIncrement == AUTO_INCREMENT){
    		return 1;
    	}else{
    		return verticalUnitIncrement;
    	}
    }
    
    /**
     * Return the block value for the Vertical scrolling.
     */
    public function getVerticalBlockIncrement():int{
    	if(verticalBlockIncrement == AUTO_INCREMENT){
    		return 10;
    	}else{
    		return verticalBlockIncrement;
    	}
    }
    
	/**
	 * Returns the unit value for the Horizontal scrolling.
	 */
    public function getHorizontalUnitIncrement():int{
    	if(horizontalUnitIncrement == AUTO_INCREMENT){
    		return getColumnWidth();
    	}else{
    		return horizontalUnitIncrement;
    	}
    }
    
    /**
     * Return the block value for the Horizontal scrolling.
     */
    public function getHorizontalBlockIncrement():int{
    	if(horizontalBlockIncrement == AUTO_INCREMENT){
    		return getColumnWidth()*10;
    	}else{
    		return horizontalBlockIncrement;
    	}
    }
    
	/**
	 * Sets the unit value for the Vertical scrolling.
	 */
    public function setVerticalUnitIncrement(increment:int):void{
    	if(verticalUnitIncrement != increment){
    		verticalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    /**
     * Sets the block value for the Vertical scrolling.
     */
    public function setVerticalBlockIncrement(increment:int):void{
    	if(verticalBlockIncrement != increment){
    		verticalBlockIncrement = increment;
			fireStateChanged();
    	}
    }
    
	/**
	 * Sets the unit value for the Horizontal scrolling.
	 */
    public function setHorizontalUnitIncrement(increment:int):void{
    	if(horizontalUnitIncrement != increment){
    		horizontalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    /**
     * Sets the block value for the Horizontal scrolling.
     */
    public function setHorizontalBlockIncrement(increment:int):void{
    	if(horizontalBlockIncrement != increment){
    		horizontalBlockIncrement = increment;
			fireStateChanged();
    	}
    }

	/**
	 * Scrolls to view bottom left content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */
	public function scrollToBottomLeft():void{
		setViewPosition(new IntPoint(0, int.MAX_VALUE));
	}
	/**
	 * Scrolls to view bottom right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToBottomRight():void{
		setViewPosition(new IntPoint(int.MAX_VALUE, int.MAX_VALUE));
	}
	/**
	 * Scrolls to view top left content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopLeft():void{
		setViewPosition(new IntPoint(0, 0));
	}
	/**
	 * Scrolls to view to right content. 
	 * This will make the scrollbars of <code>JScrollPane</code> scrolled automatically, 
	 * if it is located in a <code>JScrollPane</code>.
	 */	
	public function scrollToTopRight():void{
		setViewPosition(new IntPoint(int.MAX_VALUE, 0));
	}	    
	
	public function scrollRectToVisible(contentRect:IntRectangle, programmatic:Boolean=true):void{
		setViewPosition(new IntPoint(contentRect.x, contentRect.y), programmatic);
	}
	
	public function setViewPosition(p:IntPoint, programmatic:Boolean=true):void{
		if(!viewPos.equals(p)){
			restrictionViewPos(p);
			if(viewPos.equals(p)){
				return;
			}
			viewPos.setLocation(p);
			validateScroll();
			fireStateChanged(programmatic);
		}
	}
	
	public function setViewportTestSize(s:IntDimension):void{
    	viewportSizeTesting = true;
    	setSize(s);
    	validateScroll();
    	viewportSizeTesting = false;
	}
	
	public function getViewSize():IntDimension{
    	var t:TextField = getTextField();
    	var wRange:int, hRange:int;
    	if(isWordWrap()){
    		wRange = t.textWidth;
    		t.scrollH = 0;
    	}else{
	    	if(t.maxScrollH > 0){
    			wRange = t.textWidth + t.maxScrollH;
	    	}else{
    			wRange = t.textWidth;
    			t.scrollH = 0;
	    	}
    	}
		var extent:int = t.bottomScrollV - t.scrollV + 1;
		var maxValue:int = t.maxScrollV + extent;
		var minValue:int = 1;
    	hRange = maxValue - minValue;
    	return new IntDimension(wRange, hRange);
	}
	
	public function getExtentSize():IntDimension{
    	var t:TextField = getTextField();
		var extentVer:int = t.bottomScrollV - t.scrollV + 1;
		var extentHor:int = t.textWidth;
    	return new IntDimension(extentHor, extentVer);
	}
	
	public function getViewportPane():Component{
		return this;
	}
	
	public function getViewPosition():IntPoint{
		return viewPos.clone();
	}
	
	/**
	 * Scroll the text with viewpos
	 */
    protected function validateScroll():void{
		var xS:int = viewPos.x;
		var yS:int = viewPos.y + 1;
    	var t:TextField = getTextField();
		if(t.scrollH != xS){
			t.scrollH = xS;
		}
		if(t.scrollV != yS){
			t.scrollV = yS;
		}
		//t.background = false; //avoid TextField background lose effect bug
    }
	
	protected function restrictionViewPos(p:IntPoint):IntPoint{
		var maxPos:IntPoint = getViewMaxPos();
		p.x = Math.max(0, Math.min(maxPos.x, p.x));
		p.y = Math.max(0, Math.min(maxPos.y, p.y));
		return p;
	}
	
	private function getViewMaxPos():IntPoint{
		var showSize:IntDimension = getExtentSize();
		var viewSize:IntDimension = getViewSize();
		var p:IntPoint = new IntPoint(viewSize.width-showSize.width, viewSize.height-showSize.height);
		if(p.x < 0) p.x = 0;
		if(p.y < 0) p.y = 0;
		return p;
	}
}
}