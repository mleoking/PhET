/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.flourescent.model.*;
import edu.colorado.phet.flourescent.view.ElectronGraphic;
import edu.colorado.phet.flourescent.view.EnergyLevelMonitorPanel;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.view.ResonatingCavityGraphic;
import edu.colorado.phet.lasers.view.AtomGraphic;
import edu.colorado.phet.lasers.view.LaserEnergyLevelMonitorPanel;
import edu.colorado.phet.lasers.view.EnergyLevelsDialog;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;

/**
 * DischargeLampModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SingleAtomModule extends DischargeLampModule {
    private DischargeLampAtom atom;

    /**
     * Constructor
     *
     * @param clock
     */
    protected SingleAtomModule( String name, AbstractClock clock, int numEnergyLevels ) {
        super( name, clock );
        addAtom( getTube(), numEnergyLevels );

        // Make the area from which the cathode emits electrons very small
        super.getCathode().setLength( 1 );

        // Add module-specific controls
        addControls();
    }

    /**
     *
     */
    private void addControls() {

        // Add an energy level monitor panel. Note that the panel has a null layout, so we have to put it in a
        // panel that does have one, so it gets laid out properly
        final EnergyLevelMonitorPanel elmp = addEnergyMonitorPanel();

        // Add a button for firing a single electron. This also tells the energy level panel that if an
        // electron has been produced
        JButton singleShotBtn = new JButton( "Fire electron" );
        singleShotBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Electron electron = getCathode().produceElectron();
                if( electron != null ) {
                    elmp.addElectron( electron );
                }
            }
        } );
        getControlPanel().add( singleShotBtn );

        JCheckBox slowMotionCB = new JCheckBox( new AbstractAction( "Run in slow motion" ) {
            public void actionPerformed( ActionEvent e ) {
                JCheckBox cb = (JCheckBox)e.getSource();
                if( cb.isSelected() ) {
                    getClock().setDt( FluorescentLightsConfig.DT / 5 );
                }
                else {
                    getClock().setDt( FluorescentLightsConfig.DT );
                }
            }
        });
        getControlPanel().add( slowMotionCB );
    }

    /**
     *
     * @return
     */
    private EnergyLevelMonitorPanel addEnergyMonitorPanel() {
        final EnergyLevelMonitorPanel elmp = new EnergyLevelMonitorPanel( this, getClock(), atom.getStates(), 150, 300 );
        JPanel jp = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0,0,1,1,0,0,
                                                         GridBagConstraints.NORTHWEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0,0,0,0),0,0 );
        elmp.setBorder( new EtchedBorder( ) );
        jp.add( elmp, gbc );

        // Add the spinner that controls the number of energy levels
        final JSpinner numLevelsSpinner = new JSpinner( new SpinnerNumberModel( FluorescentLightsConfig.NUM_ENERGY_LEVELS, 2,
                                                                          FluorescentLightsConfig.MAX_NUM_ENERGY_LEVELS,
                                                                          1 ) );
        numLevelsSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                atom.setStates( createAtomicStates( ((Integer)numLevelsSpinner.getValue()).intValue() ));
                elmp.setEnergyLevels( atom.getStates() );
            }
        } );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        jp.add(numLevelsSpinner, gbc);
        getControlPanel().add( jp );
        
        return elmp;
    }

    /**
     * Adds some atoms and their graphics
     *
     * @param tube
     * @param numEnergyLevels
     */
    private void addAtom( ResonatingCavity tube, int numEnergyLevels ) {
        Rectangle2D tubeBounds = tube.getBounds();

        // Todo: consolidate for both modules
        AtomicState[] states = createAtomicStates( numEnergyLevels );

        atom = new DischargeLampAtom( (LaserModel)getModel(), states );
        atom.setPosition( tubeBounds.getX() + tubeBounds.getWidth() / 2,
                          tubeBounds.getY() + tubeBounds.getHeight() / 2 );
        AtomGraphic atomGraphic = addAtom( atom );

        // Make the atom movable with the mouse within the bounds of the tube
        Rectangle2D atomBounds = new Rectangle2D.Double( tubeBounds.getMinX() + atom.getRadius(),
                                                         tubeBounds.getMinY() + atom.getRadius(),
                                                         tubeBounds.getWidth() - atom.getRadius() * 2,
                                                         tubeBounds.getHeight() - atom.getRadius() * 2 );
        atomGraphic.setIsMouseable( true, atomBounds );
        atomGraphic.setCursorHand();
    }

    private AtomicState[] createAtomicStates( int numEnergyLevels ) {
        AtomicState[] states = new AtomicState[numEnergyLevels];
        double minVisibleEnergy = Photon.wavelengthToEnergy( Photon.DEEP_RED );
        double maxVisibleEnergy = Photon.wavelengthToEnergy( Photon.BLUE );
        double dE = states.length > 2 ? ( maxVisibleEnergy - minVisibleEnergy ) / ( states.length - 2 ) : 0;

        states[0] = new GroundState();
        for( int i = 1; i < states.length; i++ ) {
            states[i] = new AtomicState();
            states[i].setMeanLifetime( DischargeLampAtom.DEFAULT_STATE_LIFETIME );
            states[i].setEnergyLevel( minVisibleEnergy + (i - 1) * dE );
            states[i].setNextLowerEnergyState( states[i - 1] );
            states[i - 1].setNextHigherEnergyState( states[i] );
        }
        states[states.length - 1].setNextHigherEnergyState( AtomicState.MaxEnergyState.instance() );
        return states;
    }
}
