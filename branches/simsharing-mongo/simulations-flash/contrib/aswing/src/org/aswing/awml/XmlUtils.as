/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.ConversionUtils;

/**
 * Provides utility routines for XML parser.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.XmlUtils {
	
	/**
	 * Extracts pure node name from the passed XML element omitting namespace prefix.
	 * 
	 * @param xml the <code>XMLNode</code> to extract node name from
	 * @return extracted node name
	 */
	public static function getNodeName(xml:XMLNode):String {
		var idx:Number = xml.nodeName.indexOf(":");
		return (idx == -1) ? xml.nodeName : xml.nodeName.substr(idx + 1);
	}
	
	/**
	 * Searches the xml node for the child node with the specified name.
	 * 
	 * @param xml the parent XML node to search its children
	 * @param childName the child node namd to search
	 * @return found child node or <code>null</code>
	 */
	public static function getNodeChild(xml:XMLNode, childName:String):XMLNode {
		for (var i:Number = 0; i < xml.childNodes.length; i++) {
			if (getNodeName(xml.childNodes[i]) == childName) {
				return xml.childNodes[i];
			}	
		}
		return null;
	}
	
	/**
	 * Converts string represenration of the XML document to the XMLNode object.
	 * 
	 * @param str the string representation of the XML document to convert.
	 * @return XMLNode representation of the XML document.
	 */
	public static function strToXml(str:String):XMLNode {
        var xml:XML = new XML();
        xml.ignoreWhite = true;
        xml.parseXML(str);
        return xml.firstChild; 
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
	public static function getAttributeAsString(node:XMLNode, attribute:String, defaultValue:String):String {
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
	public static function getAttributeAsNumber(node:XMLNode, attribute:String, defaultValue:Number):Number {
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
	public static function getAttributeAsBoolean(node:XMLNode, attribute:String, defaultValue:Boolean):Boolean {
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
	public static function getAttributeAsArray(node:XMLNode, attribute:String, defaultValue:Array):Array {
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
	public static function getAttributeAsMovieClip(node:XMLNode, attribute:String, defaultValue:MovieClip):MovieClip {
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
	public static function getAttributeAsClass(node:XMLNode, attribute:String, defaultValue:Function):Function {
		return ConversionUtils.toClass(getNodeAttribute(node, attribute), defaultValue);
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
	public static function getValueAsString(node:XMLNode, defaultValue:String):String {
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
	public static function getValueAsNumber(node:XMLNode, defaultValue:Number):Number {
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
	public static function getValueAsBoolean(node:XMLNode, defaultValue:Boolean):Boolean {
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
	public static function getValueAsMovieClip(node:XMLNode, defaultValue:MovieClip):MovieClip {
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
	public static function getValueAsClass(node:XMLNode, defaultValue:Function):Function {
		return ConversionUtils.toClass(getNodeValue(node), defaultValue);
	}


	/**
	 * Returns string value of the specified attribute.
	 */
	private static function getNodeAttribute(node:XMLNode, attribute:String):String {
		return node.attributes[attribute];
	}

	/**
	 * Returns string value of the specified node.
	 */
	private static function getNodeValue(node:XMLNode):String {
		return node.nodeValue;
	}
	
}