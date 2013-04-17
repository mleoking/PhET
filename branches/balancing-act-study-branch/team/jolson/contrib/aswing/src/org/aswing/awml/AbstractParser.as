/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.awml.AwmlEventListenerInfo;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.ConversionUtils;
import org.aswing.EventDispatcher;
import org.aswing.util.HashMap;
import org.aswing.util.StringUtils;

/**
 * Provides core utility methods for AWML parsers.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.AbstractParser {
	
	/**
	 * Temporary replacement for "{" escaped symbol.
	 */
	private static var REPLACE_OPEN_BRACKET:String = String.fromCharCode(0x01);

	/**
	 * Temporary replacement for "}" escaped symbol.
	 */
	private static var REPLACE_CLOSE_BRACKET:String = String.fromCharCode(0x02);

	/**
	 * Temporary replacement for "}" escaped symbol.
	 */
	private static var REPLACE_SLASH:String = String.fromCharCode(0x03);
	
	
	/**
	 * Private constructor.
	 */
	private function AbstractParser(Void) {
		//
	}
	
	/**
	 * Parses AWML node and its children.
	 * <p>
	 * Abstract method. Need to be overridden.
	 * 
	 * @param awml the AWML node to parse
	 * @param instance the AsWing object instance to initialize with AWML data
	 * If instance is undefined new instance based on AWML node name
	 * will be created
	 * @param namespace (optional) the namespace contained current element
	 * @return initialized instance
	 */
	public function parse(awml:XMLNode, instance, namespace:AwmlNamespace) {
		
		for (var i = 0; i < awml.childNodes.length; i++) {
			parseChild(awml.childNodes[i], AwmlUtils.getNodeName(awml.childNodes[i]), instance, namespace);	
		}
		
		return instance;
	}

	/**
	 * Parses AWML children nodes of the AWML object. 
	 * <p>
	 * Abstract method. Need to be overridden.
	 * 
	 * @param awml the child AWML node
	 * @param nodeName the child AWML node name
	 * @param instance the AsWing object instance to initialize with data
	 * @param namespace (optional) the namespace contained current element
	 * from child AWML elements 
	 */
	private function parseChild(awml:XMLNode, nodeName:String, instance, namespace:AwmlNamespace):Void {
		//
	}

	/**
	 * getAttributeAsString(node:XMLNode, attribute:String, defaultValue:String)<br>
	 * getAttributeAsString(node:XMLNode, attribute:String) defaultValue default to empty string<br>
	 * 
	 * Gets string representation of the <code>attribute</code> value from the passed <code>node</code>.
	 * If requested attribute doesn't exist returns <code>defaultValue</code>.
	 * 
	 * @param node the XML node to get the attribute from
	 * @param attribute the attribute to get
	 * @param defaultValue the default value to return if specified attribute doestn't exist
	 * @return string value of the specified attribute for the given XML node
	 */
	private function getAttributeAsString(node:XMLNode, attribute:String, defaultValue:String):String {
		return ConversionUtils.toString(getNodeAttribute(node, attribute), defaultValue);
	}
	
	/**
	 * getAttributeAsNumber(node:XMLNode, attribute:String, defaultValue:Number)<br>
	 * getAttributeAsNumber(node:XMLNode, attribute:String) defaultValue default to <code>0</code><br>
	 * 
	 * Gets numeric representation of the <code>attribute</code> value from the passed <code>node</code>.
	 * If requested attribute doesn't exist returns <code>defaultValue</code>.
	 * 
	 * @param node the XML node to get the attribute from
	 * @param attribute the attribute to get
	 * @param defaultValue the default value to return if specified attribute doestn't exist
	 * @return numeric value of the specified attribute for the given XML node
	 */
	private function getAttributeAsNumber(node:XMLNode, attribute:String, defaultValue:Number):Number {
		return ConversionUtils.toNumber(getNodeAttribute(node, attribute), defaultValue);
	}

	/**
	 * getAttributeAsBoolean(node:XMLNode, attribute:String, defaultValue:Boolean)<br>
	 * getAttributeAsBoolean(node:XMLNode, attribute:String) defaultValue default to <code>false</code><br>
	 * 
	 * Gets boolean representation of the <code>attribute</code> value from the passed <code>node</code>.
	 * If requested attribute doesn't exist returns <code>defaultValue</code>.
	 * 
	 * @param node the XML node to get the attribute from
	 * @param attribute the attribute to get
	 * @param defaultValue the default value to return if specified attribute doestn't exist
	 * @return boolean value of the specified attribute for the given XML node
	 */
	private function getAttributeAsBoolean(node:XMLNode, attribute:String, defaultValue:Boolean):Boolean {
		return ConversionUtils.toBoolean(getNodeAttribute(node, attribute), defaultValue);
	}

	/**
	 * getAttributeAsArray(node:XMLNode, attribute:String, defaultValue:Array)<br>
	 * getAttributeAsArray(node:XMLNode, attribute:String) defaultValue default to <code>[]</code> (empty array)<br>
	 * 
	 * Gets array representation of the <code>attribute</code> value from the passed <code>node</code>.
	 * Splits attribute value on tokens using space dilimeter.
	 * If requested attribute doesn't exist returns <code>defaultValue</code>.
	 * 
	 * @param node the XML node to get the attribute from
	 * @param attribute the attribute to get
	 * @param defaultValue the default value to return if specified attribute doestn't exist
	 * @return array representation of the specified attribute for the given XML node
	 */
	private function getAttributeAsArray(node:XMLNode, attribute:String, defaultValue:Array):Array {
		return ConversionUtils.toArray(getNodeAttribute(node, attribute), defaultValue);
	}

	/**
	 * getAttributeAsMovieClip(node:XMLNode, attribute:String, defaultValue:MovieClip)<br>
	 * getAttributeAsMovieClip(node:XMLNode, attribute:String) defaultValue default to 
	 * org.aswing.ASWingUtils#getRootMovieClip<br>
	 * 
	 * Gets MovieClip using <code>eval</code> of the <code>attribute</code> value 
	 * from the passed <code>node</code>. If requested attribute doesn't exist 
	 * returns <code>defaultValue</code>.
	 * 
	 * @param node the XML node to get the attribute from
	 * @param attribute the attribute to get
	 * @param defaultValue the default value to return if specified attribute doestn't exist
	 * @return MovieClip value of the specified attribute for the given XML node
	 */
	private function getAttributeAsMovieClip(node:XMLNode, attribute:String, defaultValue:MovieClip):MovieClip {
		return ConversionUtils.toMovieClip(getNodeAttribute(node, attribute), defaultValue);
	}

	/**
	 * getAttributeAsClass(node:XMLNode, attribute:String, defaultValue:Function)<br>
	 * getAttributeAsClass(node:XMLNode, attribute:String) defaultValue default <code>null</code> 
	 * 
	 * Gets reference to the class constructor through {@code Reflection} API using 
	 * <code>attribute</code> value from the passed <code>node</code> as a class name. 
	 * If requested attribute doesn't exist returns <code>defaultValue</code>.
	 * 
	 * @param node the XML node to get the attribute from
	 * @param attribute the attribute to get
	 * @param defaultValue the default value to return if specified attribute doestn't exist
	 * @return Reference to the class constructor represented the specified attribute 
	 * for the given XML node
	 */
	private function getAttributeAsClass(node:XMLNode, attribute:String, defaultValue:Function):Function {
		return ConversionUtils.toClass(getNodeAttribute(node, attribute), defaultValue);
	}
	
	/**
	 * Gets array of the event listener info objects from the specified attribute.
	 * Event listener info consists from listener ID registered within {@link AwmlManager} 
	 * and its method name which must be called to handle the event. 
	 * 
	 * @param node the XML node to get the event listener info from
	 * @param attribute the attribute to get
	 * @return array of the {@link AwmlEventInfo} object instances
	 */
	private function getAttributeAsEventListenerInfos(node:XMLNode, attribute:String):Array {
		
		var values:Array = ConversionUtils.toArray(getNodeAttribute(node, attribute));
		var results:Array = new Array();
		
		for (var i = 0; i < values.length; i++) {
			var info:AwmlEventListenerInfo = new AwmlEventListenerInfo();
			var value:String = StringUtils.trimOuter(values[i]);
			
			// split on listener and method
			var p:Number = value.lastIndexOf(".");
			if (p != -1) {
				info.listener = value.substring(0, p);
				info.method =  value.substring(p + 1);
				// remove brackets
				var b:Number = info.method.indexOf("(");
				if (b != -1) info.method = info.method.substring(0, b);
			} else {
				info.listener = value;
			}
			
			results.push(info);
		}
				
		return results;
	}

	/**
	 * getValueAsString(node:XMLNode, defaultValue:String)<br>
	 * getValueAsString(node:XMLNode) defaultValue default to empty string<br>
	 * 
	 * Gets string representation of the value from the passed <code>node</code>.
	 * If value doesn't exist returns <code>defaultValue</code>.
	 * 
	 * @param node the XML node to get the value from
	 * @param defaultValue the default value to return if node value doestn't exist
	 * @return string value of the specified node
	 */
	private function getValueAsString(node:XMLNode, defaultValue:String):String {
		return ConversionUtils.toString(getNodeValue(node), defaultValue);
	}
	
	/**
	 * getValueAsNumber(node:XMLNode, defaultValue:Number)<br>
	 * getValueAsNumber(node:XMLNode) defaultValue default to <code>0</code><br>
	 * 
	 * Gets numeric representation of the value from the passed <code>node</code>.
	 * If requested value doesn't exist returns <code>defaultValue</code>.
	 * 
	 * @param node the XML node to get the value from
	 * @param defaultValue the default value to return if specified value doestn't exist
	 * @return numeric value of the specified node.
	 */
	private function getValueAsNumber(node:XMLNode, defaultValue:Number):Number {
		return ConversionUtils.toNumber(getNodeValue(node), defaultValue);
	}

	/**
	 * getValueAsBoolean(node:XMLNode, defaultValue:Boolean)<br>
	 * getValueAsBoolean(node:XMLNode) defaultValue default to <code>false</code><br>
	 * 
	 * Gets boolean representation of the value from the passed <code>node</code>.
	 * If requested value doesn't exist returns <code>defaultValue</code>.
	 * 
	 * @param node the XML node to get the value from
	 * @param defaultValue the default value to return if specified value doestn't exist
	 * @return boolean value of the specified node
	 */
	private function getValueAsBoolean(node:XMLNode, defaultValue:Boolean):Boolean {
		return ConversionUtils.toBoolean(getNodeValue(node), defaultValue);
	}

	/**
	 * getValueAsMovieClip(node:XMLNode, defaultValue:MovieClip)<br>
	 * getValueAsMovieClip(node:XMLNode) defaultValue default to 
	 * org.aswing.ASWingUtils#getRootMovieClip<br>
	 * 
	 * Gets MovieClip using <code>eval</code> of the value from the passed <code>node</code>.
	 * If requested MovieClip doesn't exist returns <code>defaultValue</code>.
	 * 
	 * @param node the XML node to get the value from
	 * @param defaultValue the default value to return if specified value doestn't exist
	 * @return MovieClip value of the specified node
	 */
	private function getValueAsMovieClip(node:XMLNode, defaultValue:MovieClip):MovieClip {
		return ConversionUtils.toMovieClip(getNodeValue(node), defaultValue);
	}

	/**
	 * getValueAsClass(node:XMLNode, defaultValue:Function)<br>
	 * getValueAsClass(node:XMLNode) defaultValue default to <code>null</code> 
	 * 
	 * Gets reference to the class constructor through {@code Reflection} API using 
	 * value from the passed <code>node</code> as a class name. 
	 * If requested attribute doesn't exist returns <code>defaultValue</code>.
	 * 
	 * @param node the XML node to get the value from
	 * @param defaultValue the default value to return if specified value doestn't exist
	 * @return Reference to the class constructor represented the value 
	 * of the given XML node
	 */
	private function getValueAsClass(node:XMLNode, defaultValue:Function):Function {
		return ConversionUtils.toClass(getNodeValue(node), defaultValue);
	}

	/**
	 * Replaces the specified attribute's value with properties configured using
	 * AwmlParser#setProperties and returns it.
	 */
	private static function getNodeAttribute(node:XMLNode, attribute:String):String {
		return replaceProperties(node.attributes[attribute]);
	}

	/**
	 * Replaces the specified node's value with properties configured using
	 * AwmlParser#setProperties and returns it.
	 */
	private static function getNodeValue(node:XMLNode):String {
		return replaceProperties(node.nodeValue);
	}
	
	/**
	 * Replaces all properties in the specified string with the values from the
	 * passed-in property map.
	 */
	private static function replaceProperties(value:String, properties:HashMap):String {
		if (properties == undefined) properties = AwmlParser.getProperties();
		
		// if curly bracket is present in the string
		if (value != null && value.indexOf("{") != -1) {

			// replace escaped slash ("/") with char 0x03
			value = StringUtils.replace(value, "//", REPLACE_SLASH);

			// replace escaped curly brackets with char 0x01 (for "{") and 0x02 for (for "}"); 
			value = StringUtils.replace(value, "/{", REPLACE_OPEN_BRACKET);
			value = StringUtils.replace(value, "/}", REPLACE_CLOSE_BRACKET);
						
			// parse properties
			var pos1:Number = value.indexOf("{");
			var pos2:Number = value.indexOf("}", pos1 + 1);
			
			while (pos1 != -1 && pos2 != -1) {
				
				var name:String = value.substring(pos1+1, pos2);
				var property:String = properties.get(name);
				if (property == undefined) property = "";
				
				value = value.substring(0, pos1) + property + value.substring(pos2 + 1);
				
				pos1 = value.indexOf("{");
				pos2 = value.indexOf("}", pos1 + 1);
			}
			
			// replaces back 0x01 and 0x02 with unescaped curly brackets
			value = StringUtils.replace(value, REPLACE_OPEN_BRACKET, "{");
			value = StringUtils.replace(value, REPLACE_CLOSE_BRACKET, "}");
			value = StringUtils.replace(value, REPLACE_SLASH, "/");
		}
		
		return value;
	}

	/**
	 * Attaches specified event handlers using {@code AwmlEventListenerInfo} objects stored in
	 * the passed in <code>listenerInfos</code> array to the <code>dispatcher</code> event source.
	 * @param dispatcher the event source object
	 * @param eventName the event name to handle 
	 * @param listenerInfos the array of the {@code AwmlEventListenerInfo} objects to handle the event
	 */	
	private function attachEventListeners(dispatcher:EventDispatcher, eventName:String, listenerInfos:Array):Void {
		for (var i = 0; i < listenerInfos.length; i++) {
			attachEventListener(dispatcher, eventName, listenerInfos[i]);
		}
	}

	/**
	 * Attaches specified event handler using {@code AwmlEventListenerInfo} object to the 
	 * <code>dispatcher</code> event source.
	 * @param dispatcher the event source object
	 * @param eventName the event name to handle 
	 * @param listenerInfo the {@code AwmlEventListenerInfo} object to handle the event
	 */	
	private function attachEventListener(dispatcher:EventDispatcher, eventName:String, listenerInfo:AwmlEventListenerInfo):Void {
		var listener:Object = AwmlParser.getEventListener(listenerInfo.listener);
		if (listener != null) {
			if (listener[listenerInfo.method] != null) {
				dispatcher.addEventListener(eventName, listener[listenerInfo.method], listener);
			}
		}
	}
		
}
