/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.AbstractColorChooserPanelParser;
import org.aswing.colorchooser.JColorMixer;

/**
 * Parses {@link org.aswing.colorchooser.JColorMixer} level elements.
 * 
 * @author Dina Nasy
 */
class org.aswing.awml.component.ColorMixerParser extends AbstractColorChooserPanelParser
{
		
	public function ColorMixerParser(Void) {
		super();
	}
	
    public function parse(awml:XMLNode, colorMixer:JColorMixer, namespace:AwmlNamespace) {
    	
        colorMixer = super.parse(awml, colorMixer, namespace);
        
        return colorMixer;
	}

	private function getClass(Void):Function {
    	return JColorMixer;	
    }

}
