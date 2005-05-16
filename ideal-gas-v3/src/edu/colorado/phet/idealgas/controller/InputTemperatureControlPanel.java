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

/**
 * InputTemperatureControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InputTemperatureControlPanel extends JPanel {
    private GridBagConstraints particleControlsGbc;

    public InputTemperatureControlPanel( IdealGasModule module, Pump[] pumps ) {
        super( new GridBagLayout() );
        makeParticlesControls( module, pumps );
    }

    /**
     * Creates the panel that holds controls for the particles in the box
     */
    private void makeParticlesControls( IdealGasModule module, final Pump[] pumps ) {
        particleControlsGbc = new GridBagConstraints( 0, 0,
                                                      2, 1, 1, 1,
                                                      GridBagConstraints.CENTER,
                                                      GridBagConstraints.HORIZONTAL,
                                                      new Insets( 0, 0, 0, 0 ), 0, 0 );

        // Add control for temperature at which particles are introduced
        JLabel tempLbl = new JLabel( SimStrings.get( "AdvancedControlPanel.Particle_Temperature"));
        particleControlsGbc.insets = new Insets( 10, 10, 10, 10 );
        particleControlsGbc.gridwidth = 1;
        particleControlsGbc.gridx = 0;
        particleControlsGbc.gridy = 1;
        particleControlsGbc.anchor = GridBagConstraints.EAST;
        this.add( tempLbl, particleControlsGbc );

        // todo: figure out where the 2.5 comes from, and don't use a hard-coded constant here!!!!!
        final double hackConst = 2.5;
        final JSpinner tempSpinner = new JSpinner( new SpinnerNumberModel( IdealGasModel.DEFAULT_ENERGY / IdealGasConfig.TEMPERATURE_SCALE_FACTOR / hackConst,
                                                                           50, 1000, 1 ) );
        particleControlsGbc.gridx = 1;
        particleControlsGbc.anchor = GridBagConstraints.WEST;
        this.add( tempSpinner, particleControlsGbc );
        // Changes the temperature at which particles are pumped into the box
        tempSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double temp = ((Double)tempSpinner.getValue()).doubleValue() * IdealGasConfig.TEMPERATURE_SCALE_FACTOR * hackConst;
                for( int i = 0; i < pumps.length; i++ ) {
                    pumps[i].setPumpingEnergyStrategy( new Pump.FixedEnergyStrategy( temp ));
                }
            }
        } );
    }
}
