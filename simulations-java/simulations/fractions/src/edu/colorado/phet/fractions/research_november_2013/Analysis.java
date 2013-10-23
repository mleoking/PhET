// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionScreenType;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by Sam on 10/21/13.
 */
public class Analysis {
    private final TextArea reportArea = new TextArea();
    private final PCanvas reportCanvas = new PCanvas();
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
        reportCanvas.getLayer().addChild( reportNode );
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

        //Auto-shrink to fit in the window
//        Function.LinearFunction time = new Function.LinearFunction( representation.startTime, Math.max( representation.endTime, representation.startTime + 60000 ), 100, 700 );

        //Don't shrink
        Function.LinearFunction time = new Function.LinearFunction( representation.startTime, representation.startTime + 60000, 100, 700 );

        List<Record> list = representation.orderedEventsAndProperties();
        for ( Record r : list ) {
            if ( r instanceof ParameterLife ) {
                final ParameterLife recordX = (ParameterLife) r;
                final ArrayList<PropertyChange> recordList = recordX.propertyChanges;
                if ( recordList.size() > 0 ) {
                    if ( recordList.get( 0 ).type.equals( "java.lang.Boolean" ) || recordList.get( 0 ).property.equals( "clicks" ) || recordList.get( 0 ).type.equals( "java.lang.String" ) ) {
                        ArrayList<PNode> bars = new ArrayList<PNode>();
                        for ( int i = 0; i < recordList.size(); i++ ) {
                            PropertyChange record = recordList.get( i );
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
                    else if ( recordList.get( 0 ).type.equals( "java.lang.Integer" ) ) {
                        ArrayList<PNode> segments = new ArrayList<PNode>();
                        for ( int i = 0; i < recordList.size(); i++ ) {
                            PropertyChange record = recordList.get( i );
                            double maxTime = i == recordList.size() - 1 ? representation.endTime : recordList.get( i + 1 ).timestamp;
                            Integer value = (Integer) record.value;
                            Function.LinearFunction yFunction = record.property.endsWith( ".filledTargets" ) ?
                                                                new Function.LinearFunction( 0, 4, y + 20, y ) :
                                                                new Function.LinearFunction( 0, 8, y + 20, y );
                            PhetPPath bar = new PhetPPath( new Line2D.Double( time.evaluate( record.timestamp ), yFunction.evaluate( value ), time.evaluate( maxTime ), yFunction.evaluate( value ) ), new BasicStroke( 2 ), Color.black );
                            segments.add( bar );
                            reportNode.addChild( bar );
                        }
                        PText textNode = new PText( recordList.get( 0 ).property );
                        textNode.setOffset( segments.get( 0 ).getFullBounds().getX() - textNode.getFullBounds().getWidth(), segments.get( 0 ).getFullBounds().getCenterY() - textNode.getFullBounds().getHeight() / 2 );
                        reportNode.addChild( textNode );
                        y = y + 30;
                    }
                }
            }
            else if ( r instanceof Event ) {
                Event event = (Event) r;
                double radius = 4;
                double centerX = time.evaluate( event.timestamp );
                double centerY = y;
                PNode shape = event.hitTrue() ? new PText( "\u2605" ) {{setTextPaint( Color.black );}} :
                              event.hitFalse() ? new PText( "X" ) {{setTextPaint( Color.black );}} :
                              new PhetPPath( new Ellipse2D.Double( 0, 0, radius * 2, radius * 2 ), Color.blue, new BasicStroke( 1 ), Color.black );
                shape.setOffset( centerX - shape.getFullBounds().getWidth() / 2, centerY - shape.getFullBounds().getHeight() / 2 );
                PText text = new PText( event.name + ": " + event.parameters.toString() );
                text.setOffset( shape.getFullBounds().getMaxX() + 2, shape.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
                y += 20;
                reportNode.addChild( shape );
                reportNode.addChild( text );
            }
        }
    }

    private StateRepresentation parseAll() {
        final HashMap<String, ArrayList<PropertyChange>> properties = new HashMap<String, ArrayList<PropertyChange>>();
        final ArrayList<Event> events = new ArrayList<Event>();
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
                    properties.put( propertyName, new ArrayList<PropertyChange>() );
                }

                ArrayList<PropertyChange> records = properties.get( propertyName );
                records.add( new PropertyChange( propertyName, time, value, type ) );
            }
            else if ( entries.get( 3 ).equals( "event" ) ) {
                String eventName = entries.get( 4 );
                Event event = new Event( time, eventName, new ArrayList<String>( entries.subList( 5, entries.size() ) ) );
                events.add( event );
            }
            else {
            }
        }
        return new StateRepresentation( startTime, time, properties, events );
    }

    public static class Event implements Record {
        long timestamp;
        String name;
        ArrayList<String> parameters;

        public Event( long timestamp, String name, ArrayList<String> parameters ) {
            this.timestamp = timestamp;
            this.name = name;
            this.parameters = parameters;
            map = new HashMap<String, String>();
            for ( String parameter : parameters ) {
                StringTokenizer st = new StringTokenizer( parameter, "=" );
                map.put( st.nextToken().trim(), st.nextToken().trim() );
            }
        }

        public long getTime() {
            return timestamp;
        }

        //true if there is a hit parameter and it is true
        public boolean hitTrue() { return map.containsKey( "hit" ) && map.get( "hit" ).equals( "true" ); }

        public boolean hitFalse() { return map.containsKey( "hit" ) && map.get( "hit" ).equals( "false" ); }

        private final HashMap<String, String> map;
    }

    public static interface Record {
        long getTime();
    }

    public static class PropertyChange implements Record {
        long timestamp;
        Object value;
        private String property;
        private String type;

        public PropertyChange( String property, long timestamp, Object value, String type ) {
            this.property = property;
            this.timestamp = timestamp;
            this.value = value;
            this.type = type;
        }

        public long getTime() {
            return timestamp;
        }
    }

    private static class StateRepresentation {
        public final HashMap<String, ArrayList<PropertyChange>> properties;
        public final ArrayList<Event> events;
        private final long startTime;
        private final long endTime;

        public StateRepresentation( long startTime, long endTime, HashMap<String, ArrayList<PropertyChange>> properties, ArrayList<Event> events ) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.properties = properties;

            this.events = events;
        }

        public ArrayList<Record> orderedEventsAndProperties() {
            ArrayList<Record> out = new ArrayList<Record>();
            for ( ArrayList<PropertyChange> propertyChanges : properties.values() ) {
                out.add( new ParameterLife( propertyChanges ) );
            }
            out.addAll( events );
            Collections.sort( out, new Comparator<Record>() {
                public int compare( Record o1, Record o2 ) {
                    return Double.compare( o1.getTime(), o2.getTime() );
                }
            } );
            return out;
        }
    }

    private static class ParameterLife implements Record {
        private ArrayList<PropertyChange> propertyChanges;

        public ParameterLife( ArrayList<PropertyChange> propertyChanges ) {
            this.propertyChanges = propertyChanges;
        }

        public long getTime() {
            return this.propertyChanges.get( 0 ).timestamp;
        }

    }

    private final HashMap<String, Color> modulePaintHashMap;

    //update but without adding new data
    public void sync() {
        addMessage( System.currentTimeMillis() + "\tmodel\ttime\tfeature\tchanged\t" );
        messages.remove( messages.size() - 1 );
    }

    public static void main( String[] args ) throws IOException {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                File file = new File( "C:/Users/Sam/Desktop/trace.txt" );
                try {
                    String s = FileUtils.loadFileAsString( file );
                    final ArrayList<String> messages = new ArrayList<String>();
                    final Analysis report = new Analysis();
                    StringTokenizer st = new StringTokenizer( s, "\n" );
                    while ( st.hasMoreTokens() ) {
                        messages.add( st.nextToken() );
                    }
                    report.addMessages( messages );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    private void addMessages( ArrayList<String> messages ) {
        this.messages.addAll( messages );
        StateRepresentation representation = parseAll();
        showRepresentation( representation );
    }
}