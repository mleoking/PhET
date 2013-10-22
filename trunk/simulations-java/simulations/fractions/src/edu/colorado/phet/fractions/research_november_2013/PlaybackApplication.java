// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionScreenType;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by Sam on 10/21/13.
 */
public class PlaybackApplication implements ResearchApplication {
    private final ArrayList<VoidFunction1<PNode>> bafLevelStartedListeners = new ArrayList<VoidFunction1<PNode>>();
    private final HashMap<String, Property> properties;

    public PlaybackApplication( String text ) {
        StringTokenizer st = new StringTokenizer( text, "\n" );
        properties = new HashMap<String, Property>();
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

    public static void main( String[] args ) throws IOException {
        File file = new File( "C:/Users/Sam/Desktop/trace.txt" );

        String text = FileUtils.loadFileAsString( file );
        new PlaybackApplication( text ).start();
    }

    private void start() {
    }

    public ObservableProperty<Boolean> windowNotIconified() {
        return properties.get( "window.up" );
    }

    public ObservableProperty<Boolean> windowActive() {
        return properties.get( "window.active" );
    }

    public ObservableProperty<String> module() {
        return properties.get( "tab" );
    }

    public ObservableProperty<String> introRepresentation() {
        return properties.get( "tab1.rep" );
    }

    public ObservableProperty<Integer> introDenominator() {
        return properties.get( "tab1.denominator" );
    }

    public ObservableProperty<Integer> introNumerator() {
        return properties.get( "tab1.numerator" );
    }

    public ObservableProperty<Integer> introMaximum() {
        return properties.get( "tab1.max" );
    }

    public ObservableProperty<Integer> totalClicks() {
        return properties.get( "clicks" );
    }

    public ObservableProperty<BuildAFractionScreenType> bafScreenType() {
        return null;
    }

    public void addBAFLevelStartedListener( VoidFunction1<PNode> listener ) {
        //TODO: trigger calls there
        bafLevelStartedListeners.add( listener );
    }
}
