/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.PanelParser;
import org.aswing.SoftBox;
import org.aswing.SoftBoxLayout;

/**
 * Parses {@link org.aswing.SoftBox} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.SoftBoxParser extends PanelParser {
    
    private static var ATTR_AXIS:String = "axis";
    private static var ATTR_GAP:String = "gap"; 
    private static var ATTR_ALIGN:String = "align";
    
    private static var AXIS_X:String = "x";
	private static var AXIS_Y:String = "y";
    private static var ALIGN_LEFT:String = "left";
    private static var ALIGN_RIGHT:String = "right";
    private static var ALIGN_CENTER:String = "center";
    private static var ALIGN_TOP:String = "top";
    private static var ALIGN_BOTTOM:String = "bottom";
    
    /**
     * Constructor.
     */
    public function SoftBoxParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, box:SoftBox, namespace:AwmlNamespace) {
    	
        box = super.parse(awml, box, namespace);
        
        // init axis
        var axis:String = getAttributeAsString(awml, ATTR_AXIS, null);
        switch (axis) {
        	case AXIS_X:
        		box.setAxis(SoftBoxLayout.X_AXIS);
        		break;	
        	case AXIS_Y:
        		box.setAxis(SoftBoxLayout.Y_AXIS);
        		break;	
        }
        
        // init gap
        box.setGap(getAttributeAsNumber(awml, ATTR_GAP, box.getGap()));
        
        // init align
        var align:String = getAttributeAsString(awml, ATTR_ALIGN);
        switch(align) {
        	case ALIGN_TOP:
        		box.setAlign(SoftBoxLayout.TOP);
        		break;
        	case ALIGN_BOTTOM:
        		box.setAlign(SoftBoxLayout.BOTTOM);
        		break;
        	case ALIGN_CENTER:
        		box.setAlign(SoftBoxLayout.CENTER);
        		break;
        	case ALIGN_LEFT:
        		box.setAlign(SoftBoxLayout.LEFT);
        		break;
        	case ALIGN_RIGHT:
        		box.setAlign(SoftBoxLayout.RIGHT);
        		break;
        } 
        
        return box;
	}
    
    private function getClass(Void):Function {
    	return SoftBox;	
    }
    
}
