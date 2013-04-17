/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.ContainerParser;
import org.aswing.FloorPane;

/**
 * Parses {@link org.aswing.FloorPane} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.FloorPaneParser extends ContainerParser {
    
    private static var ATTR_HORIZONTAL_ALIGN:String = "horizontal-align";
    private static var ATTR_VERTICAL_ALIGN:String = "vertical-align";
    private static var ATTR_OFFSET_X:String = "offset-x";
    private static var ATTR_OFFSET_Y:String = "offset-y";
    private static var ATTR_MASK_FLOOR:String = "mask-floor";
    private static var ATTR_SCALE_MODE:String = "scale-mode";
    private static var ATTR_CUSTOM_SCALE:String = "custom-scale";
    private static var ATTR_PREFERRED_SIZE_STRATEGY:String = "preferred-size-strategy";
    
    private static var ALIGN_TOP:String = "top";
    private static var ALIGN_BOTTOM:String = "bottom";
    private static var ALIGN_CENTER:String = "center";
    private static var ALIGN_LEFT:String = "left";
    private static var ALIGN_RIGHT:String = "right";
    
    private static var STRATEGY_BOTH:String = "both";
    private static var STRATEGY_IMAGE:String = "image";
    private static var STRATEGY_LAYOUT:String = "layout";
    
    private static var SCALE_NONE:String = "none";
    private static var SCALE_FIT_PANE:String = "fit-pane";
    private static var SCALE_FIT_WIDTH:String = "fit-width";
    private static var SCALE_FIT_HEIGHT:String = "fit-height";
    private static var SCALE_STRETCH_PANE:String = "stretch-pane";
    private static var SCALE_CUSTOM:String = "custom";
    
    /**
     * Private Constructor.
     */
    private function FloorPaneParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, pane:FloorPane, namespace:AwmlNamespace) {
    	
        pane = super.parse(awml, pane, namespace);
        
        // init offsets
        pane.setOffsetX(getAttributeAsNumber(awml, ATTR_OFFSET_X, pane.getOffsetX()));
        pane.setOffsetY(getAttributeAsNumber(awml, ATTR_OFFSET_Y, pane.getOffsetY()));
        
        // init mask floor
        pane.setMaskFloor(getAttributeAsBoolean(awml, ATTR_MASK_FLOOR, pane.isMaskFloor()));
        
        // init scale image mode
        var scaleMode:String = getAttributeAsString(awml, ATTR_SCALE_MODE);
        switch (scaleMode) {
        	case SCALE_NONE:
        		pane.setScaleMode(FloorPane.SCALE_NONE);
        		break;	
        	case SCALE_FIT_PANE:
        		pane.setScaleMode(FloorPane.SCALE_FIT_PANE);
        		break;	
        	case SCALE_FIT_WIDTH:
        		pane.setScaleMode(FloorPane.SCALE_FIT_WIDTH);
        		break;	
        	case SCALE_FIT_HEIGHT:
        		pane.setScaleMode(FloorPane.SCALE_FIT_HEIGHT);
        		break;	
        	case SCALE_STRETCH_PANE:
        		pane.setScaleMode(FloorPane.SCALE_STRETCH_PANE);
        		break;	
        	case SCALE_CUSTOM:
        		pane.setScaleMode(FloorPane.SCALE_CUSTOM);
        		break;	
        }
        
        // set custom scale
        var scale:Number = getAttributeAsNumber(awml, ATTR_CUSTOM_SCALE, null);
        if (scale != null) pane.setCustomScale(scale);
        
        // init aligns
        var hAlign:String = getAttributeAsString(awml, ATTR_HORIZONTAL_ALIGN);
        switch (hAlign) {
        	case ALIGN_LEFT:
        		pane.setHorizontalAlignment(FloorPane.LEFT);
        		break;	
        	case ALIGN_CENTER:
        		pane.setHorizontalAlignment(FloorPane.CENTER);
        		break;	
        	case ALIGN_RIGHT:
        		pane.setHorizontalAlignment(FloorPane.RIGHT);
        		break;	
        }

        var vAlign:String = getAttributeAsString(awml, ATTR_VERTICAL_ALIGN);
        switch (vAlign) {
        	case ALIGN_TOP:
        		pane.setHorizontalAlignment(FloorPane.TOP);
        		break;	
        	case ALIGN_CENTER:
        		pane.setHorizontalAlignment(FloorPane.CENTER);
        		break;	
        	case ALIGN_BOTTOM:
        		pane.setHorizontalAlignment(FloorPane.BOTTOM);
        		break;	
        }

        // init preferred size strategy
        var strategy:String = getAttributeAsString(awml, ATTR_PREFERRED_SIZE_STRATEGY);
        switch (strategy) {
        	case STRATEGY_BOTH:
        		pane.setPrefferSizeStrategy(FloorPane.PREFER_SIZE_BOTH);
        		break;	
        	case STRATEGY_IMAGE:
        		pane.setPrefferSizeStrategy(FloorPane.PREFER_SIZE_IMAGE);
        		break;	
        	case STRATEGY_LAYOUT:
        		pane.setPrefferSizeStrategy(FloorPane.PREFER_SIZE_LAYOUT);
        		break;	
        }
        
        return pane;
    }
        
}
