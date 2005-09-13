/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.model.RampTimeSeriesModel;
import edu.colorado.phet.timeseries.TimeSeriesModel;

/**
 * User: Sam Reid
 * Date: Aug 4, 2005
 * Time: 5:35:10 PM
 * Copyright (c) Aug 4, 2005 by Sam Reid
 */

public class RampModel {
    private RampPhysicalModel rampPhysicalModel;
    private RampTimeSeriesModel rampTimeSeriesModel;

    public RampModel( RampModule rampModule, AbstractClock clock ) {
        rampPhysicalModel = new RampPhysicalModel();
        rampPhysicalModel.reset();
        rampTimeSeriesModel = new RampTimeSeriesModel( rampModule );
        clock.addClockTickListener( rampTimeSeriesModel );
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
