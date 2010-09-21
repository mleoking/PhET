/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.component.PopupParser;
import org.aswing.Component;
import org.aswing.JWindow;
import org.aswing.LayoutManager;

/**
 * Parses {@link org.aswing.JWindow} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.WindowParser extends PopupParser {
	
	private static var ATTR_ACTIVE:String = "active";
	
	private static var ATTR_ON_WINDOW_ACTIVATED:String = "on-window-activated";
	private static var ATTR_ON_WINDOW_DEACTIVATED:String = "on-window-deactivated";
	private static var ATTR_ON_WINDOW_ICONIFIED:String = "on-window-iconified";
	private static var ATTR_ON_WINDOW_RESTORED:String = "on-window-restored";
	private static var ATTR_ON_WINDOW_MAXIMIZED:String = "on-window-maximized";
	
	/**
	 * Constructor.
	 */
	public function WindowParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, window:JWindow, namespace:AwmlNamespace) {

		window = super.parse(awml, window, namespace);
		
		// set active
		window.setActive(getAttributeAsBoolean(awml,  ATTR_ACTIVE, window.isActive()));
		
        // init events
        attachEventListeners(window, JWindow.ON_WINDOW_ACTIVATED, getAttributeAsEventListenerInfos(awml, ATTR_ON_WINDOW_ACTIVATED));
        attachEventListeners(window, JWindow.ON_WINDOW_DEACTIVATED, getAttributeAsEventListenerInfos(awml, ATTR_ON_WINDOW_DEACTIVATED));
        attachEventListeners(window, JWindow.ON_WINDOW_ICONIFIED, getAttributeAsEventListenerInfos(awml, ATTR_ON_WINDOW_ICONIFIED));
        attachEventListeners(window, JWindow.ON_WINDOW_RESTORED, getAttributeAsEventListenerInfos(awml, ATTR_ON_WINDOW_RESTORED));
        attachEventListeners(window, JWindow.ON_WINDOW_MAXIMIZED, getAttributeAsEventListenerInfos(awml, ATTR_ON_WINDOW_MAXIMIZED));
		
		return window;
	}

	private function parseChild(awml:XMLNode, nodeName:String, window:JWindow, namespace:AwmlNamespace):Void {
		super.parseChild(awml, nodeName, window, namespace);
	}

	/**
	 * Appends <code>component</code> to the <code>window</code>.
	 * 
	 * @param window the window to add the component to
	 * @param component the component to add to the window
	 * @param index (optional) the index to insert component into window
	 */
	private function append(window:JWindow, component:Component, index:Number):Void {
		if (index != null) {
			window.getContentPane().insert(index, component);
		} else {
			window.getContentPane().append(component);
		}
	}
	
	/**
	 * Set <code>layout</code> to the <code>window</code>.
	 * 
	 * @param window the window to set layout
	 * @param layout the layout to set 
	 */
	private function setLayout(window:JWindow, layout:LayoutManager):Void {
		window.getContentPane().setLayout(layout);
	}
	
    private function getClass(Void):Function {
    	return JWindow;	
    }
    
}
