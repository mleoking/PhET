package edu.colorado.phet.flexcommon {
import edu.colorado.phet.flashcommon.StringUtils;

import mx.core.Application;

public class FlexSimStrings {
    private static var document: XML;
    private static const debug: Boolean = false;

    public static function get( key: String, defaultString: String, formatArray: Array = null ): String {
        if ( document == null ) {
            //Null check for when running locally/IntelliJ/Flash debug player
            document = new XML( Application.application == null ? null : Application.application.parameters.internationalization );
        }
        var list: XMLList = document.descendants( "string" );

        //When mixing arabic and western characters in Flash Player 9, the word ordering is incorrect unless
        //you manually override the RTL direction.  This can be done by specifying the unicode control character for RTL encoding
        //at the beginning of the string.
        //Note that this did not fix the problem in Linux in our testing.
        //We apply the fix when using MessageFormatter pattern, since that must mix numeric characters in {0}, {1} etc
        //With arabic characters.
        //See #2703
        var RTLControlCode = FlexCommon.getInstance().getRTLControlCode();

        for each ( var item: XML in list ) {
            if ( item.attribute( "key" ).toString() == key ) {
                const translatedString: String = RTLControlCode + format( item.attribute( "value" ).toString(), formatArray );
                if ( debug ) {
                    return "[Translated String] " + translatedString;
                }
                else {
                    return translatedString;
                }
            }
        }
        if ( defaultString == null ) {
            return key;
        }
        else {
            if ( debug ) {
                return "[Default String] " + format( defaultString, formatArray );
            }
            else {
                return RTLControlCode + format( defaultString, formatArray );
            }
        }
    }

    public static function format( pattern: String, args: Array ): String {
        return StringUtils.format( pattern, args );
    }


}
}