/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.JLabel;
import org.aswing.overflow.JTable;
import org.aswing.table.TableCell;

/**
 * Default table cell to render text
 * @author iiley
 */
class org.aswing.table.DefaultTextCell extends JLabel implements TableCell{
	private var value;
	
	public function DefaultTextCell(){
		super();
		setHorizontalAlignment(LEFT);
		setOpaque(true);
	}
	
	/**
	 * Simpler this method to speed up performance
	 */
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
    
	//**********************************************************
	//                  Implementing TableCell
	//**********************************************************
	public function setCellValue(value) : Void {
		this.value = value;
		setText(value.toString());
	}
	
	public function getCellValue(){
		return value;
	}
	
	public function setTableCellStatus(table:JTable, isSelected:Boolean, row:Number, column:Number):Void{
		if(isSelected){
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		}else{
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		setFont(table.getFont());
	}
	
	public function getCellComponent() : Component {
		return this;
	}
	
	public function toString():String{
		return "TextCell[label:" + super.toString() + "]\n";
	}
}