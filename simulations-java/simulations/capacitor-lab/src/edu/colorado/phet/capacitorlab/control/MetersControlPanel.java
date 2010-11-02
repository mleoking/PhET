/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.EFieldDetector;
import edu.colorado.phet.capacitorlab.model.Voltmeter;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricCanvas;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel for meter settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MetersControlPanel extends PhetTitledPanel {
    
    private final JCheckBox capacitanceCheckBox, chargeCheckBox, energyCheckBox, voltmeterCheckBox, eFieldDetectorCheckBox;
    
    public MetersControlPanel( final DielectricCanvas canvas, DielectricModel model ) {
        super( CLStrings.METERS );
        
        // Capacitance meter
        {
            final PNode meter = canvas.getCapacitanceMeterNode();
            capacitanceCheckBox = new JCheckBox( CLStrings.CAPACITANCE );
            capacitanceCheckBox.setSelected( meter.getVisible() );
            capacitanceCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( capacitanceCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        capacitanceCheckBox.setSelected( meter.getVisible() );
                    }
                }
            } );
        }
        
        // Plate Charge meter
        {
            final PNode meter = canvas.getChargeMeterNode();
            chargeCheckBox = new JCheckBox( CLStrings.PLATE_CHARGE );
            chargeCheckBox.setSelected( meter.getVisible() );
            chargeCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( chargeCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        chargeCheckBox.setSelected( meter.getVisible() );
                    }
                }
            } );
        }
        
        // Energy meter
        {
            final PNode meter = canvas.getEnergyMeterNode();
            energyCheckBox = new JCheckBox( CLStrings.STORED_ENERGY );
            energyCheckBox.setSelected( meter.getVisible() );
            energyCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( energyCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        energyCheckBox.setSelected( meter.getVisible() );
                    }
                }
            } );
        }
        
        // Voltmeter
        {
            final Voltmeter voltmeter = model.getVoltmeter();
            voltmeterCheckBox = new JCheckBox( CLStrings.VOLTMETER );
            voltmeterCheckBox.setSelected( voltmeter.isVisible() );
            voltmeterCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    voltmeter.setVisible( voltmeterCheckBox.isSelected() );
                }
            } );
            voltmeter.addVisibleObserver( new SimpleObserver() {
                public void update() {
                    voltmeterCheckBox.setSelected( voltmeter.isVisible() );
                }
            } );
        }
        
        // E-field Detector
        {
            final EFieldDetector detector = model.getEFieldDetector();
            eFieldDetectorCheckBox = new JCheckBox( CLStrings.ELECTRIC_FIELD_DETECTOR );
            eFieldDetectorCheckBox.setSelected( detector.isVisible() );
            eFieldDetectorCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    detector.setVisible( eFieldDetectorCheckBox.isSelected() );
                }
            } );
            detector.addVisibleObserver( new SimpleObserver() {
                public void update() {
                    eFieldDetectorCheckBox.setSelected( detector.isVisible() );
                }
            } );
        }
        
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
