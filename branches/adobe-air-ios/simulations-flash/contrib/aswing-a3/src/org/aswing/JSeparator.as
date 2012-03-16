/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{
	
import org.aswing.plaf.basic.BasicSeparatorUI;
	
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
public class JSeparator extends Component implements Orientable{
	
    /** 
     * Horizontal orientation.
     */
    public static const HORIZONTAL:int = AsWingConstants.HORIZONTAL;
    /** 
     * Vertical orientation.
     */
    public static const VERTICAL:int   = AsWingConstants.VERTICAL;
	
	private var orientation:int;
	
	/**
	 * JSeparator(orientation:Number)<br>
	 * JSeparator() default orientation to HORIZONTAL;
	 * <p>
	 * @param orientation (optional) the orientation.
	 */
	public function JSeparator(orientation:int=AsWingConstants.HORIZONTAL){
		super();
		setName("JSeparator");
		this.orientation = orientation;
		setFocusable(false);
		updateUI();
	}

	override public function updateUI():void{
		setUI(UIManager.getUI(this));
	}
	
	override public function getUIClassID():String{
		return "SeparatorUI";
	}
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicSeparatorUI;
    }
	
	public function getOrientation():int{
		return orientation;
	}
	
	public function setOrientation(orientation:int):void{
		if (this.orientation != orientation){
			this.orientation = orientation;
			revalidate();
			repaint();
		}
	}
}

}