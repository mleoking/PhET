/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.MenuItemParser;
import org.aswing.overflow.JMenu;
import org.aswing.awml.AwmlUtils;
import org.aswing.Component;
import org.aswing.awml.AwmlParser;

/**
 * Parses {@link org.aswing.overflow.JMenu} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.MenuParser extends MenuItemParser {
    
    private static var ATTR_DELAY:String = "delay";
    private static var ATTR_POPUP_MENU_VISIBLE:String = "popup-menu-visible";
    
    /**
     * Constructor.
     */
    public function MenuParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, menu:JMenu, namespace:AwmlNamespace) {
    	
        menu = super.parse(awml, menu, namespace);
        
        // init popup menu
        menu.setDelay(getAttributeAsNumber(awml, ATTR_DELAY, menu.getDelay()));
        menu.setPopupMenuVisible(getAttributeAsBoolean(awml, ATTR_POPUP_MENU_VISIBLE, menu.isPopupMenuVisible()));
        
        return menu;
	}
	
    private function parseChild(awml:XMLNode, nodeName:String, menu:JMenu, namespace:AwmlNamespace):Void {
    	super.parseChild(awml, nodeName, menu, namespace);
    	
		if (AwmlUtils.isMenuItemNode(nodeName)) {
			var component:Component = AwmlParser.parse(awml, null, namespace);
			if (component != null) menu.append(component); 	
		}
    }	
    
    private function getClass(Void):Function {
    	return JMenu;	
    }
    
}
