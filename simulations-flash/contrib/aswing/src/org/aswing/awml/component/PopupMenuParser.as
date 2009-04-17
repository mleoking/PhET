/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.component.ComponentParser;
import org.aswing.Component;
import org.aswing.overflow.JPopupMenu;

/**
 * Parses {@link org.aswing.overflow.JPopupMenu} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.PopupMenuParser extends ComponentParser {
    
    /**
     * Constructor.
     */
    public function PopupMenuParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, popup:JPopupMenu, namespace:AwmlNamespace) {
    	
        popup = super.parse(awml, popup, namespace);
        
        return popup;
	}
	
    private function parseChild(awml:XMLNode, nodeName:String, popup:JPopupMenu, namespace:AwmlNamespace):Void {

        super.parseChild(awml, nodeName, popup, namespace);

		if (AwmlUtils.isMenuItemNode(nodeName)) {
			var component:Component = AwmlParser.parse(awml, null, namespace);
			if (component != null) popup.append(component); 	
		}
    }	

    private function getClass(Void):Function {
    	return JPopupMenu;	
    }

}
