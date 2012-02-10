// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractionsintro.common.model.SingleFractionModel;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.PieSetState;
import edu.colorado.phet.fractionsintro.intro.view.Fill;

/**
 * Model for the Fractions Intro sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroModel extends SingleFractionModel {
    private static boolean userToggled = false;
    public final Property<Fill> fill = new Property<Fill>( Fill.SEQUENTIAL );
    public final Property<ContainerSetState> containerState = new Property<ContainerSetState>( new ContainerSetState( denominator.get(), new Container[] { new Container( 1, new int[] { } ) } ).padAndTrim() );
    public final Property<Boolean> showReduced = new Property<Boolean>( false );
    public final Property<Boolean> showMixed = new Property<Boolean>( false );
    public final Property<PieSetState> pieSetState = new Property<PieSetState>( new PieSetState() );

    public FractionsIntroModel() {
        //synchronize the container state with the numerator and denominator for when the user uses the spinners
        numerator.addObserver( new ChangeObserver<Integer>() {
            public void update( Integer newValue, Integer oldValue ) {

                if ( !userToggled ) {
                    int delta = newValue - oldValue;
                    containerState.set( containerState.get().addPieces( delta ) );
                }
            }
        } );

        denominator.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer denominator ) {

                if ( !userToggled ) {

                    //When changing denominator, move pieces to nearby slots
                    ContainerSetState oldState = containerState.get();
                    ContainerSetState newState = new ContainerSetState( denominator, new Container[] { new Container( denominator, new int[0] ) } ).padAndTrim();

                    //for each piece in oldState, find the closest unoccupied location in newState and add it there
                    for ( CellPointer cellPointer : oldState.getFilledCells() ) {

                        //Find closest unoccupied location
                        CellPointer cp = newState.getClosestUnoccupiedLocation( cellPointer );
                        if ( cp != null ) {
                            newState = newState.toggle( cp );
                        }
                        else {
                            System.out.println( "Null CP!" );
                        }
                    }

                    containerState.set( newState.padAndTrim() );
                }
            }
        } );

        containerState.addObserver( new ChangeObserver<ContainerSetState>() {
            public void update( ContainerSetState newValue, ContainerSetState oldValue ) {

                //If caused by the user, then send the changes back to the numerator & denominator.
                if ( userToggled ) {
                    if ( newValue.denominator == oldValue.denominator ) {
                        numerator.set( newValue.numerator );
                    }
                }
            }
        } );

        //When the user drags slices, update the ContainerState (so it will update the spinner and make it easy to switch representations)
        pieSetState.addObserver( new VoidFunction1<PieSetState>() {
            public void apply( PieSetState pieSetState ) {
//                setUserToggled( true );
//                containerState.set( pieSetState.toContainerState() );
//                setUserToggled( false );
            }
        } );

        //Animate the model when the clock ticks
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                pieSetState.set( pieSetState.get().stepInTime( clockEvent.getSimulationTimeChange() ) );
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