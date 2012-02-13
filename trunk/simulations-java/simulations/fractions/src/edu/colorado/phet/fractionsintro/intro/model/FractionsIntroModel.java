// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.ChosenRepresentation;

/**
 * Model for the Fractions Intro sim.
 * <p/>
 * Property is a convenient interface for clients, but causes problems when mapping between multiple representations.
 * I should create an isolated case of this, because trivial cases seem like they should work.
 * One solution would be to have the Property.set() methods only be called from the user side, not from the model side.
 * When any of the client interface methods are called, the rest of the state should update to reflect the new state.
 * This means that all handlers in this class should call to the state itself, not to any of the derived properties.
 * <p/>
 * New style:
 * 1. No way to forget to call reset() on some representation instances
 * 2. Interface setters are all focused on "update the entire state to match the given request"
 *
 * @author Sam Reid
 */
public class FractionsIntroModel {
    private final Property<FractionsIntroModelState> state = new Property<FractionsIntroModelState>( new FractionsIntroModelState() );

    public final Clock clock = new ConstantDtClock();

    //Observable parts of the model
    public final Property<ChosenRepresentation> representation =
            clientInterface( state, new Function1<FractionsIntroModelState, ChosenRepresentation>() {
                                 public ChosenRepresentation apply( FractionsIntroModelState s ) {
                                     return s.representation;
                                 }
                             },
                             new Function2<FractionsIntroModelState, ChosenRepresentation, FractionsIntroModelState>() {
                                 public FractionsIntroModelState apply( FractionsIntroModelState s, ChosenRepresentation representation ) {
                                     return s.representation( representation );
                                 }
                             }
            );
    public final IntegerProperty numerator =
            new IntClientProperty( state, new Function1<FractionsIntroModelState, Integer>() {
                public Integer apply( FractionsIntroModelState s ) {
                    return s.numerator;
                }
            },
                                   new Function2<FractionsIntroModelState, Integer, FractionsIntroModelState>() {
                                       public FractionsIntroModelState apply( FractionsIntroModelState s, Integer numerator ) {
                                           int oldValue = s.numerator;
                                           System.out.println( "FractionsIntroModel.update, numerator: old = " + oldValue + ", new = " + numerator );
                                           int delta = numerator - oldValue;
                                           if ( delta > 0 ) {
                                               for ( int i = 0; i < delta; i++ ) {
                                                   final CellPointer target = s.containerSet.getFirstEmptyCell();
                                                   System.out.println( "target = " + target + ", in " + s.containerSet + ", for pieset's cs = " + s.pieSet.toContainerState() );
                                                   final PieSet newPieSet = s.pieSet.animateBucketSliceToPie( target );
                                                   final ContainerSet newContainerSet = newPieSet.toContainerState();
                                                   System.out.println( "newContainerSet = " + newContainerSet );
                                                   s = s.pieSet( newPieSet ).containerSet( newContainerSet ).numerator( numerator );
                                                   System.out.println();
                                               }
                                           }
                                           else if ( delta < 0 ) {
                                               for ( int i = 0; i < Math.abs( delta ); i++ ) {
                                                   final ContainerSet newContainerSet = s.containerSet.toggle( s.containerSet.getLastFullCell() );
                                                   s = s.containerSet( newContainerSet ).pieSet( PieSet.fromContainerSetState( newContainerSet ) ).numerator( newContainerSet.numerator );
//                                                state.set( state.get().containerSet( state.get().containerSet.toggle( containerSet.get().getLastFullCell() ) ) );
                                               }
                                           }
                                           else {
                                               //Nothing to do if delta == 0
                                           }
                                           return s;
                                       }
                                   }
            ).toIntegerProperty();
    public final IntegerProperty denominator =
            new IntClientProperty( state, new Function1<FractionsIntroModelState, Integer>() {
                public Integer apply( FractionsIntroModelState s ) {
                    return s.denominator;
                }
            },
                                   new Function2<FractionsIntroModelState, Integer, FractionsIntroModelState>() {
                                       public FractionsIntroModelState apply( FractionsIntroModelState s, Integer denominator ) {

                                           //create a new container set
                                           ContainerSet cs = s.containerSet.denominator( denominator ).padAndTrim();
                                           return s.pieSet( PieSet.fromContainerSetState( cs ) ).containerSet( cs ).denominator( denominator );
                                       }
                                   }
            ).toIntegerProperty();
    public final Property<ContainerSet> containerSet = clientInterface(
            state, new Function1<FractionsIntroModelState, ContainerSet>() {
                public ContainerSet apply( FractionsIntroModelState s ) {
                    return s.containerSet;
                }
            },
            new Function2<FractionsIntroModelState, ContainerSet, FractionsIntroModelState>() {
                public FractionsIntroModelState apply( FractionsIntroModelState s, ContainerSet containerSet ) {
                    return s.containerSet( containerSet );
                }
            }
    );
    public final Property<PieSet> pieSet = clientInterface(
            state, new Function1<FractionsIntroModelState, PieSet>() {
                public PieSet apply( FractionsIntroModelState s ) {
                    return s.pieSet;
                }
            },
            new Function2<FractionsIntroModelState, PieSet, FractionsIntroModelState>() {
                public FractionsIntroModelState apply( FractionsIntroModelState s, PieSet pieSet ) {
                    final ContainerSet cs = pieSet.toContainerState();
                    //Update both the pie set and container state to match the user specified pie set
                    return s.pieSet( pieSet ).containerSet( cs ).numerator( cs.numerator );
                }
            }
    );

    private static <T> Property<T> clientInterface( final Property<FractionsIntroModelState> state,
                                                    final Function1<FractionsIntroModelState, T> get,
                                                    final Function2<FractionsIntroModelState, T, FractionsIntroModelState> change ) {
        //TODO: Maybe override set() here and use another function to update the model?
        final Property<T> property = new Property<T>( get.apply( state.get() ) ) {
            @Override public void set( T value ) {
                //When the user calls set on this property, use its value and the current state of the model to update the entire model.
                //Then send out updates to all properties that changed (including this one)
                FractionsIntroModelState newState = change.apply( state.get(), value );
                state.set( newState );

                //Make sure the change went through
                assert ( get.apply( newState ).equals( value ) );

                super.set( get.apply( newState ) );
            }
        };

        //When the state changes, notify observers
        state.addObserver( new VoidFunction1<FractionsIntroModelState>() {
            public void apply( FractionsIntroModelState fractionsIntroModelState ) {

                //Blocks against values that didn't really change value
                //This loops and will set the state again.  That is bad and can cause faulty loops, it should just notify the observers.
                property.set( get.apply( state.get() ) );
            }
        } );

        return property;
    }

    //TODO: Factor out code between intClientInterface and the generic one
//    private static IntClientProperty intClientInterface( final Property<FractionsIntroModelState> state,
//                                                         final Function1<FractionsIntroModelState, Integer> get,
//                                                         final Function2<FractionsIntroModelState, Integer, FractionsIntroModelState> change ) {
//        //TODO: Maybe override set() here and use another function to update the model?
//        final IntegerProperty property = new IntegerProperty( get.apply( state.get() ) ) {
//            @Override public void set( Integer value ) {
//                //When the user calls set on this property, use its value and the current state of the model to update the entire model.
//                //Then send out updates to all properties that changed (including this one)
//                FractionsIntroModelState newState = change.apply( state.get(), value );
//                state.set( newState );
//
//                //Make sure the change went through
//                final boolean checked = get.apply( newState ).equals( value );
//                if ( !checked ) {
//                    throw new RuntimeException( "Value mismatched, tried to set: " + value + ", but value is still: " + get.apply( newState ) );
//                }
//
//                super.set( get.apply( newState ) );
//            }
//        };
//
//        //When the state changes, notify observers
//        state.addObserver( new VoidFunction1<FractionsIntroModelState>() {
//            public void apply( FractionsIntroModelState fractionsIntroModelState ) {
//
//                System.out.println( "get.apply( state.get() ) = " + get.apply( state.get() ) );
//                //Blocks against values that didn't really change value
//                property.set( get.apply( state.get() ) );
//            }
//        } );
//
//        return property;
//    }

    public FractionsIntroModel() {

//        denominator.addObserver( new VoidFunction1<Integer>() {
//            public void apply( final Integer denominator ) {
//                runModelUpdate( new VoidFunction0() {
//                    public void apply() {
//                        //When changing denominator, move pieces to nearby slots
//                        state.set( state.get().containerSet( containerSet.get().denominator( denominator ).padAndTrim() ) );
//                    }
//                } );
//                //pieSet.set( fromContainerSetState( containerSet.get() ) );
//            }
//        } );

//        containerSet.addObserver( new ChangeObserver<ContainerSet>() {
//            public void update( final ContainerSet newValue, final ContainerSet oldValue ) {
//                runModelUpdate( new VoidFunction0() {
//                    public void apply() {                //If caused by the user, then send the changes back to the numerator & denominator.
//                        if ( newValue.denominator == oldValue.denominator ) {
//                            numerator.set( newValue.numerator );
//                        }
//                    }
//                } );
//
//            }
//        } );

        //When the user drags slices, update the ContainerState (so it will update the spinner and make it easy to switch representations)
//        pieSet.addObserver( new SimpleObserver() {
//            public void update() {
//                runModelUpdate( new VoidFunction0() {
//                    public void apply() {
//                        containerSet.set( pieSet.get().toContainerState() );
//                    }
//                } );
//            }
//        } );

        //Animate the model when the clock ticks
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                final FractionsIntroModelState s = state.get();
                final PieSet newPieSet = s.pieSet.stepInTime( clockEvent.getSimulationTimeChange() );
                state.set( s.pieSet( newPieSet ).containerSet( newPieSet.toContainerState() ) );
            }
        } );
    }

    public void resetAll() {
        state.set( new FractionsIntroModelState() );
    }

    public Clock getClock() {
        return clock;
    }
}