package com.pixelzoom.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


/**
 * Shows all System properties and values, sorted by property name.
 *
 * @author Chris Malley (cmal
 *         ley@pixelzoom.com)
 */
public class ShowSystemProperties {

    public static void main( String[] args ) {

        // Get the system properties
        Properties properties = System.getProperties();

        // Sort 'em
        Object[] keySet = properties.keySet().toArray();
        List keys = Arrays.asList( keySet );
        Collections.sort( keys );

        // Print 'em
        String[][] rowData = new String[keys.size()][2];
        for ( int i = 0; i < keys.size(); i++ ) {
            Object key = keys.get( i );
            Object value = properties.get( key );
            System.out.println( key + "," + value );
            rowData[i][0] = key.toString();
            rowData[i][1] = "" + value.toString();
        }
    }
}
