import org.aswing.util.Reflection;
import org.aswing.ASWingUtils;
/**
 * @author Igor Sadovskiy
 */
 
/**
 * Provides routines to convert values from <code>String</code> to another
 * data types.
 */ 
class org.aswing.awml.ConversionUtils {

	/**
	 * toObject(value:Object, defaultValue:Object)
	 * toObject(value:Object)
	 * 
	 * Converts specified value to <code>Object</code>. If converted value is <code>undefined</code>
	 * returns <code>defaultValue</code>. If <code>defaultValue</code> is undefined
	 * returns <code>null</code>.
	 * 
	 * @param value the value to convert
	 * @param defaultValue the default value
	 * @return converted object value 
	 */
	public static function toObject(value, defaultValue:Object):Object {
		return (value !== undefined) ? value : (defaultValue !== undefined) ? defaultValue : null;	
	}

	/**
	 * toString(value:Object, defaultValue:String)
	 * toString(value:Object)
	 * 
	 * Converts specified value to <code>Number</code>. If converted value is <code>NaN</code>
	 * returns <code>defaultValue</code>. If <code>defaultValue</code> is undefined
	 * returns empty string.
	 * 
	 * @param value the value to convert
	 * @param defaultValue the default value
	 * @return converted string value 
	 */
	public static function toString(value:Object, defaultValue:String):String {
		var result:String = value.toString();
		return (result != null) ? result : (defaultValue !== undefined) ? defaultValue : "";	
	}
	
	/**
	 * toNumber(value:Object, defaultValue:Number)
	 * toNumber(value:Object)
	 * 
	 * Converts specified value to <code>Number</code>. If converted value is <code>NaN</code>
	 * returns <code>defaultValue</code>. If <code>defaultValue</code> is undefined
	 * returns <code>0</code>.
	 * 
	 * @param value the value to convert
	 * @param defaultValue the default value
	 * @return converted numeric value 
	 */
	public static function toNumber(value:Object, defaultValue:Number):Number {
		var result:Number = Number(value.toString());
		return (!isNaN(result)) ? result : (defaultValue !== undefined) ? defaultValue : 0;	
	}

	/**
	 * toBoolean(value:Object, defaultValue:Boolean)
	 * toBoolean(value:Object) 
	 *
	 * Converts specified value to <code>Boolean</code>. If converted value is <code>undefined</code>
	 * returns <code>defaultValue</code>. If <code>defaultValue</code> is undefined
	 * returns <code>false</code>.
	 * 
	 * @param value the value to convert
	 * @param defaultValue the default value
	 * @return converted boolean value 
	 */
	public static function toBoolean(value:Object, defaultValue:Boolean):Boolean {
		var str:String = value.toString().toLowerCase();
		var result:Boolean = (str == "true") ? true : (str == "false") ? false : null;
		return (result != null) ? result : (defaultValue !== undefined) ? defaultValue : false;	
	}

	/**
	 * toMovieClip(value:Object, defaultValue:MovieClip)
	 * toMovieClip(value:Object)
	 * 
	 * Converts specified value to <code>MovieClip</code>. If converted value 
	 * is <code>undefined</code> returns <code>defaultValue</code>. 
	 * If <code>defaultValue</code> is undefined returns value of
	 * ASWingUtils#getRootMovieClip.
	 * 
	 * @param value the value to convert
	 * @param defaultValue the default value
	 * @return converted movie clip
	 */
	public static function toMovieClip(value:Object, defaultValue:MovieClip):MovieClip {
		var result:MovieClip = eval(value.toString());
		return (result != null) ? result : (defaultValue !== undefined) ? defaultValue : ASWingUtils.getRootMovieClip();	
	}

	/**
	 * toClass(value:Object, defaultValue:Function)
	 * toClass(value:Object)
	 * 
	 * Converts specified value to reference to the class constructor.
	 * If converted value is <code>undefined</code> returns <code>defaultValue</code>. 
	 * If <code>defaultValue</code> is undefined returns <code>null</code>.
	 * 
	 * @param value the value to convert
	 * @param defaultValue the default value
	 * @return converted reference to the class constructor
	 */
	public static function toClass(value:Object, defaultValue:Function):Function {
		var result:Function = Reflection.getClass(value.toString());
		return (result != null) ? result : (defaultValue !== undefined) ? defaultValue : null;	
	}
	
	/**
	 * toArray(value:Object, defaultValue:Array, delimiter:String)
	 * toArray(value:Object, defaultValue:Array)
	 * toArray(value:Object)
	 * 
	 * Converts specified value to <code>MovieClip</code> using passed in 
	 * delimeter. If <code>delimiter</code> argument is omitted, " " is used.
	 * If converted value is <code>undefined</code> returns <code>defaultValue</code>. 
	 * If <code>defaultValue</code> is undefined returns empty array. 
	 * 
	 * @param value the value to convert
	 * @param defaultValue the default value
	 * @param delimiter the delimiter to split string on array elements
	 * @return converted array
	 */
	public static function toArray(value:Object, defaultValue:Array, delimiter:String):Array {
		if (delimiter == undefined) delimiter = " ";
		var result:Array = value.toString().split(delimiter);
		return (result != null) ? result : (defaultValue !== undefined) ? defaultValue : [];	
	}
	

	/**
	 * Private constructor.
	 */	
	private function ConversionUtils() 
	{
	}
}