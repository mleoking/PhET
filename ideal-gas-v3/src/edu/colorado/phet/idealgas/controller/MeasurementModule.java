/**
 * Class: MeasurementModule
 * Class: edu.colorado.phet.idealgas.controller
 * User: Ron LeMaster
 * Date: Sep 16, 2004
 * Time: 7:56:59 PM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.view.monitors.EnergyHistogramDialog;

public class MeasurementModule extends IdealGasModule {
    PhetControlPanel previousControlPanel = null;
    PhetApplication application;
    private EnergyHistogramDialog histogramDlg;

    public MeasurementModule( AbstractClock clock ) {
        super( clock,  "Measurements" );

        setControlPanel( new MeasurementControlPanel( this ) );
    }

    public void activate( PhetApplication application ) {
        super.activate( application );

        // Set up the energy histogramDlg. Note that we can't do this in the constructor
        // because we a reference to the application's Frame
        histogramDlg = new EnergyHistogramDialog( application.getApplicationView().getPhetFrame(),
                                                  (IdealGasModel)getModel() );
        histogramDlg.setVisible( true );
    }
}
