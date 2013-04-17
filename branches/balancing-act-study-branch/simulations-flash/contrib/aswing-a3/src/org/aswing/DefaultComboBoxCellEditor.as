/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing {

import org.aswing.AbstractCellEditor;
import org.aswing.Component;
import org.aswing.FocusManager;
import org.aswing.JComboBox;
import org.aswing.event.AWEvent;

/**
 * The default editor for table and tree cells, use a combobox.
 * <p>
 * @author iiley
 */
public class DefaultComboBoxCellEditor extends AbstractCellEditor{
	
	protected var comboBox:JComboBox;
	
	public function DefaultComboBoxCellEditor(){
		super();
		setClickCountToStart(1);
	}
	
	public function getComboBox():JComboBox{
		if(comboBox == null){
			comboBox = new JComboBox();
		}
		return comboBox;
	}
	
 	override public function getEditorComponent():Component{
 		return getComboBox();
 	}
	
	override public function getCellEditorValue():* {
		return getComboBox().getSelectedItem();
	}
	
    /**
     * Sets the value of this cell. 
     * @param value the new value of this cell
     */
	override protected function setCellEditorValue(value:*):void{
		getComboBox().setSelectedItem(value);
	}
	
	public function toString():String{
		return "DefaultComboBoxCellEditor[]";
	}
}
}