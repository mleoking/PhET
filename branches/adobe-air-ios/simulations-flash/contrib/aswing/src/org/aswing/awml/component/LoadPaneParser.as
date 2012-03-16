/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.FloorPaneParser;
import org.aswing.overflow.JLoadPane;

/**
 * Parses {@link org.aswing.overflow.JLoadPane} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.LoadPaneParser extends FloorPaneParser {
	
	private static var ATTR_URL:String = "url";
	
    private static var ATTR_ON_LOAD_START:String = "on-load-start";
    private static var ATTR_ON_LOAD_PROGRESS:String = "on-load-progress";
    private static var ATTR_ON_LOAD_COMPLETE:String = "on-load-complete";
    private static var ATTR_ON_LOAD_INIT:String = "on-load-init";
    private static var ATTR_ON_LOAD_ERROR:String = "on-load-error";
	
	
	/**
	 * Constructor.
	 */
	public function LoadPaneParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, pane:JLoadPane, namespace:AwmlNamespace) {
		
		pane = super.parse(awml, pane, namespace);
		
		// init columns
		pane.setPath(getAttributeAsString(awml, ATTR_URL, pane.getPath()));
		
        // init events
        attachEventListeners(pane, JLoadPane.ON_LOAD_START, getAttributeAsEventListenerInfos(awml, ATTR_ON_LOAD_START));
        attachEventListeners(pane, JLoadPane.ON_LOAD_PROGRESS, getAttributeAsEventListenerInfos(awml, ATTR_ON_LOAD_PROGRESS));
        attachEventListeners(pane, JLoadPane.ON_LOAD_COMPLETE, getAttributeAsEventListenerInfos(awml, ATTR_ON_LOAD_COMPLETE));
        attachEventListeners(pane, JLoadPane.ON_LOAD_INIT, getAttributeAsEventListenerInfos(awml, ATTR_ON_LOAD_INIT));
        attachEventListeners(pane, JLoadPane.ON_LOAD_ERROR, getAttributeAsEventListenerInfos(awml, ATTR_ON_LOAD_ERROR));
		
		return pane;
	}
	
    private function getClass(Void):Function {
    	return JLoadPane;	
    }
	
}
