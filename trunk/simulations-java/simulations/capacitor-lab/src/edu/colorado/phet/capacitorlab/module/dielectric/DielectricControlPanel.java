// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.control.DielectricPropertiesControlPanel;
import edu.colorado.phet.capacitorlab.control.MetersControlPanel;
import edu.colorado.phet.capacitorlab.control.ViewControlPanel;
import edu.colorado.phet.capacitorlab.developer.DeveloperControlPanel;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * Control panel for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricControlPanel extends ControlPanel {

    private final JPanel dielectricPropertiesControlPanel;

    public DielectricControlPanel( Resettable resettable, DielectricModel model, CLGlobalProperties globalProperties ) {

        addControlFullWidth( new ViewControlPanel( model.plateChargesVisible, model.eFieldVisible ) );
        addControlFullWidth( new MetersControlPanel( model.getCapacitanceMeter(), model.getPlateChargeMeter(), model.getStoredEnergyMeter(), model.getVoltmeter(), model.getEFieldDetector() ) );
        dielectricPropertiesControlPanel = new DielectricPropertiesControlPanel( model.getCapacitor(), model.getDielectricMaterials(), model.dielectricChargeView );
        addControlFullWidth( dielectricPropertiesControlPanel );

        if ( globalProperties.dev ) {
            addControlFullWidth( new DeveloperControlPanel( globalProperties.frame, model ) );
        }

        addResetAllButton( resettable );
    }

    public void setDielectricPropertiesControlPanelVisible( boolean visible ) {
        dielectricPropertiesControlPanel.setVisible( visible );
    }
}
