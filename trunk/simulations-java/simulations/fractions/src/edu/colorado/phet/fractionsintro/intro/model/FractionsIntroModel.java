// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;

import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ParameterKeys;
import edu.colorado.phet.fractionsintro.intro.controller.SetCakeSet;
import edu.colorado.phet.fractionsintro.intro.controller.SetCircularPieSet;
import edu.colorado.phet.fractionsintro.intro.controller.SetDenominator;
import edu.colorado.phet.fractionsintro.intro.controller.SetHorizontalBarSet;
import edu.colorado.phet.fractionsintro.intro.controller.SetMaximum;
import edu.colorado.phet.fractionsintro.intro.controller.SetNumerator;
import edu.colorado.phet.fractionsintro.intro.controller.SetRepresentation;
import edu.colorado.phet.fractionsintro.intro.controller.SetVerticalBarSet;
import edu.colorado.phet.fractionsintro.intro.controller.SetWaterGlassSet;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.CakeSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.FactorySet;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.fractionsintro.FractionsIntroApplication.recordRegressionData;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ModelActions.changed;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ModelComponentTypes.containerSetComponentType;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ModelComponents.containerSetComponent;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ParameterKeys.containerSetKey;

/**
 * Model for the Fractions Intro sim.  This is the most complicated class in the sim, because it has to manage several different ways of changing
 * multiple different representations.  It provides a <code>Property<T></code> interface for clients which enables them to (a) observe changes to a desired
 * component of the model and (b) to make a change to the model by changing that property.  When changing that property, the entire model will be
 * updated to reflect the new value.  See implementation-notes.txt for more details.
 * <p/>
 * Property is a convenient interface for clients, but causes problems when mapping between multiple representations.
 *
 * @author Sam Reid
 */
public class FractionsIntroModel implements Serializable {
    private final Property<IntroState> state;

    //Clock for the model.
    //Animate the model when the clock ticks
    public final Clock clock;

    //Observable parts of the model, see docs in main constructor
    public final SettableProperty<Representation> representation;
    public final IntegerProperty numerator;
    public final IntegerProperty denominator;
    public final SettableProperty<PieSet> pieSet;
    public final SettableProperty<PieSet> horizontalBarSet;
    public final SettableProperty<PieSet> verticalBarSet;
    public final SettableProperty<PieSet> waterGlassSet;
    public final SettableProperty<PieSet> cakeSet;
    public final IntegerProperty maximum;
    private final IntroState initialState;//For resetting
    public final FactorySet factorySet;

    public FractionsIntroModel( IntroState initialState, final FactorySet factorySet ) {
        this.factorySet = factorySet;

        this.initialState = initialState;
        state = new Property<IntroState>( initialState );
        clock = new ConstantDtClock() {{
            addClockListener( new ClockAdapter() {
                @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                    final IntroState s = state.get();
                    final IntroState newState = s.updatePieSets( new F<PieSet, PieSet>() {
                        @Override public PieSet f( PieSet p ) {
                            return p.stepInTime();
                        }
                    } );

                    //Fix the z-ordering for cake slices
                    state.set( newState.cakeSet( CakeSliceFactory.sort( newState.cakeSet ) ) );
                }
            } );
        }};
        representation = new ClientProperty<Representation>( state, new F<IntroState, Representation>() {
            public Representation f( IntroState s ) {
                return s.representation;
            }

        }, new F<Representation, F<IntroState, IntroState>>() {
            @Override public F<IntroState, IntroState> f( final Representation r ) {
                return evaluate( new SetRepresentation( r ) );
            }
        }
        );

        numerator = new IntClientProperty( state, new F<IntroState, Integer>() {
            public Integer f( IntroState s ) {
                return s.numerator;
            }
        }, new F<Integer, F<IntroState, IntroState>>() {
            @Override public F<IntroState, IntroState> f( final Integer numerator ) {
                return evaluate( new SetNumerator( numerator ) );
            }
        }
        ).toIntegerProperty();

        denominator = new IntClientProperty( state, new F<IntroState, Integer>() {
            public Integer f( IntroState s ) {
                return s.denominator;
            }
        }, new F<Integer, F<IntroState, IntroState>>() {
            @Override public F<IntroState, IntroState> f( final Integer denominator ) {
                return evaluate( new SetDenominator( denominator ) );
            }
        }
        ).toIntegerProperty();

        //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
        pieSet = new ClientProperty<PieSet>( state, new F<IntroState, PieSet>() {
            public PieSet f( IntroState s ) {
                return s.pieSet;
            }
        }, new F<PieSet, F<IntroState, IntroState>>() {
            @Override public F<IntroState, IntroState> f( final PieSet pieSet ) {
                return evaluate( new SetCircularPieSet( pieSet ) );
            }
        }
        );

        //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
        horizontalBarSet = new ClientProperty<PieSet>( state, new F<IntroState, PieSet>() {
            public PieSet f( IntroState s ) {
                return s.horizontalBarSet;
            }
        },
                                                       new F<PieSet, F<IntroState, IntroState>>() {
                                                           @Override public F<IntroState, IntroState> f( final PieSet pieSet ) {
                                                               return evaluate( new SetHorizontalBarSet( pieSet ) );
                                                           }
                                                       }
        );

        //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
        verticalBarSet = new ClientProperty<PieSet>( state, new F<IntroState, PieSet>() {
            public PieSet f( IntroState s ) {
                return s.verticalBarSet;
            }
        }, new F<PieSet, F<IntroState, IntroState>>() {
            @Override public F<IntroState, IntroState> f( final PieSet pieSet ) {
                return evaluate( new SetVerticalBarSet( pieSet ) );
            }
        }
        );

        //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
        waterGlassSet = new ClientProperty<PieSet>( state, new F<IntroState, PieSet>() {
            public PieSet f( IntroState s ) {
                return s.waterGlassSet;
            }
        }, new F<PieSet, F<IntroState, IntroState>>() {
            @Override public F<IntroState, IntroState> f( final PieSet pieSet ) {
                return evaluate( new SetWaterGlassSet( pieSet ) );
            }
        }
        );

        //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
        cakeSet = new ClientProperty<PieSet>( state, new F<IntroState, PieSet>() {
            public PieSet f( IntroState s ) {
                return s.cakeSet;
            }
        }, new F<PieSet, F<IntroState, IntroState>>() {
            @Override public F<IntroState, IntroState> f( final PieSet pieSet ) {
                return evaluate( new SetCakeSet( pieSet ) );
            }
        }
        );

        //When the user changes the max value with the spinner, update the model
        maximum = new IntClientProperty( state, new F<IntroState, Integer>() {
            @Override public Integer f( IntroState s ) {
                return s.maximum;
            }
        }, new F<Integer, F<IntroState, IntroState>>() {
            @Override public F<IntroState, IntroState> f( final Integer maximum ) {
                return evaluate( new SetMaximum( maximum ) );
            }
        }
        ).toIntegerProperty();

        //Send messages when the container state changes
        ClientProperty<ContainerSet> containerSetClientProperty = new ClientProperty<ContainerSet>( state, new F<IntroState, ContainerSet>() {
            @Override public ContainerSet f( final IntroState introState ) {
                return introState.containerSet;
            }
        }, new F<ContainerSet, F<IntroState, IntroState>>() {
            @Override public F<IntroState, IntroState> f( final ContainerSet containerSet ) {
                throw new RuntimeException( "Shouldn't be called (should be read-only)" );
            }
        }
        );
        containerSetClientProperty.addObserver( new VoidFunction1<ContainerSet>() {
            public void apply( final ContainerSet containerSet ) {
                SimSharingManager.sendModelMessage( containerSetComponent, containerSetComponentType, changed, parameterSet( ParameterKeys.numerator, containerSet.numerator ).with(
                        ParameterKeys.denominator, containerSet.denominator ).with( containerSetKey, containerSet.toString() ) );
            }
        } );
    }

    private F<IntroState, IntroState> evaluate( final F<IntroState, IntroState> f ) { return recordRegressionData ? RegressionTestRecorder.record( f ) : f; }

    public void resetAll() { state.set( initialState ); }

    public Clock getClock() { return clock;}

    public long getRandomSeed() {
        return state.get().randomSeed;
    }
}