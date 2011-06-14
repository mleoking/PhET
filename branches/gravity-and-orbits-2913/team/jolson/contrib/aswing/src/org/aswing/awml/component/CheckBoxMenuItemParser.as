/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.MenuItemParser;
import org.aswing.JCheckBoxMenuItem;

/**
 * Parses {@link org.aswing.JCheckBoxMenuItem} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.CheckBoxMenuItemParser extends MenuItemParser {
    
    /**
     * Constructor.
     */
    public function CheckBoxMenuItemParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, item:JCheckBoxMenuItem, namespace:AwmlNamespace) {
    	
        item = super.parse(awml, item, namespace);
        
        return item;
	}
    
    private function getClass(Void):Function {
    	return JCheckBoxMenuItem;	
    }
    
}
