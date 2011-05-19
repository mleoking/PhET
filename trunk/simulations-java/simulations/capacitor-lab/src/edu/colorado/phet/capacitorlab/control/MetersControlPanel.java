// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.CapacitanceMeter;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.PlateChargeMeter;
import edu.colorado.phet.capacitorlab.model.meter.BarMeter.StoredEnergyMeter;
import edu.colorado.phet.capacitorlab.model.meter.EFieldDetector;
import edu.colorado.phet.capacitorlab.model.meter.Voltmeter;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;

/**
 * Control panel for choosing which meters are visible.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MetersControlPanel extends PhetTitledPanel {

    public MetersControlPanel( CapacitanceMeter capacitanceMeter, PlateChargeMeter plateChangeMeter, StoredEnergyMeter storedEnergyMeter, Voltmeter voltmeter, EFieldDetector eFieldDetector ) {
        super( CLStrings.METERS );

        // check boxes
        JCheckBox capacitanceCheckBox = new PropertyCheckBox( CLStrings.CAPACITANCE, capacitanceMeter.visibleProperty );
        JCheckBox plateChargeCheckBox = new PropertyCheckBox( CLStrings.PLATE_CHARGE, plateChangeMeter.visibleProperty );
        JCheckBox storedEnergyCheckBox = new PropertyCheckBox( CLStrings.STORED_ENERGY, storedEnergyMeter.visibleProperty );
        JCheckBox voltmeterCheckBox = new PropertyCheckBox( CLStrings.VOLTMETER, voltmeter.visibleProperty );
        JCheckBox eFieldDetectorCheckBox = new PropertyCheckBox( CLStrings.ELECTRIC_FIELD_DETECTOR, eFieldDetector.visibleProperty );

        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( capacitanceCheckBox );
        innerPanel.add( plateChargeCheckBox );
        innerPanel.add( storedEnergyCheckBox );
        innerPanel.add( voltmeterCheckBox );
        innerPanel.add( eFieldDetectorCheckBox );

        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
    }
}
