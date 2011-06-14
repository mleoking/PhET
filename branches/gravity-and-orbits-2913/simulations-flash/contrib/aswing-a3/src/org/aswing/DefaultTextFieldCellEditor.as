/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing { 

import org.aswing.AbstractCellEditor;
import org.aswing.Component;
import org.aswing.JTextField;

/**
 * The default editor for table and tree cells, use a textfield.
 * <p>
 * @author iiley
 */
public class DefaultTextFieldCellEditor extends AbstractCellEditor{
	
	protected var textField:JTextField;
	
	public function DefaultTextFieldCellEditor(){
		super();
		setClickCountToStart(2);
	}
	
	public function getTextField():JTextField{
		if(textField == null){
			textField = new JTextField();
			//textField.setBorder(null);
			textField.setRestrict(getRestrict());
		}
		return textField;
	}
	
	/**
	 * Subclass override this method to implement specified input restrict
	 */
	protected function getRestrict():String{
		return null;
	}
	
	/**
	 * Subclass override this method to implement specified value transform
	 */
	protected function transforValueFromText(text:String):*{
		return text;
	}
	
 	override public function getEditorComponent():Component{
 		return getTextField();
 	}
	
	override public function getCellEditorValue():* {		
		return transforValueFromText(getTextField().getText());
	}
	
   /**
    * Sets the value of this cell. 
    * @param value the new value of this cell
    */
	override protected function setCellEditorValue(value:*):void{
		getTextField().setText(value+"");
		getTextField().selectAll();
	}
	
	public function toString():String{
		return "DefaultTextFieldCellEditor[]";
	}
}
}