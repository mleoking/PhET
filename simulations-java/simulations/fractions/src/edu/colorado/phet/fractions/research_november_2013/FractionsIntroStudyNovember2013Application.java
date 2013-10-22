// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.ModuleEvent;
import edu.colorado.phet.common.phetcommon.application.ModuleObserver;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.FractionLabModule;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.fractionmatcher.MatchingGameModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing;
import edu.colorado.phet.fractions.fractionsintro.equalitylab.EqualityLabModule;
import edu.colorado.phet.fractions.fractionsintro.intro.FractionsIntroModule;
import edu.colorado.phet.fractions.fractionsintro.intro.view.Representation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDebug;

/**
 * "Fractions Intro" PhET Application
 *
 * @author Sam Reid
 */
public class FractionsIntroStudyNovember2013Application extends PiccoloPhetApplication implements ResearchApplication {

    //Global flag for whether this functionality should be enabled
    public static boolean recordRegressionData;
    public static FractionsIntroStudyNovember2013Application instance;
    private final Property<String> module;
    private final Property<Boolean> windowNotIconified;
    private final Property<Boolean> windowActive;
    private final FractionsIntroModule introModule;
    private final Property<String> introRepresentation;
    private final Property<Integer> totalClicks;
    private final BuildAFractionModule buildAFractionModule;
    private Function0<Long> timeFunction = new Function0<Long>() {
        public Long apply() {
            return System.currentTimeMillis();
        }
    };

    public FractionsIntroStudyNovember2013Application( PhetApplicationConfig config ) {
        super( config );
        instance = this;

        //New properties for recording
        totalClicks = new Property<Integer>( 0 );

        addModuleObserver( new ModuleObserver() {
            public void moduleAdded( ModuleEvent event ) {
//                report.moduleAdded( event.getModule() );
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
                module.set( event.getModule().getName() );
//                report.setActiveModule( event.getModule() );
            }

            public void moduleRemoved( ModuleEvent event ) {

            }
        } );
        windowActive = new Property<Boolean>( true );
        windowNotIconified = new Property<Boolean>( true );

        //See code in PhetFrame
        getPhetFrame().addWindowListener( new WindowAdapter() {
            public void windowIconified( WindowEvent e ) { windowNotIconified.set( false ); }

            public void windowDeiconified( WindowEvent e ) { windowNotIconified.set( true ); }

            public void windowActivated( WindowEvent e ) { windowActive.set( true ); }

            public void windowDeactivated( WindowEvent e ) {windowActive.set( false );}
        } );

        //Another way to do this would be to pass a FunctionInvoker to all the modules
        recordRegressionData = config.hasCommandLineArg( "-recordRegressionData" );
        introModule = new FractionsIntroModule();
        addModule( introModule );
        final BooleanProperty audioEnabled = new BooleanProperty( true );
        buildAFractionModule = new BuildAFractionModule( new BuildAFractionModel( new BooleanProperty( false ), audioEnabled ) );
        addModule( buildAFractionModule );
        EqualityLabModule equalityLabModule = new EqualityLabModule();
        addModule( equalityLabModule );
        MatchingGameModule matchingGameModule = new MatchingGameModule( config.isDev(), audioEnabled );
        addModule( matchingGameModule );
        FractionLabModule fractionLabModule = new FractionLabModule( false );
        addModule( fractionLabModule );

        module = new Property<String>( getModule( 0 ).getName() );
        introRepresentation = new Property<String>( introModule.model.representation.get().toString() );
        introModule.model.representation.addObserver( new VoidFunction1<Representation>() {
            public void apply( Representation representation ) {
                introRepresentation.set(
                        representation.equals( Representation.CAKE ) ? "Cake" :
                        representation.equals( Representation.HORIZONTAL_BAR ) ? "HorizontalBar" :
                        representation.equals( Representation.NUMBER_LINE ) ? "NumberLine" :
                        representation.equals( Representation.PIE ) ? "Pie" :
                        representation.equals( Representation.VERTICAL_BAR ) ? "VerticalBar" :
                        representation.equals( Representation.WATER_GLASSES ) ? "WaterGlasses" :
                        null
                );
            }
        } );
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

        trackState( "window.up", windowNotIconified() );

        trackState( "window.up", this.windowNotIconified() );
        trackState( "window.active", this.windowActive() );
        trackState( "tab", this.module() );
        trackState( "tab1.rep", this.introRepresentation() );
        trackState( "tab1.denominator", this.introDenominator() );
        trackState( "tab1.numerator", this.introNumerator() );
        trackState( "tab1.max", this.introMaximum() );
        trackState( "clicks", this.totalClicks() );
        trackState( "tab2.screen", this.bafScreenType() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", FractionsIntroStudyNovember2013Application.class );
        new ApplicationVisualization( FractionsIntroStudyNovember2013Application.instance );
    }

    private void trackState( final String name, ObservableProperty property ) {
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
    }

    public ObservableProperty<Boolean> windowNotIconified() { return windowNotIconified; }

    public ObservableProperty<Boolean> windowActive() { return windowActive; }

    public ObservableProperty<String> module() { return module;}

    public ObservableProperty<String> introRepresentation() {
        return introRepresentation;
    }

    public ObservableProperty<Integer> introDenominator() {
        return introModule.model.denominator;
    }

    public ObservableProperty<Integer> introNumerator() {
        return introModule.model.numerator;
    }

    public ObservableProperty<Integer> introMaximum() {
        return introModule.model.maximum;
    }

    public ObservableProperty<Integer> totalClicks() {
        return totalClicks;
    }

    public ObservableProperty bafScreenType() {
        return buildAFractionModule.canvas.screenType;
    }

    public void addBAFLevelStartedListener( VoidFunction1<PNode> listener ) {
        buildAFractionModule.canvas.addLevelStartedListener( listener );
    }

    public Function0<Long> time() {
        return timeFunction;
    }

    public Function0<Long> endTime() {
        return timeFunction;
    }
}