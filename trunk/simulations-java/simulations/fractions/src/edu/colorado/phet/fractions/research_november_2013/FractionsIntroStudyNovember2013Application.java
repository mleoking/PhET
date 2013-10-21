// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import fj.F;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.ModuleEvent;
import edu.colorado.phet.common.phetcommon.application.ModuleObserver;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.FractionLabModule;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionScreenType;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberSceneNode;
import edu.colorado.phet.fractions.buildafraction.view.shapes.ShapeSceneNode;
import edu.colorado.phet.fractions.fractionmatcher.MatchingGameModule;
import edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing;
import edu.colorado.phet.fractions.fractionsintro.equalitylab.EqualityLabModule;
import edu.colorado.phet.fractions.fractionsintro.intro.FractionsIntroModule;
import edu.colorado.phet.fractions.fractionsintro.intro.view.Representation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDebug;

/**
 * "Fractions Intro" PhET Application
 *
 * @author Sam Reid
 */
public class FractionsIntroStudyNovember2013Application extends PiccoloPhetApplication {

    //Global flag for whether this functionality should be enabled
    public static boolean recordRegressionData;
    public static boolean isReport = true;
    public static Report report = new Report();
    private final TextArea reportArea = new TextArea();
    private final Property<Module> module;
    private final PhetPCanvas reportCanvas = new PhetPCanvas();

    public FractionsIntroStudyNovember2013Application( PhetApplicationConfig config ) {
        super( config );

        //New properties for recording
        final Property<Integer> totalClicks = new Property<Integer>( 0 );

        addModuleObserver( new ModuleObserver() {
            public void moduleAdded( ModuleEvent event ) {
                report.moduleAdded( event.getModule() );
                event.getModule().getSimulationPanel().addMouseListener( new MouseListener() {
                    public void mouseClicked( MouseEvent e ) {

                    }

                    public void mousePressed( MouseEvent e ) {
                        totalClicks.set( totalClicks.get() + 1 );
                    }

                    public void mouseReleased( MouseEvent e ) {

                    }

                    public void mouseEntered( MouseEvent e ) {

                    }

                    public void mouseExited( MouseEvent e ) {

                    }
                } );
            }

            public void activeModuleChanged( ModuleEvent event ) {
                module.set( event.getModule() );
                report.setActiveModule( event.getModule() );
            }

            public void moduleRemoved( ModuleEvent event ) {

            }
        } );
        final Property<Boolean> windowActive = new Property<Boolean>( true );
        final Property<Boolean> windowNotIconified = new Property<Boolean>( true );

        //See code in PhetFrame
        getPhetFrame().addWindowListener( new WindowAdapter() {
            public void windowIconified( WindowEvent e ) { windowNotIconified.set( false ); }

            public void windowDeiconified( WindowEvent e ) { windowNotIconified.set( true ); }

            public void windowActivated( WindowEvent e ) { windowActive.set( true ); }

            public void windowDeactivated( WindowEvent e ) {windowActive.set( false );}
        } );

        //Another way to do this would be to pass a FunctionInvoker to all the modules
        recordRegressionData = config.hasCommandLineArg( "-recordRegressionData" );
        FractionsIntroModule introModule = new FractionsIntroModule();
        addModule( introModule );
        final BooleanProperty audioEnabled = new BooleanProperty( true );
        BuildAFractionModule buildAFractionModule = new BuildAFractionModule( new BuildAFractionModel( new BooleanProperty( false ), audioEnabled ) );
        addModule( buildAFractionModule );
        EqualityLabModule equalityLabModule = new EqualityLabModule();
        addModule( equalityLabModule );
        MatchingGameModule matchingGameModule = new MatchingGameModule( config.isDev(), audioEnabled );
        addModule( matchingGameModule );
        FractionLabModule fractionLabModule = new FractionLabModule( false );
        addModule( fractionLabModule );

        module = new Property<Module>( getModule( 0 ) );
        //Add developer menu items for debugging performance, see #3314

        getPhetFrame().getDeveloperMenu().add( new JCheckBoxMenuItem( "PDebug.regionManagement", PDebug.debugRegionManagement ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    PDebug.debugRegionManagement = isSelected();
                }
            } );
        }} );

        getPhetFrame().getDeveloperMenu().add( new JCheckBoxMenuItem( "PDebug.debugFullBounds", PDebug.debugFullBounds ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    PDebug.debugFullBounds = isSelected();
                }
            } );
        }} );

        getPhetFrame().getDeveloperMenu().add( new JCheckBoxMenuItem( "PDebug.debugBounds", PDebug.debugBounds ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    PDebug.debugBounds = isSelected();
                }
            } );
        }} );

        getPhetFrame().getDeveloperMenu().add( new JCheckBoxMenuItem( "PDebug.debugPaintCalls", PDebug.debugPaintCalls ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    PDebug.debugPaintCalls = isSelected();
                }
            } );
        }} );

        if ( isReport ) {
            JFrame frame = new JFrame( "Report" );
            frame.setContentPane( new JScrollPane( reportArea ) );
            frame.setSize( 800, 600 );
            frame.setVisible( true );

            JFrame canvasFrame = new JFrame( "Visualization" );
            canvasFrame.setContentPane( reportCanvas );
            canvasFrame.setSize( 800, 600 );
            canvasFrame.setLocation( 0, 400 );
            canvasFrame.setVisible( true );

            HashMap<Module, Paint> modulePaintHashMap = new HashMap<Module, Paint>();
            modulePaintHashMap.put( introModule, Color.blue );
            modulePaintHashMap.put( buildAFractionModule, Color.red );
            modulePaintHashMap.put( equalityLabModule, Color.green );
            modulePaintHashMap.put( matchingGameModule, Color.yellow );
            modulePaintHashMap.put( fractionLabModule, Color.magenta );

            final ArrayList<VoidFunction0> tickListeners = new ArrayList<VoidFunction0>();
            final VoidFunction1<VoidFunction0> addTickListener = new VoidFunction1<VoidFunction0>() {
                public void apply( VoidFunction0 voidFunction0 ) {
                    tickListeners.add( voidFunction0 );
                }
            };
            final long startTime = System.currentTimeMillis();
            final Property<Function.LinearFunction> timeScalingFunction = new Property<Function.LinearFunction>( new Function.LinearFunction( startTime, startTime + 60000, 0, 700 ) );

            HashMap<Representation, Paint> representationPaintHashMap = new HashMap<Representation, Paint>();
            representationPaintHashMap.put( Representation.PIE, new Color( 0x8cc63f ) );
            representationPaintHashMap.put( Representation.HORIZONTAL_BAR, new Color( 0xe94545 ) );
            representationPaintHashMap.put( Representation.VERTICAL_BAR, new Color( 0x57b6dd ) );
            representationPaintHashMap.put( Representation.WATER_GLASSES, new Color( 0xffc800 ) );
            representationPaintHashMap.put( Representation.CAKE, new Color( 0xa55a41 ) );
            representationPaintHashMap.put( Representation.NUMBER_LINE, Color.black );

            final Function1<Boolean, Paint> booleanPaintMap = new Function1<Boolean, Paint>() {
                public Paint apply( Boolean b ) {
                    return b ? Color.green : Color.gray;
                }
            };
            addVariable( "window.up", windowNotIconified, new EnumPropertyNode<Boolean>( windowNotIconified, booleanPaintMap, 6.0, timeScalingFunction, addTickListener ) );
            addVariable( "window.active", windowActive, new EnumPropertyNode<Boolean>( windowActive, booleanPaintMap, 16.0, timeScalingFunction, addTickListener ) );
            addVariable( "tab", module, new EnumPropertyNode<Module>( module, toFunction( modulePaintHashMap ), 30.0, timeScalingFunction, addTickListener ) );
            addVariable( "tab1.rep", introModule.model.representation, new EnumPropertyNode<Representation>( introModule.model.representation, toFunction( representationPaintHashMap ), 40.0, timeScalingFunction, addTickListener ) );
            addVariable( "tab1.denominator", introModule.model.denominator, new NumericPropertyNode<Integer>( introModule.model.denominator, new Function.LinearFunction( 1, 8, 80, 50 ), timeScalingFunction, addTickListener ) );
            addVariable( "tab1.numerator", introModule.model.numerator, new NumericPropertyNode<Integer>( introModule.model.numerator, new Function.LinearFunction( 1, 48, 300, 90 ), timeScalingFunction, addTickListener ) );
            addVariable( "tab1.max", introModule.model.maximum, new NumericPropertyNode<Integer>( introModule.model.maximum, new Function.LinearFunction( 1, 6, 340, 300 ), timeScalingFunction, addTickListener ) );
            addVariable( "clicks", totalClicks, new EventOverlayNode<Integer>( totalClicks, 0, 600, timeScalingFunction, addTickListener ) );
            addVariable( "tab2.screen", buildAFractionModule.canvas.screenType, new EnumPropertyNode<BuildAFractionScreenType>( buildAFractionModule.canvas.screenType, new Function1<BuildAFractionScreenType, Paint>() {
                public Paint apply( BuildAFractionScreenType type ) {
                    return type.equals( BuildAFractionScreenType.LEVEL_SELECTION ) ? Color.green :
                           type.equals( BuildAFractionScreenType.SHAPES ) ? Color.red :
                           Color.black;
                }
            }, 350.0, timeScalingFunction, addTickListener ) );

            //Keep track of levels started so we can easily create a full state matrix at the end of a run (knowing the number of levels started)
            final Property<Integer> buildAFractionLevelsStarted = new Property<Integer>( 0 );

            addVariable( "tab2.levelsStarted", buildAFractionLevelsStarted, new NumericPropertyNode<Integer>( buildAFractionLevelsStarted, new Function.LinearFunction( 0, 10, 400, 360 ), timeScalingFunction, addTickListener ) );

            final Property<Double> y = new Property<Double>( 415.0 );
            //when a level is started, show values for it.

            //todo: show star lines?
            buildAFractionModule.canvas.addLevelStartedListener( new VoidFunction1<PNode>() {
                public void apply( PNode node ) {
                    buildAFractionLevelsStarted.set( buildAFractionLevelsStarted.get() + 1 );

                    //TODO: How to record newly created properties?  Will it get recorded in addVariable?
                    Property<Boolean> started = new Property<Boolean>( false );
                    final EnumPropertyNode<Boolean> levelNode = new EnumPropertyNode<Boolean>( started, booleanPaintMap, y.get(), timeScalingFunction, addTickListener );
                    addVariable( "tab2.levelIndex." + buildAFractionLevelsStarted.get(), started, levelNode );
                    started.set( true );

                    if ( node instanceof ShapeSceneNode ) {
                        ShapeSceneNode shapeSceneNode = (ShapeSceneNode) node;
                        ShapeLevel level = shapeSceneNode.level;
                        String targetString = level.targets.toString();
                        //TODO: Send event
                        PText description = new PText( targetString );
                        addEventNode( description, levelNode.getFullBounds().getCenterY() - description.getFullBounds().getHeight() / 2, timeScalingFunction );
                        shapeSceneNode.dropListeners.add( new VoidFunction1<ShapeSceneNode.DropResult>() {
                            public void apply( ShapeSceneNode.DropResult dropResult ) {
                                PText text = new PText( ( dropResult.hit ? "\u2605" : "x" ) + dropResult.source + " in " + dropResult.target );
                                addEventNode( text, levelNode.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2, timeScalingFunction );
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
                        addEventNode( description, levelNode.getFullBounds().getCenterY() - description.getFullBounds().getHeight() / 2, timeScalingFunction );

                        numberSceneNode.dropListeners.add( new VoidFunction1<ShapeSceneNode.DropResult>() {
                            public void apply( ShapeSceneNode.DropResult dropResult ) {
                                PText text = new PText( ( dropResult.hit ? "\u2605" : "x" ) + dropResult.source + " in " + dropResult.target );
                                addEventNode( text, levelNode.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2, timeScalingFunction );
                            }
                        } );
                    }

                    y.set( y.get() + 12 );
                }
            } );

            Timer t = new Timer( 1000, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    report.update();
                    reportArea.setText( report.toString() );
                    for ( VoidFunction0 listener : tickListeners ) {
                        listener.apply();
                    }
                    if ( System.currentTimeMillis() - startTime > 60000 ) {
                        timeScalingFunction.set( new Function.LinearFunction( startTime, System.currentTimeMillis(), 0, 700 ) );
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

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", FractionsIntroStudyNovember2013Application.class );
    }

    private void addEventNode( final PNode node, final double y, final Property<Function.LinearFunction> timeScalingFunction ) {
        final long time = System.currentTimeMillis();
        reportCanvas.addScreenChild( node );
        timeScalingFunction.addObserver( new VoidFunction1<Function.LinearFunction>() {
            public void apply( Function.LinearFunction linearFunction ) {
                node.setOffset( 100 + linearFunction.evaluate( time ), y );
            }
        } );
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
                        }.apply( o ) )
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