/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.ComponentParser;
import org.aswing.overflow.JAdjuster;

/**
 * Parses {@link org.aswing.overflow.JAdjuster} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.AdjusterParser extends ComponentParser {
	
	private static var ATTR_ORIENTATION:String = "orientation";
	private static var ATTR_MINIMUM:String = "minimum";
	private static var ATTR_MAXIMUM:String = "maximum";
	private static var ATTR_EXTENT:String = "extent";
	private static var ATTR_VALUE:String = "value";
	private static var ATTR_COLUMNS:String = "columns";
	private static var ATTR_EDITABLE:String = "editable";
	
	private static var ORIENTATION_HORIZONTAL:String = "horizontal";
	private static var ORIENTATION_VERTICAL:String = "vertical"; 
	
	/**
	 * Constructor.
	 */
	public function AdjusterParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, adjuster:JAdjuster, namespace:AwmlNamespace) {
		
		adjuster = super.parse(awml, adjuster, namespace);
		
		adjuster.setColumns(getAttributeAsNumber(awml, ATTR_COLUMNS, adjuster.getColumns()));
		adjuster.setEditable(getAttributeAsBoolean(awml, ATTR_EDITABLE, adjuster.isEditable()));
		
		// init adjust bounds
		adjuster.setMinimum(getAttributeAsNumber(awml, ATTR_MINIMUM, adjuster.getMinimum()));
		adjuster.setMaximum(getAttributeAsNumber(awml, ATTR_MAXIMUM, adjuster.getMaximum()));
		adjuster.setExtent(getAttributeAsNumber(awml, ATTR_EXTENT, adjuster.getExtent()));
		adjuster.setValue(getAttributeAsNumber(awml, ATTR_VALUE, adjuster.getValue()));
		
		// set orientation
		var orientation:String = getAttributeAsString(awml, ATTR_ORIENTATION, null);
		switch (orientation) {
			case ORIENTATION_HORIZONTAL:
				adjuster.setOrientation(JAdjuster.HORIZONTAL);
				break;
			case ORIENTATION_VERTICAL:
				adjuster.setOrientation(JAdjuster.VERTICAL);
				break;
		}
		
        // init events
        attachEventListeners(adjuster, JAdjuster.ON_ACT, getAttributeAsEventListenerInfos(awml, ATTR_ON_ACTION));
        attachEventListeners(adjuster, JAdjuster.ON_STATE_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_STATE_CHANGED));
		
		return adjuster;
	}

    private function getClass(Void):Function {
    	return JAdjuster;	
    }

}
