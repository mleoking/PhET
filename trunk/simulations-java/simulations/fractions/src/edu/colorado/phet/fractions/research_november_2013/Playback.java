// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 * Created by Sam on 10/21/13.
 */
public class Playback {
    public static void main( String[] args ) throws IOException {
        File file = new File( "C:/Users/Sam/Desktop/trace.txt" );
        String text = FileUtils.loadFileAsString( file );
        StringTokenizer st = new StringTokenizer( text, "\n" );
        HashMap<String, Property> properties = new HashMap<String, Property>();
        while ( st.hasMoreTokens() ) {
            String line = st.nextToken();
            StringTokenizer st2 = new StringTokenizer( line, "\t" );
            ArrayList<String> elements = new ArrayList<String>();
            while ( st2.hasMoreTokens() ) {
                String element = st2.nextToken();
                elements.add( element );
            }
            if ( elements.get( 3 ).equals( "property" ) ) {
                String propertyName = elements.get( 2 );
//                    System.out.println( "Found property: " + propertyName );
                String value = elements.get( 5 ).substring( elements.get( 5 ).indexOf( '=' ) + 1 ).trim();
                if ( !properties.containsKey( propertyName ) ) {
                    properties.put( propertyName, new Property( value ) );
                    System.out.println( "created " + propertyName + " with value " + value );
                }
                else {
                    properties.get( propertyName ).set( value );
                    System.out.println( "Set " + propertyName + " to " + value );
                }
            }
        }
    }
}
