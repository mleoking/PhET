/*
 Copyright aswing.org, see the LICENCE.txt.
*/
package org.aswing.util
{
import flash.utils.getDefinitionByName;
import flash.utils.getQualifiedClassName;
import flash.net.registerClassAlias;
import flash.utils.ByteArray;


public class ObjectUtils
{
	/**
	 * Deep clone object using thiswind@gmail.com 's solution
	 */
	public static function baseClone(source:*):*{
		var typeName:String = getQualifiedClassName(source);
        var packageName:String = typeName.split("::")[1];
        var type:Class = Class(getDefinitionByName(typeName));

        registerClassAlias(packageName, type);
        
        var copier:ByteArray = new ByteArray();
        copier.writeObject(source);
        copier.position = 0;
        return copier.readObject();
	}
	
	/**
	 * Checks wherever passed-in value is <code>String</code>.
	 */
	public static function isString(value:*):Boolean {
		return ( typeof(value) == "string" || value is String );
	}
	
	/**
	 * Checks wherever passed-in value is <code>Number</code>.
	 */
	public static function isNumber(value:*):Boolean {
		return ( typeof(value) == "number" || value is Number );
	}

	/**
	 * Checks wherever passed-in value is <code>Boolean</code>.
	 */
	public static function isBoolean(value:*):Boolean {
		return ( typeof(value) == "boolean" || value is Boolean );
	}

	/**
	 * Checks wherever passed-in value is <code>Function</code>.
	 */
	public static function isFunction(value:*):Boolean {
		return ( typeof(value) == "function" || value is Function );
	}

	
}
}