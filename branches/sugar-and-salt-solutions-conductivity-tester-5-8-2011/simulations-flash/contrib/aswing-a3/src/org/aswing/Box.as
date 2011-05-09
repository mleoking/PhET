/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing { 
import org.aswing.BoxLayout;
import org.aswing.Component;
import org.aswing.JPanel;
import org.aswing.JSpacer;

/**
 * A <code>JPanel</code> with <code>BoxLayout</code>.
 * 
 * @author iiley
 */
public class Box extends JPanel{
	/**
	 * Box(axis:int, gap:int)<br>
	 * Box(axis:int) default gap to 0.
	 * Creates a panel with a BoxLayout.
	 * @param axis (optional)the axis of layout, default is X_AXIS
	 *  {@link org.aswing.BoxLayout#X_AXIS} or {@link org.aswing.BoxLayout#Y_AXIS}
     * @param gap (optional)the gap between each component, default 0
	 * @see org.aswing.SoftBoxLayout
	 */
	public function Box(axis:int=0, gap:int=0){
		super();
		setName("Box");
		setLayout(new BoxLayout(axis, gap));
	}
	
	/**
	 * Sets new axis for the default BoxLayout.
	 * @param axis the new axis
	 */
	public function setAxis(axis:int):void {
		BoxLayout(getLayout()).setAxis(axis);
	}

	/**
	 * Gets current axis of the default BoxLayout.
	 * @return axis 
	 */
	public function getAxis():int {
		return BoxLayout(getLayout()).getAxis();
	}

	/**
	 * Sets new gap for the default BoxLayout.
	 * @param gap the new gap
	 */
	public function setGap(gap:int):void {
		BoxLayout(getLayout()).setGap(gap);
	}

	/**
	 * Gets current gap of the default BoxLayout.
	 * @return gap 
	 */
	public function getGap():int {
		return BoxLayout(getLayout()).getGap();
	}
	
	/**
	 * Creates and return a Horizontal Box.
     * @param gap (optional)the gap between each component, default 0
     * @return a horizontal Box.
	 */	
	public static function createHorizontalBox(gap:int=0):Box{
		return new Box(BoxLayout.X_AXIS, gap);
	}
	
	/**
	 * Creates and return a Vertical Box.
     * @param gap (optional)the gap between each component, default 0
     * @return a vertical Box.
	 */
	public static function createVerticalBox(gap:int=0):Box{
		return new Box(BoxLayout.Y_AXIS, gap);
	}
	

	/**
	 * Creates a glue that displays its components from left to right.
	 * @see org.aswing.JSpacer#createHorizontalGlue
	 */
	public static function createHorizontalGlue():Component{
		return JSpacer.createHorizontalSpacer(0);
	}
	
	/**
	 * Creates a glue that displays its components from top to bottom.
	 * @see org.aswing.JSpacer#createVerticalGlue
	 */
	public static function createVerticalGlue():Component{
		return JSpacer.createVerticalSpacer(0);
	}
}
	
}