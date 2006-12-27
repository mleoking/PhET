/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2;

import edu.colorado.phet.coreadditions.clock2.clocks.SwingTimerClock;

/**
 * Combines the AbstractClock and the WallToSimulationAdapter to provide a more cohesive interface.
 * This is usually the functionality a Phet application will need.
 */
public class DefaultClock implements AbstractClock {
    AbstractClock tickSource;//with its own ticklisteners
    WallToSimulationAdapter adapter;

    /**
     * Creates a DefaultClock, which uses a SwingTimer and an IdentityTimeConverter.
     */
    public DefaultClock( int requestedWaitTime ) {
        this( new SwingTimerClock( requestedWaitTime ), new IdentityTimeConverter() );
    }

    public WallToSimulationTimeConverter getWallToSimulationTimeConverter() {
        return adapter.getWallToSimulationTimeConverter();
    }

    public DefaultClock( AbstractClock tickSource, WallToSimulationTimeConverter converter ) {
        this.tickSource = tickSource;
        this.adapter = new WallToSimulationAdapter( converter );
        tickSource.addTickListener( adapter );
    }

    public void setWallToSimulationTimeConverter( WallToSimulationTimeConverter converter ) {
        adapter.setWallToSimulationTimeConerter( converter );
    }

    public void addTickListener( TickListener t ) {
        tickSource.addTickListener( t );
    }

    public void addSimulationTimeListener( SimulationTimeListener stl ) {
        adapter.addSimulationTimeListener( stl );
    }

    public void removeTickListener( TickListener t ) {
        tickSource.removeTickListener( t );
    }

    public void addClockStateListener( ClockStateListener listener ) {
        tickSource.addClockStateListener( listener );
    }

    public void setRequestedDelay( int requestedWaitTime ) {
        tickSource.setRequestedDelay( requestedWaitTime );
    }

    public int getRequestedDelay() {
        return tickSource.getRequestedDelay();
    }

    public boolean isRunning() {
        return tickSource.isRunning();
    }

    public void removeSimulationTimeListener( SimulationTimeListener stl ) {
        adapter.removeSimulationTimeListener( stl );
    }

    public void start() {
        tickSource.start();
    }

    public void stop() {
        tickSource.stop();
    }

    public void kill() {
        tickSource.kill();
    }
}
