/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASWingConstants;
import org.aswing.Component;
import org.aswing.plaf.SeparatorUI;
import org.aswing.UIManager;

/**
 * <code>JSeparator</code> provides a general purpose component for
 * implementing divider lines - most commonly used as a divider
 * between menu items that breaks them up into logical groupings.
 * Instead of using <code>JSeparator</code> directly,
 * you can use the <code>JMenu</code> or <code>JPopupMenu</code>
 * <code>addSeparator</code> method to create and add a separator.
 * <code>JSeparator</code>s may also be used elsewhere in a GUI
 * wherever a visual divider is useful.
 * 
 * @author iiley
 */
class org.aswing.JSeparator extends Component {
	
    /** 
     * Horizontal orientation.
     */
    public static var HORIZONTAL:Number = ASWingConstants.HORIZONTAL;
    /** 
     * Vertical orientation.
     */
    public static var VERTICAL:Number   = ASWingConstants.VERTICAL;
	
	private var orientation:Number;
	private var gap:Number;
	
	/**
	 * JSeparator(orientation:Number)<br>
	 * JSeparator() default orientation to HORIZONTAL;
	 * <p>
	 * @param orientation (optional) the orientation.
	 * @param gap (optional) the gap between separator and container's edges
	 */
	public function JSeparator(orientation:Number, gap:Number){
		this.orientation = (orientation == undefined ? HORIZONTAL : orientation);
		this.gap = (gap == undefined ? 0 : gap);
		setFocusable(false);
		updateUI();
	}
	
	public function getUI():SeparatorUI{
		return SeparatorUI(ui);
	}
	
	public function setUI(ui:SeparatorUI):Void{
		super.setUI(ui);
	}
	
	public function updateUI():Void{
		setUI(SeparatorUI(UIManager.getUI(this)));
	}
	
	public function getUIClassID():String{
		return "SeparatorUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicSeparatorUI;
    }
	
	public function getOrientation():Number{
		return orientation;
	}
	
	public function setOrientation(orientation:Number):Void{
		if (this.orientation != orientation){
			this.orientation = orientation;
			revalidate();
			repaint();
		}
	}
	
	public function getGap():Number{
		return gap;
	}
	
	public function setGap(gap:Number):Void{
		if (this.gap != gap){
			this.gap = gap;
			revalidate();
			repaint();
		}
	}
	
}
