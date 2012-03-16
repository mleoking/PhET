/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.border{

/**
 * @private
 */
public class TextFieldBorder extends TextComponentBorder{
	
	public function TextFieldBorder(){
		super();
	}
	
	//override this to the sub component's prefix
	override protected function getPropertyPrefix():String {
		return "TextField.";
	}
}
}