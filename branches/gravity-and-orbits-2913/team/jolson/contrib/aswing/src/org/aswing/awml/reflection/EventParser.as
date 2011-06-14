/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlEventListenerInfo;
import org.aswing.EventDispatcher;
import org.aswing.util.StringUtils;
import org.aswing.awml.AwmlParser;

/**
 *  Parses event AWML reflection element.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.reflection.EventParser extends AbstractParser {
	
	private static var ATTR_NAME:String = "name";
	private static var ATTR_LISTENER:String = "listener";
	private static var ATTR_METHOD:String = "method";
	
	/**
	 * Constructor.
	 */
	public function EventParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode, dispatcher:EventDispatcher) {
	
		super.parse(awml, dispatcher);
	
		// get event name
		var name:String = getAttributeAsString(awml, ATTR_NAME, null);
		if (name == null) return;
		
		// get listener id
		var listener:String = getAttributeAsString(awml, ATTR_LISTENER, null);
		
		// init event info
		var info:AwmlEventListenerInfo = getAttributeAsEventListenerInfo(awml, ATTR_METHOD);
		if (info.listener == null && listener != null) {
			info.listener = listener;
		}
		
		// check listener
		var listenerObj:Object = AwmlParser.getEventListener(info.listener);
		if (listenerObj == null) {
			trace("AWML Warning: Event listener object is not found for node: " + awml.toString());	
		} else if (listenerObj[info.method] == null) {
			trace("AWML Warning: Event handler method is not found for node: " + awml.toString());
		}
		
		// attach event listener
		attachEventListener(dispatcher, name, info); 
	}

	private function getAttributeAsEventListenerInfo(node:XMLNode, attribute:String):AwmlEventListenerInfo {
		var value:String = node.attributes[attribute];
		if (value == null) return null;
		
		var info:AwmlEventListenerInfo = new AwmlEventListenerInfo();
		value = StringUtils.trimOuter(value);
		
		// split on listener and method
		var p:Number = value.lastIndexOf(".");
		if (p != -1) {
			info.listener = value.substring(0, p);
			info.method =  value.substring(p + 1);
		} else {
			info.method = value;
		}
		
		// remove brackets
		var b:Number = info.method.indexOf("(");
		if (b != -1) info.method = info.method.substring(0, b);
		
		return info;
	}

}