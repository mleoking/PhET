
package edu.colorado.phet.common.timeseries;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;


/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:04 PM
 *
 */
public class RecordMode extends Mode {
    private PhetTimer timer;
    private static final double SIMULATION_TIME_DT = 1.0;

    public RecordMode( final TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel, "record" );
        timer = new PhetTimer( "record timer" );
    }

    public void step() {
        doStep( SIMULATION_TIME_DT );
    }

    public void reset() {
        timer.reset();
    }

    public PhetTimer getTimer() {
        return timer;
    }

    public void clockTicked( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
        if( !getTimeSeriesModel().isPaused() ) {
            doStep( dt );
        }
    }

    private void doStep( double dt ) {
        TimeSeriesModel timeSeriesModel = getTimeSeriesModel();
        double recorderTime = timer.getTime();
        double maxTime = timeSeriesModel.getMaxAllowedTime();
        double newTime = recorderTime + dt;// * timer.getTimerScale();
        if( newTime > maxTime ) {
            dt = ( maxTime - recorderTime );// / timer.getTimerScale();
        }
        timer.stepInTime( dt, maxTime );
        timeSeriesModel.updateModel( dt );

        timeSeriesModel.addSeriesPoint( timeSeriesModel.getModelState(), timeSeriesModel.getRecordTime() );
    }
}
