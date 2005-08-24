/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.controller;

import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.model.PhotoelectricTarget;
import edu.colorado.phet.photoelectric.module.PhotoelectricModule;
import edu.colorado.phet.photoelectric.view.GraphWindow;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.common.view.ControlPanel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * PhotoelectricControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricControlPanel {

    public PhotoelectricControlPanel( final PhotoelectricModule module, final GraphWindow graphWindow ) {
        final PhotoelectricModel model = (PhotoelectricModel)module.getModel();
        final CollimatedBeam beam = model.getBeam();

        ControlPanel controlPanel = (ControlPanel)module.getControlPanel();

        //----------------------------------------------------------------
        // Target controls
        //----------------------------------------------------------------

        JPanel targetControlPnl = new JPanel( new GridLayout( 1, 1 ) );
        targetControlPnl.setBorder( new TitledBorder( "Target" ) );
        controlPanel.addControl( targetControlPnl );

        // Put the materials in the desired order. Sodium should be at the top, and the "mystery material",
        // magnesium, should be at the end
        ArrayList selectionList = new ArrayList( );
        selectionList.add( PhotoelectricTarget.SODIUM );
        Collection materials = PhotoelectricTarget.WORK_FUNCTIONS.keySet();
        for( Iterator iterator = materials.iterator(); iterator.hasNext(); ) {
            Object obj = (Object)iterator.next();
            if( obj != PhotoelectricTarget.SODIUM && obj != PhotoelectricTarget.MAGNESIUM ) {
                selectionList.add( obj );
            }
        }
        selectionList.add(PhotoelectricTarget.MAGNESIUM);

        final JComboBox targetMaterial = new JComboBox( selectionList.toArray() );
        targetControlPnl.add( targetMaterial );
        final PhotoelectricTarget target = model.getTarget();
        targetMaterial.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                target.setMaterial( targetMaterial.getSelectedItem() );
            }
        } );
        target.setMaterial( targetMaterial.getSelectedItem() );

        //----------------------------------------------------------------
        // Beam controls
        //----------------------------------------------------------------

        JPanel beamControlPnl = new JPanel( new GridLayout( 2, 1 ) );
        beamControlPnl.setBorder( new TitledBorder( "Lamp" ) );
        controlPanel.addControl( beamControlPnl );

        // A slider for the wavelength
        final ModelSlider wavelengthSlider = new ModelSlider( SimStrings.get( "Control.Wavelength" ),
                                                              "nm",
                                                              PhotoelectricModel.MIN_WAVELENGTH,
                                                              PhotoelectricModel.MAX_WAVELENGTH,
                                                              400 );
        wavelengthSlider.setMajorTickSpacing( 100 );
        wavelengthSlider.setSliderLabelFormat( new DecimalFormat( "#" ) );
        wavelengthSlider.setPreferredSize( new Dimension( 250, 100 ) );
        beam.setWavelength( wavelengthSlider.getValue() );
        beamControlPnl.add( wavelengthSlider );
        wavelengthSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beam.setWavelength( wavelengthSlider.getValue() );
            }
        } );

        // A slider for the beam intensity
        final ModelSlider beamIntensitySlider = new ModelSlider( SimStrings.get( "Intensity" ), "",
                                                                 0, PhotoelectricModel.MAX_PHOTONS_PER_SECOND,
                                                                 PhotoelectricModel.MAX_PHOTONS_PER_SECOND / 2 );
        beamIntensitySlider.setPreferredSize( new Dimension( 250, 100 ) );
        beamIntensitySlider.setPaintLabels( false );
        beam.setPhotonsPerSecond( beamIntensitySlider.getValue() );
        beamControlPnl.add( beamIntensitySlider );
        beamIntensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beam.setPhotonsPerSecond( beamIntensitySlider.getValue() );
            }
        } );

        //----------------------------------------------------------------
        // Battery controls
        //----------------------------------------------------------------

        // A slider for the battery voltage
        DecimalFormat voltageFormat = new DecimalFormat( "0.000" );
        final ModelSlider batterySlider = new ModelSlider( SimStrings.get( "Control.BatteryVoltageLabel" ),
                                                           "V",
                                                           PhotoelectricModel.MIN_VOLTAGE,
                                                           PhotoelectricModel.MAX_VOLTAGE,
                                                           0,
                                                           voltageFormat );
        batterySlider.setPreferredSize( new Dimension( 250, 100 ) );
        batterySlider.setNumMajorTicks( 7 );
        batterySlider.setNumMinorTicksPerMajorTick( 2 );
        batterySlider.setSliderLabelFormat( new DecimalFormat( "0.00" ) );
        controlPanel.addControl( batterySlider );
        model.getTarget().setPotential( batterySlider.getValue() * PhotoelectricModel.VOLTAGE_SCALE_FACTOR );
        batterySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getTarget().setPotential( batterySlider.getValue() * PhotoelectricModel.VOLTAGE_SCALE_FACTOR );
                model.getAnode().setPotential( 0 );

//                if( targetPlate.getPotential() < 0 ) {
//                    BufferedImage bImg = circuitGraphic.getImage();
//                    circuitGraphic.setImage( BufferedImageUtils.flipX( bImg ) );
//                    circuitGraphic.setBoundsDirty();
//                    circuitGraphic.repaint();
//                }
            }
        } );

        //----------------------------------------------------------------
        // Graph options
        //----------------------------------------------------------------

        final JCheckBox currentVsVoltageCB = new JCheckBox( "Current vs voltage" );
        final JCheckBox currentVsIntensityCB = new JCheckBox( "Current vs beam intensity" );
        final JCheckBox energyVsFrequencyCB = new JCheckBox( "Electron energy vs beam frequency" );
        currentVsVoltageCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                graphWindow.setCurrentVsVoltageVisible( currentVsVoltageCB.isSelected() );
                graphWindow.setVisible( currentVsVoltageCB.isSelected()
                                        || currentVsIntensityCB.isSelected()
                                        || energyVsFrequencyCB.isSelected() );
            }
        } );
        currentVsIntensityCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                graphWindow.setCurrentVsIntensityVisible( currentVsIntensityCB.isSelected() );
                graphWindow.setVisible( currentVsVoltageCB.isSelected()
                                        || currentVsIntensityCB.isSelected()
                                        || energyVsFrequencyCB.isSelected() );
            }
        } );
        energyVsFrequencyCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                graphWindow.setEnergyVsFrequency( energyVsFrequencyCB.isSelected() );
                graphWindow.setVisible( currentVsVoltageCB.isSelected()
                                        || currentVsIntensityCB.isSelected()
                                        || energyVsFrequencyCB.isSelected() );
            }
        } );

        JPanel graphOptionsPanel = new JPanel();
        graphOptionsPanel.setBorder( new TitledBorder( "Graphs" ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( graphOptionsPanel );
        graphOptionsPanel.setLayout( layout );
        layout.addComponent( currentVsVoltageCB, 0, 0 );
        layout.addComponent( currentVsIntensityCB, 1, 0 );
        layout.addComponent( energyVsFrequencyCB, 2, 0 );
        controlPanel.addControlFullWidth( graphOptionsPanel );

        //----------------------------------------------------------------
        // Plot button
        //----------------------------------------------------------------
//        final JButton plotBtn = new JButton( "Plot" );
//        plotBtn.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                double voltage = batterySlider.getValue();
//                Ammeter ammeter = getPhotoelectricModel().getAmmeter();
//                AmmeterDataCollector ammeterReader = new AmmeterDataCollector( (Frame)SwingUtilities.getRoot( plotBtn ),
//                                                                               ammeter, getClock() );
//                double current = ammeterReader.getCurrent();
//                double wavelength = wavelengthSlider.getValue();
//                currentVsVoltageGraph.addDataPoint( voltage, current, wavelength );
//            }
//        } );
//        controlPanel.add( plotBtn );
//
//        final JButton clearPlotBtn = new JButton( "Clear" );
//        clearPlotBtn.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                currentVsVoltageGraph.clearData();
//            }
//        } );
//        controlPanel.add( clearPlotBtn );

    }

}

