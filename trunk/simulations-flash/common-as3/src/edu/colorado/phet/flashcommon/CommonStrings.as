package edu.colorado.phet.flashcommon {

import flash.display.LoaderInfo;

public class CommonStrings {
    private static var document:XML = null;

    public static function init( li:LoaderInfo ):void {
        initDocument( new XML( li.parameters.internationalization ) );
    }

    public static function initDocument( _document:XML ):void {
        document = _document;
        trace( "doc: " );
        trace( document );
        trace( document == null );
        trace( document == undefined );
        trace( document.length() );
    }

    public static function get( key:String, defaultString:String, formatArray:Array = null ):String {
        var value:String;
        if ( document == undefined || document == null ) {
            value = defaultString;
        }
        else {
            var list:XMLList = document.descendants( "string" );

            for each ( var item:XML in list ) {
                if ( item.attribute( "key" ).toString() == key ) {
                    value = item.attribute( "value" ).toString();
                    break;
                }
            }
        }

        if ( formatArray != null ) {
            return StringUtils.format( value, formatArray );
        }

        return value;
    }
}
}