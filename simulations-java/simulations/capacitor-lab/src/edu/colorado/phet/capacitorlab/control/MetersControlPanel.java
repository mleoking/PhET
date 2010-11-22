/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;

/**
 * Control panel for meter settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MetersControlPanel extends PhetTitledPanel {
    
    public MetersControlPanel( DielectricModel model ) {
        super( CLStrings.METERS );
        
        // check boxes
        JCheckBox capacitanceCheckBox = new BooleanPropertyCheckBox( CLStrings.CAPACITANCE, model.getCapacitanceMeter().getVisibleProperty() );
        JCheckBox chargeCheckBox = new BooleanPropertyCheckBox( CLStrings.PLATE_CHARGE, model.getPlateChargeMeter().getVisibleProperty() );
        JCheckBox energyCheckBox = new BooleanPropertyCheckBox( CLStrings.STORED_ENERGY, model.getStoredEnergyMeter().getVisibleProperty() );
        JCheckBox voltmeterCheckBox = new BooleanPropertyCheckBox( CLStrings.VOLTMETER, model.getVoltmeter().getVisibleProperty() );
        JCheckBox eFieldDetectorCheckBox = new BooleanPropertyCheckBox( CLStrings.ELECTRIC_FIELD_DETECTOR, model.getEFieldDetector().getVisibleProperty() );
        
        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( capacitanceCheckBox );
        innerPanel.add( chargeCheckBox );
        innerPanel.add( energyCheckBox );
        innerPanel.add( voltmeterCheckBox );
        innerPanel.add( eFieldDetectorCheckBox );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
    }
}
