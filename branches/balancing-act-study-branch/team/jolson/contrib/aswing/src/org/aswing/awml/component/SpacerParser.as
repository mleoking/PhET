/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.ComponentParser;
import org.aswing.JSpacer;

/**
 * Parses {@link org.aswing.JSpacer} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.SpacerParser extends ComponentParser {
    
    /**
     * Constructor.
     */
    public function SpacerParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, spacer:JSpacer, namespace:AwmlNamespace) {
    	
        spacer = super.parse(awml, spacer, namespace);
        
        return spacer;
	}

    private function getClass(Void):Function {
    	return JSpacer;	
    }

}
