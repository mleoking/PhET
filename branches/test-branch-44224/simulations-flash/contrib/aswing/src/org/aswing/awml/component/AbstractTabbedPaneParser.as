/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractTabbedPane;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.component.ComponentParser;
import org.aswing.Insets;
import org.aswing.awml.AwmlTabInfo;

/**
 * Parses {@link org.aswing.AbstractTabbedPane} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.AbstractTabbedPaneParser extends ComponentParser {
    
    private static var ATTR_HORIZONTAL_ALIGN:String = "horizontal-align";
    private static var ATTR_VERTICAL_ALIGN:String = "vertical-align";
    private static var ATTR_HORIZONTAL_TEXT_POSITION:String = "horizontal-text-position";
    private static var ATTR_VERTICAL_TEXT_POSITION:String = "vertical-text-position";
    private static var ATTR_ICON_TEXT_GAP:String = "icon-text-gap";
    private static var ATTR_SELECTED_INDEX:String = "selected-index";
    
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
    private function AbstractTabbedPaneParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, pane:AbstractTabbedPane, namespace:AwmlNamespace) {
    	
        pane = super.parse(awml, pane, namespace);
        
        // init aligns
        var halign:String = getAttributeAsString(awml, ATTR_HORIZONTAL_ALIGN);
        switch (halign) {
        	case ALIGN_LEFT:
        		pane.setHorizontalAlignment(AbstractTabbedPane.LEFT);
        		break;
        	case ALIGN_CENTER:
        		pane.setHorizontalAlignment(AbstractTabbedPane.CENTER);
        		break;	
        	case ALIGN_RIGHT:
        		pane.setHorizontalAlignment(AbstractTabbedPane.RIGHT);
        		break;	
        }

        var valign:String = getAttributeAsString(awml, ATTR_VERTICAL_ALIGN);
        switch (valign) {
        	case ALIGN_TOP:
        		pane.setVerticalAlignment(AbstractTabbedPane.TOP);
        		break;
        	case ALIGN_CENTER:
        		pane.setVerticalAlignment(AbstractTabbedPane.CENTER);
        		break;	
        	case ALIGN_BOTTOM:
        		pane.setVerticalAlignment(AbstractTabbedPane.BOTTOM);
        		break;	
        }
        
        // init text positions
        var hpos:String = getAttributeAsString(awml, ATTR_HORIZONTAL_TEXT_POSITION);
        switch (hpos) {
        	case POSITION_LEFT:
        		pane.setHorizontalTextPosition(AbstractTabbedPane.LEFT);
        		break;
        	case POSITION_CENTER:
        		pane.setHorizontalTextPosition(AbstractTabbedPane.CENTER);
        		break;	
        	case POSITION_RIGHT:
        		pane.setHorizontalTextPosition(AbstractTabbedPane.RIGHT);
        		break;	
        }
        
        var vpos:String = getAttributeAsString(awml, ATTR_VERTICAL_TEXT_POSITION);
        switch (vpos) {
        	case POSITION_TOP:
        		pane.setVerticalTextPosition(AbstractTabbedPane.TOP);
        		break;
        	case POSITION_CENTER:
        		pane.setVerticalTextPosition(AbstractTabbedPane.CENTER);
        		break;	
        	case POSITION_BOTTOM:
        		pane.setVerticalTextPosition(AbstractTabbedPane.BOTTOM);
        		break;	
        }
        
        // init icon text gap
        pane.setIconTextGap(getAttributeAsNumber(awml, ATTR_ICON_TEXT_GAP, pane.getIconTextGap()));
        
        // init selected index
        pane.setSelectedIndex(getAttributeAsNumber(awml, ATTR_SELECTED_INDEX, pane.getSelectedIndex()));
        
        // init events
        attachEventListeners(pane, AbstractTabbedPane.ON_STATE_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_STATE_CHANGED));
        
        return pane;
	}
    
    private function parseChild(awml:XMLNode, nodeName:String, pane:AbstractTabbedPane, namespace:AwmlNamespace):Void {
    	super.parseChild(awml, nodeName, pane, namespace);
    	
    	// init insets
    	if (nodeName == AwmlConstants.NODE_MARGINS) {
    		var insets:Insets = AwmlParser.parse(awml);
    		if (insets != null) pane.setMargin(insets);	
    	} else if (nodeName == AwmlConstants.NODE_TAB) {
    		var tab:AwmlTabInfo = AwmlParser.parse(awml, null, namespace);
    		if (tab != null) {
    			pane.appendTab(tab.component, tab.title, tab.icon, tab.tooltip);	
    		}	
    	}
    }
    
}
