/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.ParserFactory;
import org.aswing.awml.XmlUtils;

/**
 * Provides utility routines for AWML parser.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.AwmlUtils extends XmlUtils {
		

	/**
	 * Checks if passed <code>nodeName</code> belongs to component.
	 * 
	 * @param nodeName the node name to check
	 * @return <code>true</code> if belongs and <code>false</code> if not
	 */
	public static function isComponentNode(nodeName:String):Boolean {
		return ParserFactory.isTypeOf(nodeName, ParserFactory.COMPONENT);
	}

	/**
	 * Checks if passed <code>nodeName</code> belongs to component wrapper.
	 * 
	 * @param nodeName the node name to check
	 * @return <code>true</code> if belongs and <code>false</code> if not
	 */
	public static function isComponentWrapperNode(nodeName:String):Boolean {
		return ParserFactory.isTypeOf(nodeName, ParserFactory.COMPONENT_WRAPPER);
	} 

	/**
	 * Checks if passed <code>nodeName</code> belongs to menu item.
	 * 
	 * @param nodeName the node name to check
	 * @return <code>true</code> if belongs and <code>false</code> if not
	 */
	public static function isMenuItemNode(nodeName:String):Boolean {
		return ParserFactory.isTypeOf(nodeName, ParserFactory.MENU_ITEM);
	} 
	
	/**
	 * Checks if passed <code>nodeName</code> belongs to border.
	 * 
	 * @param nodeName the node name to check
	 * @return <code>true</code> if belongs and <code>false</code> if not
	 */
	public static function isBorderNode(nodeName:String):Boolean {
		return ParserFactory.isTypeOf(nodeName, ParserFactory.BORDER);
	} 

	/**
	 * Checks if passed <code>nodeName</code> belongs to layout.
	 * 
	 * @param nodeName the node name to check
	 * @return <code>true</code> if belongs and <code>false</code> if not
	 */
	public static function isLayoutNode(nodeName:String):Boolean {
		return ParserFactory.isTypeOf(nodeName, ParserFactory.LAYOUT);
	} 

	/**
	 * Checks if passed <code>nodeName</code> belongs to icon.
	 * 
	 * @param nodeName the node name to check
	 * @return <code>true</code> if belongs and <code>false</code> if not
	 */
	public static function isIconNode(nodeName:String):Boolean {
		return ParserFactory.isTypeOf(nodeName, ParserFactory.ICON);
	} 

	/**
	 * Checks if passed <code>nodeName</code> belongs to icon wrapper.
	 * 
	 * @param nodeName the node name to check
	 * @return <code>true</code> if belongs and <code>false</code> if not
	 */
	public static function isIconWrapperNode(nodeName:String):Boolean {
		return ParserFactory.isTypeOf(nodeName, ParserFactory.ICON_WRAPPER);
	} 

	/**
	 * Checks if passed <code>nodeName</code> belongs to reflection property.
	 * 
	 * @param nodeName the node name to check
	 * @return <code>true</code> if belongs and <code>false</code> if not
	 */
	public static function isPropertyNode(nodeName:String):Boolean {
		return ParserFactory.isTypeOf(nodeName, ParserFactory.PROPERTY);
	} 

	/**
	 * Checks if passed <code>nodeName</code> belongs to reflection argument.
	 * 
	 * @param nodeName the node name to check
	 * @return <code>true</code> if belongs and <code>false</code> if not
	 */
	public static function isArgumentNode(nodeName:String):Boolean {
		return ParserFactory.isTypeOf(nodeName, ParserFactory.ARGUMENT);
	} 	
	 
	 
}
