/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricCanvas;
import edu.colorado.phet.capacitorlab.util.GridPanel;
import edu.colorado.phet.capacitorlab.util.GridPanel.Anchor;
import edu.colorado.phet.capacitorlab.view.meters.CapacitanceMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.PlateChargeMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.StoredEnergyMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterNode;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel for meter settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MetersControlPanel extends PhetTitledPanel {
    
    private final JCheckBox capacitanceCheckBox, chargeCheckBox, energyCheckBox, voltmeterCheckBox, fieldDetectorCheckBox;
    
    public MetersControlPanel( CLModel model, final DielectricCanvas canvas ) {
        super( CLStrings.TITLE_METERS );
        
        // Capacitance meter
        {
            final CapacitanceMeterNode meter = canvas.getCapacitanceMeterNode();
            capacitanceCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_CAPACITANCE );
            capacitanceCheckBox.setSelected( meter.isVisible() );
            capacitanceCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( capacitanceCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        capacitanceCheckBox.setSelected( meter.isVisible() );
                    }
                }
            } );
        }
        
        // Plate Charge meter
        {
            final PlateChargeMeterNode meter = canvas.getChargeMeterNode();
            chargeCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_CHARGE );
            chargeCheckBox.setSelected( meter.isVisible() );
            chargeCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( chargeCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        chargeCheckBox.setSelected( meter.isVisible() );
                    }
                }
            } );
        }
        
        // Energy meter
        {
            final StoredEnergyMeterNode meter = canvas.getEnergyMeterNode();
            energyCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_ENERGY );
            energyCheckBox.setSelected( meter.isVisible() );
            energyCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( energyCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        energyCheckBox.setSelected( meter.isVisible() );
                    }
                }
            } );
        }
        
        // Voltmeter
        {
            final VoltmeterNode meter = canvas.getVoltMeterNode();
            voltmeterCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_VOLTMETER );
            voltmeterCheckBox.setSelected( meter.isVisible() );
            voltmeterCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( voltmeterCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        voltmeterCheckBox.setSelected( meter.isVisible() );
                    }
                }
            } );
        }
        
        // Field Detector
        {
            fieldDetectorCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_FIELD_DETECTOR );
        }
        
        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( capacitanceCheckBox );
        innerPanel.add( chargeCheckBox );
        innerPanel.add( energyCheckBox );
        innerPanel.add( voltmeterCheckBox );
        innerPanel.add( fieldDetectorCheckBox );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
    }
}
