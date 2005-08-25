/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.DischargeLampModule;
import edu.colorado.phet.dischargelamps.model.Electron;
import edu.colorado.phet.dischargelamps.model.AtomicStateFactory;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * DischargeLampEnergyMonitorPanel2
 * <p>
 * Wrapper for EnergyLevelMonitorPanel that adds a spinner to set the number of energy levels.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampEnergyMonitorPanel2 extends JPanel {
    private DischargeLampEnergyLevelMonitorPanel elmp;
    private DischargeLampModule module;

    public DischargeLampEnergyMonitorPanel2( final DischargeLampModule module, AbstractClock clock,
                                             AtomicState[] atomicStates,
                                             int panelWidth, int panelHeight ) {
        super( new GridBagLayout() );
        this.module = module;
        elmp = new DischargeLampEnergyLevelMonitorPanel( module, clock, atomicStates, panelWidth, panelHeight );
        elmp.setBorder( new EtchedBorder() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 0, 0,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 10, 0, 10 ), 0, 0 );
        this.add( elmp, gbc );

        // Add the spinner that controls the number of energy levels
        final JSpinner numLevelsSpinner = new JSpinner( new SpinnerNumberModel( DischargeLampsConfig.NUM_ENERGY_LEVELS, 2,
                                                                                DischargeLampsConfig.MAX_NUM_ENERGY_LEVELS,
                                                                                1 ) );

        // Add a listener that will create the number of atomic states specified by the spinner, and apply them
        // to all the existing atoms
        numLevelsSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                AtomicState[] atomicStates = new AtomicStateFactory().createAtomicStates( ( (Integer)numLevelsSpinner.getValue() ).intValue() );
                module.setAtomicStates( atomicStates );
            }
        } );
//        AtomicState[] defaultAtomicStates = new AtomicStateFactory().createAtomicStates( ( (Integer)numLevelsSpinner.getValue() ).intValue() );
//        elmp.setEnergyLevels( defaultAtomicStates );
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add( numLevelsSpinner, gbc );
    }

    public void addElectron( Electron electron ) {
        elmp.addElectron( electron );
    }

    public void reset() {
        elmp.setEnergyLevels( module.getAtomicStates() );
    }

    public void addAtom( Atom atom ) {
        elmp.addAtom( atom );
    }

    public void setEnergyLevels( AtomicState[] atomicStates ) {
        elmp.setEnergyLevels( atomicStates );
    }
}
