/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2;

import java.util.ArrayList;

/**
 * A SimulationTimeListener that delegates to its children.
 */
public class CompositeSimulationTimeListener implements SimulationTimeListener {
    ArrayList listeners = new ArrayList();

    public void addSimulationTimeListener( SimulationTimeListener listy ) {
        listeners.add( listy );
    }

    public int numSimulationTimeListeners() {
        return listeners.size();
    }

    public SimulationTimeListener simulationTimeListenerAt( int i ) {
        return (SimulationTimeListener)listeners.get( i );
    }

    public void removeSimulationTimeListener( SimulationTimeListener stl ) {
        listeners.remove( stl );
    }

    public void simulationTimeIncreased( double dt ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            SimulationTimeListener simulationTimeListener = (SimulationTimeListener)listeners.get( i );
            simulationTimeListener.simulationTimeIncreased( dt );
        }
    }
}
