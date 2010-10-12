package edu.colorado.phet.flashcommon {

import flash.display.LoaderInfo;

public class CommonStrings {
    private static var document: XML = null;
    private static var alreadyDebugged: Boolean = false;

    public static function init( li: LoaderInfo ): void {
        initDocument( new XML( li.parameters.commonStrings ) );
    }

    public static function initDocument( _document: XML ): void {
        document = _document;
    }

    public static function get( key: String, defaultString: String, formatArray: Array = null ): String {
        if ( !alreadyDebugged ) {
            FlashCommon.getInstance().debug( "CommonStrings.initDocument:\n" + document.toString() );
            alreadyDebugged = true;
        }
        var value: String = defaultString;
        var changed: Boolean = false;
        if ( document != null ) {
            var list: XMLList = document.descendants( "string" );

            for each ( var item: XML in list ) {
                if ( item.attribute( "key" ).toString() == key ) {
                    value = item.attribute( "value" ).toString();
                    changed = true;
                    break;
                }
            }
        }
        else {
            FlashCommon.getInstance().debug( "Document was null in CommonStrings.get" );
        }
        if ( !changed ) {
            FlashCommon.getInstance().debug( "Failed to find the key: " + key + ", in the document in CommonStrings.get" );
        }

        if ( formatArray != null ) {
            return StringUtils.format( value, formatArray );
        }
        else {
            return value;
        }
    }
}
}