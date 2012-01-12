/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.ext{

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.JLabel;

/**
 * The default grid list cell render value.toString() as texts.
 * 
 */
public class DefaultGridCell extends JLabel implements GridListCell{
	
	protected var value:*;
	
	public function DefaultGridCell(){
		super();
		setOpaque(true);
		setBackground(ASColor.WHITE);
	}
	
	public function setCellValue(value:*):void{
		this.value = value;
		setText(value+"");
	}
	
	public function getCellValue():*{
		return value;
	}
				
	public function getCellComponent():Component{
		return this;
	}
	
	public function setGridListCellStatus(gridList:GridList, selected:Boolean, index:int):void{
		var c:ASColor = index % 2 == 0 ? ASColor.WHITE : ASColor.GRAY;
		if(selected){
			c = ASColor.BLUE;
		}
		setBackground(c);
	}
	
}
}