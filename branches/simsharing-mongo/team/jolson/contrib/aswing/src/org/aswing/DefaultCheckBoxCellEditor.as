/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractCellEditor;
import org.aswing.Component;
import org.aswing.JCheckBox;

/**
 * @author iiley
 */
class org.aswing.DefaultCheckBoxCellEditor extends AbstractCellEditor{
	
	private var checkBox:JCheckBox;
	
	public function DefaultCheckBoxCellEditor(){
		super();
		setClickCountToStart(1);
	}
	
	public function getCheckBox():JCheckBox{
		if(checkBox == null){
			checkBox = new JCheckBox();
		}
		return checkBox;
	}
	
 	public function getEditorComponent():Component{
 		return getCheckBox();
 	}
	
	public function getCellEditorValue() {		
		return getCheckBox().isSelected();
	}
	
    /**
     * Sets the value of this cell. 
     * @param value the new value of this cell
     */
	public function setCellEditorValue(value):Void{
		var selected:Boolean = false;
		if(value == true){
			selected = true;
		}
		if(value == "true" || value == "True" || value == "TRUE"){
			selected = true;
		}
		getCheckBox().setSelected(selected);
	}
	
	public function toString():String{
		return "DefaultCheckBoxCellEditor[]";
	}
}