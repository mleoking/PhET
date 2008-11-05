/*  */
package edu.colorado.phet.theramp.model;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.timeseries.TimeSeriesModel;

/**
 * User: Sam Reid
 * Date: Aug 4, 2005
 * Time: 5:35:10 PM
 */

public class RampModel {
    private RampPhysicalModel rampPhysicalModel;
    private RampTimeSeriesModel rampTimeSeriesModel;

    public RampModel( RampModule rampModule, IClock clock ) {
        rampPhysicalModel = new RampPhysicalModel();
        rampPhysicalModel.reset();
        rampTimeSeriesModel = new RampTimeSeriesModel( rampModule );
        clock.addClockListener( rampTimeSeriesModel );
    }

    public TimeSeriesModel getRampTimeSeriesModel() {
        return rampTimeSeriesModel;
    }

    public RampPhysicalModel getRampPhysicalModel() {
        return rampPhysicalModel;
    }

    public void reset() {
        rampTimeSeriesModel.reset();
        rampPhysicalModel.reset();
    }

    public void setObject( RampObject rampObject ) {
        rampPhysicalModel.setObject( rampObject );
    }

    public Block getBlock() {
        return rampPhysicalModel.getBlock();
    }

    public void record() {
        rampTimeSeriesModel.setRecordMode();
        rampTimeSeriesModel.setPaused( false );
    }

    public void playback() {
        rampTimeSeriesModel.setPlaybackMode();
        rampTimeSeriesModel.setPaused( false );
    }

    public void setMass( double value ) {
        rampPhysicalModel.setMass( value );
    }

    public void setAppliedForce( double appliedForce ) {
        rampPhysicalModel.setAppliedForce( appliedForce );
    }
}
