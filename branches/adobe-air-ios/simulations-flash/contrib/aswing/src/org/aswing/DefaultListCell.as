/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.AbstractListCell;
import org.aswing.Component;
import org.aswing.geom.Point;
import org.aswing.JLabel;
import org.aswing.overflow.JSharedToolTip;

/**
 * @author iiley
 */
class org.aswing.DefaultListCell extends AbstractListCell {
	
	private var jlabel:JLabel;
			
	private static var sharedToolTip:JSharedToolTip;
	
	public function DefaultListCell(){
		super();
		if(sharedToolTip == null){
			sharedToolTip = new JSharedToolTip();
			sharedToolTip.setOffsetsRelatedToMouse(false);
			sharedToolTip.setOffsets(new Point(0, 0));
		}
	}
	
	public function setCellValue(value) : Void {
		this.value = value;
		getJLabel().setText(value.toString());
		__resized();
	}
	
	public function getCellComponent() : Component {
		return getJLabel();
	}

	private function getJLabel():JLabel{
		if(jlabel == null){
			jlabel = new JLabel();
			jlabel.setHorizontalAlignment(JLabel.LEFT);
			jlabel.setOpaque(true);
			jlabel.setFocusable(false);
			jlabel.addEventListener(JLabel.ON_RESIZED, __resized, this);
		}
		return jlabel;
	}
	
	private function __resized():Void{
		if(getJLabel().getWidth() < getJLabel().getPreferredWidth()){
			getJLabel().setToolTipText(value.toString());
			JSharedToolTip.getSharedInstance().unregisterComponent(getJLabel());
			sharedToolTip.registerComponent(getJLabel());
		}else{
			getJLabel().setToolTipText(null);
			sharedToolTip.unregisterComponent(getJLabel());
		}
	}

}
