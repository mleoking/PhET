package edu.colorado.phet.common.phetcommon.statistics;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

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
    
    public StatisticsMessageField( String keyForHumanReadableName, String name, String value ) {
        this.humanReadableName = PhetCommonResources.getString( keyForHumanReadableName );
        this.name = name;
        this.value = ( ( value == null || value.length() == 0 ) ? NULL_STRING : value );
    }
    
    public StatisticsMessageField( String keyForHumanReadableName, String name, int value ) {
        this( keyForHumanReadableName, name, String.valueOf( value ) );
    }
    
    public StatisticsMessageField( String keyForHumanReadableName, String name, long value ) {
        this( keyForHumanReadableName, name, String.valueOf( value ) );
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
        public SystemProperty( String keyForHumanReadableName, String fieldName, String systemPropertyKey ) {
            super( keyForHumanReadableName, fieldName, System.getProperty( systemPropertyKey ) );
        }
    }
}
