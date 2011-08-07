// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Abstract base class in sugar and salt solution models, which provides clock and reset functions.
 *
 * @author Sam Reid
 */
public abstract class AbstractSugarAndSaltSolutionsModel implements ResetModel {

    //Listeners which are notified when the sim is reset.
    private ArrayList<VoidFunction0> resetListeners = new ArrayList<VoidFunction0>();

    //Model clock
    public final ConstantDtClock clock;

    //Settable property that indicates whether the clock is running or paused.
    //The clock is never turned off in the first tab, since there are no dynamics and hence no pause button
    public final BooleanProperty playButtonPressed = new BooleanProperty( true );

    //Boolean flag to indicate whether the module is running, for purposes of starting and pausing the clock
    public final BooleanProperty moduleActive = new BooleanProperty( false );

    public AbstractSugarAndSaltSolutionsModel( final ConstantDtClock clock ) {
        this.clock = clock;

        //Wire up to the clock so we can update when it ticks
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateModel( clockEvent.getSimulationTimeChange() );
            }
        } );

        //Make the clock run if "play" is selected with the user controls and if the module is active
        final And clockRunning = playButtonPressed.and( moduleActive );
        clockRunning.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean clockRunning ) {
                if ( clockRunning ) {
                    clock.start();
                }
                else {
                    clock.pause();
                }
            }
        } );
    }

    protected abstract void updateModel( double simulationTimeChange );

    //Adds a listener that will be notified when the model is reset
    public void addResetListener( VoidFunction0 listener ) {
        resetListeners.add( listener );
    }

    protected void notifyReset() {
        //Notify listeners that registered for a reset message
        for ( VoidFunction0 resetListener : resetListeners ) {
            resetListener.apply();
        }
    }
}