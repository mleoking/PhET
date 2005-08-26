/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.control;

import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.view.DischargeLampEnergyMonitorPanel2;
import edu.colorado.phet.dischargelamps.DischargeLampModule;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

/**
 * DischargeLampControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampControlPanel {

    public DischargeLampControlPanel( final DischargeLampModule module, ControlPanel controlPanel ) {

//        // A combo box for atom types
//        JComponent atomTypeComboBox = new AtomTypeChooser( module );
//        controlPanel.addControl( atomTypeComboBox );
//
//        // A slider for the battery voltage
//        double maxVoltage = 5;
//        final ModelSlider batterySlider = new ModelSlider( "Battery Voltage", "V", 0, maxVoltage, maxVoltage / 2 );
////        final ModelSlider batterySlider = new ModelSlider( "Battery Voltage", "V", 0, .1, 0.05 );
//        batterySlider.setPreferredSize( new Dimension( 250, 100 ) );
//        controlPanel.addControl( batterySlider );
//        batterySlider.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                module.getCathode().setPotential( batterySlider.getValue() );
//                module.getAnode().setPotential( 0 );
//            }
//        } );
//        module.getCathode().setPotential( batterySlider.getValue() );
//
//        // A slider for the battery current
//        final ModelSlider currentSlider = new ModelSlider( "Electron Production Rate", "electrons/msec",
//                                         0, 0.3, 0, new DecimalFormat( "0.00#" ) );
//        currentSlider.setPreferredSize( new Dimension( 250, 100 ) );
//        currentSlider.setMajorTickSpacing( 0.1 );
////        currentSlider.setNumMajorTicks( 4 );              C
////        currentSlider.setNumMinorTicksPerMajorTick( 1 );
//        controlPanel.addControl( currentSlider );
//        currentSlider.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                module.getCathode().setCurrent( currentSlider.getValue() );
//            }
//        } );
//
//        // Add an energy level monitor panel. Note that the panel has a null layout, so we have to put it in a
//        // panel that does have one, so it gets laid out properly
//        DischargeLampEnergyMonitorPanel2 energyLevelsMonitorPanel =
//                new DischargeLampEnergyMonitorPanel2( module, module.getClock(),
//                                                      module.getAtomicStates(),
//                                                      150,
//                                                      300 );
//
//        controlPanel.addControl( energyLevelsMonitorPanel );
//
//        // Add a button to show/hide the spectrometer
//        final JCheckBox spectrometerCB = new JCheckBox( SimStrings.get( "ControlPanel.SpectrometerButtonLabel" ) );
//        spectrometerCB.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                module.setSpectrometerGraphicVisible( spectrometerCB.isSelected() );
//            }
//        } );
//        controlPanel.addControl( spectrometerCB );
//        module.setSpectrometerGraphicVisible.spectrometerGraphic.setVisible( spectrometerCB.isSelected() );
    }
}
