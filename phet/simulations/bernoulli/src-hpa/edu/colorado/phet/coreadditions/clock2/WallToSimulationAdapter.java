/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2;

/**
 * Converts clock tick events to simulation time steps, using a conversion.
 */
public class WallToSimulationAdapter implements TickListener {
    WallToSimulationTimeConverter conversion;
    long lastTick = 0;
    CompositeSimulationTimeListener simulation = new CompositeSimulationTimeListener();

    public WallToSimulationAdapter(WallToSimulationTimeConverter conversion) {
        this.conversion = conversion;
    }

    public void addSimulationTimeListener(SimulationTimeListener simt) {
        simulation.addSimulationTimeListener(simt);
    }

    public void clockTicked(AbstractClock source) {
        if (lastTick == 0)
            lastTick = System.currentTimeMillis();
        else {
            long timeDiff = System.currentTimeMillis() - lastTick;
            double simTime = conversion.toSimulationTime(timeDiff);
            simulation.simulationTimeIncreased(simTime);
            lastTick = System.currentTimeMillis();
        }
    }

    public void setWallToSimulationTimeConerter(WallToSimulationTimeConverter converter) {
        this.conversion = converter;
    }

    public void removeSimulationTimeListener(SimulationTimeListener stl) {
        simulation.removeSimulationTimeListener(stl);
    }

    public WallToSimulationTimeConverter getWallToSimulationTimeConverter() {
        return conversion;
    }

}
