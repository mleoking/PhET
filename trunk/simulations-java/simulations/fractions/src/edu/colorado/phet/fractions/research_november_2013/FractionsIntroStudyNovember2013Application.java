// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

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
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.FractionLabModule;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionScreenType;
import edu.colorado.phet.fractions.fractionmatcher.MatchingGameModule;
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
            VoidFunction1<VoidFunction0> addTickListener = new VoidFunction1<VoidFunction0>() {
                public void apply( VoidFunction0 voidFunction0 ) {
                    tickListeners.add( voidFunction0 );
                }
            };
            final long startTime = System.currentTimeMillis();
            final Property<Function.LinearFunction> timeScalingFunction = new Property<Function.LinearFunction>( new Function.LinearFunction( startTime, startTime + 60000, 0, 800 ) );

            HashMap<Representation, Paint> representationPaintHashMap = new HashMap<Representation, Paint>();
            representationPaintHashMap.put( Representation.PIE, new Color( 0x8cc63f ) );
            representationPaintHashMap.put( Representation.HORIZONTAL_BAR, new Color( 0xe94545 ) );
            representationPaintHashMap.put( Representation.VERTICAL_BAR, new Color( 0x57b6dd ) );
            representationPaintHashMap.put( Representation.WATER_GLASSES, new Color( 0xffc800 ) );
            representationPaintHashMap.put( Representation.CAKE, new Color( 0xa55a41 ) );
            representationPaintHashMap.put( Representation.NUMBER_LINE, Color.black );

            Function1<Boolean, Paint> booleanPaintMap = new Function1<Boolean, Paint>() {
                public Paint apply( Boolean b ) {
                    return b ? Color.green : Color.gray;
                }
            };
            addVariable( "window up", new EnumPropertyNode<Boolean>( windowNotIconified, booleanPaintMap, 6, timeScalingFunction, addTickListener ) );
            addVariable( "window active", new EnumPropertyNode<Boolean>( windowActive, booleanPaintMap, 16, timeScalingFunction, addTickListener ) );
            addVariable( "tab", new EnumPropertyNode<Module>( module, toFunction( modulePaintHashMap ), 30, timeScalingFunction, addTickListener ) );
            addVariable( "tab1.rep", new EnumPropertyNode<Representation>( introModule.model.representation, toFunction( representationPaintHashMap ), 40, timeScalingFunction, addTickListener ) );
            addVariable( "tab1.denominator", new NumericPropertyNode<Integer>( introModule.model.denominator, new Function.LinearFunction( 1, 8, 80, 50 ), timeScalingFunction, addTickListener ) );
            addVariable( "tab1.numerator", new NumericPropertyNode<Integer>( introModule.model.numerator, new Function.LinearFunction( 1, 48, 300, 90 ), timeScalingFunction, addTickListener ) );
            addVariable( "tab1.max", new NumericPropertyNode<Integer>( introModule.model.maximum, new Function.LinearFunction( 1, 6, 340, 300 ), timeScalingFunction, addTickListener ) );
            addVariable( "", new EventOverlayNode<Integer>( totalClicks, 0, 600, timeScalingFunction, addTickListener ) );
            addVariable( "tab2.screen", new EnumPropertyNode<BuildAFractionScreenType>( buildAFractionModule.canvas.screenType, new Function1<BuildAFractionScreenType, Paint>() {
                public Paint apply( BuildAFractionScreenType type ) {
                    return type.equals( BuildAFractionScreenType.LEVEL_SELECTION ) ? Color.green :
                           type.equals( BuildAFractionScreenType.SHAPES ) ? Color.red :
                           Color.black;
                }
            }, 350, timeScalingFunction, addTickListener ) );

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

    private void addVariable( String name, PNode node ) {
        reportCanvas.addScreenChild( node );
        node.setOffset( 100, 0 );
        PText text = new PText( name );
        text.setOffset( 0, node.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
        reportCanvas.addScreenChild( text );
    }
}