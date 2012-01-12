/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.ToggleButtonParser;
import org.aswing.JCheckBox;

/**
 * Parses {@link org.aswing.JCheckBox} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.CheckBoxParser extends ToggleButtonParser {
    
    /**
     * Constructor.
     */
    public function CheckBoxParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, check:JCheckBox, namespace:AwmlNamespace) {
    	
        check = super.parse(awml, check, namespace);
        
        return check;
	}
	
    private function getClass(Void):Function {
    	return JCheckBox;	
    }
	    
}
