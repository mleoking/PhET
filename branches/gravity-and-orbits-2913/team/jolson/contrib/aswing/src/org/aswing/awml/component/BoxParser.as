/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.PanelParser;
import org.aswing.Box;
import org.aswing.BoxLayout;

/**
 * Parses {@link org.aswing.Box} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.BoxParser extends PanelParser {
    
    private static var ATTR_AXIS:String = "axis";
    private static var ATTR_GAP:String = "gap"; 
    
    private static var AXIS_X:String = "x";
	private static var AXIS_Y:String = "y";
    
    
    /**
     * Constructor.
     */
    public function BoxParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, box:Box, namespace:AwmlNamespace) {
    	
        box = super.parse(awml, box, namespace);
        
        // init axis
        var axis:String = getAttributeAsString(awml, ATTR_AXIS, null);
        switch (axis) {
        	case AXIS_X:
        		box.setAxis(BoxLayout.X_AXIS);
        		break;	
        	case AXIS_Y:
        		box.setAxis(BoxLayout.Y_AXIS);
        		break;	
        }
        
        // init gap
        box.setGap(getAttributeAsNumber(awml, ATTR_GAP, box.getGap()));
        
        return box;
	}
   
    private function getClass(Void):Function {
    	return Box;	
    }
    
}
