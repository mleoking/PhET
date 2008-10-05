package edu.colorado.phet.common.phetcommon.preferences;

public class PreferenceEnumType {
    protected String propertyValue;
    protected String humanReadableText;

    public PreferenceEnumType( String humanReadableText, String propertyValue ) {
        this.humanReadableText = humanReadableText;
        this.propertyValue = propertyValue;
    }

    public String toString() {
        return humanReadableText;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public static PreferenceEnumType get( String value, PreferenceEnumType[] types ) {
        for ( int i = 0; i < types.length; i++ ) {
            if ( types[i].propertyValue.equals( value ) ) {
                return types[i];
            }
        }
        return null;//todo: improve error handling
    }
}
