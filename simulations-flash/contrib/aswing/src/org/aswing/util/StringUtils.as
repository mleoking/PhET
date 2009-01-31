/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * Utils for String
 */  
class org.aswing.util.StringUtils{
 	
 	/**
 	 * Returns value is a string type value.
 	 * with undefined or null value, false returned.
 	 */
 	public static function isString(value):Boolean{
 		if(value == undefined) return false;
 		return typeof(value) == "string" || value instanceof String;
 	}
 	
 	public static function castString(str):String{
 		if(str == undefined){
 			return str;
 		}
 		if(typeof(str) == "string"){
 			return String(str);
 		}else if(str instanceof String){
 			return str.toString();
 		}else{
 			return null;
 		}
 	}
 	
 	/**
 	 * replace oldString with newString in targetString
 	 */
 	public static function replace(targetString:String , oldString:String , newString:String):String{
 		return targetString.split(oldString).join(newString);
 	}
 	
 	/**
 	 * remove all the blankspace in targetString
 	 */
 	public static function trimAll(targetString:String):String{
 		return replace(targetString, " ", "");
 	}
 	
 	/**
 	 * remove only the blankspace on targetString's left
 	 */
 	public static function trimLeft(targetString:String):String{
 		var tempIndex:Number = 0;
 		var tempChar:String = "";
 		for(var i:Number=0 ; i<targetString.length ; i++){
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
 		var tempIndex:Number = targetString.length-1;
 		var tempChar:String = "";
 		for(var i:Number=targetString.length-1 ; i>=0 ; i--){
 			tempChar = targetString.charAt(i);
 			if(tempChar != " "){
 				tempIndex = i;
 				break;
 			}
 		}
 		return targetString.substring(0 , tempIndex+1);
 	}
 	
 	/**
 	 * Remove outer (left and right) blank spaces from the target string
 	 */
 	public static function trimOuter(targetString:String):String {
 		return trimLeft(trimRight(targetString));	
 	}
 	
 	public static function getCharsArray(targetString:String , hasBlankSpace:Boolean):Array{
 		var tempString:String = targetString;
		if(hasBlankSpace == false){
			tempString = trimAll(targetString);
		} 		
 		return tempString.split("");
 	}
 	
 	public static function startsWith(targetString:String, subString:String):Boolean {
 		return (targetString.indexOf(subString) == 0);	
 	}

 	public static function endsWith(targetString:String, subString:String):Boolean {
 		return (targetString.lastIndexOf(subString) == (targetString.length - subString.length));	
 	}
 	
 }
