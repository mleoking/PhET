/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.MenuItemParser;
import org.aswing.JRadioButtonMenuItem;

/**
 * Parses {@link org.aswing.JRadioButtonMenuItem} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.RadioButtonMenuItemParser extends MenuItemParser {
    
    /**
     * Constructor.
     */
    public function RadioButtonMenuItemParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, item:JRadioButtonMenuItem, namespace:AwmlNamespace) {
    	
        item = super.parse(awml, item, namespace);
        
        return item;
	}
    
    private function getClass(Void):Function {
    	return JRadioButtonMenuItem;	
    }
    
}
