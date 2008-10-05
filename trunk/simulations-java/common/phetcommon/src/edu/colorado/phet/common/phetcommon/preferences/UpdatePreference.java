package edu.colorado.phet.common.phetcommon.preferences;

public class UpdatePreference extends PreferenceEnumType {
    private static final UpdatePreference AUTOMATICALLY_CHECK_FOR_UPDATES_ON_SIM_STARTUP = new UpdatePreference( "automatic-checks", "Check for updates when simulation starts (recommended)" );
    private static final UpdatePreference NO_AUTOMATIC_CHECKS_FOR_UPDATES = new UpdatePreference( "no-automatic-checks", "Don't check for updates" );

    public static UpdatePreference get( String value ) {
        return (UpdatePreference) PreferenceEnumType.get( value, TYPES );
    }

    public static UpdatePreference[] TYPES = new UpdatePreference[]{
            UpdatePreference.AUTOMATICALLY_CHECK_FOR_UPDATES_ON_SIM_STARTUP,
            UpdatePreference.NO_AUTOMATIC_CHECKS_FOR_UPDATES
    };

    public UpdatePreference( String propertyValue, String humanReadableText ) {
        super( humanReadableText, propertyValue );
    }

}
