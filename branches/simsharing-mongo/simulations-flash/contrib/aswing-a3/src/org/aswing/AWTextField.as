/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{
	
import flash.text.TextField;
import flash.events.Event;
import org.aswing.event.InteractiveEvent;

/**
 * Dispatched when the text changed, programmatic change or user change.
 * 
 * @eventType org.aswing.event.InteractiveEvent.TEXT_CHANGED
 */
[Event(name="textChanged", type="org.aswing.event.InteractiveEvent")]

/**
 * Dispatched when the scroll changed, programmatic change or user change, for 
 * example text scrolled by user use mouse wheel or by set the scrollH/scrollV 
 * properties of <code>TextField</code>.
 * 
 * @eventType org.aswing.event.InteractiveEvent.SCROLL_CHANGED
 */
[Event(name="scrollChanged", type="org.aswing.event.InteractiveEvent")]

/**
 * TextField with more events support for AsWing text component use.
 */
public class AWTextField extends TextField{
	
	public function AWTextField(){
		super();
		addEventListener(Event.SCROLL, __onAWTextFieldScroll);
		addEventListener(Event.CHANGE, __onAWTextFieldChange);
	}
	
	private function __onAWTextFieldScroll(e:Event):void{
		fireScrollChangeEvent(false);
	}
	
	private function __onAWTextFieldChange(e:Event):void{
		fireTextChangeEvent(false);
	}
	
	protected function fireTextChangeEvent(programmatic:Boolean=true):void{
		dispatchEvent(new InteractiveEvent(InteractiveEvent.TEXT_CHANGED, programmatic));		
	}
	
	protected function fireScrollChangeEvent(programmatic:Boolean=true):void{
		dispatchEvent(new InteractiveEvent(InteractiveEvent.SCROLL_CHANGED, programmatic));		
	}
	
	/**
	 * Sets the <code>htmlText</code> and fire <code>InteractiveEvent.TEXT_CHANGED</code> event.
	 */
	override public function set htmlText(value:String):void{
		super.htmlText = value;
		fireTextChangeEvent();
	}
	
	/**
	 * Sets the <code>text</code> and fire <code>InteractiveEvent.TEXT_CHANGED</code> event.
	 */
	override public function set text(value:String):void{
		super.text = value;
		fireTextChangeEvent();
	}
	
	/**
	 * Appends new text and fire <code>InteractiveEvent.TEXT_CHANGED</code> event.
	 */
	override public function appendText(newText:String):void{
		super.appendText(newText);
		fireTextChangeEvent();
	}
	
	/**
	 * Replace selected text and fire <code>InteractiveEvent.TEXT_CHANGED</code> event.
	 */	
	override public function replaceSelectedText(value:String):void{
		super.replaceSelectedText(value);
		fireTextChangeEvent();
	}
	
	/**
	 * Replace text and fire <code>InteractiveEvent.TEXT_CHANGED</code> event.
	 */	
	override public function replaceText(beginIndex:int, endIndex:int, newText:String):void{
		super.replaceText(beginIndex, endIndex, newText);
		fireTextChangeEvent();
	}
	
	override public function set scrollH(value:int):void{
		if(value != scrollH){
			super.scrollH = value;
			fireScrollChangeEvent();
		}
	}
	
	override public function set scrollV(value:int):void{
		if(value != scrollV){
			super.scrollV = value;
			fireScrollChangeEvent();		
		}
	}
}

}