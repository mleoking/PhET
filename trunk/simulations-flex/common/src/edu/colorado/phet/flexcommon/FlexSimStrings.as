package edu.colorado.phet.flexcommon {

import mx.core.Application;

public class FlexSimStrings {
    private static var document:XML;

    public static function get(key:String, defaultString:String =null):String {
        if (document == null) {
            document = new XML(Application.application.parameters.internationalization);
        }
        var list:XMLList = document.descendants("string");

        for each (var item:XML in list) {
            if (item.attribute("key").toString() == key) {
                return item.attribute("value").toString();
            }
        }
        if (defaultString==null)
            return key;
        else
            return defaultString;
    }
}
}