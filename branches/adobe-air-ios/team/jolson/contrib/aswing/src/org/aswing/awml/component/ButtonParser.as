/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.AbstractButtonParser;
import org.aswing.JButton;

/**
 * Parses {@link org.aswing.JButton} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.ButtonParser extends AbstractButtonParser {
    
    /**
     * Constructor.
     */
    public function ButtonParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, button:JButton, namespace:AwmlNamespace) {
    	
        button = super.parse(awml, button, namespace);
        
        return button;
	}
    
    private function getClass(Void):Function {
    	return JButton;	
    }
    
}
