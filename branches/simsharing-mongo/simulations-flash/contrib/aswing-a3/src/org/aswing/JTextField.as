/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.geom.*;
import org.aswing.event.FocusKeyEvent;
import flash.ui.Keyboard;
import org.aswing.event.AWEvent;
import org.aswing.plaf.basic.BasicTextFieldUI;

/**
 * Dispatched when the user input ENTER in the textfield.
 * @eventType org.aswing.event.AWEvent.ACT
 * @see org.aswing.JTextField#addActionListener()
 */
[Event(name="act", type="org.aswing.event.AWEvent")]

/**
 * JTextField is a component that allows the editing of a single line of text. 
 * @author Tomato, iiley
 */
public class JTextField extends JTextComponent{
	
	private static var defaultMaxChars:int = 0;
	
	private var columns:int;
	
	public function JTextField(text:String="", columns:int=0){
		super();
		setName("JTextField");
		getTextField().multiline = false;
		getTextField().text = text;
		setMaxChars(defaultMaxChars);
		this.columns = columns;
		addEventListener(FocusKeyEvent.FOCUS_KEY_DOWN, __onFocusKeyDown);
		updateUI();
	}
	
	override public function updateUI():void{
		setUI(UIManager.getUI(this));
	}
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicTextFieldUI;
    }
	
	override public function getUIClassID():String{
		return "TextFieldUI";
	}
	
	/**
	 * Sets the maxChars property for default value when <code>JTextFeild</code> be created.
	 * By default it is 0, you can change it by this method.
	 * @param n the default maxChars to set
	 */
	public static function setDefaultMaxChars(n:int):void{
		defaultMaxChars = n;
	}
	
	/**
	 * Returns the maxChars property for default value when <code>JTextFeild</code> be created.
	 * @return the default maxChars value.
	 */
	public static function getDefaultMaxChars():int{
		return defaultMaxChars;
	}	
	
	/**
	 * Sets the number of columns in this JTextField, if it changed then call parent to do layout. 
	 * @param columns the number of columns to use to calculate the preferred width;
	 * if columns is set to zero or min than zero, the preferred width will be matched just to view all of the text.
	 * default value is zero if missed this param.
	 */
	public function setColumns(columns:int=0):void{
		if(columns < 0) columns = 0;
		if(this.columns != columns){
			this.columns = columns;
			revalidate();
		}
	}
	
	/**
	 * @see #setColumns
	 */
	public function getColumns():Number{
		return columns;
	}	
	
    /**
     * Adds a action listener to this text field. JTextField fire a action event when 
     * user press Enter Key when input to text field.
	 * @param listener the listener
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
	 * @see org.aswing.event.AWEvent#ACT
     */
    public function addActionListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
    	addEventListener(AWEvent.ACT, listener, false, priority, useWeakReference);
    }
    
	/**
	 * Removes a action listener.
	 * @param listener the listener to be removed.
	 * @see org.aswing.event.AWEvent#ACT
	 */
	public function removeActionListener(listener:Function):void{
		removeEventListener(AWEvent.ACT, listener);
	}   	
	
	override protected function isAutoSize():Boolean{
		return columns == 0;
	}
	
	/**
	 * JTextComponent need count preferred size itself.
	 */
	override protected function countPreferredSize():IntDimension{
		if(columns > 0){
			var columnWidth:int = getColumnWidth();
			var width:int = columnWidth * columns + getWidthMargin();
			var height:int = getRowHeight() + getHeightMargin();
			var size:IntDimension = new IntDimension(width, height);
			return getInsets().getOutsideSize(size);
		}else{
			return getInsets().getOutsideSize(getTextFieldAutoSizedSize());
		}
	}
	
	//-------------------------------------------------------------------------
	
	private function __onFocusKeyDown(e:FocusKeyEvent):void{
		if(e.keyCode == Keyboard.ENTER){
			dispatchEvent(new AWEvent(AWEvent.ACT));
		}
	}
}
}