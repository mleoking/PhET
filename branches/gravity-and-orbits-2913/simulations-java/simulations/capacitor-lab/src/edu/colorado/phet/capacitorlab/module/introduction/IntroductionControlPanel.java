// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.introduction;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.control.MetersControlPanel;
import edu.colorado.phet.capacitorlab.control.ViewControlPanel;
import edu.colorado.phet.capacitorlab.developer.DeveloperControlPanel;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * Control panel for the "Introduction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionControlPanel extends ControlPanel {

    public IntroductionControlPanel( Resettable resettable, DielectricModel model, CLCanvas canvas, CLGlobalProperties globalProperties ) {
        addControlFullWidth( new ViewControlPanel( canvas.getPlateChargesVisibleProperty(), canvas.getEFieldVisibleProperty() ) );
        addControlFullWidth( new MetersControlPanel( model.capacitanceMeter, CLStrings.CAPACITANCE,
                                                     model.plateChargeMeter, CLStrings.PLATE_CHARGE,
                                                     model.storedEnergyMeter, model.voltmeter, model.eFieldDetector ) );
        if ( globalProperties.dev ) {
            addControlFullWidth( new DeveloperControlPanel( globalProperties.frame, model ) );
        }
        addResetAllButton( resettable );
    }
}
