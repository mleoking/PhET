// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;

/**
 * Control panel for meter settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MetersControlPanel extends PhetTitledPanel {

    public MetersControlPanel( CapacitanceMeter capacitanceMeter, PlateChargeMeter plateChangeMeter, StoredEnergyMeter storedEnergyMeter, Voltmeter voltmeter, EFieldDetector eFieldDetector ) {
        super( CLStrings.METERS );

        // check boxes
        JCheckBox capacitanceCheckBox = new PropertyCheckBox( CLStrings.CAPACITANCE, capacitanceMeter.getVisibleProperty() );
        JCheckBox plateChargeCheckBox = new PropertyCheckBox( CLStrings.PLATE_CHARGE, plateChangeMeter.getVisibleProperty() );
        JCheckBox storedEnergyCheckBox = new PropertyCheckBox( CLStrings.STORED_ENERGY, storedEnergyMeter.getVisibleProperty() );
        JCheckBox voltmeterCheckBox = new PropertyCheckBox( CLStrings.VOLTMETER, voltmeter.getVisibleProperty() );
        JCheckBox eFieldDetectorCheckBox = new PropertyCheckBox( CLStrings.ELECTRIC_FIELD_DETECTOR, eFieldDetector.getVisibleProperty() );

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
