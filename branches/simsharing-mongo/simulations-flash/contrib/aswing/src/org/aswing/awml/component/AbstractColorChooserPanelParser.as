/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.component.ComponentParser;
import org.aswing.colorchooser.AbstractColorChooserPanel;

/**
 * Parses {@link org.aswing.colorchooser.AbstractColorChooserPanel} level elements.
 * 
 * @author Dina Nasy
 */
class org.aswing.awml.component.AbstractColorChooserPanelParser extends ComponentParser {
	
	private static var ATTR_ALPHA_SECTION_VISIBLE:String = "alpha-section-visible";
	private static var ATTR_HEX_SECTION_VISIBLE:String = "hex-section-visible";
	private static var ATTR_NO_COLOR_SECTION_VISIBLE:String = "no-color-section-visible";
	
	private static var ATTR_ON_COLOR_ADJUSTING:String = "on-color-adjusting";
	
	/**
	 * Private Constructor.
	 */
	private function AbstractColorChooserPanelParser(Void) {
		super();
	}

    public function parse(awml:XMLNode, colorChooser:AbstractColorChooserPanel, namespace:AwmlNamespace) {
    	
        colorChooser = super.parse(awml, colorChooser, namespace);
        
		colorChooser.setAlphaSectionVisible(getAttributeAsBoolean(awml, ATTR_ALPHA_SECTION_VISIBLE, colorChooser.isAlphaSectionVisible()));
		colorChooser.setHexSectionVisible(getAttributeAsBoolean(awml, ATTR_HEX_SECTION_VISIBLE, colorChooser.isHexSectionVisible()));
		colorChooser.setNoColorSectionVisible(getAttributeAsBoolean(awml, ATTR_NO_COLOR_SECTION_VISIBLE, colorChooser.isNoColorSectionVisible()));
		
		 // init events
        attachEventListeners(colorChooser, AbstractColorChooserPanel.ON_COLOR_ADJUSTING, getAttributeAsEventListenerInfos(awml, ATTR_ON_COLOR_ADJUSTING));
        attachEventListeners(colorChooser, AbstractColorChooserPanel.ON_STATE_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_STATE_CHANGED));

        return colorChooser;
	}

    private function parseChild(awml:XMLNode, nodeName:String, colorChooser:AbstractColorChooserPanel, namespace:AwmlNamespace):Void {

        super.parseChild(awml, nodeName, colorChooser, namespace);
        
        if (nodeName == AwmlConstants.NODE_SELECTED_COLOR) {
            var color:ASColor = AwmlParser.parse(awml);
            if (color != null) colorChooser.setSelectedColor(color);
        }   
    }   
	
}
