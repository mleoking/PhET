package edu.colorado.phet.common.phetcommon.tracking;

/**
 * TrackingMessageField is a field in a tracking message, consisting of a name/value pair.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TrackingMessageField {
    
    private final String name;
    private final String value;

    public TrackingMessageField( String name, String value ) {
        this.name = name;
        this.value = value;
    }
    
    public TrackingMessageField( String name, int value ) {
        this( name, String.valueOf( value ) );
    }
    
    public TrackingMessageField( String name, long value ) {
        this( name, String.valueOf( value ) );
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }

    public static class SystemProperty extends TrackingMessageField {
        public SystemProperty( String fieldName, String s ) {
            super( fieldName, System.getProperty( s ) );
        }
    }
}
