/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * Utils about Object
 */
class org.aswing.util.ObjectUtils{
	
	public static function baseClone(existObject:Object):Object{
		if(existObject instanceof Object){
			var newObject:Object = new Object();
			for(var i:String in existObject){
				if(existObject[i] instanceof Object){
					newObject[i] = new Object();
					newObject[i] = baseClone(existObject[i]);
				}else{
					newObject[i] = existObject[i];
				}
			}
			return newObject;
		}else{
			return existObject;
		}
	}
	
	/**
	 * Checks wherever passed-in value is <code>String</code>.
	 */
	public static function isString(value):Boolean {
		return ( typeof(value) == "string" || value instanceof String );
	}
	
	/**
	 * Checks wherever passed-in value is <code>Number</code>.
	 */
	public static function isNumber(value):Boolean {
		return ( typeof(value) == "number" || value instanceof Number );
	}

	/**
	 * Checks wherever passed-in value is <code>Boolean</code>.
	 */
	public static function isBoolean(value):Boolean {
		return ( typeof(value) == "boolean" || value instanceof Boolean );
	}

	/**
	 * Checks wherever passed-in value is <code>Function</code>.
	 */
	public static function isFunction(value):Boolean {
		return ( typeof(value) == "function" || value instanceof Function );
	}

	/**
	 * Checks wherever passed-in value is <code>MovieClip</code>.
	 */
	public static function isMovieClip(value):Boolean {
		return ( typeof(value) == "movieclip" || value instanceof MovieClip );
	}
	
}
