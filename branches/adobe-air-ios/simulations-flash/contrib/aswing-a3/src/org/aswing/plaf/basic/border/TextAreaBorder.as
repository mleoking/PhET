/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.border{

/**
 * @private
 */
public class TextAreaBorder extends TextComponentBorder{
	
	public function TextAreaBorder(){
		super();
	}
	
	//override this to the sub component's prefix
	override protected function getPropertyPrefix():String {
		return "TextArea.";
	}
	
}
}