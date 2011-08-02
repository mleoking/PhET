// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

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

    public AbstractSugarAndSaltSolutionsModel( ConstantDtClock clock ) {
        this.clock = clock;

        //Wire up to the clock so we can update when it ticks
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateModel( clockEvent.getSimulationTimeChange() );
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