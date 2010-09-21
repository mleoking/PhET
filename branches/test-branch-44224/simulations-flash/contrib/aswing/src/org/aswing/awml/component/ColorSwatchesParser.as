/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.AbstractColorChooserPanelParser;
import org.aswing.colorchooser.JColorSwatches;
import org.aswing.awml.AwmlUtils;
import org.aswing.Component;
import org.aswing.awml.AwmlParser;

/**
 * Parses {@link org.aswing.colorchooser.JColorSwatches} level elements.
 * 
 * @author Dina Nasy
 */
class org.aswing.awml.component.ColorSwatchesParser extends AbstractColorChooserPanelParser
{
		
	public function ColorSwatchesParser(Void) {
		super();
	}
	
    public function parse(awml:XMLNode, colorSwatches:JColorSwatches, namespace:AwmlNamespace) {
    	
        colorSwatches = super.parse(awml, colorSwatches, namespace);
        
        return colorSwatches;
	}

    private function parseChild(awml:XMLNode, nodeName:String, colorSwatches:JColorSwatches, namespace:AwmlNamespace):Void {

        super.parseChild(awml, nodeName, colorSwatches, namespace);
        
        if (AwmlUtils.isComponentNode(nodeName)) {
            var component:Component = AwmlParser.parse(awml, null, namespace);
            if (component != null) { 
            	colorSwatches.addComponentColorSectionBar(component);
            }
        }   
    }   
	
	private function getClass(Void):Function {
    	return JColorSwatches;	
    }

}
