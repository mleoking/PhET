// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.FractionLabModule;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.fractionmatcher.MatchingGameModule;
import edu.colorado.phet.fractions.fractionsintro.equalitylab.EqualityLabModule;
import edu.colorado.phet.fractions.fractionsintro.intro.FractionsIntroModule;
import edu.colorado.phet.fractions.fractionsintro.intro.view.Representation;
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

    public FractionsIntroStudyNovember2013Application( PhetApplicationConfig config ) {
        super( config );

        addModuleObserver( new ModuleObserver() {
            public void moduleAdded( ModuleEvent event ) {
                report.moduleAdded( event.getModule() );
            }

            public void activeModuleChanged( ModuleEvent event ) {
                module.set( event.getModule() );
                report.setActiveModule( event.getModule() );
            }

            public void moduleRemoved( ModuleEvent event ) {

            }
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
            final PhetPCanvas visualizationCanvas = new PhetPCanvas();
            visualizationCanvas.addScreenChild( new PText( "Hello" ) );
            canvasFrame.setContentPane( visualizationCanvas );
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
            final Property<Function.LinearFunction> timeScalingFunction = new Property<Function.LinearFunction>( new Function.LinearFunction( startTime, startTime + 10000, 0, 800 ) );

            final EnumPropertyNode<Module> moduleNode = new EnumPropertyNode<Module>( module, modulePaintHashMap, 5, timeScalingFunction, addTickListener );
            HashMap<Representation, Paint> representationPaintHashMap = new HashMap<Representation, Paint>();
            representationPaintHashMap.put( Representation.PIE, Color.green );
            representationPaintHashMap.put( Representation.HORIZONTAL_BAR, Color.blue );
            representationPaintHashMap.put( Representation.VERTICAL_BAR, Color.red );
            representationPaintHashMap.put( Representation.CAKE, Color.darkGray );
            representationPaintHashMap.put( Representation.WATER_GLASSES, Color.yellow );
            representationPaintHashMap.put( Representation.NUMBER_LINE, Color.black );
            final EnumPropertyNode<Representation> representationNode = new EnumPropertyNode<Representation>( introModule.model.representation, representationPaintHashMap, 15, timeScalingFunction, addTickListener );
            final NumericPropertyNode<Integer> denominator = new NumericPropertyNode<Integer>( introModule.model.denominator, new Function.LinearFunction( 1, 8, 50, 25 ), timeScalingFunction, addTickListener );
            final NumericPropertyNode<Integer> numerator = new NumericPropertyNode<Integer>( introModule.model.numerator, new Function.LinearFunction( 1, 48, 300, 60 ), timeScalingFunction, addTickListener );
            final NumericPropertyNode<Integer> max = new NumericPropertyNode<Integer>( introModule.model.maximum, new Function.LinearFunction( 1, 6, 350, 310 ), timeScalingFunction, addTickListener );
            visualizationCanvas.addScreenChild( moduleNode );
            visualizationCanvas.addScreenChild( representationNode );
            visualizationCanvas.addScreenChild( denominator );
            visualizationCanvas.addScreenChild( numerator );
            visualizationCanvas.addScreenChild( max );

            Timer t = new Timer( 1000, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    report.update();
                    reportArea.setText( report.toString() );
                    for ( VoidFunction0 listener : tickListeners ) {
                        listener.apply();
                    }
                    if ( System.currentTimeMillis() - startTime > 10000 ) {
                        timeScalingFunction.set( new Function.LinearFunction( startTime, System.currentTimeMillis(), 0, 800 ) );
                    }
                }
            } );
            t.start();
        }
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", FractionsIntroStudyNovember2013Application.class );
    }
}