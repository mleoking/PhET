/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.resizer.DefaultResizer;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.frame.FrameResizer extends DefaultResizer {
	
	public function FrameResizer() {
		super();
		setResizeArrowColor(UIManager.getColor("Frame.resizeArrow"));
		setResizeArrowLightColor(UIManager.getColor("Frame.resizeArrowLight"));
		setResizeArrowDarkColor(UIManager.getColor("Frame.resizeArrowDark"));
	}

}