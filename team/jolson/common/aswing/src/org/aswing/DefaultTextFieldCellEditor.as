/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractCellEditor;
import org.aswing.Component;
import org.aswing.JTextField;

/**
 * The default editor for table and tree cells, use a textfield.
 * <p>
 * @author iiley
 */
class org.aswing.DefaultTextFieldCellEditor extends AbstractCellEditor{
	
	private var textField:JTextField;
	
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
	private function getRestrict():String{
		return undefined;
	}
	
	/**
	 * Subclass override this method to implement specified value transform
	 */
	private function transforValueFromText(text:String){
		return text;
	}
	
 	public function getEditorComponent():Component{
 		return getTextField();
 	}
	
	public function getCellEditorValue() {		
		return transforValueFromText(getTextField().getText());
	}
	
   /**
    * Sets the value of this cell. 
    * @param value the new value of this cell
    */
	public function setCellEditorValue(value):Void{
		getTextField().setText(value+"");
		getTextField().selectAll();
	}
	
	public function toString():String{
		return "DefaultTextFieldCellEditor[]";
	}
}