// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractionsintro.common.model.SingleFractionModel;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.Fill;

/**
 * Model for the Fractions Intro sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroModel extends SingleFractionModel {
    private static boolean userToggled = false;
    public final Property<Fill> fill = new Property<Fill>( Fill.SEQUENTIAL );
    public final Property<ContainerSet> containerState = new Property<ContainerSet>( new ContainerSet( denominator.get(), new Container[] { new Container( 1, new int[] { } ) } ).padAndTrim() );
    public final Property<Boolean> showReduced = new Property<Boolean>( false );
    public final Property<Boolean> showMixed = new Property<Boolean>( false );
    public final Property<PieSet> pieSet = new Property<PieSet>( new PieSet() );

    public FractionsIntroModel() {
        //synchronize the container state with the numerator and denominator for when the user uses the spinners
        numerator.addObserver( new ChangeObserver<Integer>() {
            public void update( Integer newValue, Integer oldValue ) {

                if ( !userToggled ) {
                    int delta = newValue - oldValue;
                    containerState.set( containerState.get().addPieces( delta ) );
                    pieSet.set( PieSet.fromContainerSetState( containerState.get() ) );
                }
            }
        } );

        denominator.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer denominator ) {

                if ( !userToggled ) {

                    //When changing denominator, move pieces to nearby slots
                    ContainerSet newState = containerState.get().denominator( denominator );
                    containerState.set( newState.padAndTrim() );
                    pieSet.set( PieSet.fromContainerSetState( containerState.get() ) );
                }
            }
        } );

        containerState.addObserver( new ChangeObserver<ContainerSet>() {
            public void update( ContainerSet newValue, ContainerSet oldValue ) {

                //If caused by the user, then send the changes back to the numerator & denominator.
                if ( userToggled ) {
                    if ( newValue.denominator == oldValue.denominator ) {
                        numerator.set( newValue.numerator );
                    }
                }
            }
        } );

        //When the user drags slices, update the ContainerState (so it will update the spinner and make it easy to switch representations)
        pieSet.addObserver( new SimpleObserver() {
            public void update() {
                setUserToggled( true );
                containerState.set( pieSet.get().toContainerState() );
                setUserToggled( false );
            }
        } );

        //Animate the model when the clock ticks
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                pieSet.set( pieSet.get().stepInTime( clockEvent.getSimulationTimeChange() ) );
            }
        } );
    }

    public void resetAll() {
        super.resetAll();
        fill.reset();
        showReduced.reset();
        showMixed.reset();
    }

    //Flag to indicate the source of changes--if coming from the user, then changes need to be pushed to numerator and denominator
    //TODO: Any better way of doing this?
    public static void setUserToggled( boolean userToggled ) {
        FractionsIntroModel.userToggled = userToggled;
    }

    public Clock getClock() {
        return clock;
    }
}