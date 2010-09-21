/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.DefaultTextFieldCellEditor;

/**
 * @author iiley
 */
class org.aswing.DefaultNumberTextFieldCellEditor extends DefaultTextFieldCellEditor {
	
	public function DefaultNumberTextFieldCellEditor() {
		super();
	}
	
	/**
	 * Subclass override this method to implement specified input restrict
	 */
	private function getRestrict():String{
		return "-0123456789.E";
	}
	
	/**
	 * Subclass override this method to implement specified value transform
	 */
	private function transforValueFromText(text:String){
		return parseFloat(text);
	}	
}