import org.aswing.ASColor;
import org.aswing.DefaultListCell;

import test.CircleIcon;
import test.ColorIcon;

/**
 * @author iiley
 */
class test.list.IconListCell extends DefaultListCell{
		
	public function IconListCell(){
		super();
	}
	
	private static var iconPaths:Array = ["pic1","pic2","pic3"];
	
	public function setCellValue(value) : Void {
		if(this.value != value){
			this.value = value;
			getJLabel().setText(value.toString());
			if(Math.random() > 0.5){
				getJLabel().setIcon(new ColorIcon(null, new ASColor(Math.random()*0xFFFFFF), 10+Math.random()*30, 10+Math.random()*30));
			}else{
				getJLabel().setIcon(new CircleIcon(new ASColor(Math.random()*0xFFFFFF), 10+Math.random()*30, 10+Math.random()*30));
			}
		}
	}
}