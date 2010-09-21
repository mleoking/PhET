/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.ContainerParser;
import org.aswing.overflow.JPopup;

/**
 * Parses {@link org.aswing.overflow.JPopup} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.PopupParser extends ContainerParser {
	
	private static var ATTR_MODAL:String = "modal";
	private static var ATTR_PACK:String = "pack";
	
	private static var ATTR_ON_WINDOW_OPENED:String = "on-window-opened";
	private static var ATTR_ON_WINDOW_CLOSED:String = "on-window-closed";
	private static var ATTR_ON_WINDOW_CLOSING:String = "on-window-closing";
	
	/**
	 * Constructor.
	 */
	public function PopupParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, popup:JPopup, namespace:AwmlNamespace) {

		popup = super.parse(awml, popup, namespace);
		
		// set modal
		popup.setModal(getAttributeAsBoolean(awml, ATTR_MODAL, popup.isModal()));
		
		// pack window
		if (getAttributeAsBoolean(awml, ATTR_PACK, false)) { 
			popup.pack();
		}

        // init events
        attachEventListeners(popup, JPopup.ON_WINDOW_OPENED, getAttributeAsEventListenerInfos(awml, ATTR_ON_WINDOW_OPENED));
        attachEventListeners(popup, JPopup.ON_WINDOW_CLOSED, getAttributeAsEventListenerInfos(awml, ATTR_ON_WINDOW_CLOSED));
        attachEventListeners(popup, JPopup.ON_WINDOW_CLOSING, getAttributeAsEventListenerInfos(awml, ATTR_ON_WINDOW_CLOSING));
		
		return popup;
	}
		
	private function parseChild(awml:XMLNode, nodeName:String, popup:JPopup, namespace:AwmlNamespace):Void {
		super.parseChild(awml, nodeName, popup, namespace);
	}
		
    private function getClass(Void):Function {
    	return JPopup;	
    }
    
    private function getArguments(awml:XMLNode):Array {
    	return [getOwner(awml)];
    }    
	
}
