
package com.pixelzoom.util;

import java.util.*;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;


/**
 * PrintSystemProperties prints the System properties (sorted) to System.out.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PrintSystemProperties {

    public static void main( String[] args ) {
        
        // Get the system properties
        Properties properties = System.getProperties();
        
        // Sort 'em
        Object[] keySet = properties.keySet().toArray();
        List keys = Arrays.asList( keySet );
        Collections.sort( keys );

        // Print out each key/value pair
        for( int i = 0; i < keys.size(); i++ ) {
            Object key = keys.get( i );
            Object value = properties.get( key );
            System.out.println( key + ": " + value );
        }
    }
}
