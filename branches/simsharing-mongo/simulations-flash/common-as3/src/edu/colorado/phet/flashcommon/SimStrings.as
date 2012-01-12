package edu.colorado.phet.flashcommon {

import flash.display.LoaderInfo;

public class SimStrings {
    private static var document: XML;

    public static function init( li: LoaderInfo ): void {
        initDocument( new XML( li.parameters.internationalization ) );
    }

    public static function initDocument( _document: XML ): void {
        document = _document;
    }

    public static function get( key: String, defaultString: String, formatArray: Array = null ): String {
        var value: String = defaultString;
        if ( document != null ) {
            var list: XMLList = document.descendants( "string" );

            for each ( var item: XML in list ) {
                if ( item.attribute( "key" ).toString() == key ) {
                    value = item.attribute( "value" ).toString();
                    break;
                }
            }
        }

        if ( formatArray != null ) {
            return StringUtils.format( value, formatArray );
        }
        else {
            return value;
        }
        //Uncomment this and comment the above for simple long string testing
//        return defaultString+"x"+defaultString;
    }

    public static function stringExists( key:String ):Boolean {
        var defaultValue:String = "THIS IS A DEFAULT VALUE - IGNORE IT";
        return SimStrings.get( key, defaultValue ) != defaultValue
    }
}
}
