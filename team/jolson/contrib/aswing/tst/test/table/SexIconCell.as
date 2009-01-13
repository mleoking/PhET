/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.Icon;
import org.aswing.table.DefaultTextCell;

import test.CircleIcon;
import test.ColorIcon;

/**
 * @author iiley
 */
class test.table.SexIconCell extends DefaultTextCell{
	
	private static var MALE_ICON:Icon;
	private static var FEMALE_ICON:Icon;
	
	public function SexIconCell(){
		super();
		if(MALE_ICON == undefined){
			FEMALE_ICON = new CircleIcon(ASColor.RED, 18, 18);
			MALE_ICON = new ColorIcon(null, ASColor.BLUE, 18, 18);
		}
	}
	
	public function setCellValue(value : Object) : Void {
		this.value = value;
		setText(value.toString());
		if(value){
			setIcon(MALE_ICON);
		}else{
			setIcon(FEMALE_ICON);
		}
	}
}