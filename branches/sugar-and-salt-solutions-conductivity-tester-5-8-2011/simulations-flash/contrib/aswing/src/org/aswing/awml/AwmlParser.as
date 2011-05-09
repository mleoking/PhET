/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.ParserFactory;
import org.aswing.util.List;
import org.aswing.util.StringUtils;
import org.aswing.util.Vector;
import org.aswing.awml.XmlUtils;
import org.aswing.util.HashMap;

/**
 * Provides AWML parsing routines. Manages parsing process by calling proper parsers for appropriate
 * components, layouts, borders and etc. described in AWML document.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.AwmlParser {
	
	/** 
	 * New component with the existed ID will be appended. Existed component 
	 * with the same ID will stay. 
	 */
	public static var STRATEGY_APPEND:Number = 0;
	/**
	 * New component with the existed ID will replace old one. Old one will be 
	 * destroyed.
	 */
	public static var STRATEGY_REPLACE:Number = 1;
	/**
	 * New component with the existed ID will be ignored. 
	 */
	public static var STRATEGY_IGNORE:Number = 2; 
	/**
	 * In new component will have already existed ID Exception will be thrown. 
	 */
	public static var STRATEGY_EXCEPTION:Number = 3; 
	
	private static var ATTR_PARSER_NAME:String = "parser-name";
	private static var ATTR_PARSER_CLASS:String = "parser-class";
	
	private static var listenerIDs:List;
	private static var listeners:List;
	private static var properties:HashMap;
	private static var parseStrategy:Number = STRATEGY_APPEND;
		
	/**
	 * Sets new component parsing strategy.
	 * Specifies how parser should behave if new component
	 * has ID already existed in the namespace.
	 * 
	 * @param strategy the new strategy
	 * @see #STRATEGY_APPEND
	 * @see #STRATEGY_REPLACE
	 * @see #STRATEGY_IGNORE
	 * @see #STRATEGY_EXCEPTION
	 */
	public static function setParsingStrategy(strategy:Number):Void {
		parseStrategy = strategy;
	}

	/**
	 * Returns currently configured component parsing strategy.
	 * Specifies how parser should behave if new component
	 * has ID already existed in the namespace.
	 * 
	 * @return component add strategy 
	 * @see #STRATEGY_APPEND
	 * @see #STRATEGY_REPLACE
	 * @see #STRATEGY_IGNORE
	 * @see #STRATEGY_EXCEPTION
	 */
	public static function getParsingStrategy(Void):Number {
		return parseStrategy;	
	}
		
	/**
	 * parseAll(awml:XMLNode)<br>
	 * parseAll(awml:String)<br>
	 * 
	 * Parses passed-in <code>awml</code> document.
	 * 
	 * @param awml the AWML document to parse. Can be both <code>Strig</code> 
	 * and <code>XMLNode</code>.
	 */
	public static function parseAll(awml):Void {
		var awmlXML:XMLNode = getAwmlXML(awml);
		if (AwmlUtils.getNodeName(awmlXML) == AwmlConstants.NODE_ASWING) {
			for (var i = 0; i < awmlXML.childNodes.length; i++) {
				parse(awmlXML.childNodes[i]);	
			}	
		} else {
			parse(awmlXML);	
		}
	}		
		
	/**
	 * parse(awml:XMLNode, instance)<br>
	 * parse(awml:String, instance)<br>
	 * parse(awml:XMLNode)<br>
	 * parse(awml:String)<br>
	 * 
	 * Parses passed <code>awml</code> document and initializes <code>instance</code> with appropriate
	 * properties and sub-elements. If no <code>instance</code> is specified new object is
	 * created with type based on top element from the <code>awml</code> document.
	 * 
	 * @param awml the AWML document to parse. Can be both <code>Strig</code> 
	 * and <code>XMLNode</code>
	 * @param instance (optional) the object to initialize with AWML data
	 * @param namespace (optional) the namespace contained current instance
	 * @return instance initialized with AWML data. 
	 */
	public static function parse(awml, instance, namespace:AwmlNamespace) {
		var awmlXML:XMLNode = getAwmlXML(awml);
		
		// recognize parser
		var parserName:String = AwmlUtils.getNodeName(awmlXML);
		parserName = XmlUtils.getAttributeAsString(awmlXML, ATTR_PARSER_NAME, parserName);
		
		// get parser instance
		var parserClass:Function = XmlUtils.getAttributeAsClass(awmlXML, ATTR_PARSER_CLASS, null);
		var parser:AbstractParser = (parserClass == null) ? ParserFactory.get(parserName) : new parserClass();
		
		// parse
		instance = parser.parse(awmlXML, instance, namespace);
		return instance;	
	}
	
	/**
	 * Checks for passed-in argument type and converts into <code>XMLNode</code>
	 * id required.
	 */
	private static function getAwmlXML(awml):XMLNode {
		return ( (awml instanceof XMLNode) ? awml : AwmlUtils.strToXml(awml) );
	}

	/**
	 * Checks if ID's list is created and returns it.
	 */
	private static function getListenerIDs(Void):List {
		if (listenerIDs == null) {
			listenerIDs = new Vector();	
		}	
		return listenerIDs;
	}
	
	/**
	 * Checks if listener's list is created and returns it.
	 */
	private static function getListeners(Void):List {
		if (listeners == null) {
			listeners = new Vector();	
		}	
		return listeners;
	}

	/**
	 * Defines new property map.
	 * 
	 * @param props the new property map
	 */
	public static function setProperties(props:HashMap):Void {
		properties = props;	
	}

	/**
	 * Checks if property map is existed, creates it (if necessary) and returns it.
	 * 
	 * @return the property map
	 */
	public static function getProperties(Void):HashMap {
		if (properties == null) {
			properties = new HashMap();	
		}	
		return properties;
	}
	
	/**
	 * addEventListener(id:String, object:Object)<br>
	 * addEventListener(id:String, class:Function)<br>
	 * <p>Registers object's instance or class as event listener within parser and
	 * associates it with the specified <code>id</code>.
	 * @param id the <code>id</code> of the instance or class to refer it from the AWML
	 * @param listener the reference to the class or object's instance  
	 */
	public static function addEventListener(id:String, listener:Object):Void {
		getListenerIDs().append(id);
		getListeners().append(listener);
	} 
	
	/**
	 * Returns registered AWML event listener by the <code>id</code> associated with.
	 * @param id the id of the listener to get
	 * @return the listener with the specified <code>id</code>
	 */
	public static function getEventListener(id:String):Object {
		return getListeners().get(getListenerIDs().indexOf(id));
	}
	
	/**
	 * removeEventListener(id:String)<br>
	 * removeEventListener(object:Object)<br>
	 * removeEventListener(class:Function)<br>
	 * 
	 * <p>Unregister AWML event listener by specified <code>id</code> or reference to class
	 * or object's instance
	 * @param idOrListener the <code>id</code> or reference to the instance or class to remove
	 */
	public static function removeEventListener(idOrListener:Object):Void {
		var index:Number = (StringUtils.isString(idOrListener)) ? 
			getListenerIDs().indexOf(idOrListener) : getListeners().indexOf(idOrListener);
			
		if (index != -1) {
			getListenerIDs().removeAt(index);
			getListeners().removeAt(index);
		}
	}
		
	/**
	 * Removes all registered AWML listeners.
	 */
	public static function removeAllEventListeners(Void):Void {
		getListenerIDs().clear();
		getListeners().clear();
	}
	
	/**
	 * Private constructor.
	 */
	private function AwmlParser(Void) {
		//
	}
	 
}
