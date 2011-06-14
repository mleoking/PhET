/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import flash.events.MouseEvent;

/**
 * The list cell for combobox drop down list.
 * @author iiley
 */
public class DefaultComboBoxListCell extends DefaultListCell{

	protected var rolloverBackground:ASColor;
	protected var rolloverForeground:ASColor;
	protected var realBackground:ASColor;
	protected var realForeground:ASColor;
		
	public function DefaultComboBoxListCell(){
		super();
	}
	
	override protected function initJLabel(jlabel:JLabel):void{
		super.initJLabel(jlabel);
		jlabel.addEventListener(MouseEvent.ROLL_OVER, __labelRollover, false, 0, true);
		jlabel.addEventListener(MouseEvent.ROLL_OUT, __labelRollout, false, 0, true);
	}
	
	override public function setListCellStatus(list:JList, isSelected:Boolean, index:int):void{
		var com:Component = getCellComponent();
		if(isSelected){
			com.setBackground((realBackground = list.getSelectionBackground()));
			com.setForeground((realForeground = list.getSelectionForeground()));
		}else{
			com.setBackground((realBackground = list.getBackground()));
			com.setForeground((realForeground = list.getForeground()));
		}
		com.setFont(list.getFont());
		rolloverBackground = list.getSelectionBackground().changeAlpha(0.8);
		rolloverForeground = list.getSelectionForeground();
	}
	
	private function __labelRollover(e:MouseEvent):void{
		if(rolloverBackground){
			getJLabel().setBackground(rolloverBackground);
			getJLabel().setForeground(rolloverForeground);
		}
	}
	
	private function __labelRollout(e:MouseEvent):void{
		if(realBackground){
			getJLabel().setBackground(realBackground);
			getJLabel().setForeground(realForeground);
		}
	}	
}
}