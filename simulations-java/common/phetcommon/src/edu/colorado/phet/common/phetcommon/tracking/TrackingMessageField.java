package edu.colorado.phet.common.phetcommon.tracking;

/**
 * TrackingMessageField is a field in a tracking message, consisting of a name/value pair.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TrackingMessageField {
    private String name;
    private String value;

    public TrackingMessageField( String name, String value ) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }

    public static class SystemProperty extends TrackingMessageField {
        public SystemProperty( String s ) {
            super( s, System.getProperty( s ) );
        }
    }
}
