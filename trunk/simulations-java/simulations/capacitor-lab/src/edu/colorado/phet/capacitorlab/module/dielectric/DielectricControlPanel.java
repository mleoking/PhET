// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.control.DielectricPropertiesControlPanel;
import edu.colorado.phet.capacitorlab.control.MetersControlPanel;
import edu.colorado.phet.capacitorlab.control.ViewControlPanel;
import edu.colorado.phet.capacitorlab.developer.DeveloperControlPanel;
import edu.colorado.phet.capacitorlab.module.CLControlPanel;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * Control panel for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricControlPanel extends CLControlPanel {

    private final JPanel dielectricPropertiesControlPanel;

    public DielectricControlPanel( Frame parentFrame, Module module, DielectricModel model, DielectricCanvas canvas, boolean dev ) {

        addControlFullWidth( new ViewControlPanel( model.plateChargesVisible, model.eFieldVisible ) );
        addControlFullWidth( new MetersControlPanel( model.getCapacitanceMeter(), model.getPlateChargeMeter(), model.getStoredEnergyMeter(), model.getVoltmeter(), model.getEFieldDetector() ) );
        dielectricPropertiesControlPanel = new DielectricPropertiesControlPanel( model.getCapacitor(), model.getDielectricMaterials(), model.dielectricChargeView );
        addControlFullWidth( dielectricPropertiesControlPanel );

        if ( dev ) {
            addControlFullWidth( new DeveloperControlPanel( parentFrame, model ) );
        }

        addResetAllButton( module );
    }

    public void setDielectricPropertiesControlPanelVisible( boolean visible ) {
        dielectricPropertiesControlPanel.setVisible( visible );
    }
}
