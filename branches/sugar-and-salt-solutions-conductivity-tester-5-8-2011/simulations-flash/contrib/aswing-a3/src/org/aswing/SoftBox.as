/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing { 

/**
 * A <code>JPanel</code> with <code>SoftBoxLayout</code>.
 * 
 * @author iiley
 */
public class SoftBox extends JPanel {
	
	/**
	 * SoftBox(axis:int, gap:int, align:int)<br>
	 * SoftBox(axis:int, gap:int)<br> default align to LEFT.
	 * SoftBox(axis:int) default gap to 0.
	 * Creates a panel with a SoftBoxLayout.
	 * @param axis the axis of layout.
	 *  {@link org.aswing.SoftBoxLayout#X_AXIS} or {@link org.aswing.SoftBoxLayout#Y_AXIS}
     * @param gap (optional)the gap between each component, default 0
     * @param align (optional)the alignment value, default is LEFT
	 * @see org.aswing.SoftBoxLayout
	 */
	public function SoftBox(axis:int, gap:int=0, align:int=AsWingConstants.LEFT){
		super();
		setName("SoftBox");
		setLayout(new SoftBoxLayout(axis, gap, align));
	}
	
	/** Sets new axis for the default SoftBoxLayout.
	 * @param axis the new axis
	 */
	public function setAxis(axis:int):void {
		SoftBoxLayout(getLayout()).setAxis(axis);
	}

	/**
	 * Gets current axis of the default SoftBoxLayout.
	 * @return axis 
	 */
	public function getAxis():int {
		return SoftBoxLayout(getLayout()).getAxis();
	}

	/**
	 * Sets new gap for the default SoftBoxLayout.
	 * @param gap the new gap
	 */
	public function setGap(gap:int):void {
		SoftBoxLayout(getLayout()).setGap(gap);
	}

	/**
	 * Gets current gap of the default SoftBoxLayout.
	 * @return gap 
	 */
	public function getGap():int {
		return SoftBoxLayout(getLayout()).getGap();
	}
	
	/**
	 * Sets new align for the default SoftBoxLayout.
	 * @param align the new align
	 */
	public function setAlign(align:int):void {
		SoftBoxLayout(getLayout()).setAlign(align);
	}

	/**
	 * Gets current align of the default SoftBoxLayout.
	 * @return align 
	 */
	public function getAlign():int {
		return SoftBoxLayout(getLayout()).getAlign();
	}	
	
	/**
	 * Creates and return a Horizontal SoftBox.
     * @param gap (optional)the gap between each component, default 0
     * @param align (optional)the alignment value, default is LEFT
     * @return a horizontal SoftBox.
	 */
	public static function createHorizontalBox(gap:int=0, align:int=AsWingConstants.LEFT):SoftBox{
		return new SoftBox(SoftBoxLayout.X_AXIS, gap, align);
	}
	
	/**
	 * Creates and return a Vertical SoftBox.
     * @param gap the gap between each component, default 0
     * @param align (optional)the alignment value, default is LEFT
     * @return a vertical SoftBox.
	 */
	public static function createVerticalBox(gap:int=0, align:int=AsWingConstants.LEFT):SoftBox{
		return new SoftBox(SoftBoxLayout.Y_AXIS, gap, align);
	}
	
	/**
	 * @see org.aswing.JSpacer#createHorizontalGlue
	 */
	public static function createHorizontalGlue(width:int=4):Component{
		return JSpacer.createHorizontalSpacer(width);
	}
	
	/**
	 * @see org.aswing.JSpacer#createVerticalGlue
	 */
	public static function createVerticalGlue(height:int=4):Component{
		return JSpacer.createVerticalSpacer(height);
	}
}

}