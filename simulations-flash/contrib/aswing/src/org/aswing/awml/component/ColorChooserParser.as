/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlTabInfo;
import org.aswing.awml.component.AbstractColorChooserPanelParser;
import org.aswing.colorchooser.AbstractColorChooserPanel;
import org.aswing.JColorChooser;

/**
 * Parses {@link org.aswing.JColorChooser} level elements.
 * 
 * @author Dina Nasy
 */
class org.aswing.awml.component.ColorChooserParser extends AbstractColorChooserPanelParser
{
		
	public function ColorChooserParser(Void) {
		super();
	}
	
    public function parse(awml:XMLNode, colorChooser:JColorChooser, namespace:AwmlNamespace) {
    	
        colorChooser = super.parse(awml, colorChooser, namespace);
        
        return colorChooser;
	}

    private function parseChild(awml:XMLNode, nodeName:String, colorChooser:JColorChooser, namespace:AwmlNamespace):Void {
    	super.parseChild(awml, nodeName, colorChooser, namespace);
    	
    	if (nodeName == AwmlConstants.NODE_TAB) {
    		var tab:AwmlTabInfo = AwmlParser.parse(awml, null, namespace);
    		if (tab != null && AbstractColorChooserPanel(tab.component) != null) {
    			colorChooser.addChooserPanel(tab.title, AbstractColorChooserPanel(tab.component));	
    		}	
    	}
    }

	private function getClass(Void):Function {
    	return JColorChooser;	
    }

}
