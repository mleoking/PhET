/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractCellEditor;
import org.aswing.Component;
import org.aswing.FocusManager;
import org.aswing.JComboBox;
import org.aswing.util.Delegate;

/**
 * The default editor for table and tree cells, use a combobox.
 * <p>
 * @author iiley
 */
class org.aswing.DefaultComboBoxCellEditor extends AbstractCellEditor{
	
	private var comboBox:JComboBox;
	
	public function DefaultComboBoxCellEditor(){
		super();
		setClickCountToStart(1);
	}
	
	public function getComboBox():JComboBox{
		if(comboBox == null){
			comboBox = new JComboBox();
			comboBox.setFocusable(false);
			var childrenListener:Object = new Object();
			childrenListener[Component.ON_FOCUS_LOST] = Delegate.create(this, __comboBoxFocusLost);
			for(var i:Number=0; i<comboBox.getComponentCount(); i++){
				comboBox.getComponent(i).addEventListener(childrenListener);
			}
		}
		return comboBox;
	}
	
	private function __comboBoxFocusLost():Void{
		var focusOwner:Component = FocusManager.getCurrentManager().getFocusOwner();
		//when focus leave combobox or its children, cancel editing
		if(!(focusOwner == getComboBox() || focusOwner.getParent() == getComboBox()
			|| getComboBox().isPopupVisible())){
			cancelCellEditing();
		}
	}
	
 	public function getEditorComponent():Component{
 		return getComboBox();
 	}
	
	public function getCellEditorValue() {
		return getComboBox().getSelectedItem();
	}
	
    /**
     * Sets the value of this cell. 
     * @param value the new value of this cell
     */
	public function setCellEditorValue(value):Void{
		getComboBox().setSelectedItem(value);
	}
	
	public function toString():String{
		return "DefaultComboBoxCellEditor[]";
	}
}