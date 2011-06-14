/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.layout.EmptyLayoutParser;
import org.aswing.SoftBoxLayout;

/**
 * Parses {@link org.aswing.SoftBoxLayout} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.layout.SoftBoxLayoutParser extends EmptyLayoutParser {
    
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
    public function SoftBoxLayoutParser(Void) {
        super();
    }

    public function parse(awml:XMLNode, layout:SoftBoxLayout) {
        
        layout = super.parse(awml, layout);
        
        // init layout
        var axis:String = getAttributeAsString(awml, ATTR_AXIS);
        switch (axis) {
            case AXIS_Y:
                layout.setAxis(SoftBoxLayout.Y_AXIS);
                break;
            case AXIS_X:
                layout.setAxis(SoftBoxLayout.X_AXIS);
                break;  
        }
        
        // init gap
        layout.setGap(getAttributeAsNumber(awml, ATTR_GAP, layout.getGap()));

        // init align
        var align:String = getAttributeAsString(awml, ATTR_ALIGN);
        switch(align) {
        	case ALIGN_TOP:
        		layout.setAlign(SoftBoxLayout.TOP);
        		break;
        	case ALIGN_BOTTOM:
        		layout.setAlign(SoftBoxLayout.BOTTOM);
        		break;
        	case ALIGN_CENTER:
        		layout.setAlign(SoftBoxLayout.CENTER);
        		break;
        	case ALIGN_LEFT:
        		layout.setAlign(SoftBoxLayout.LEFT);
        		break;
        	case ALIGN_RIGHT:
        		layout.setAlign(SoftBoxLayout.RIGHT);
        		break;
        } 
        
        return layout;
    }   
    
    private function getClass(Void):Function {
    	return SoftBoxLayout;	
    }
    
}