/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.AbstractButtonParser;
import org.aswing.overflow.JToggleButton;

/**
 * Parses {@link org.aswing.overflow.JToggleButton} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.ToggleButtonParser extends AbstractButtonParser {
        
    /**
     * Constructor.
     */
    public function ToggleButtonParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, button:JToggleButton, namespace:AwmlNamespace) {
    	
        button = super.parse(awml, button, namespace);
        
        return button;
	}
    
    private function getClass(Void):Function {
    	return JToggleButton;	
    }
    
}
