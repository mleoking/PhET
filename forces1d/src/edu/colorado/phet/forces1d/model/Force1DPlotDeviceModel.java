/** Sam Reid*/
package edu.colorado.phet.forces1d.model;

import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceModel;


/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 8:01:44 PM
 * Copyright (c) Nov 27, 2004 by Sam Reid
 */
public class Force1DPlotDeviceModel extends PlotDeviceModel {
    private Force1DModule module;
    private Force1DModel model;

    public Force1DPlotDeviceModel( Force1DModule module, final Force1DModel model, double maxTime ) {
        super( maxTime );
        this.module = module;
        this.model = model;
        addListener( new PlotDeviceModel.ListenerAdapter() {
            public void recordingStarted() {
                model.setPaused( false );
            }

            public void recordingPaused() {
                model.setPaused( true );
            }
        } );
    }

    public void reset() {
        module.reset();
    }

    protected void stepRecord( double dt ) {
        double value = model.getAppliedForce();
        model.addAppliedForcePoint( value, dt );
    }

    protected void stepPlayback( double dt ) {
    }

    public void cursorMovedToTime( double modelX ) {
        module.cursorMovedToTime( modelX );
    }


}
