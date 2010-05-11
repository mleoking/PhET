/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricCanvas;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel for meter settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MetersControlPanel extends CLTitledControlPanel {
    
    private final JCheckBox capacitanceCheckBox, plateChargeCheckBox, energyCheckBox, voltmeterCheckBox, fieldDetectorCheckBox, devMeterCheckBox;

    public MetersControlPanel( final DielectricCanvas canvas, boolean dev ) {
        super( CLStrings.TITLE_METERS );
        
        capacitanceCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_CAPACITANCE );
        
        plateChargeCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_PLATE_CHARGE );
        plateChargeCheckBox.setSelected( canvas.getPlateChargeMeterNode().isVisible() );
        plateChargeCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                canvas.getPlateChargeMeterNode().setVisible( plateChargeCheckBox.isSelected() );
            }
        });
        canvas.getPlateChargeMeterNode().addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                    plateChargeCheckBox.setSelected( canvas.getPlateChargeMeterNode().isVisible() );
                }
            }
        });
        
        energyCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_ENERGY );
        
        voltmeterCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_VOLTMETER );
        
        fieldDetectorCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_FIELD_DETECTOR );
        
        devMeterCheckBox = new JCheckBox( "Developer Meter" );
        devMeterCheckBox.setForeground( Color.RED );
        devMeterCheckBox.setSelected( canvas.getDeveloperMeterNode().isVisible() );
        devMeterCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                canvas.getDeveloperMeterNode().setVisible( devMeterCheckBox.isSelected() );
            }
        });
        canvas.getDeveloperMeterNode().addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                    devMeterCheckBox.setSelected( canvas.getDeveloperMeterNode().isVisible() );
                }
            }
        });
        
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
        if ( dev ) {
            layout.addComponent( devMeterCheckBox, row++, column );
        }
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // default state
        //XXX
    }
}
