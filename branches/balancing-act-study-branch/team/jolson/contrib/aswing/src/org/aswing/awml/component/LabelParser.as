/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.component.ComponentParser;
import org.aswing.Icon;
import org.aswing.JLabel;

/**
 * Parses {@link org.aswing.JLabel} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.LabelParser extends ComponentParser {
    
    private static var ATTR_HORIZONTAL_ALIGN:String = "horizontal-align";
    private static var ATTR_VERTICAL_ALIGN:String = "vertical-align";
    private static var ATTR_HORIZONTAL_POSITION:String = "horizontal-position";
    private static var ATTR_VERTICAL_POSITION:String = "vertical-position";
    private static var ATTR_TEXT:String = "text";
    private static var ATTR_ICON_TEXT_GAP:String = "icon-text-gap";
    
    private static var ALIGN_TOP:String = "top";
    private static var ALIGN_CENTER:String = "center";
    private static var ALIGN_BOTTOM:String = "bottom";
    private static var ALIGN_LEFT:String = "left";
    private static var ALIGN_RIGHT:String = "right";
     
    
    /**
     * Constructor.
     */
    public function LabelParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, label:JLabel, namespace:AwmlNamespace) {
    	
        label = super.parse(awml, label, namespace);
        
        // init aligns
        var halign:String = getAttributeAsString(awml, ATTR_HORIZONTAL_ALIGN);
        switch (halign) {
        	case ALIGN_LEFT:
        		label.setHorizontalAlignment(JLabel.LEFT);
        		break;
        	case ALIGN_CENTER:
        		label.setHorizontalAlignment(JLabel.CENTER);
        		break;	
        	case ALIGN_RIGHT:
        		label.setHorizontalAlignment(JLabel.RIGHT);
        		break;	
        }

        var valign:String = getAttributeAsString(awml, ATTR_VERTICAL_ALIGN);
        switch (valign) {
        	case ALIGN_TOP:
        		label.setVerticalAlignment(JLabel.TOP);
        		break;
        	case ALIGN_CENTER:
        		label.setVerticalAlignment(JLabel.CENTER);
        		break;	
        	case ALIGN_BOTTOM:
        		label.setVerticalAlignment(JLabel.BOTTOM);
        		break;	
        }
        
        // init text positions
        var hpos:String = getAttributeAsString(awml, ATTR_HORIZONTAL_POSITION);
        switch (hpos) {
        	case ALIGN_LEFT:
        		label.setHorizontalTextPosition(JLabel.LEFT);
        		break;
        	case ALIGN_CENTER:
        		label.setHorizontalTextPosition(JLabel.CENTER);
        		break;	
        	case ALIGN_RIGHT:
        		label.setHorizontalTextPosition(JLabel.RIGHT);
        		break;	
        }
        
        var vpos:String = getAttributeAsString(awml, ATTR_VERTICAL_POSITION);
        switch (vpos) {
        	case ALIGN_TOP:
        		label.setVerticalTextPosition(JLabel.TOP);
        		break;
        	case ALIGN_CENTER:
        		label.setVerticalTextPosition(JLabel.CENTER);
        		break;	
        	case ALIGN_BOTTOM:
        		label.setVerticalTextPosition(JLabel.BOTTOM);
        		break;	
        }
        
        // init text
        label.setText(getAttributeAsString(awml, ATTR_TEXT, label.getText()));
        
        // init icon text gap
        label.setIconTextGap(getAttributeAsNumber(awml, ATTR_ICON_TEXT_GAP, label.getIconTextGap()));
        
        return label;
	}

    private function parseChild(awml:XMLNode, nodeName:String, label:JLabel, namespace:AwmlNamespace):Void {
    	super.parseChild(awml, nodeName, label, namespace);
    	
		if (AwmlUtils.isIconNode(nodeName)) {
    		var icon:Icon = AwmlParser.parse(awml);
    		if (icon != null) label.setIcon(icon);	
    	}
    }
    
    private function getClass(Void):Function {
    	return JLabel;	
    }
    
}
