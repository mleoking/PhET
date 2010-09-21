package edu.colorado.phet.flexcommon {
import mx.core.Application;

public class FlexSimStrings {
    private static var document:XML;
    private static const debug:Boolean = false;

    public static function get(key:String, defaultString:String,formatArray : Array = null):String {
        if (document == null) {
            document = new XML(Application.application.parameters.internationalization);
        }
        var list:XMLList = document.descendants("string");

        for each (var item:XML in list) {
            if (item.attribute("key").toString() == key) {
                const translatedString:String = format(item.attribute("value").toString(),formatArray);
                if (debug) {
                    return "[Translated String] " + translatedString;
                } else {
                    return translatedString;
                }
            }
        }
        if (defaultString == null)
            return key;
        else {
            if (debug) {
                return "[Default String] " + format(defaultString,formatArray);
            } else {
                return format(defaultString,formatArray);
            }
        }
    }
    
    //Copied from CommonStrings.as
    // format a string, by replacing instances of {#} with the #'th element of the
	// argument array passed. braces not used for these purposes should be
	// escaped with a backslash: \{0\} will not be replaced with anything, and
	// will appear to be "{0}". double-backslash will also be replaced with a single one
	//
	// example:
	// format("This \{is\} a {0} of the {1} system", ["test", "emergency broadcast"])
	//     returns "This {is} a test of the emergency broadcast system"
	public static function format(pattern : String, args : Array) : String {
        if (args==null){
            return pattern;
        }
		// the string accumulated so far that will be returned
		var ret : String = "";
		
		// length of the pattern (in case you couldn't tell from the actual statement. Yes YOU!)
		var len : Number = pattern.length;
		
		// copy characters from pattern to ret, unless it is a brace
		for(var i : Number = 0; i < len; i++) {
			// character at this location
			var character : String = pattern.charAt(i);
			
			if(character == "\\" && i < len - 1 && (pattern.charAt(i+1) == "{" || pattern.charAt(i+1) == "}")) {
				// escaped brace
				i++;
				ret += pattern.charAt(i);
			} else if(character == "\\" && i < len - 1 && pattern.charAt(i+1) == "\\") {
				// escaped backslash
				i++;
				ret += "\\";
			} else if(character == "{") {
				// needs to be replaced
				
				// find the next ending brace afterwards
				var endIndex : Number = pattern.indexOf("}", i);
				
				if(endIndex == -1) {
					// if this doesn't exist, act like the backslash is a normal character
					ret += pattern.charAt(i);
				} else {
					// exists, so we parse the number
					var num : Number = new Number(pattern.substring(i+1, endIndex));
					
					// extract the string from the arguments
					var str : String = args[num];
					
					if(str != undefined) {
						// the string exists, so we append it
						ret += args[num];
						
						// set the index to the ending brace (it will be incremented before
						// anything else next iteration)
						i = endIndex;
					} else {
						// the string does not exist in the arguments. pretend like the initial
						// backslash is an ordinary character
						ret += pattern.charAt(i);
					}
				}
			} else {
				// any other character
				ret += pattern.charAt(i);
			}
		}
		
		return ret;
	}
}
}