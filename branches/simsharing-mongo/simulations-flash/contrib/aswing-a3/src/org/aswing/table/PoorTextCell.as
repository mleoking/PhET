/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.table{

import org.aswing.*;
import org.aswing.geom.*;
import org.aswing.plaf.ComponentUI;
import org.aswing.graphics.*;
import flash.text.*;

/**
 * A poor table cell to render text faster.
 * @author iiley
 */
public class PoorTextCell extends Component implements TableCell{
	
	protected var textField:TextField;
	protected var text:String;
	protected var cellValue:*;
	
	public function PoorTextCell() {
		super();
		setOpaque(true);
		textField = new TextField();
		textField.autoSize = TextFieldAutoSize.LEFT;
		textField.selectable = false;
		textField.mouseEnabled = false;
		setFontValidated(false);
		addChild(textField);
	}
	
	override protected function paint(b:IntRectangle):void{
		var t:String = text == null ? "" : text;
		if(textField.text !== t){
			textField.text = t;
		}
		if(!isFontValidated()){
			AsWingUtils.applyTextFont(textField, getFont());
			setFontValidated(true);
		}
		AsWingUtils.applyTextColor(textField, getForeground());
		textField.x = b.x;
		textField.y = b.y + (b.height-textField.height)/2;
		if(isOpaque()){
			graphics.clear();
			var g:Graphics2D = new Graphics2D(graphics);
			g.fillRectangle(new SolidBrush(getBackground()), b.x, b.y, b.width, b.height);
		}
	}
		
	override public function setComBounds(b:IntRectangle):void{
		readyToPaint = true;
		if(!b.equals(bounds)){
			if(b.width != bounds.width || b.height != bounds.height){
				repaint();
			}
			bounds.setRect(b);
			locate();
			valid = false;
		}
	}
	
	/**
	 * Simpler this method to speed up performance
	 */
	override public function invalidate():void {
		valid = false;
	}
	/**
	 * Simpler this method to speed up performance
	 */
	override public function revalidate():void {
		valid = false;
	}
	
	public function setText(text:String):void{
		if(text != this.text){
			this.text = text;
			repaint();
		}
	}
	
	public function getText():String{
		return text;
	}
	
	//------------------------------------------------------------------------------------------------

	public function setTableCellStatus(table : JTable, isSelected : Boolean, row : int, column : int) : void {
		if(isSelected){
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		}else{
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		setFont(table.getFont());
	}

	public function setCellValue(value:*) : void {
		cellValue = value;
		setText(value + "");
	}

	public function getCellValue():* {
		return cellValue;
	}

	public function getCellComponent() : Component {
		return this;
	}

	override public function toString():String{
		return "PoorTextCell[component:" + super.toString() + "]\n";
	}
}
}