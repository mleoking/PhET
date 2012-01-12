/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{
	
import org.aswing.error.ImpMissError;
	
/**
 * Abstract list cell.
 * @author iiley
 */
public class AbstractListCell implements ListCell{
	
	protected var value:*;
	
	public function AbstractListCell(){
	}
	
	public function setListCellStatus(list : JList, isSelected : Boolean, index:int) : void {
		var com:Component = getCellComponent();
		if(isSelected){
			com.setBackground(list.getSelectionBackground());
			com.setForeground(list.getSelectionForeground());
		}else{
			com.setBackground(list.getBackground());
			com.setForeground(list.getForeground());
		}
		com.setFont(list.getFont());
	}

	public function setCellValue(value:*) : void {
		this.value = value;
	}

	public function getCellValue():* {
		return value;
	}
	
	/**
	 * Subclass should override this method
	 */
	public function getCellComponent() : Component {
		throw new ImpMissError();
		return null;
	}
}
}