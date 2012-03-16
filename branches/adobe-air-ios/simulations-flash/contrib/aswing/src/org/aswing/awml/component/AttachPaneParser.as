/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.FloorPaneParser;
import org.aswing.JAttachPane;

/**
 * Parses {@link org.aswing.JAttachPane} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.AttachPaneParser extends FloorPaneParser {
	
	private static var ATTR_SYMBOL_ID:String = "symbol-id";
	
	private static var ATTR_ON_ATTACHED:String = "on-attached";
	
	/**
	 * Constructor.
	 */
	public function AttachPaneParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, pane:JAttachPane, namespace:AwmlNamespace) {
		
		pane = super.parse(awml, pane, namespace);
		
		// init columns
		pane.setPath(getAttributeAsString(awml, ATTR_SYMBOL_ID, pane.getPath()));
		
        // init events
        attachEventListeners(pane, JAttachPane.ON_ATTACHED, getAttributeAsEventListenerInfos(awml, ATTR_ON_ATTACHED));
		
		return pane;
	}
	
    private function getClass(Void):Function {
    	return JAttachPane;	
    }
	
}
