/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.component.ComponentParser;
import org.aswing.overflow.JMenu;
import org.aswing.overflow.JMenuBar;

/**
 * Parses {@link org.aswing.overflow.JMenuBar} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.MenuBarParser extends ComponentParser {
    
    /**
     * Constructor.
     */
    public function MenuBarParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, bar:JMenuBar, namespace:AwmlNamespace) {
    	
        bar = super.parse(awml, bar, namespace);
        
        return bar;
	}
	
    private function parseChild(awml:XMLNode, nodeName:String, bar:JMenuBar, namespace:AwmlNamespace):Void {

        super.parseChild(awml, nodeName, bar, namespace);

		if (nodeName == AwmlConstants.NODE_MENU) {
			var	menu:JMenu = AwmlParser.parse(awml, null, namespace);
			if (menu != null) bar.append(menu);
		}
    }	

    private function getClass(Void):Function {
    	return JMenuBar;	
    }

}
