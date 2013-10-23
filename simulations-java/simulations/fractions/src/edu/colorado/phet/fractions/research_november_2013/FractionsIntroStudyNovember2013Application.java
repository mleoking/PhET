// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import fj.F;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.Timer;

import edu.colorado.phet.common.games.GameSimSharing;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.ModuleEvent;
import edu.colorado.phet.common.phetcommon.application.ModuleObserver;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.Log;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction3;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.FractionLabModule;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.model.Level;
import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel;
import edu.colorado.phet.fractions.buildafraction.view.SceneNode;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberSceneNode;
import edu.colorado.phet.fractions.buildafraction.view.shapes.ShapeSceneNode;
import edu.colorado.phet.fractions.fractionmatcher.MatchingGameModule;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameState;
import edu.colorado.phet.fractions.fractionmatcher.model.MovableFraction;
import edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing;
import edu.colorado.phet.fractions.fractionsintro.equalitylab.EqualityLabModule;
import edu.colorado.phet.fractions.fractionsintro.intro.FractionsIntroModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDebug;

/**
 * "Fractions Intro" PhET Application
 *
 * @author Sam Reid
 */
public class FractionsIntroStudyNovember2013Application extends PiccoloPhetApplication {

    //Global flag for whether this functionality should be enabled
    public static boolean recordRegressionData;
    public static FractionsIntroStudyNovember2013Application instance;

    public FractionsIntroStudyNovember2013Application( PhetApplicationConfig config ) {
        super( config );
        instance = this;

        //New properties for recording
        final Property<Integer> totalClicks = new Property<Integer>( 0 );

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
        final Property<String> module = new Property<String>( introModule.getName() );
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
            }

            public void moduleRemoved( ModuleEvent event ) {

            }
        } );
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

        trackState( "window.up", windowNotIconified );
        trackState( "window.active", windowActive );
        trackState( "tab", module );
        trackState( "tab1.rep", toStringProperty( introModule.model.representation ) );
        trackState( "tab1.denominator", introModule.model.denominator );
        trackState( "tab1.numerator", introModule.model.numerator );
        trackState( "tab1.max", introModule.model.maximum );
        trackState( "clicks", totalClicks );
        trackState( "tab2.screenType", toStringProperty( buildAFractionModule.canvas.screenType ) );

        trackState( "tab3.leftRepresentation", toStringProperty( equalityLabModule.equalityLabModel.leftRepresentation ) );
        trackState( "tab3.rightRepresentation", toStringProperty( equalityLabModule.equalityLabModel.rightRepresentation ) );
        trackState( "tab3.sameRepresentation", equalityLabModule.equalityLabModel.sameAsLeft );
        trackState( "tab3.scale", equalityLabModule.equalityLabModel.scale );
        trackState( "tab3.maximum", equalityLabModule.equalityLabModel.maximum );
        trackState( "tab3.numerator", equalityLabModule.equalityLabModel.numerator );
        trackState( "tab3.denominator", equalityLabModule.equalityLabModel.denominator );

        trackState( "tab4.audio", matchingGameModule.audioEnabled );
        trackState( "tab4.timerVisible", matchingGameModule.model.timerVisible );
        trackState( "tab4.level", matchingGameModule.model.level );
        trackState( "tab4.checks", matchingGameModule.model.checks );
        trackState( "tab4.choosingSettings", matchingGameModule.model.choosingSettings );
        trackState( "tab4.leftScaleValue", matchingGameModule.model.leftScaleValue );
        trackState( "tab4.rightScaleValue", matchingGameModule.model.rightScaleValue );
        trackState( "tab4.revealClues", matchingGameModule.model.revealClues );
        trackState( "tab4.score", matchingGameModule.model.score );
        trackState( "tab4.scored", matchingGameModule.model.scored );

        buildAFractionModule.canvas.addLevelStartedListener( new VoidFunction1<PNode>() {
            public void apply( final PNode node ) {

                List<String> targetString = null;
                String type = null;
                int index = -1;
                ArrayList<VoidFunction1<ShapeSceneNode.DropResult>> dropListeners = null;
                Level level = null;
                if ( node instanceof ShapeSceneNode ) {
                    type = "Shapes";
                    ShapeSceneNode shapeSceneNode = (ShapeSceneNode) node;
                    index = shapeSceneNode.levelIndex;
                    ShapeLevel shapeLevel = shapeSceneNode.level;
                    level = shapeLevel;
                    targetString = new ArrayList<String>( shapeLevel.targets.map( new F<MixedFraction, String>() {
                        @Override public String f( MixedFraction mixedFraction ) {
                            return mixedFraction.toString();
                        }
                    } ).toCollection() );
                    dropListeners = shapeSceneNode.dropListeners;
                }
                else if ( node instanceof NumberSceneNode ) {
                    type = "Numbers";
                    NumberSceneNode numberSceneNode = (NumberSceneNode) node;
                    NumberLevel numberLevel = numberSceneNode.level;
                    level = numberLevel;
                    index = numberSceneNode.levelIndex;

                    //TODO: Send event
                    targetString = new ArrayList<String>( numberLevel.targets.map( new F<NumberTarget, String>() {
                        @Override public String f( NumberTarget numberTarget ) {
                            return numberTarget.mixedFraction + " : " + numberTarget.filledPattern.map( new F<FilledPattern, String>() {
                                @Override public String f( FilledPattern filledPattern ) {
                                    return filledPattern.type.toString();
                                }
                            } );
                        }
                    } ).toCollection() );
                    dropListeners = numberSceneNode.dropListeners;
                }
                final int id = ( (SceneNode) node ).id;
                dropListeners.add( new VoidFunction1<ShapeSceneNode.DropResult>() {
                    public void apply( ShapeSceneNode.DropResult dropResult ) {
                        ParameterSet parameters = ParameterSet.parameterSet(
                                FractionsIntroSimSharing.ParameterKeys.levelID, id ).
                                with( FractionsIntroSimSharing.ParameterKeys.hit, dropResult.hit ).
                                with( FractionsIntroSimSharing.ParameterKeys.source, dropResult.source.toString() ).
                                with( FractionsIntroSimSharing.ParameterKeys.target, dropResult.target.toString() );
                        if ( dropResult.selectedPieceSize != -1 ) {
                            parameters = parameters.with( FractionsIntroSimSharing.ParameterKeys.divisions, dropResult.selectedPieceSize );
                        }

                        SimSharingManager.sendModelMessage( FractionsIntroSimSharing.ModelComponents.event, FractionsIntroSimSharing.ModelComponentTypes.event, FractionsIntroSimSharing.ModelActions.shapeContainerDropped,
                                                            parameters );
                    }
                } );

                SimSharingManager.sendModelMessage( FractionsIntroSimSharing.ModelComponents.event, FractionsIntroSimSharing.ModelComponentTypes.event, FractionsIntroSimSharing.ModelActions.buildAFractionLevelStarted,
                                                    ParameterSet.parameterSet( ParameterKeys.id, id ).
                                                            with( FractionsIntroSimSharing.ParameterKeys.targets, targetString.toString() ).
                                                            with( ParameterKeys.type, type ).
                                                            with( GameSimSharing.ParameterKeys.level, index ) );
                trackState( "bafLevelID." + id + ".matchExists", level.matchExists );
                trackState( "bafLevelID." + id + ".filledTargets", level.filledTargets );
                trackState( "bafLevelID." + id + ".createdFractions", toStringProperty( level.createdFractions ) );
            }
        } );

        matchingGameModule.model.addLevelStartedListenerPost( new VoidFunction3<IModelAction, Integer, MatchingGameState>() {
            public void apply( IModelAction action, Integer level, MatchingGameState state ) {
                SimSharingManager.sendModelMessage( FractionsIntroSimSharing.ModelComponents.event, FractionsIntroSimSharing.ModelComponentTypes.event,
                                                    action,
                                                    ParameterSet.parameterSet( FractionsIntroSimSharing.ParameterKeys.fractions, state.fractions.map( new F<MovableFraction, String>() {
                                                        @Override public String f( MovableFraction movableFraction ) {
                                                            return movableFraction.numerator + "/" + movableFraction.denominator + " " + movableFraction.representationName;
                                                        }
                                                    } ).toString() ) );
            }
        } );
    }

    private ObservableProperty<String> toStringProperty( ObservableProperty screenType ) {
        final Property<String> p = new Property<String>( screenType.get().toString() );
        screenType.addObserver( new VoidFunction1() {
            public void apply( Object obj ) {
                p.set( obj.toString() );
            }
        } );
        return p;
    }

    public static void main( final String[] args ) {
        final Analysis report = new Analysis();
        SimSharingManager.initListeners.add( new VoidFunction1<SimSharingManager>() {
            public void apply( SimSharingManager simSharingManager ) {
                simSharingManager.addLog( new Log() {
                    public void addMessage( SimSharingMessage message ) throws IOException {
                        report.addMessage( message.toString() );
                    }

                    public String getName() {
                        return "Fractions Intro Study November 2013";
                    }

                    public void shutdown() {

                    }
                } );
            }
        } );
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", FractionsIntroStudyNovember2013Application.class );

        //TODO: Remove these for publication
        new Timer( 60, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                report.sync();
            }
        } ).start();
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
}