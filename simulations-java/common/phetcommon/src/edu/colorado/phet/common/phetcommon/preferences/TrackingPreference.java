package edu.colorado.phet.common.phetcommon.preferences;

public class TrackingPreference extends PreferenceEnumType {
    public static final TrackingPreference[] TYPES = new TrackingPreference[]{
            new TrackingPreference( "allow-tracking", "Send PhET anonymous usage information (recommended)" ),
            new TrackingPreference( "dont-allow-tracking", "Don't send any information to PhET" )
    };

    public static TrackingPreference get( String value ) {
        return (TrackingPreference) PreferenceEnumType.get( value, TYPES );
    }

    public TrackingPreference( String propertyValue, String humanReadableText ) {
        super( humanReadableText, propertyValue );
    }

}
