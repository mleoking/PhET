package edu.colorado.phet.common.timeseries;

/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:04 PM
 */
public class RecordMode extends Mode {
    private double recordTime = 0;

    public RecordMode( final TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel );
    }

    public void reset() {
        recordTime = 0.0;
    }

    public void step( double dt ) {
        double maxTime = getTimeSeriesModel().getMaxRecordTime();
        double newTime = recordTime + dt;
        if( newTime > maxTime ) {
            dt = ( maxTime - recordTime );
        }
        recordTime += dt;
        getTimeSeriesModel().updateModel( dt );
        getTimeSeriesModel().addSeriesPoint( getTimeSeriesModel().getModelState(), recordTime );
    }

    public double getRecordTime() {
        return recordTime;
    }
}
