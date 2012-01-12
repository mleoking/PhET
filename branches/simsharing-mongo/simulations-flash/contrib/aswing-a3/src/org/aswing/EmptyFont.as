/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import flash.text.*;

/**
 * EmptyFont is a font that will not change text field's format.
 */
public class EmptyFont extends ASFont{
	
	public function EmptyFont(){
		super();
	}
	
	/**
	 * Do nothing here.
	 * @param textField the text filed to be applied font.
	 * @param beginIndex The zero-based index position specifying the first character of the desired range of text. 
	 * @param endIndex The zero-based index position specifying the last character of the desired range of text. 
	 */
	override public function apply(textField:TextField, beginIndex:int=-1, endIndex:int=-1):void{
	}
	
	/**
	 * Returns <code>new TextFormat()</code>.
	 * @return <code>new TextFormat()</code>.
	 */
	override public function getTextFormat():TextFormat{
		return new TextFormat();
	}	
}
}