/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.geom.Point;
import org.aswing.overflow.JSharedToolTip;
import org.aswing.tree.list.DefaultListTreeCell;

/**
 * @author iiley
 */
class org.aswing.tree.list.DefaultListTreeCellWithTip extends DefaultListTreeCell {
	
	private static var sharedToolTip:JSharedToolTip;
	
	public function DefaultListTreeCellWithTip() {
		super();
		if(sharedToolTip == null){
			sharedToolTip = new JSharedToolTip();
			sharedToolTip.setOffsetsRelatedToMouse(false);
			sharedToolTip.setOffsets(new Point(0, 0));
		}
		addEventListener(ON_RESIZED, __resized, this);
	}
	
	public function setCellValue(value) : Void {
		super.setCellValue(value);
		__resized();
	}
	
	private function __resized():Void{
		if(getWidth() < getPreferredWidth()){
			setToolTipText(getText());
			JSharedToolTip.getSharedInstance().unregisterComponent(this);
			sharedToolTip.registerComponent(this);
		}else{
			setToolTipText(null);
			sharedToolTip.unregisterComponent(this);
		}
	}
}