/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.AbstractTabbedPaneParser;
import org.aswing.overflow.JTabbedPane;

/**
 * Parses {@link org.aswing.overflow.JTabbedPane} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.TabbedPaneParser extends AbstractTabbedPaneParser {
    
	private static var ATTR_TAB_PLACEMENT:String = "tab-placement";
	
	private static var TAB_PLACEMENT_TOP:String = "top";
	private static var TAB_PLACEMENT_LEFT:String = "left";
	private static var TAB_PLACEMENT_RIGHT:String = "right";
	private static var TAB_PLACEMENT_BOTTOM:String = "bottom";
    
    
    /**
     * Constructor.
     */
    public function TabbedPaneParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, pane:JTabbedPane, namespace:AwmlNamespace) {
    	
        pane = super.parse(awml, pane, namespace);
        
        // init tab placement
        var placement:String = getAttributeAsString(awml, ATTR_TAB_PLACEMENT);
        switch (placement) {
        	case TAB_PLACEMENT_TOP:
        		pane.setTabPlacement(JTabbedPane.TOP);
        		break;
        	case TAB_PLACEMENT_LEFT:
        		pane.setTabPlacement(JTabbedPane.LEFT);
        		break;
        	case TAB_PLACEMENT_RIGHT:
        		pane.setTabPlacement(JTabbedPane.RIGHT);
        		break;
        	case TAB_PLACEMENT_BOTTOM:
        		pane.setTabPlacement(JTabbedPane.BOTTOM);
        		break;
        }
        
        return pane;
	}
    
    private function getClass(Void):Function {
    	return JTabbedPane;	
    }
    
}
