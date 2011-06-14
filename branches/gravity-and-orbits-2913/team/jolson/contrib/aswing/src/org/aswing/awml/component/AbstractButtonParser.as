/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractButton;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.ButtonGroupManager;
import org.aswing.awml.component.ComponentParser;
import org.aswing.Icon;
import org.aswing.Insets;

/**
 * Parses {@link org.aswing.AbstractButton} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.AbstractButtonParser extends ComponentParser {
    
    private static var ATTR_HORIZONTAL_ALIGN:String = "horizontal-align";
    private static var ATTR_VERTICAL_ALIGN:String = "vertical-align";
    private static var ATTR_HORIZONTAL_POSITION:String = "horizontal-position";
    private static var ATTR_VERTICAL_POSITION:String = "vertical-position";
    private static var ATTR_TEXT:String = "text";
    private static var ATTR_ICON_TEXT_GAP:String = "icon-text-gap";
    private static var ATTR_ROLL_OVER_ENABLED:String = "roll-over-enabled";
    private static var ATTR_SELECTED:String = "selected";
    private static var ATTR_GROUP_ID:String = "group-id";
    
    private static var ATTR_ON_SELECTION_CHANGED:String = "on-selection-changed";
    
    private static var ALIGN_TOP:String = "top";
    private static var ALIGN_CENTER:String = "center";
    private static var ALIGN_BOTTOM:String = "bottom";
    private static var ALIGN_LEFT:String = "left";
    private static var ALIGN_RIGHT:String = "right";
    
    private static var POSITION_TOP:String = "top";
    private static var POSITION_CENTER:String = "center";
    private static var POSITION_BOTTOM:String = "bottom";
    private static var POSITION_LEFT:String = "left";
    private static var POSITION_RIGHT:String = "right";
    
    
    /**
     * Private Constructor.
     */
    private function AbstractButtonParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, button:AbstractButton, namespace:AwmlNamespace) {
    	
        button = super.parse(awml, button, namespace);
        
        // init aligns
        var halign:String = getAttributeAsString(awml, ATTR_HORIZONTAL_ALIGN);
        switch (halign) {
        	case ALIGN_LEFT:
        		button.setHorizontalAlignment(AbstractButton.LEFT);
        		break;
        	case ALIGN_CENTER:
        		button.setHorizontalAlignment(AbstractButton.CENTER);
        		break;	
        	case ALIGN_RIGHT:
        		button.setHorizontalAlignment(AbstractButton.RIGHT);
        		break;	
        }

        var valign:String = getAttributeAsString(awml, ATTR_VERTICAL_ALIGN);
        switch (valign) {
        	case ALIGN_TOP:
        		button.setVerticalAlignment(AbstractButton.TOP);
        		break;
        	case ALIGN_CENTER:
        		button.setVerticalAlignment(AbstractButton.CENTER);
        		break;	
        	case ALIGN_BOTTOM:
        		button.setVerticalAlignment(AbstractButton.BOTTOM);
        		break;	
        }
        
        // init text positions
        var hpos:String = getAttributeAsString(awml, ATTR_HORIZONTAL_POSITION);
        switch (hpos) {
        	case POSITION_LEFT:
        		button.setHorizontalTextPosition(AbstractButton.LEFT);
        		break;
        	case POSITION_CENTER:
        		button.setHorizontalTextPosition(AbstractButton.CENTER);
        		break;	
        	case POSITION_RIGHT:
        		button.setHorizontalTextPosition(AbstractButton.RIGHT);
        		break;	
        }
        
        var vpos:String = getAttributeAsString(awml, ATTR_VERTICAL_POSITION);
        switch (vpos) {
        	case POSITION_TOP:
        		button.setVerticalTextPosition(AbstractButton.TOP);
        		break;
        	case POSITION_CENTER:
        		button.setVerticalTextPosition(AbstractButton.CENTER);
        		break;	
        	case POSITION_BOTTOM:
        		button.setVerticalTextPosition(AbstractButton.BOTTOM);
        		break;	
        }
        
        // init text
        button.setText(getAttributeAsString(awml, ATTR_TEXT, button.getText()));
        
        // init icon text gap
        button.setIconTextGap(getAttributeAsNumber(awml, ATTR_ICON_TEXT_GAP, button.getIconTextGap()));
        
        // set roll over enabled
        button.setRollOverEnabled(getAttributeAsBoolean(awml, ATTR_ROLL_OVER_ENABLED, button.isRollOverEnabled()));
        
        // init selected
        button.setSelected(getAttributeAsBoolean(awml, ATTR_SELECTED, button.isSelected()));
        
        // init button group by id
		var groupId:String = getAttributeAsString(awml, ATTR_GROUP_ID, null);
		ButtonGroupManager.append(groupId, button);        
        
        // init events
        attachEventListeners(button, AbstractButton.ON_ACT, getAttributeAsEventListenerInfos(awml, ATTR_ON_ACTION));
        attachEventListeners(button, AbstractButton.ON_STATE_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_STATE_CHANGED));
        attachEventListeners(button, AbstractButton.ON_SELECTION_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_SELECTION_CHANGED));
        
        return button;
	}
    
    private function parseChild(awml:XMLNode, nodeName:String, button:AbstractButton, namespace:AwmlNamespace):Void {
    	super.parseChild(awml, nodeName, button, namespace);
    	
    	// init insets
    	if (nodeName == AwmlConstants.NODE_MARGINS) {
    		var insets:Insets = AwmlParser.parse(awml);
    		if (insets != null) button.setMargin(insets);	
    	} else if (AwmlUtils.isIconNode(nodeName)) {
    		var icon:Icon = AwmlParser.parse(awml);
    		if (icon != null) button.setIcon(icon);	
    	} else if (nodeName == AwmlConstants.NODE_ICON) {
    		var icon:Icon = AwmlParser.parse(awml);
    		if (icon != null) button.setIcon(icon);
    	} else if (nodeName == AwmlConstants.NODE_SELECTED_ICON) {
    		var icon:Icon = AwmlParser.parse(awml);
    		if (icon != null) button.setSelectedIcon(icon);
    	} else if (nodeName == AwmlConstants.NODE_PRESSED_ICON) {
    		var icon:Icon = AwmlParser.parse(awml);
    		if (icon != null) button.setPressedIcon(icon);
    	} else if (nodeName == AwmlConstants.NODE_DISABLED_ICON) {
    		var icon:Icon = AwmlParser.parse(awml);
    		if (icon != null) button.setDisabledIcon(icon);
    	} else if (nodeName == AwmlConstants.NODE_DISABLED_SELECTED_ICON) {
    		var icon:Icon = AwmlParser.parse(awml);
    		if (icon != null) button.setDisabledSelectedIcon(icon);
    	} else if (nodeName == AwmlConstants.NODE_ROLL_OVER_ICON) {
    		var icon:Icon = AwmlParser.parse(awml);
    		if (icon != null) button.setRollOverIcon(icon);
    	} else if (nodeName == AwmlConstants.NODE_ROLL_OVER_SELECTED_ICON) {
    		var icon:Icon = AwmlParser.parse(awml);
    		if (icon != null) button.setRollOverSelectedIcon(icon);
    	}
    }
    
}
