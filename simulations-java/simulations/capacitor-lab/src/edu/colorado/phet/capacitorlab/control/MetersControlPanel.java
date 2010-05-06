/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control panel for meter settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MetersControlPanel extends CLTitledControlPanel {
    
    private final JCheckBox capacitanceCheckBox, plateChargeCheckBox, energyCheckBox, voltmeterCheckBox, fieldDetectorCheckBox;

    public MetersControlPanel() {
        super( CLStrings.TITLE_METERS );
        
        capacitanceCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_CAPACITANCE );
        
        plateChargeCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_PLATE_CHARGE );
        
        energyCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_ENERGY );
        
        voltmeterCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_VOLTMETER );
        
        fieldDetectorCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_FIELD_DETECTOR );
        
        // layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( capacitanceCheckBox, row++, column );
        layout.addComponent( plateChargeCheckBox, row++, column );
        layout.addComponent( energyCheckBox, row++, column );
        layout.addComponent( voltmeterCheckBox, row++, column );
        layout.addComponent( fieldDetectorCheckBox, row++, column );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // default state
        //XXX
    }
}
