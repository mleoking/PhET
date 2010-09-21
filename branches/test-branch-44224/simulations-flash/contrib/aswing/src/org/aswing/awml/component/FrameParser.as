/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.component.WindowParser;
import org.aswing.geom.Rectangle;
import org.aswing.Icon;
import org.aswing.JFrame;

/**
 * Parses {@link org.aswing.JFrame} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.FrameParser extends WindowParser {
	
	private static var ATTR_CLOSABLE:String = "closable";
	private static var ATTR_DRAGGABLE:String = "draggable";
	private static var ATTR_RESIZABLE:String = "resizable";
	private static var ATTR_TITLE:String = "title";
	private static var ATTR_STATE:String = "state";
	
	private static var ATTR_ON_ABILITY_CHANGED:String = "on-ability-changed";
	
	private static var STATE_NORMAL:String = "normal";
	private static var STATE_ICONIFIED:String = "iconified";
	private static var STATE_MAXIMIZED:String = "maximized";
	
	/**
	 * Constructor.
	 */
	public function FrameParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, frame:JFrame, namespace:AwmlNamespace) {
		
		frame = super.parse(awml, frame, namespace);
		
		// init closable
		frame.setClosable(getAttributeAsBoolean(awml, ATTR_CLOSABLE, frame.isClosable()));
		
		// init draggable
		frame.setDragable(getAttributeAsBoolean(awml, ATTR_DRAGGABLE, frame.isDragable()));
		
		// init resizable
		frame.setResizable(getAttributeAsBoolean(awml, ATTR_RESIZABLE, frame.isResizable()));
		
		// init title
		frame.setTitle(getAttributeAsString(awml, ATTR_TITLE, frame.getTitle()));
		
		// init state
		var state:String = getAttributeAsString(awml, ATTR_STATE);
		switch (state) {
			case STATE_ICONIFIED:
				frame.setState(JFrame.ICONIFIED);
				break;
			case STATE_MAXIMIZED:
				frame.setState(JFrame.MAXIMIZED);
				break;
			case STATE_NORMAL:
				frame.setState(JFrame.NORMAL);
				break;
		} 
		
        // init events
        attachEventListeners(frame, JFrame.ON_STATE_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_STATE_CHANGED));
        attachEventListeners(frame, JFrame.ON_ABILITY_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_ABILITY_CHANGED));
		
		return frame;
	}
	
	private function parseChild(awml:XMLNode, nodeName:String, frame:JFrame, namespace:AwmlNamespace):Void {

		super.parseChild(awml, nodeName, frame, namespace);

		if (nodeName == AwmlConstants.NODE_MAXIMIZED_BOUNDS) {
			var bounds:Rectangle = AwmlParser.parse(awml);
			if (bounds != null) frame.setMaximizedBounds(bounds);
		} else if (AwmlUtils.isIconNode(nodeName)) {
			var icon:Icon = AwmlParser.parse(awml);
			if (icon != null) frame.setIcon(icon);
		}
	}
	
    private function getClass(Void):Function {
    	return JFrame;	
    }
    	
}
