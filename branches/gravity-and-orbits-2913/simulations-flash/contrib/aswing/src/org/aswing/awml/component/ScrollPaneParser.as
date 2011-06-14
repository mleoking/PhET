/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.component.ComponentParser;
import org.aswing.Component;
import org.aswing.JScrollBar;
import org.aswing.JScrollPane;
import org.aswing.overflow.JViewport;

/**
 * Parses {@link org.aswing.JScrollPane} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.ScrollPaneParser extends ComponentParser {
	
	private static var ATTR_HORIZONTAL_SCROLL_BAR_POLICY:String = "horizontal-scroll-bar-policy";
	private static var ATTR_VERTICAL_SCROLL_BAR_POLICY:String = "vertical-scroll-bar-policy";
	
	private static var ATTR_ON_ADJUSTMENT_CHANGED:String = "on-adjustment-changed";
	private static var ATTR_ON_VIEW_PORT_CHANGED:String = "on-view-port-changed";
	
	private static var POLICY_NEVER:String = "never";
	private static var POLICY_AS_NEEDED:String = "as-needed";
	private static var POLICY_ALWAYS:String = "always";
	
	/**
	 * Constructor.
	 */
	public function ScrollPaneParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, pane:JScrollPane, namespace:AwmlNamespace) {
		
		pane = super.parse(awml, pane, namespace);
		
		// init scroll bar policies
		var hPolicy:String = getAttributeAsString(awml, ATTR_HORIZONTAL_SCROLL_BAR_POLICY);
		switch (hPolicy) {
			case POLICY_NEVER:
				pane.setHorizontalScrollBarPolicy(JScrollPane.SCROLLBAR_NEVER);
				break;
			case POLICY_AS_NEEDED:
				pane.setHorizontalScrollBarPolicy(JScrollPane.SCROLLBAR_AS_NEEDED);
				break;
			case POLICY_ALWAYS:
				pane.setHorizontalScrollBarPolicy(JScrollPane.SCROLLBAR_ALWAYS);
				break;
		}
				
		var vPolicy:String = getAttributeAsString(awml, ATTR_VERTICAL_SCROLL_BAR_POLICY);
		switch (vPolicy) {
			case POLICY_NEVER:
				pane.setVerticalScrollBarPolicy(JScrollPane.SCROLLBAR_NEVER);
				break;
			case POLICY_AS_NEEDED:
				pane.setVerticalScrollBarPolicy(JScrollPane.SCROLLBAR_AS_NEEDED);
				break;
			case POLICY_ALWAYS:
				pane.setVerticalScrollBarPolicy(JScrollPane.SCROLLBAR_ALWAYS);
				break;
		}
		
        // init events
        attachEventListeners(pane, JScrollPane.ON_ADJUSTMENT_VALUE_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_ADJUSTMENT_CHANGED));
        attachEventListeners(pane, JScrollPane.ON_VIEWPORT_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_VIEW_PORT_CHANGED));
		
		return pane;
	}

	private function parseChild(awml:XMLNode, nodeName:String, pane:JScrollPane, namespace:AwmlNamespace):Void {

		super.parseChild(awml, nodeName, pane, namespace);

		if (AwmlUtils.isComponentNode(nodeName)) {
			var component:Component = AwmlParser.parse(awml, null, namespace);
			if (component != null) pane.setView(component);
		} else if (nodeName == AwmlConstants.NODE_SCROLL_VIEW_PORT) {
			var viewport:JViewport = JViewport(pane.getViewport());
			if (viewport != null) {
				AwmlParser.parse(awml, viewport, namespace);
			}
		}
	}

    private function getClass(Void):Function {
    	return JScrollPane;	
    }

}
