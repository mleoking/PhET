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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionScreenType;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by Sam on 10/21/13.
 */
public class Analysis {
    private final PNode reportNode;
    private final TextArea reportArea;
    private final ArrayList<String> messages = new ArrayList<String>();

    public Analysis() {
        JFrame frame = new JFrame( "Report" );
        reportArea = new TextArea();
        frame.setContentPane( new JScrollPane( reportArea ) );
        frame.setSize( 800, 600 );
        frame.setVisible( true );

        JFrame canvasFrame = new JFrame( "Visualization" );
        PCanvas reportCanvas = new PCanvas();
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
        displayGraphically( representation );
        createTextReport( messages, representation );
    }

    private void createTextReport( ArrayList<String> messages, StateRepresentation representation ) {
        String reportText = toReportText( messages, representation );
        if ( !reportText.equals( reportArea.getText() ) ) { reportArea.setText( reportText ); }
    }

    private String toReportText( ArrayList<String> messages, StateRepresentation representation ) {
        long seconds = ( representation.endTime - representation.startTime ) / 1000;
        long minute = TimeUnit.SECONDS.toMinutes( seconds );
        long second = TimeUnit.SECONDS.toSeconds( seconds ) - ( TimeUnit.SECONDS.toMinutes( seconds ) * 60 );
        String timeText = minute + ":" + ( second < 10 ? "0" + second : second );
        List<Record> list = representation.orderedEventsAndPropertyDeltas();
        HashMap<String, Object> properties = new HashMap<String, Object>();
        final ArrayList<Event> events = new ArrayList<Event>();

        HashMap<String, Integer> clicksPerTab = new HashMap<String, Integer>();
        HashMap<String, Integer> clicksPerIntroRepresentation = new HashMap<String, Integer>();
        HashMap<String, Integer> clicksPerFractionLabRepresentation = new HashMap<String, Integer>();
        HashSet<String> visitedIntroRepresentations = new HashSet<String>();

        HashMap<String, Long> timePerTab = new HashMap<String, Long>();
        HashMap<String, Long> timePerIntroRepresentation = new HashMap<String, Long>();
        HashMap<String, Long> timePerEqualityLabLeftRepresentation = new HashMap<String, Long>();
        HashMap<String, Long> timePerEqualityLabSameRepresentations = new HashMap<String, Long>();
        HashSet<String> visitedEqualityLabRepresentations = new HashSet<String>();
        HashMap<String, Long> timePerFractionLabRepresentation = new HashMap<String, Long>();
        final ArrayList<BAFLevel> bafLevels = new ArrayList<BAFLevel>();

        long previousTime = list.size() > 0 ? list.get( 0 ).getTime() : 0;
        for ( Record record : list ) {
            long elapsedTime = record.getTime() - previousTime;//TODO: rounding or counting errors?

            //before moving to new steps, process previous for timing

            if ( properties.get( "tab" ) != null ) {
                String tabString = properties.get( "tab" ).toString();
                augment( timePerTab, tabString, elapsedTime );

                if ( tabString.equals( "Intro" ) ) {
                    Object tab1Rep = properties.get( "tab1.rep" );
                    if ( tab1Rep != null ) {
                        augment( timePerIntroRepresentation, tab1Rep.toString(), elapsedTime );
                    }
                }
                if ( tabString.equals( "Equality Lab" ) ) {
                    Object repObject = properties.get( "tab3.leftRepresentation" );
                    if ( repObject != null ) {
                        augment( timePerEqualityLabLeftRepresentation, repObject.toString(), elapsedTime );
                    }
                }

                if ( tabString.equals( "Equality Lab" ) ) {
                    Object repObject = properties.get( "tab3.sameRepresentation" );
                    if ( repObject != null ) {
                        augment( timePerEqualityLabSameRepresentations, repObject.toString(), elapsedTime );
                    }
                }
                if ( tabString.equals( "Fraction Lab" ) ) {
                    Object repObject = properties.get( "tab5.representation" );
                    if ( repObject != null ) {
                        augment( timePerFractionLabRepresentation, repObject.toString(), elapsedTime );
                    }
                }
            }


            if ( record instanceof PropertyChange ) {
                String tabName = properties.get( "tab" ) == null ? "<none>" : properties.get( "tab" ).toString();

                PropertyChange pc = (PropertyChange) record;
                properties.put( pc.property, pc.value );

                if ( tabName.equals( "Intro" ) && pc.property.equals( "tab1.rep" ) ) {
                    visitedIntroRepresentations.add( pc.value.toString() );
                }
                if ( tabName.equals( "Equality Lab" ) && pc.property.equals( "tab3.leftRepresentation" ) ) {
                    visitedEqualityLabRepresentations.add( pc.value.toString() );
                }

                //Ignore first "clicks = 0" event
                if ( pc.property.equals( "clicks" ) && ( (Integer) pc.value ) > 0 ) {
                    //check the state and histogram it
                    augment( clicksPerTab, tabName, 1 );

                    if ( tabName.equals( "Intro" ) ) {
                        augment( clicksPerIntroRepresentation, properties.get( "tab1.rep" ).toString(), 1 );
                    }
                    if ( tabName.equals( "Fraction Lab" ) ) {
                        augment( clicksPerFractionLabRepresentation, properties.get( "tab5.representation" ).toString(), 1 );
                    }
                }
            }
            else if ( record instanceof Event ) {
                Event event = (Event) record;
                events.add( event );
                if ( event.name.equals( "buildAFractionLevelStarted" ) ) {
                    int id = Integer.parseInt( event.parameters.get( "id" ) );
                    int level = Integer.parseInt( event.parameters.get( "level" ) );
                    String type = event.parameters.get( "type" );
                    String targets = event.parameters.get( "targets" );
                    boolean isNew = true;
                    for ( BAFLevel bafLevel : bafLevels ) {
                        if ( bafLevel.id == id ) {
                            isNew = false;
                        }
                    }
                    if ( isNew ) {
                        bafLevels.add( new BAFLevel( id, level, type, targets ) );
                    }
                }
            }
            else {
                throw new RuntimeException( "?" );
            }

            String tabName = properties.get( "tab" ) == null ? "<none>" : properties.get( "tab" ).toString();

            if ( tabName.equals( "Intro" ) && properties.containsKey( "tab1.rep" ) ) {
                visitedIntroRepresentations.add( properties.get( "tab1.rep" ).toString() );
            }
            if ( tabName.equals( "Equality Lab" ) && properties.containsKey( "tab3.leftRepresentation" ) ) {
                visitedEqualityLabRepresentations.add( properties.get( "tab3.leftRepresentation" ).toString() );
            }

            previousTime = record.getTime();
        }

        ObservableList<String> bafTargetResults = new ObservableList<BAFLevel>( bafLevels ).map( new Function1<BAFLevel, String>() {
            public String apply( BAFLevel bafLevel ) {
                //search all events for hit or miss
                final HashMap<Integer, ArrayList<String>> guesses = new HashMap<Integer, ArrayList<String>>();
                for ( Event event : events ) {
                    if ( event.parameters.containsKey( "levelID" ) && event.parameters.get( "levelID" ).equals( "" + bafLevel.id ) ) {
                        Integer key = Integer.parseInt( event.parameters.get( "targetIndex" ) );
                        if ( !guesses.containsKey( key ) ) {
                            guesses.put( key, new ArrayList<String>() );
                        }
                        guesses.get( key ).add( "created " + event.parameters.get( "source" ) + " with divisions = " + event.parameters.get( "divisions" ) + " for target " + event.parameters.get( "target" ) + ": " + ( event.parameters.get( "hit" ).equals( "true" ) ? "right" : "wrong" ) );//Track all the times it was correct or wrong
                    }
                }
                return bafLevel.toString() + ":\n" + new ObservableList<Integer>( guesses.keySet() ).map( new Function1<Integer, Object>() {
                    public Object apply( Integer integer ) {
                        return "\tTarget " + integer + ": " + guesses.get( integer );
                    }
                } ).mkString( "\n" );
            }
        } );

        ArrayList<String> numeratorSpinnerUpButton = new ArrayList<String>();
        ArrayList<String> denominatorSpinnerUpButton = new ArrayList<String>();
        ArrayList<String> numeratorSpinnerDownButton = new ArrayList<String>();
        ArrayList<String> denominatorSpinnerDownButton = new ArrayList<String>();
        ArrayList<String> sliceComponent = new ArrayList<String>();
        for ( String message : messages ) {
            if ( message.contains( "\tnumeratorSpinnerUpButton\t" ) ) {
                numeratorSpinnerUpButton.add( message );
            }
            else if ( message.contains( "\tdenominatorSpinnerUpButton\t" ) ) {
                denominatorSpinnerUpButton.add( message );
            }
            else if ( message.contains( "\tnumeratorSpinnerDownButton\t" ) ) {
                numeratorSpinnerDownButton.add( message );
            }
            else if ( message.contains( "\tdenominatorSpinnerDownButton\t" ) ) {
                denominatorSpinnerDownButton.add( message );
            }
            else if ( message.contains( "user\tsliceComponent\tsprite\tendDrag" ) ) {
                sliceComponent.add( message );
            }
        }

        return "Elapsed time: " + timeText + "\n" +
               "Time per tab: " + valuesToStrings( timePerTab ) + "\n" +
               "Clicks Per Tab: " + clicksPerTab + "\n" +
               "Number of clicks on numerator/denominator up/down spinners (tabs 1,3): " + ( numeratorSpinnerUpButton.size() + numeratorSpinnerDownButton.size() + denominatorSpinnerDownButton.size() + denominatorSpinnerUpButton.size() ) + "\n" +
               "Number of clicks on pieces themselves (tabs 1,3): " + sliceComponent.size() + "\n" +
               "INTRO\n" +
               "Clicks Per Intro Representation: " + clicksPerIntroRepresentation + "\n" +
               "Number of intro representations visited: " + visitedIntroRepresentations.size() + "\n" +
               "Visited intro representations: " + visitedIntroRepresentations + "\n" +
               "Time per intro representation: " + valuesToStrings( timePerIntroRepresentation ) + "\n" +
               "EQUALITY LAB:\n" +
               "Time per equality lab representation (left): " + valuesToStrings( timePerEqualityLabLeftRepresentation ) + "\n" +
               "Time per equality lab representations same: " + valuesToStrings( timePerEqualityLabSameRepresentations ) + "\n" +
               "Number of equality lab representations visited: " + visitedEqualityLabRepresentations.size() + "\n" +
               "Visited equality lab representations: " + visitedEqualityLabRepresentations + "\n" +
               "FRACTION LAB:\n" +
               "Time per Fraction Lab Representation :" + valuesToStrings( timePerFractionLabRepresentation ) + "\n" +
               "Clicks per Fraction Lab Representation :" + clicksPerFractionLabRepresentation + "\n" +
               "BUILD A FRACTION:\n" +
               "Build a Fraction Levels\n" + bafTargetResults.mkString( "\n" );
    }

    private void augment( HashMap<String, Integer> map, String key, int newValue ) {
        map.put( key, ( map.containsKey( key ) ? map.get( key ) : 0 ) + newValue );
    }

    private void augment( HashMap<String, Long> map, String key, long newValue ) {
        map.put( key, ( map.containsKey( key ) ? map.get( key ) : 0 ) + newValue );
    }

    private HashMap<String, String> valuesToStrings( HashMap<String, Long> timePerTab ) {
        HashMap<String, String> out = new HashMap<String, String>();
        for ( String key : timePerTab.keySet() ) {
            out.put( key, timePerTab.get( key ) / 1000 + " sec" );
        }
        return out;
    }

    private void displayGraphically( StateRepresentation representation ) {

        HashMap<String, Color> representationPaintHashMap = new HashMap<String, Color>();
        representationPaintHashMap.put( "PIE", new Color( 0x8cc63f ) );
        representationPaintHashMap.put( "HORIZONTAL_BAR", new Color( 0xe94545 ) );
        representationPaintHashMap.put( "VERTICAL_BAR", new Color( 0x57b6dd ) );
        representationPaintHashMap.put( "WATER_GLASSES", new Color( 0xffc800 ) );
        representationPaintHashMap.put( "CAKE", new Color( 0xa55a41 ) );
        representationPaintHashMap.put( "NUMBER_LINE", Color.black );

        HashMap<String, Color> screenTypeHashMap = new HashMap<String, Color>();
        screenTypeHashMap.put( BuildAFractionScreenType.LEVEL_SELECTION.toString(), Color.gray );
        screenTypeHashMap.put( BuildAFractionScreenType.SHAPES.toString(), Color.red );
        screenTypeHashMap.put( BuildAFractionScreenType.NUMBERS.toString(), Color.black );

        reportNode.removeAllChildren();

        PNode gridLines = new PNode();
        reportNode.addChild( gridLines );
        double y = 0;

        double labelInset = 6;

        //Don't shrink
        Function.LinearFunction time = new Function.LinearFunction( representation.startTime, representation.startTime + 60000, 100, 700 );

        List<Record> list = representation.orderedEventsAndPropertyLifetimes();
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
                                         record.property.equals( "tab2.screenType" ) ? screenTypeHashMap.get( record.value ) :
                                         record.property.equals( "tab3.leftRepresentation" ) ? representationPaintHashMap.get( record.value ) :
                                         record.property.equals( "tab3.rightRepresentation" ) ? representationPaintHashMap.get( record.value ) :
                                         record.property.equals( "tab5.representation" ) ? record.value.equals( "PIE" ) ? Color.red : new Color( 0x57b6dd ) :
                                         Color.red;
                            PhetPPath bar = new PhetPPath( new Rectangle2D.Double( time.evaluate( record.timestamp ), y, time.evaluate( maxTime ) - time.evaluate( record.timestamp ), 16 ), fill, new BasicStroke( 1 ), Color.black );
                            bars.add( bar );
                            reportNode.addChild( bar );

                            PText text = new PText( value.toString() );
                            if ( text.getFullBounds().getWidth() < bar.getFullBounds().getWidth() ) {
                                text.setOffset( bar.getFullBounds().getMinX(), bar.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
                                reportNode.addChild( text );
                            }
                        }

                        PText textNode = new PText( recordList.get( 0 ).property );
                        textNode.setOffset( bars.get( 0 ).getFullBounds().getX() - textNode.getFullBounds().getWidth() - labelInset, bars.get( 0 ).getFullBounds().getCenterY() - textNode.getFullBounds().getHeight() / 2 );
                        reportNode.addChild( textNode );
                        y = y + 20;
                    }
                    else if ( recordList.get( 0 ).type.equals( "java.lang.Integer" ) || recordList.get( 0 ).type.equals( "java.lang.Double" ) ) {
                        ArrayList<PNode> segments = new ArrayList<PNode>();
                        for ( int i = 0; i < recordList.size(); i++ ) {
                            PropertyChange record = recordList.get( i );
                            double maxTime = i == recordList.size() - 1 ? representation.endTime : recordList.get( i + 1 ).timestamp;
                            Number value = (Number) record.value;
                            Function.LinearFunction yFunction = record.property.endsWith( ".filledTargets" ) ?
                                                                new Function.LinearFunction( 0, 4, y + 20, y ) :
                                                                recordList.get( 0 ).type.equals( "java.lang.Double" ) ? new Function.LinearFunction( 0, 1, y + 20, y ) :
                                                                new Function.LinearFunction( 0, 8, y + 20, y );
                            PhetPPath bar = new PhetPPath( new Line2D.Double( time.evaluate( record.timestamp ), yFunction.evaluate( value.doubleValue() ), time.evaluate( maxTime ), yFunction.evaluate( value.doubleValue() ) ), new BasicStroke( 2 ), Color.black );
                            segments.add( bar );
                            reportNode.addChild( bar );

                            PText number = new PText( value instanceof Integer ? value + "" : new DecimalFormat( "0.00" ).format( value.doubleValue() ) );
                            if ( number.getFullBounds().getWidth() < bar.getFullBounds().getWidth() ) {
                                number.setOffset( bar.getFullBounds().getMinX(), bar.getFullBounds().getCenterY() - number.getFullBounds().getHeight() / 2 );
                                PhetPPath textBackground = new PhetPPath( RectangleUtils.expand( number.getFullBounds(), 2, 2 ), Color.yellow );
                                reportNode.addChild( textBackground );
                                reportNode.addChild( number );
                            }
                        }
                        PText textNode = new PText( recordList.get( 0 ).property );
                        textNode.setOffset( segments.get( 0 ).getFullBounds().getX() - textNode.getFullBounds().getWidth() - labelInset, segments.get( 0 ).getFullBounds().getCenterY() - textNode.getFullBounds().getHeight() / 2 );
                        reportNode.addChild( textNode );
                        y = y + 30;
                    }
                }
            }
            else if ( r instanceof Event ) {
                Event event = (Event) r;
                //suppress the "sync" message
                if ( event.name.equals( "changed" ) ) {
                    //noop
                }
                else {
                    double radius = 4;
                    double centerX = time.evaluate( event.timestamp );
                    double centerY = y;
                    PNode shape = event.hitTrue() ? new PText( "\u2605" ) {{setTextPaint( Color.black );}} :
                                  event.hitFalse() ? new PText( "X" ) {{setTextPaint( Color.black );}} :
                                  new PhetPPath( new Ellipse2D.Double( 0, 0, radius * 2, radius * 2 ), Color.blue, new BasicStroke( 1 ), Color.black );
                    shape.setOffset( centerX - shape.getFullBounds().getWidth() / 2, centerY - shape.getFullBounds().getHeight() / 2 );
                    PText text = new PText( event.name + ": " + event.parameterList.toString() );
                    text.setOffset( shape.getFullBounds().getMaxX() + 2, shape.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
                    y += 20;
                    reportNode.addChild( shape );
                    reportNode.addChild( text );
                }
            }
        }

        //overlay time ticks
//        int msPerTick = 60000;//one minute
        int msPerTick = 10000;//10 seconds
        for ( long t = representation.startTime + msPerTick; t <= representation.endTime; t = t + msPerTick ) {
            double x = time.evaluate( t );
            double y0 = 0;
            double y1 = y;
            boolean isMinute = ( t - representation.startTime ) % 60000 == 0;
            PhetPPath timeLine = new PhetPPath( new Line2D.Double( x, y0, x, y1 ), new BasicStroke( isMinute ? 2 : 1 ), isMinute ? Color.black : Color.gray );
            gridLines.addChild( timeLine );

            long seconds = ( t - representation.startTime ) / 1000;
            long minute = TimeUnit.SECONDS.toMinutes( seconds );
            long second = TimeUnit.SECONDS.toSeconds( seconds ) - ( TimeUnit.SECONDS.toMinutes( seconds ) * 60 );
            String timeText = minute + ":" + ( second < 10 ? "0" + second : second );
            PText text = new PText( timeText );
            text.setOffset( timeLine.getFullBounds().getCenterX() - text.getFullBounds().getWidth() / 2, timeLine.getFullBounds().getMaxY() + 2 );
            gridLines.addChild( text );
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
                else if ( type.equals( "java.lang.Double" ) ) {
                    value = Double.parseDouble( valueString );
                }
                else {
                    System.out.println( "Unknown type: " + type + ", " + line );
                }
                if ( !properties.containsKey( propertyName ) ) {
                    properties.put( propertyName, new ArrayList<PropertyChange>() );
                }

                ArrayList<PropertyChange> records = properties.get( propertyName );
                records.add( new PropertyChange( propertyName, time, value, type ) );
            }
            else if ( entries.get( 3 ).equals( "event" ) ||
                      ( entries.get( 2 ).equals( "time" ) && entries.get( 3 ).equals( "feature" ) ) ) {
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
        ArrayList<String> parameterList;
        private final HashMap<String, String> parameters;

        public Event( long timestamp, String name, ArrayList<String> parameterList ) {
            this.timestamp = timestamp;
            this.name = name;
            this.parameterList = parameterList;
            parameters = new HashMap<String, String>();
            for ( String parameter : parameterList ) {
                StringTokenizer st = new StringTokenizer( parameter, "=" );
                parameters.put( st.nextToken().trim(), st.nextToken().trim() );
            }
        }

        public long getTime() {
            return timestamp;
        }

        //true if there is a hit parameter and it is true
        public boolean hitTrue() { return parameters.containsKey( "hit" ) && parameters.get( "hit" ).equals( "true" ); }

        public boolean hitFalse() { return parameters.containsKey( "hit" ) && parameters.get( "hit" ).equals( "false" ); }
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

        public ArrayList<Record> orderedEventsAndPropertyDeltas() {
            ArrayList<Record> out = new ArrayList<Record>();
            for ( ArrayList<PropertyChange> propertyChanges : properties.values() ) {
                for ( PropertyChange propertyChange : propertyChanges ) {
                    out.add( propertyChange );
                }
            }
            out.addAll( events );

            Collections.sort( out, new Comparator<Record>() {
                public int compare( Record o1, Record o2 ) {
                    return Double.compare( o1.getTime(), o2.getTime() );
                }
            } );
            return out;
        }

        public ArrayList<Record> orderedEventsAndPropertyLifetimes() {
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
        displayGraphically( representation );
        createTextReport( messages, representation );
    }
}