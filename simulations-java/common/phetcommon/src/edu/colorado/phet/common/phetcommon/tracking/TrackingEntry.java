package edu.colorado.phet.common.phetcommon.tracking;

public class TrackingEntry {
    private String key;
    private String value;

    public TrackingEntry( String key, String value ) {
        this.key = key;
        this.value = value;
    }

    public String toPHP() {
        return key + "=" + valueToPHP( value );
    }

    private String valueToPHP( String value ) {
        if ( value == null ) {
            return "null";
        }
        else {

            //See also
            // http://forums.digitalpoint.com/showthread.php?s=1e314cbd77a6b11d904f186a60d388af&t=13647
            //regadring espace characters for php

            String str = value.replaceAll( " ", "%20" );
//            str = str.replace( ".", "&#46;" );
            return str;
        }
    }

    public String toHumanReadable() {
        return key + " = " + value;
    }

    public static class SystemProperty extends TrackingEntry {
        public SystemProperty( String s ) {
            super( s, System.getProperty( s ) );
        }
    }
}
