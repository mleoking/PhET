// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.introduction;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.control.MetersControlPanel;
import edu.colorado.phet.capacitorlab.control.ViewControlPanel;
import edu.colorado.phet.capacitorlab.developer.DeveloperControlPanel;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricCanvas;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * Control panel for the "Introduction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionControlPanel extends ControlPanel {

    public IntroductionControlPanel( Resettable resettable, DielectricModel model, DielectricCanvas canvas, CLGlobalProperties globalProperties ) {
        addControlFullWidth( new ViewControlPanel( canvas.plateChargesVisible, canvas.eFieldVisible ) );
        addControlFullWidth( new MetersControlPanel( model.getCapacitanceMeter(), model.getPlateChargeMeter(), model.getStoredEnergyMeter(), model.getVoltmeter(), model.getEFieldDetector() ) );
        if ( globalProperties.dev ) {
            addControlFullWidth( new DeveloperControlPanel( globalProperties.frame, model ) );
        }
        addResetAllButton( resettable );
    }
}
