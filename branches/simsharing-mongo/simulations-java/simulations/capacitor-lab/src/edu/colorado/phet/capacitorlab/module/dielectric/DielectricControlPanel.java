// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.control.DielectricPropertiesControlPanel;
import edu.colorado.phet.capacitorlab.control.MetersControlPanel;
import edu.colorado.phet.capacitorlab.control.ViewControlPanel;
import edu.colorado.phet.capacitorlab.developer.DeveloperControlPanel;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * Control panel for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricControlPanel extends ControlPanel {

    public DielectricControlPanel( Resettable resettable, DielectricModel model, CLCanvas canvas, CLGlobalProperties globalProperties ) {
        addControlFullWidth( new ViewControlPanel( canvas.getPlateChargesVisibleProperty(), canvas.getEFieldVisibleProperty() ) );
        addControlFullWidth( new MetersControlPanel( model.capacitanceMeter, CLStrings.CAPACITANCE,
                                                     model.plateChargeMeter, CLStrings.PLATE_CHARGE,
                                                     model.storedEnergyMeter, model.voltmeter, model.eFieldDetector ) );
        addControlFullWidth( new DielectricPropertiesControlPanel( model.circuit.getCapacitor(), model.dielectricMaterials, canvas.getDielectricChargeViewProperty() ) );
        if ( globalProperties.dev ) {
            addControlFullWidth( new DeveloperControlPanel( globalProperties.frame, model ) );
        }
        addResetAllButton( resettable );
    }
}
