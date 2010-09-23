/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.util
{
public class StringUtils
{
	/**
 	 * Returns value is a string type value.
 	 * with undefined or null value, false returned.
 	 */
 	public static function isString(value:*):Boolean{
 		return value is String;
 	}
 	
 	public static function castString(str:*):String{
 		return str as String;
 	}
 	
 	/**
 	 * replace oldString with newString in targetString
 	 */
 	public static function replace(targetString:String , oldString:String , newString:String):String{
 		return targetString.split(oldString).join(newString);
 	}
 	
 	/**
 	 * remove the blankspaces of left and right in targetString
 	 */
 	public static function trim(targetString:String):String{
 		return trimLeft(trimRight(targetString));
 	}
 	
 	/**
 	 * remove only the blankspace on targetString's left
 	 */
 	public static function trimLeft(targetString:String):String{
 		var tempIndex:int = 0;
 		var tempChar:String = "";
 		for(var i:int=0 ; i<targetString.length ; i++){
 			tempChar = targetString.charAt(i);
 			if(tempChar != " "){
 				tempIndex = i;
 				break;
 			}
 		}
 		return targetString.substr(tempIndex);
 	}
 	
 	/**
 	 * remove only the blankspace on targetString's right
 	 */
 	public static function trimRight(targetString:String):String{
 		var tempIndex:int = targetString.length-1;
 		var tempChar:String = "";
 		for(var i:int=targetString.length-1 ; i>=0 ; i--){
 			tempChar = targetString.charAt(i);
 			if(tempChar != " "){
 				tempIndex = i;
 				break;
 			}
 		}
 		return targetString.substring(0 , tempIndex+1);
 	}
 	
 	public static function getCharsArray(targetString:String , hasBlankSpace:Boolean):Array{
 		var tempString:String = targetString;
		if(hasBlankSpace == false){
			tempString = trim(targetString);
		} 		
 		return tempString.split("");
 	}
 	
 	public static function startsWith(targetString:String, subString:String):Boolean {
 		return (targetString.indexOf(subString) == 0);	
 	}

 	public static function endsWith(targetString:String, subString:String):Boolean {
 		return (targetString.lastIndexOf(subString) == (targetString.length - subString.length));	
 	}
 	
 	public static function isLetter(chars:String):Boolean{
 		if(chars == null || chars == ""){
 			return false;
 		}
 		for(var i:int=0; i<chars.length; i++){
 			var code:uint = chars.charCodeAt(i);
 			if(code < 65 || code > 122 || (code > 90 && code < 97)){
 				return false;
 			}
 		}
 		return true;
 	}
}
}