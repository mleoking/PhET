// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionScreenType;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by Sam on 10/21/13.
 */
public class PlaybackApplication implements ResearchApplication {
    private final ArrayList<VoidFunction1<PNode>> bafLevelStartedListeners = new ArrayList<VoidFunction1<PNode>>();
    private final HashMap<String, Property> properties;
    private final String text;
    private long t = 0;
    private Long startTime = null;
    private long endTime = 0;
    private Function0<Long> theTime = new Function0<Long>() {
        public Long apply() {
            return t;
        }
    };
    private Function0<Long> theEndTime = new Function0<Long>() {
        public Long apply() {
            return endTime;
        }
    };

    public PlaybackApplication( String text ) {
        this.text = text;
        properties = new HashMap<String, Property>();
        processAll();

        //get ready for launch
        for ( Property property : properties.values() ) {
            property.reset();
        }

        //get ready to go again (processAll called once in constructor and once again in running)
        t = startTime;
    }

    public static void main( String[] args ) throws IOException {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                File file = new File( "C:/Users/Sam/Desktop/trace.txt" );
                String text = null;
                try {
                    text = FileUtils.loadFileAsString( file );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                PlaybackApplication app = new PlaybackApplication( text );
                new ApplicationVisualization( app );
                app.start();
            }
        } );
    }

    private void processAll() {
        StringTokenizer st = new StringTokenizer( text, "\n" );
        while ( st.hasMoreTokens() ) {
            String line = st.nextToken();
            StringTokenizer st2 = new StringTokenizer( line, "\t" );
            ArrayList<String> elements = new ArrayList<String>();
            while ( st2.hasMoreTokens() ) {
                String element = st2.nextToken();
                elements.add( element );
            }

            //Crazy hack workaround to get the times to read out right
            t = Long.parseLong( elements.get( 0 ) );
            if ( startTime == null ) {
                startTime = t;
            }
            if ( t > endTime ) {
                endTime = t;
            }
            if ( elements.get( 3 ).equals( "property" ) ) {
                String propertyName = elements.get( 2 );
//                    System.out.println( "Found property: " + propertyName );
                String values = elements.get( 5 );
                String type = elements.get( 6 ).substring( elements.get( 6 ).indexOf( '=' ) + 1 ).trim();
                String valueString = values.substring( values.indexOf( '=' ) + 1 ).trim();
                System.out.println( "value=" + valueString + ", types=" + type );
                Object value = null;
                if ( type.equals( "java.lang.Integer" ) ) {
                    value = Integer.parseInt( valueString );
                }
                else if ( type.equals( "java.lang.Boolean" ) ) {
                    value = Boolean.parseBoolean( valueString );
                }
                else if ( type.equals( "java.lang.String" ) ) {
                    value = valueString;
                }
                else if ( type.equals( "edu.colorado.phet.fractions.buildafraction.view.BuildAFractionScreenType" ) ) {
                    value = BuildAFractionScreenType.valueOf( valueString );
                    System.out.println( "PARSED " + value );
                }
                else {
                    System.out.println( "Unknown type: " + type );
                }
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

    public void start() {
        processAll();
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

        //TODO: Wire this up
        return new Property<BuildAFractionScreenType>( BuildAFractionScreenType.LEVEL_SELECTION );
    }

    public void addBAFLevelStartedListener( VoidFunction1<PNode> listener ) {
        //TODO: trigger calls there
        bafLevelStartedListeners.add( listener );
    }

    public Function0<Long> time() {
        return theTime;
    }

    public Function0<Long> endTime() {
        return theEndTime;
    }
}