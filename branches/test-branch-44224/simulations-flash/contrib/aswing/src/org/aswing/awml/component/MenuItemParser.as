/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.AbstractButtonParser;
import org.aswing.overflow.JMenuItem;

/**
 * Parses {@link org.aswing.overflow.JMenuItem} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.MenuItemParser extends AbstractButtonParser {
    
    /**
     * Constructor.
     */
    public function MenuItemParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, item:JMenuItem, namespace:AwmlNamespace) {
    	
        item = super.parse(awml, item, namespace);
        
        //TODO add ket accelerator support
        
        return item;
	}
    
    private function getClass(Void):Function {
    	return JMenuItem;	
    }
    
}
