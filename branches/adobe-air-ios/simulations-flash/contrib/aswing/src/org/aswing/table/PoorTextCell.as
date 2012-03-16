/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASWingUtils;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.overflow.JTable;
import org.aswing.plaf.ComponentUI;
import org.aswing.table.TableCell;

/**
 * A poor table cell to render text faster.
 * @author iiley
 */
class org.aswing.table.PoorTextCell extends Component implements TableCell {
	
	private var textField:TextField;
	private var text:String;
	private var cellValue;
	
	public function PoorTextCell() {
		super();
		setOpaque(true);
	}
	
	private function create():Void{
		super.create();
		textField = creater.createTF(target_mc, "poorText");
		setFontValidated(false);
	}
	
	private function paint(b:Rectangle):Void{
		var t:String = text == null ? "" : text;
		
		if(textField.text != t){
			textField.text = t;
		}
		if(!isFontValidated()){
			ASWingUtils.applyTextFont(textField, font);
			setFontValidated(true);
		}
    	ASWingUtils.applyTextColor(textField, getForeground());
		textField._x = b.x;
		textField._y = b.y;
		if(isOpaque()){
			target_mc.clear();
			ComponentUI.fillRectBackGround(this, getGraphics(), b);
		}
	}
	public function setBounds(b:Rectangle):Void{
		if(!b.equals(bounds)){
			if(b.width != bounds.width || b.height != bounds.height){
				repaint();
			}
			bounds.setRect(b);
	    	valid = false;
		}
	}
	/**
	 * Simpler this method to speed up performance
	 */
	public function invalidate():Void {
    	valid = false;
    }
    /**
	 * Simpler this method to speed up performance
	 */
    public function revalidate():Void {
    	valid = false;
    }
	
	public function setText(text:String):Void{
		if(text != this.text){
			this.text = text;
			repaint();
		}
	}
	
	public function getText():String{
		return text;
	}
	
	//------------------------------------------------------------------------------------------------

	public function setTableCellStatus(table : JTable, isSelected : Boolean, row : Number, column : Number) : Void {
		if(isSelected){
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		}else{
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		setFont(table.getFont());
	}

	public function setCellValue(value) : Void {
		cellValue = value;
		setText(value.toString());
	}

	public function getCellValue() {
		return cellValue;
	}

	public function getCellComponent() : Component {
		return this;
	}

	public function toString():String{
		return "PoorTextCell[component:" + super.toString() + "]\n";
	}
}