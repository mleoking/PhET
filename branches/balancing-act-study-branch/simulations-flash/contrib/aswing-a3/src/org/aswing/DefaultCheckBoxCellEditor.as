/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing { 

import org.aswing.AbstractCellEditor;
import org.aswing.Component;
import org.aswing.JCheckBox;

/**
 * @author iiley
 */
public class DefaultCheckBoxCellEditor extends AbstractCellEditor{
	
	protected var checkBox:JCheckBox;
	
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
	
 	override public function getEditorComponent():Component{
 		return getCheckBox();
 	}
	
	override public function getCellEditorValue():* {		
		return getCheckBox().isSelected();
	}
	
    /**
     * Sets the value of this cell. 
     * @param value the new value of this cell
     */
	override protected function setCellEditorValue(value:*):void{
		var selected:Boolean = false;
		if(value == true){
			selected = true;
		}
		if(value is String){
			var va:String = value as String;
			if(va.toLowerCase() == "true"){
				selected = true;
			}
		}
		getCheckBox().setSelected(selected);
	}
	
	public function toString():String{
		return "DefaultCheckBoxCellEditor[]";
	}
}
}