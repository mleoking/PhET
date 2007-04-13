/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.bernoulli.bernoulli.clock2;

/**
 * Listens for time simulation events.
 */
public interface SimulationTimeListener {
    public void simulationTimeIncreased(double dt);
}
