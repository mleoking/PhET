/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.ComponentParser;
import org.aswing.JSlider;

/**
 * Parses {@link org.aswing.JSlider} level elements.
 * 
 * @author iiley
 */
class org.aswing.awml.component.SliderParser extends ComponentParser {
	
	private static var ATTR_MINIMUM:String = "minimum";
	private static var ATTR_MAXIMUM:String = "maximum";
	private static var ATTR_EXTENT:String = "extent";
	private static var ATTR_VALUE:String = "value";
	private static var ATTR_ORIENTATION:String = "orientation";
	
	private static var ATTR_MAJOR_TICK_SPACING:String = "major-tick-spacing";
	private static var ATTR_MINOR_TICK_SPACING:String = "minor-tick-spacing";
	private static var ATTR_INVERTED:String = "inverted";
	private static var ATTR_SNAG_TO_TICKS:String = "snap-to-ticks";
	private static var ATTR_PAINT_TRACK:String = "paint-track";
	private static var ATTR_PAINT_TICKS:String = "paint-ticks";
	private static var ATTR_SHOW_VALUE_TIP:String = "show-value-tip";
	
	private static var ORIENTATION_HORIZONTAL:String = "horizontal";
	private static var ORIENTATION_VERTICAL:String = "vertical"; 
	
	/**
	 * Constructor.
	 */
	public function SliderParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, slider:JSlider, namespace:AwmlNamespace) {
		
		slider = super.parse(awml, slider, namespace);
		
		// init scroll bounds
		slider.setMinimum(getAttributeAsNumber(awml, ATTR_MINIMUM, slider.getMinimum()));
		slider.setMaximum(getAttributeAsNumber(awml, ATTR_MAXIMUM, slider.getMaximum()));
		slider.setExtent(getAttributeAsNumber(awml, ATTR_EXTENT, slider.getExtent()));
		slider.setValue(getAttributeAsNumber(awml, ATTR_VALUE, slider.getValue()));
				
		// init other attributs
		slider.setMajorTickSpacing(getAttributeAsNumber(awml, ATTR_MAJOR_TICK_SPACING, slider.getMajorTickSpacing()));
		slider.setMinorTickSpacing(getAttributeAsNumber(awml, ATTR_MINOR_TICK_SPACING, slider.getMinorTickSpacing()));
		slider.setInverted(getAttributeAsBoolean(awml, ATTR_INVERTED, slider.getInverted()));
		slider.setSnapToTicks(getAttributeAsBoolean(awml, ATTR_SNAG_TO_TICKS, slider.getSnapToTicks()));
		slider.setPaintTrack(getAttributeAsBoolean(awml, ATTR_PAINT_TRACK, slider.getPaintTrack()));
		slider.setPaintTicks(getAttributeAsBoolean(awml, ATTR_PAINT_TICKS, slider.getPaintTicks()));
		slider.setShowValueTip(getAttributeAsBoolean(awml, ATTR_SHOW_VALUE_TIP, slider.getShowValueTip()));
		
		// set orientation
		var orientation:String = getAttributeAsString(awml, ATTR_ORIENTATION, null);
		switch (orientation) {
			case ORIENTATION_HORIZONTAL:
				slider.setOrientation(JSlider.HORIZONTAL);
				break;
			case ORIENTATION_VERTICAL:
				slider.setOrientation(JSlider.VERTICAL);
				break;
		}
		
        // init events
        attachEventListeners(slider, JSlider.ON_STATE_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_STATE_CHANGED));

		return slider;
	}
	
    private function getClass(Void):Function {
    	return JSlider;	
    }
	
}