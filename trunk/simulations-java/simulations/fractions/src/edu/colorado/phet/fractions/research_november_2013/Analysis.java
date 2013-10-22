// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionScreenType;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by Sam on 10/21/13.
 */
public class Analysis {
    private final TextArea reportArea = new TextArea();
    private final PhetPCanvas reportCanvas = new PhetPCanvas();
    private final PNode reportNode;
    private final ArrayList<String> messages = new ArrayList<String>();

    public Analysis() {
        JFrame frame = new JFrame( "Report" );
        frame.setContentPane( new JScrollPane( reportArea ) );
        frame.setSize( 800, 600 );
        frame.setVisible( true );

        JFrame canvasFrame = new JFrame( "Visualization" );
        canvasFrame.setContentPane( reportCanvas );
        reportNode = new PNode();
        reportCanvas.addScreenChild( reportNode );
        canvasFrame.setSize( 800, 600 );
        canvasFrame.setLocation( 0, 400 );
        canvasFrame.setVisible( true );

        modulePaintHashMap = new HashMap<String, Color>();
        modulePaintHashMap.put( "Intro", Color.blue );
        modulePaintHashMap.put( "Build a Fraction", Color.red );
        modulePaintHashMap.put( "Equality Lab", Color.green );
        modulePaintHashMap.put( "Matching Game", Color.yellow );
        modulePaintHashMap.put( "Fraction Lab", Color.magenta );
    }

    private void addVariable( final String name, PNode node ) {
        reportCanvas.addScreenChild( node );
        node.setOffset( 100, 0 );
        PText text = new PText( name );
        text.setOffset( 0, node.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
        reportCanvas.addScreenChild( text );
    }

//    private void addEventNode( String name, final PNode node, final double y, final Property<Function.LinearFunction> timeScalingFunction ) {
//        final long time = app.time().apply();
//        reportCanvas.addScreenChild( node );
//        timeScalingFunction.addObserver( new VoidFunction1<Function.LinearFunction>() {
//            public void apply( Function.LinearFunction linearFunction ) {
//                node.setOffset( 100 + linearFunction.evaluate( time ), y );
//            }
//        } );
//        SimSharingManager.sendModelMessage( FractionsIntroSimSharing.ModelComponents.event, FractionsIntroSimSharing.ModelComponentTypes.event, FractionsIntroSimSharing.ModelActions.occurred,
//                                            ParameterSet.parameterSet( ParameterKeys.name, name ) );
//    }

    //when you see a new property, add it to the trace
    public void addMessage( String line ) {
        messages.add( line );
        StateRepresentation representation = parseAll();
        showRepresentation( representation );
    }

    private void showRepresentation( StateRepresentation representation ) {

        HashMap<String, Color> representationPaintHashMap = new HashMap<String, Color>();
        representationPaintHashMap.put( "Pie", new Color( 0x8cc63f ) );
        representationPaintHashMap.put( "HorizontalBar", new Color( 0xe94545 ) );
        representationPaintHashMap.put( "VerticalBar", new Color( 0x57b6dd ) );
        representationPaintHashMap.put( "WaterGlasses", new Color( 0xffc800 ) );
        representationPaintHashMap.put( "Cake", new Color( 0xa55a41 ) );
        representationPaintHashMap.put( "NumberLine", Color.black );

        reportNode.removeAllChildren();
        double y = 0;
        Function.LinearFunction time = new Function.LinearFunction( representation.startTime, Math.max( representation.endTime, representation.startTime + 60000 ), 100, 700 );
        List<ArrayList<Record>> list = new ArrayList<ArrayList<Record>>();
        for ( ArrayList<Record> records : representation.properties.values() ) {
            list.add( records );
        }
        Collections.sort( list, new Comparator<ArrayList<Record>>() {
            public int compare( ArrayList<Record> o1, ArrayList<Record> o2 ) {
                return Double.compare( o1.get( 0 ).timestamp, o2.get( 0 ).timestamp );
            }
        } );
        for ( ArrayList<Record> recordList : list ) {
            if ( recordList.size() > 0 ) {
                ArrayList<PNode> bars = new ArrayList<PNode>();
                for ( int i = 0; i < recordList.size(); i++ ) {
                    Record record = recordList.get( i );
                    double maxTime = i == recordList.size() - 1 ? representation.endTime : recordList.get( i + 1 ).timestamp;
                    Object value = record.value;
                    Color fill = record.type.equals( "java.lang.Boolean" ) ? value == Boolean.TRUE ? Color.green : Color.gray :
                                 record.property.equals( "tab" ) ? modulePaintHashMap.get( record.value ) :
                                 record.property.equals( "tab1.rep" ) ? representationPaintHashMap.get( record.value ) :
                                 Color.red;
                    PhetPPath bar = new PhetPPath( new Rectangle2D.Double( time.evaluate( record.timestamp ), y, time.evaluate( maxTime ) - time.evaluate( record.timestamp ), 10 ), fill, new BasicStroke( 1 ), Color.black );
                    bars.add( bar );
                    reportNode.addChild( bar );
                }
                PText textNode = new PText( recordList.get( 0 ).property );
                textNode.setOffset( bars.get( 0 ).getFullBounds().getX() - textNode.getFullBounds().getWidth(), bars.get( 0 ).getFullBounds().getCenterY() - textNode.getFullBounds().getHeight() / 2 );
                reportNode.addChild( textNode );
                y = y + 20;
            }
        }
    }

    private StateRepresentation parseAll() {
        final HashMap<String, ArrayList<Record>> properties = new HashMap<String, ArrayList<Record>>();
        final ArrayList<Record> events = new ArrayList<Record>();
        Long startTime = null;
        Long time = null;
        for ( String line : messages ) {

            StringTokenizer tokenizer = new StringTokenizer( line, "\t" );
            ArrayList<String> entries = new ArrayList<String>();
            while ( tokenizer.hasMoreTokens() ) {
                String element = tokenizer.nextToken();
                entries.add( element );
            }

            //Crazy hack workaround to get the times to read out right
            time = Long.parseLong( entries.get( 0 ) );
            if ( startTime == null ) {
                startTime = time;
            }
            if ( entries.get( 3 ).equals( "property" ) ) {
                String propertyName = entries.get( 2 );
//                    System.out.println( "Found property: " + propertyName );
                String values = entries.get( 5 );
                String type = entries.get( 6 ).substring( entries.get( 6 ).indexOf( '=' ) + 1 ).trim();
                String valueString = values.substring( values.indexOf( '=' ) + 1 ).trim();
//                System.out.println( "value=" + valueString + ", types=" + type );
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
//                    System.out.println( "PARSED " + value );
                }
                else {
                    System.out.println( "Unknown type: " + type );
                }
                if ( !properties.containsKey( propertyName ) ) {
                    properties.put( propertyName, new ArrayList<Record>() );
                }

                ArrayList<Record> records = properties.get( propertyName );
                records.add( new Record( propertyName, time, value, type ) );
            }
            else if ( entries.get( 3 ).equals( "event" ) ) {

            }
            else {
            }
        }
        return new StateRepresentation( startTime, time, properties, events );
    }

    public static class Record {
        long timestamp;
        Object value;
        private String property;
        private String type;

        public Record( String property, long timestamp, Object value, String type ) {
            this.property = property;
            this.timestamp = timestamp;
            this.value = value;
            this.type = type;
        }
    }

    private static class StateRepresentation {
        public final HashMap<String, ArrayList<Record>> properties;
        public final ArrayList<Record> events;
        private final long startTime;
        private final long endTime;

        public StateRepresentation( long startTime, long endTime, HashMap<String, ArrayList<Record>> properties, ArrayList<Record> events ) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.properties = properties;

            this.events = events;
        }
    }

    private final HashMap<String, Color> modulePaintHashMap;
}