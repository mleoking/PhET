/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.overflow.JList;
import org.aswing.ListCell;

/**
 * @author iiley
 */
class org.aswing.AbstractListCell implements ListCell{
	
	private var value;
	
	private function AbstractListCell(){
	}
	
	public function setListCellStatus(list : JList, isSelected : Boolean, index:Number) : Void {
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

	public function setCellValue(value) : Void {
		this.value = value;
	}

	public function getCellValue() {
		return value;
	}
	
	/**
	 * Subclass should override this method
	 */
	public function getCellComponent() : Component {
		trace("Subclass should override this method");
		throw new Error("Subclass should override this method");
		return null;
	}
}