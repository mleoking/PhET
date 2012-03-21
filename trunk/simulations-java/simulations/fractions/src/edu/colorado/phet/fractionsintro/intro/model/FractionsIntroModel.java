// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;
import fj.F2;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.fractionsintro.intro.model.containerset.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.CakeSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.FactorySet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.SliceFactory;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

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
public class FractionsIntroModel {
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

        final SliceFactory cakeSliceFactory = factorySet.cakeSliceFactory;
        final SliceFactory HorizontalSliceFactory = factorySet.horizontalSliceFactory;
        final SliceFactory VerticalSliceFactory = factorySet.verticalSliceFactory;
        final SliceFactory CircularSliceFactory = factorySet.circularSliceFactory;
        final SliceFactory WaterGlassSetFactory = factorySet.waterGlassSetFactory;

        this.initialState = initialState;
        state = new Property<IntroState>( initialState );
        clock = new ConstantDtClock() {{
            addClockListener( new ClockAdapter() {
                @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                    final IntroState s = state.get();
                    final double dt = clockEvent.getSimulationTimeChange();
                    final IntroState newState = s.updatePieSets( new F<PieSet, PieSet>() {
                        @Override public PieSet f( PieSet p ) {
                            return p.stepInTime();
                        }
                    } );

                    //Fix the z-ordering for cake slices
                    final IntroState sorted = newState.cakeSet( CakeSliceFactory.sort( newState.cakeSet ) );
                    state.set( sorted );
                }
            } );
        }};
        representation = new ClientProperty<Representation>( state, new F<IntroState, Representation>() {
            public Representation f( IntroState s ) {
                return s.representation;
            }

        }, new F2<IntroState, Representation, IntroState>() {
            public IntroState f( IntroState s, Representation r ) {

                //Workaround for a bug: when dragging number line quickly, pie set gets out of sync.  So update it when representations change
                return s.representation( r ).fromContainerSet( s.containerSet, factorySet );
            }
        }
        );

        numerator = new IntClientProperty( state, new F<IntroState, Integer>() {
            public Integer f( IntroState s ) {
                return s.numerator;
            }
        }, new F2<IntroState, Integer, IntroState>() {
            public IntroState f( IntroState s, Integer numerator ) {
                int oldValue = s.numerator;
                int delta = numerator - oldValue;
                if ( delta > 0 ) {
                    for ( int i = 0; i < delta; i++ ) {
                        final CellPointer firstEmptyCell = s.containerSet.getFirstEmptyCell();
                        s = s.updatePieSets( new F<PieSet, PieSet>() {
                            @Override public PieSet f( PieSet p ) {
                                return p.animateBucketSliceToPie( firstEmptyCell );
                            }
                        } ).numerator( numerator );
                    }
                }
                else if ( delta < 0 ) {
                    for ( int i = 0; i < Math.abs( delta ); i++ ) {
                        final CellPointer lastFullCell = s.containerSet.getLastFullCell();
                        s = s.updatePieSets( new F<PieSet, PieSet>() {
                            @Override public PieSet f( PieSet p ) {
                                return p.animateSliceToBucket( lastFullCell );
                            }
                        } ).numerator( numerator );
                    }
                }
                else {
                    //Nothing to do if delta == 0
                }
                return s;
            }
        }
        ).toIntegerProperty();

        denominator = new IntClientProperty( state, new F<IntroState, Integer>() {
            public Integer f( IntroState s ) {
                return s.denominator;
            }
        }, new F2<IntroState, Integer, IntroState>() {
            public IntroState f( IntroState s, Integer denominator ) {

                //create a new container set
                ContainerSet c = s.containerSet.update( s.maximum, denominator );
                return s.containerSet( c ).denominator( denominator ).fromContainerSet( c, factorySet );
            }
        }
        ).toIntegerProperty();

        //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
        pieSet = new ClientProperty<PieSet>(
                state, new F<IntroState, PieSet>() {
            public PieSet f( IntroState s ) {
                return s.pieSet;
            }
        },
                new F2<IntroState, PieSet, IntroState>() {
                    public IntroState f( IntroState s, PieSet pieSet ) {
                        final ContainerSet c = pieSet.toContainerSet();
                        //Update both the pie set and container state to match the user specified pie set
                        return s.pieSet( pieSet ).
                                containerSet( c ).
                                numerator( c.numerator ).
                                horizontalBarSet( HorizontalSliceFactory.fromContainerSetState( c ) ).
                                verticalBarSet( VerticalSliceFactory.fromContainerSetState( c ) ).
                                waterGlassSet( WaterGlassSetFactory.fromContainerSetState( c ) ).
                                cakeSet( cakeSliceFactory.fromContainerSetState( c ) );
                    }
                }
        );

        //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
        horizontalBarSet = new ClientProperty<PieSet>(
                state, new F<IntroState, PieSet>() {
            public PieSet f( IntroState s ) {
                return s.horizontalBarSet;
            }
        },
                new F2<IntroState, PieSet, IntroState>() {
                    public IntroState f( IntroState s, PieSet p ) {
                        final ContainerSet cs = p.toContainerSet();
                        //Update both the pie set and container state to match the user specified pie set
                        return s.horizontalBarSet( p ).
                                containerSet( cs ).
                                numerator( cs.numerator ).
                                pieSet( CircularSliceFactory.fromContainerSetState( cs ) ).
                                verticalBarSet( VerticalSliceFactory.fromContainerSetState( cs ) ).
                                waterGlassSet( WaterGlassSetFactory.fromContainerSetState( cs ) ).
                                cakeSet( cakeSliceFactory.fromContainerSetState( cs ) );
                    }
                }
        );

        //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
        verticalBarSet = new ClientProperty<PieSet>(
                state, new F<IntroState, PieSet>() {
            public PieSet f( IntroState s ) {
                return s.verticalBarSet;
            }
        },
                new F2<IntroState, PieSet, IntroState>() {
                    public IntroState f( IntroState s, PieSet p ) {
                        final ContainerSet cs = p.toContainerSet();
                        //Update both the pie set and container state to match the user specified pie set
                        return s.verticalBarSet( p ).
                                containerSet( cs ).
                                horizontalBarSet( HorizontalSliceFactory.fromContainerSetState( cs ) ).
                                numerator( cs.numerator ).
                                pieSet( CircularSliceFactory.fromContainerSetState( cs ) ).
                                waterGlassSet( WaterGlassSetFactory.fromContainerSetState( cs ) ).
                                cakeSet( cakeSliceFactory.fromContainerSetState( cs ) );
                    }
                }
        );

        //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
        waterGlassSet = new ClientProperty<PieSet>(
                state, new F<IntroState, PieSet>() {
            public PieSet f( IntroState s ) {
                return s.waterGlassSet;
            }
        },
                new F2<IntroState, PieSet, IntroState>() {
                    public IntroState f( IntroState s, PieSet p ) {
                        final ContainerSet cs = p.toContainerSet();
                        //Update both the pie set and container state to match the user specified pie set
                        return s.waterGlassSet( p ).
                                containerSet( cs ).
                                verticalBarSet( VerticalSliceFactory.fromContainerSetState( cs ) ).
                                horizontalBarSet( HorizontalSliceFactory.fromContainerSetState( cs ) ).
                                numerator( cs.numerator ).
                                pieSet( CircularSliceFactory.fromContainerSetState( cs ) ).
                                cakeSet( CircularSliceFactory.fromContainerSetState( cs ) );
                    }
                }
        );

        //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
        cakeSet = new ClientProperty<PieSet>(
                state, new F<IntroState, PieSet>() {
            public PieSet f( IntroState s ) {
                return s.cakeSet;
            }
        },
                new F2<IntroState, PieSet, IntroState>() {
                    public IntroState f( IntroState s, PieSet p ) {
                        final ContainerSet cs = p.toContainerSet();
                        //Update both the pie set and container state to match the user specified pie set
                        return s.cakeSet( p ).
                                containerSet( cs ).
                                verticalBarSet( VerticalSliceFactory.fromContainerSetState( cs ) ).
                                horizontalBarSet( HorizontalSliceFactory.fromContainerSetState( cs ) ).
                                numerator( cs.numerator ).
                                pieSet( CircularSliceFactory.fromContainerSetState( cs ) ).
                                waterGlassSet( WaterGlassSetFactory.fromContainerSetState( cs ) );
                    }
                }
        );

        //When the user changes the max value with the spinner, update the model
        maximum = new IntClientProperty( state, new F<IntroState, Integer>() {
            @Override public Integer f( IntroState s ) {
                return s.maximum;
            }
        }, new F2<IntroState, Integer, IntroState>() {
            @Override public IntroState f( final IntroState s, final Integer maximum ) {
                final ContainerSet c = s.containerSet.maximum( maximum );
                IntroState newState = s.maximum( maximum ).
                        containerSet( c ).
                        fromContainerSet( c, factorySet ).
                        numerator( c.numerator ).
                        denominator( c.denominator );

                int lastMax = s.maximum;
                int delta = maximum - lastMax;

                //Eject any pieces in the pie that will be dropped
                int numPiecesToEject = s.containerSet.getContainer( s.containerSet.getContainers().length() - 1 ).getFilledCells().length();

                //Animate pie pieces leaving from pies that are dropped when max decreases
                //Do this by creating a new slice in the location of the deleted slice, and animating it to the bucket.
                //This is necessary since the location of pies changed when max changed.
                if ( delta < 0 && numPiecesToEject > 0 ) {
                    ContainerSet csx = s.containerSet;
                    for ( int i = 0; i < numPiecesToEject; i++ ) {
                        final CellPointer cp = csx.getLastFullCell();

                        //TODO: improve readability
                        final Slice newPieSlice = CircularSliceFactory.createPieCell( s.maximum, cp.container, cp.cell, s.denominator );
                        newState = newState.pieSet( newState.pieSet.slices( newState.pieSet.slices.cons( newPieSlice ) ).animateSliceToBucket( newPieSlice ) );

                        final Slice newHorizontalSlice = HorizontalSliceFactory.createPieCell( s.maximum, cp.container, cp.cell, s.denominator );
                        newState = newState.horizontalBarSet( newState.horizontalBarSet.slices( newState.horizontalBarSet.slices.cons( newHorizontalSlice ) ).animateSliceToBucket( newHorizontalSlice ) );

                        final Slice newVerticalSlice = VerticalSliceFactory.createPieCell( s.maximum, cp.container, cp.cell, s.denominator );
                        newState = newState.verticalBarSet( newState.verticalBarSet.slices( newState.verticalBarSet.slices.cons( newVerticalSlice ) ).animateSliceToBucket( newVerticalSlice ) );

                        final Slice newWaterSlice = WaterGlassSetFactory.createPieCell( s.maximum, cp.container, cp.cell, s.denominator );
                        newState = newState.waterGlassSet( newState.waterGlassSet.slices( newState.waterGlassSet.slices.cons( newWaterSlice ) ).animateSliceToBucket( newWaterSlice ) );

                        final Slice newCakeSlice = cakeSliceFactory.createPieCell( s.maximum, cp.container, cp.cell, s.denominator );
                        newState = newState.cakeSet( newState.cakeSet.slices( newState.cakeSet.slices.cons( newCakeSlice ) ).animateSliceToBucket( newCakeSlice ) );

                        csx = csx.toggle( cp );
                    }
                }
                return newState;
            }
        }
        ).toIntegerProperty();
    }

    public void resetAll() { state.set( initialState ); }

    public Clock getClock() { return clock;}
}