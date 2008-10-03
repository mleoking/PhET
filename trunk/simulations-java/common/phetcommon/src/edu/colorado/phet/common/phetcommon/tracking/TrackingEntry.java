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
            String str = value.replace( " ", "%20" );
//            str = str.replace( ".", "&#46;" );
            return str;
        }
    }

    public static class SystemProperty extends TrackingEntry {
        public SystemProperty( String s ) {
            super( s, System.getProperty( s ) );
        }
    }
}
