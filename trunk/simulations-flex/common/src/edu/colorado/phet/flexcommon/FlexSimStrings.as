package edu.colorado.phet.flexcommon {
import mx.core.Application;

public class FlexSimStrings {
    private static var document:XML;
    private static const debug:Boolean = false;

    public static function get(key:String, defaultString:String = null):String {
        if (document == null) {
            document = new XML(Application.application.parameters.internationalization);
        }
        var list:XMLList = document.descendants("string");

        for each (var item:XML in list) {
            if (item.attribute("key").toString() == key) {
                if (debug) {
                    return "[Translated String] " + item.attribute("value").toString();
                } else {
                    return item.attribute("value").toString();
                }
            }
        }
        if (defaultString == null)
            return key;
        else {
            if (debug) {
                return "[Default String] " + defaultString;
            } else {
                return defaultString;
            }
        }
    }
}
}