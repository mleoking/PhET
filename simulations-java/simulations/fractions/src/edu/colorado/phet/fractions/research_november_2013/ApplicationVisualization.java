// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import fj.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.property.*;
import edu.colorado.phet.common.phetcommon.simsharing.*;
import edu.colorado.phet.common.phetcommon.simsharing.messages.*;
import edu.colorado.phet.common.phetcommon.util.function.*;
import edu.colorado.phet.common.piccolophet.*;
import edu.colorado.phet.fractions.buildafraction.model.numbers.*;
import edu.colorado.phet.fractions.buildafraction.model.shapes.*;
import edu.colorado.phet.fractions.buildafraction.view.*;
import edu.colorado.phet.fractions.buildafraction.view.numbers.*;
import edu.colorado.phet.fractions.buildafraction.view.shapes.*;
import edu.colorado.phet.fractions.fractionmatcher.view.*;
import edu.colorado.phet.fractions.fractionsintro.*;
import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.nodes.*;

/**
 * Created by Sam on 10/21/13.
 */
public class ApplicationVisualization {
    public static Report report = new Report();
    private final TextArea reportArea = new TextArea();
    private final PhetPCanvas reportCanvas = new PhetPCanvas();
    private final ResearchApplication app;

    public ApplicationVisualization( final ResearchApplication app ) {
        this.app = app;
        JFrame frame = new JFrame( "Report" );
        frame.setContentPane( new JScrollPane( reportArea ) );
        frame.setSize( 800, 600 );
        frame.setVisible( true );

        JFrame canvasFrame = new JFrame( "Visualization" );
        canvasFrame.setContentPane( reportCanvas );
        canvasFrame.setSize( 800, 600 );
        canvasFrame.setLocation( 0, 400 );
        canvasFrame.setVisible( true );

        HashMap<String, Paint> modulePaintHashMap = new HashMap<String, Paint>();
        modulePaintHashMap.put( "Intro", Color.blue );
        modulePaintHashMap.put( "Build a Fraction", Color.red );
        modulePaintHashMap.put( "Equality Lab", Color.green );
        modulePaintHashMap.put( "Matching Game", Color.yellow );
        modulePaintHashMap.put( "Fraction Lab", Color.magenta );

        final ArrayList<VoidFunction0> tickListeners = new ArrayList<VoidFunction0>();
        final VoidFunction1<VoidFunction0> addTickListener = new

                VoidFunction1<VoidFunction0>() {
                    public void apply( VoidFunction0 voidFunction0 ) {
                        tickListeners.add( voidFunction0 );
                    }
                };
        final long startTime = app.time().apply();
        System.out.println( "startTime = " + startTime );
        final Property<Function.LinearFunction> timeScalingFunction = new Property<Function.LinearFunction>( new Function.LinearFunction( startTime, startTime + 60000, 0, 700 ) );

        HashMap<String, Paint> representationPaintHashMap = new HashMap<String, Paint>();
        representationPaintHashMap.put( "Pie", new Color( 0x8cc63f ) );
        representationPaintHashMap.put( "HorizontalBar", new Color( 0xe94545 ) );
        representationPaintHashMap.put( "VerticalBar", new Color( 0x57b6dd ) );
        representationPaintHashMap.put( "WaterGlasses", new Color( 0xffc800 ) );
        representationPaintHashMap.put( "Cake", new Color( 0xa55a41 ) );
        representationPaintHashMap.put( "NumberLine", Color.black );

        final Function1<Boolean, Paint> booleanPaintMap = new

                Function1<Boolean, Paint>() {
                    public Paint apply( Boolean b ) {
                        return b ? Color.green : Color.gray;
                    }
                };
        addVariable( "window.up", app.windowNotIconified(), new EnumPropertyNode<Boolean>( app.windowNotIconified(), booleanPaintMap, 6.0, timeScalingFunction, addTickListener, app.time() ) );
        addVariable( "window.active", app.windowActive(), new EnumPropertyNode<Boolean>( app.windowActive(), booleanPaintMap, 16.0, timeScalingFunction, addTickListener, app.time() ) );
        addVariable( "tab", app.module(), new EnumPropertyNode<String>( app.module(), toFunction( modulePaintHashMap ), 30.0, timeScalingFunction, addTickListener, app.time() ) );
        addVariable( "tab1.rep", app.introRepresentation(), new EnumPropertyNode<String>( app.introRepresentation(), toFunction( representationPaintHashMap ), 40.0, timeScalingFunction, addTickListener, app.time() ) );
        addVariable( "tab1.denominator", app.introDenominator(), new NumericPropertyNode<Integer>( app.introDenominator(), new Function.LinearFunction( 1, 8, 80, 50 ), timeScalingFunction, addTickListener ) );
        addVariable( "tab1.numerator", app.introNumerator(), new NumericPropertyNode<Integer>( app.introNumerator(), new Function.LinearFunction( 1, 48, 300, 90 ), timeScalingFunction, addTickListener ) );
        addVariable( "tab1.max", app.introMaximum(), new NumericPropertyNode<Integer>( app.introMaximum(), new Function.LinearFunction( 1, 6, 340, 300 ), timeScalingFunction, addTickListener ) );
        addVariable( "clicks", app.totalClicks(), new EventOverlayNode<Integer>( app.totalClicks(), 0, 600, timeScalingFunction, addTickListener ) );
        addVariable( "tab2.screen", app.bafScreenType(), new EnumPropertyNode<BuildAFractionScreenType>( app.bafScreenType(), new Function1<BuildAFractionScreenType, Paint>() {
            public Paint apply( BuildAFractionScreenType type ) {
                return type.equals( BuildAFractionScreenType.LEVEL_SELECTION ) ? Color.green :
                       type.equals( BuildAFractionScreenType.SHAPES ) ? Color.red :
                       Color.black;
            }
        }, 350.0, timeScalingFunction, addTickListener, app.time() ) );

        //Keep track of levels started so we can easily create a full state matrix at the end of a run (knowing the number of levels started)
        final Property<Integer> buildAFractionLevelsStarted = new Property<Integer>( 0 );

        addVariable( "tab2.levelsStarted", buildAFractionLevelsStarted, new NumericPropertyNode<Integer>( buildAFractionLevelsStarted, new Function.LinearFunction( 0, 10, 400, 360 ), timeScalingFunction, addTickListener ) );

        final Property<Double> y = new Property<Double>( 415.0 );
        //when a level is started, show values for it.

        //todo: show star lines?
        app.addBAFLevelStartedListener( new VoidFunction1<PNode>() {
            public void apply( PNode node ) {
                buildAFractionLevelsStarted.set( buildAFractionLevelsStarted.get() + 1 );

                //TODO: How to record newly created properties?  Will it get recorded in addVariable?
                Property<Boolean> started = new Property<Boolean>( false );
                final EnumPropertyNode<Boolean> levelNode = new EnumPropertyNode<Boolean>( started, booleanPaintMap, y.get(), timeScalingFunction, addTickListener, app.time() );
                final String levelKey = "tab2.levelIndex." + buildAFractionLevelsStarted.get();
                addVariable( levelKey, started, levelNode );
                started.set( true );

                if ( node instanceof ShapeSceneNode ) {
                    ShapeSceneNode shapeSceneNode = (ShapeSceneNode) node;
                    ShapeLevel level = shapeSceneNode.level;
                    String targetString = level.targets.toString();
                    //TODO: Send event
                    PText description = new PText( targetString );
                    addEventNode( targetString, description, levelNode.getFullBounds().getCenterY() - description.getFullBounds().getHeight() / 2, timeScalingFunction );
                    shapeSceneNode.dropListeners.add( new VoidFunction1<ShapeSceneNode.DropResult>() {
                        public void apply( ShapeSceneNode.DropResult dropResult ) {
                            String dropResultText = ( dropResult.hit ? "\u2605" : "x" ) + dropResult.source + " in " + dropResult.target;
                            PText text = new PText( dropResultText );
                            addEventNode( levelKey + " " + dropResultText, text, levelNode.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2, timeScalingFunction );
                        }
                    } );
                }
                else if ( node instanceof NumberSceneNode ) {
                    NumberSceneNode numberSceneNode = (NumberSceneNode) node;
                    NumberLevel level = numberSceneNode.level;

                    //TODO: Send event
                    String targetString = level.targets.map( new F<NumberTarget, Object>() {
                        @Override public Object f( NumberTarget numberTarget ) {
                            return numberTarget.mixedFraction + " : " + numberTarget.filledPattern.map( new F<FilledPattern, Object>() {
                                @Override public Object f( FilledPattern filledPattern ) {
                                    return filledPattern.type;
                                }
                            } );
                        }
                    } ).toString();
                    PText description = new PText( targetString );
                    addEventNode( targetString, description, levelNode.getFullBounds().getCenterY() - description.getFullBounds().getHeight() / 2, timeScalingFunction );

                    numberSceneNode.dropListeners.add( new VoidFunction1<ShapeSceneNode.DropResult>() {
                        public void apply( ShapeSceneNode.DropResult dropResult ) {
                            String dropResultText = ( dropResult.hit ? "\u2605" : "x" ) + dropResult.source + " in " + dropResult.target;
                            PText text = new PText( dropResultText );
                            addEventNode( levelKey + " " + dropResultText, text, levelNode.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2, timeScalingFunction );
                        }
                    } );
                }

                y.set( y.get() + 12 );
            }
        } );

        if ( app instanceof FractionsIntroStudyNovember2013Application ) {
            Timer t = new Timer( 1000, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    report.update();
                    reportArea.setText( report.toString() );
                    for ( VoidFunction0 listener : tickListeners ) {
                        listener.apply();
                    }
                    if ( app.time().apply() - startTime > 60000 ) {
                        timeScalingFunction.set( new Function.LinearFunction( startTime, app.time().apply(), 0, 700 ) );
                    }
                }
            } );
            t.start();
        }
    }

    private static <T> Function1<T, Paint> toFunction( final HashMap<T, Paint> map ) {
        return new Function1<T, Paint>() {
            public Paint apply( T t ) {
                return map.get( t );
            }
        };
    }

    private void addEventNode( String name, final PNode node, final double y, final Property<Function.LinearFunction> timeScalingFunction ) {
        final long time = app.time().apply();
        reportCanvas.addScreenChild( node );
        timeScalingFunction.addObserver( new VoidFunction1<Function.LinearFunction>() {
            public void apply( Function.LinearFunction linearFunction ) {
                node.setOffset( 100 + linearFunction.evaluate( time ), y );
            }
        } );
        SimSharingManager.sendModelMessage( FractionsIntroSimSharing.ModelComponents.event, FractionsIntroSimSharing.ModelComponentTypes.event, FractionsIntroSimSharing.ModelActions.occurred,
                                            ParameterSet.parameterSet( ParameterKeys.name, name ) );
    }

    private void addVariable( final String name, ObservableProperty property, PNode node ) {
        property.addObserver( new VoidFunction1() {
            public void apply( final Object o ) {

                SimSharingManager.sendModelMessage(
                        new IModelComponent() {
                            @Override public String toString() {
                                return name;
                            }
                        },
                        FractionsIntroSimSharing.ModelComponentTypes.property,
                        FractionsIntroSimSharing.ModelActions.changed,
                        ParameterSet.parameterSet( ParameterKeys.value, new Function1<Object, String>() {
                            public String apply( Object o ) {
                                return o instanceof Module ? ( (Module) o ).getName() : o.toString();
                            }
                        }.apply( o ) ).with( ParameterKeys.type, o.getClass().getName() )
                );
            }
        } );
        reportCanvas.addScreenChild( node );
        node.setOffset( 100, 0 );
        PText text = new PText( name );
        text.setOffset( 0, node.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
        reportCanvas.addScreenChild( text );
    }
}