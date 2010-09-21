/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.AbstractTabbedPaneParser;
import org.aswing.overflow.JAccordion;

/**
 * Parses {@link org.aswing.overflow.JAccordion} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.AccordionParser extends AbstractTabbedPaneParser {
    
    /**
     * Constructor.
     */
    public function AccordionParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, accordion:JAccordion, namespace:AwmlNamespace) {
    	
        accordion = super.parse(awml, accordion, namespace);
        
        return accordion;
	}

    private function getClass(Void):Function {
    	return JAccordion;	
    }
    
}
