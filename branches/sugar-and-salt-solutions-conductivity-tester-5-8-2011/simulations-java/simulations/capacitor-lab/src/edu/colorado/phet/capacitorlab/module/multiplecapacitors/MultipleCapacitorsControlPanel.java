// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.control.CircuitChoiceControl;
import edu.colorado.phet.capacitorlab.control.MetersControlPanel;
import edu.colorado.phet.capacitorlab.control.ViewControlPanel;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * Control panel for the "Multiple Capacitors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsControlPanel extends ControlPanel {

    public MultipleCapacitorsControlPanel( Resettable resettable, MultipleCapacitorsModel model, CLGlobalProperties globalProperties ) {
        addControlFullWidth( new ViewControlPanel( model.plateChargesVisible, model.eFieldVisible ) );
        addControlFullWidth( new MetersControlPanel( model.getCapacitanceMeter(), model.getPlateChargeMeter(), model.getStoredEnergyMeter(), model.getVoltmeter(), model.getEFieldDetector() ) );
        addControlFullWidth( new CircuitChoiceControl( model.getCircuits(), model.currentCircuit ) );
        addResetAllButton( resettable );
    }
}
