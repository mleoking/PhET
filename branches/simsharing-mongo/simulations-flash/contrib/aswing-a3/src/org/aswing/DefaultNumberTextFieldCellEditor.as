/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing { 

import org.aswing.DefaultTextFieldCellEditor;

/**
 * @author iiley
 */
public class DefaultNumberTextFieldCellEditor extends DefaultTextFieldCellEditor {
	
	public function DefaultNumberTextFieldCellEditor() {
		super();
	}
	
	/**
	 * Subclass override this method to implement specified input restrict
	 */
	override protected function getRestrict():String{
		return "-0123456789.E";
	}
	
	/**
	 * Subclass override this method to implement specified value transform
	 */
	override protected function transforValueFromText(text:String):*{
		return parseFloat(text);
	}	
}
}