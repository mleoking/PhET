package edu.colorado.phet.common.phetcommon.statistics;

/**
 * StatisticsMessageField is a field in a statistics message, consisting of a name/value pair.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StatisticsMessageField {
    
    private static final String NULL_STRING = "null"; // server requires this explicit representation of null
    
    private final String humanReadableName;
    private final String name;
    private final String value;
    
    public StatisticsMessageField( String humanReadableName, String name, String value ) {
        this.humanReadableName = humanReadableName;
        this.name = name;
        this.value = ( ( value == null || value.length() == 0 ) ? NULL_STRING : value );
    }
    
    public StatisticsMessageField( String humanReadableName, String name, int value ) {
        this( humanReadableName, name, String.valueOf( value ) );
    }
    
    public StatisticsMessageField( String humanReadableName, String name, long value ) {
        this( humanReadableName, name, String.valueOf( value ) );
    }
    
    public String getHumanReadableName() {
        return humanReadableName;
    }

    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }

    public static class SystemProperty extends StatisticsMessageField {
        public SystemProperty( String humanReadableName, String fieldName, String s ) {
            super( humanReadableName, fieldName, System.getProperty( s ) );
        }
    }
}
