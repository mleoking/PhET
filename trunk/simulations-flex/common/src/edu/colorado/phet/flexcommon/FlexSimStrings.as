package edu.colorado.phet.flexcommon {
import edu.colorado.phet.flashcommon.StringUtils;

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

    public static function format(pattern : String, args : Array) : String {
        return StringUtils.format( pattern, args );
    }
    
    
}
}