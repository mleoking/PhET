/**
 * Class: MeasurementApparatusPanel
 * Package: edu.colorado.phet.idealgas.graphics
 * Author: Another Guy
 * Date: Jan 20, 2004
 */
package edu.colorado.phet.idealgas.graphics;

//import edu.colorado.phet.controller.PhetApplication;
//import edu.colorado.phet.controller.PhetControlPanel;
//import edu.colorado.phet.idealgas.controller.MeasurementControlPanel;
import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.idealgas.physics.GasMolecule;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.application.PhetApplication;

public class MeasurementApparatusPanel extends BaseIdealGasApparatusPanel {

    PhetControlPanel previousControlPanel = null;
    PhetApplication application;
    private EnergyHistogramDialog histogramDlg;

    public MeasurementApparatusPanel( PhetApplication application ) {
        super( application, "Measurements" );
        this.application = application;

        // Set up the energy histogramDlg
        histogramDlg = new EnergyHistogramDialog( (IdealGasApplication)PhetApplication.instance() );
    }

    public void activate() {
        super.activate();
        previousControlPanel = PhetApplication.instance().getPhetMainPanel().getControlPanel();
        PhetApplication.instance().getPhetMainPanel().setControlPanel( new MeasurementControlPanel( application ) );
        histogramDlg.setVisible( true );
    }

    public void deactivate() {
        PhetApplication.instance().getPhetMainPanel().setControlPanel( previousControlPanel );
        GasMolecule.enableParticleParticleInteractions( true );
        histogramDlg.setVisible( false );
        super.deactivate();
    }
}
