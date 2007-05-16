/*PhET, 2004.*/
package edu.colorado.phet.common.timeseries;


/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 1:12:18 PM
 */
public abstract class Mode {
    private TimeSeriesModel timeSeriesModel;

    public Mode( TimeSeriesModel timeSeriesModel ) {
        this.timeSeriesModel = timeSeriesModel;
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return timeSeriesModel;
    }

    public abstract void step( double dt );
}