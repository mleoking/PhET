/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.Pump;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * InputTemperatureControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InputTemperatureControlPanel extends JPanel {
    private static double maxTemperature = 10000;

    private GridBagConstraints gbc;
    private IdealGasModule module;

    public InputTemperatureControlPanel( IdealGasModule module, Pump[] pumps ) {
        super( new GridBagLayout() );
        this.module = module;
        makeParticlesControls( pumps );
    }

    /**
     * Creates the panel that holds controls for the particles in the box
     */
    private void makeParticlesControls( final Pump[] pumps ) {
        gbc = new GridBagConstraints( 0, 0,
                                      1, 1, 1, 1,
                                      GridBagConstraints.CENTER,
                                      GridBagConstraints.NONE,
                                      new Insets( 0, 0, 0, 0 ), 0, 0 );

        // Add control for temperature at which particles are introduced
        // todo: figure out where the 2.5 comes from, and don't use a hard-coded constant here!!!!!
        final double hackConst = 2.5;
        final JSpinner tempSpinner = new JSpinner( new SpinnerNumberModel( IdealGasModel.DEFAULT_ENERGY / IdealGasConfig.TEMPERATURE_SCALE_FACTOR / hackConst,
                                                                           50, maxTemperature, 1 ) );
        tempSpinner.setEnabled( false );
        final JCheckBox tempLbl = new JCheckBox( SimStrings.get( "AdvancedControlPanel.Particle_Temperature" ), false );
        tempLbl.addActionListener( new ActionListener() {
            Pump.PumpingEnergyStrategy orgEnergyStrategy = module.getPumpingEnergyStrategy();
            Pump.PumpingEnergyStrategy energyStrategy = null;

            public void actionPerformed( ActionEvent e ) {
                tempSpinner.setEnabled( tempLbl.isSelected() );
                if( !tempLbl.isSelected() ) {
                    energyStrategy = orgEnergyStrategy;
                }
                else {
                    orgEnergyStrategy = module.getPumpingEnergyStrategy();
                    double temp = ( (Double)tempSpinner.getValue() ).doubleValue() * IdealGasConfig.TEMPERATURE_SCALE_FACTOR * hackConst;
                    energyStrategy = new Pump.FixedEnergyStrategy( temp );
                }
                for( int i = 0; i < pumps.length; i++ ) {
                    pumps[i].setPumpingEnergyStrategy( energyStrategy );
                }
            }
        } );

        gbc.insets = new Insets( 0, 0, 0, 0 );
//        gbc.insets = new Insets( 0, 10, 0, 0 );
        gbc.anchor = GridBagConstraints.NORTHWEST;
        this.add( tempLbl, gbc );

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add( tempSpinner, gbc );
        // Changes the temperature at which particles are pumped into the box
        tempSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double temp = ( (Double)tempSpinner.getValue() ).doubleValue() * IdealGasConfig.TEMPERATURE_SCALE_FACTOR * hackConst;
                for( int i = 0; i < pumps.length; i++ ) {
                    pumps[i].setPumpingEnergyStrategy( new Pump.FixedEnergyStrategy( temp ) );
                }
            }
        } );

    }
}
