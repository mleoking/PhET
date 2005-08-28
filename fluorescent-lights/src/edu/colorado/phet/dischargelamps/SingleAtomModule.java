/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.util.RadioButtonSelector;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.model.Electron;
import edu.colorado.phet.dischargelamps.model.AtomicStateFactory;
import edu.colorado.phet.dischargelamps.view.DischargeLampEnergyMonitorPanel2;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.view.AtomGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

/**
 * SingleAtomModule
 * <p/>
 * Provides a lamp with a single atom
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
        super( name, clock, numEnergyLevels );
        addAtom( getTube(), numEnergyLevels );

        // Make the area from which the cathode emits electrons very small
        super.getCathode().setLength( 1 );

        // Add module-specific controls
        addControls();
    }

    /**
     * todo: clean this up
     */
    private void addControls() {

        // Put the current slider in a set of controls with the Fire button
        final ModelSlider currentSlider = getCurrentSlider();
        getControlPanel().remove( currentSlider );
        double maxCurrent = 0.01;
        getCurrentSlider().setMaximum( maxCurrent );
        getCurrentSlider().setValue( maxCurrent / 2 );

        JPanel electronProductionControlPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 0, 0,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        ButtonGroup electronProductionBtnGrp = new ButtonGroup();

        // Add a button for firing a single electron. This also tells the energy level panel that if an
        // electron has been produced
        final DischargeLampEnergyMonitorPanel2 elmp = super.getEneregyLevelsMonitorPanel();
        final JButton singleShotBtn = new JButton( "Fire electron" );
        singleShotBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Electron electron = getCathode().produceElectron();
                if( electron != null ) {
                    elmp.addElectron( electron );
                }
            }
        } );
        JRadioButton continuousRB = new JRadioButton( new AbstractAction( "Continuous" ) {
            public void actionPerformed( ActionEvent e ) {
                setContinuousElectronProduction( currentSlider, singleShotBtn );
            }
        } );
        JRadioButton singleShotRB = new JRadioButton( new AbstractAction( "Single" ) {
            public void actionPerformed( ActionEvent e ) {
                setSingleShotElectronProduction( currentSlider, singleShotBtn );
            }
        } );
        electronProductionBtnGrp.add( continuousRB );
        electronProductionBtnGrp.add( singleShotRB );
        singleShotRB.setSelected( true );
        setSingleShotElectronProduction( currentSlider, singleShotBtn );

        JPanel rbPanel = new JPanel();
        rbPanel.add( singleShotRB );
        rbPanel.add( continuousRB );
        electronProductionControlPanel.add( rbPanel, gbc );
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 1;
        gbc.gridx = 0;
        electronProductionControlPanel.add( singleShotBtn, gbc );
        electronProductionControlPanel.add( currentSlider, gbc );

        singleShotRB.setSelected( true );
        currentSlider.setVisible( false );
        getControlPanel().add( electronProductionControlPanel );

        getControlPanel().add( elmp );

        JCheckBox slowMotionCB = new JCheckBox( new AbstractAction( "Run in slow motion" ) {
            public void actionPerformed( ActionEvent e ) {
                JCheckBox cb = (JCheckBox)e.getSource();
                if( cb.isSelected() ) {
                    getClock().setDt( DischargeLampsConfig.DT / 5 );
                }
                else {
                    getClock().setDt( DischargeLampsConfig.DT );
                }
            }
        } );
        getControlPanel().add( slowMotionCB );
    }

    /**
     * Sets the simulation to fire single electrons when the "Fire" button is pressed
     * @param currentSlider
     * @param singleShotBtn
     */
    private void setSingleShotElectronProduction( ModelSlider currentSlider, JButton singleShotBtn ) {
        currentSlider.setVisible( false );
        getCathode().setElectronsPerSecond( 0 );
        singleShotBtn.setVisible( true );
    }

    /**
     * Sets the simulation to fire electrons continuously at a rate determined by the
     * "Electron Production Rate" slider
     * @param currentSlider
     * @param singleShotBtn
     */
    private void setContinuousElectronProduction( ModelSlider currentSlider, JButton singleShotBtn ) {
        currentSlider.setVisible( true );
        getCathode().setElectronsPerSecond( currentSlider.getValue() );
        singleShotBtn.setVisible( false );
    }

    /**
     * Adds some atoms and their graphics
     *
     * @param tube
     * @param numEnergyLevels
     */
    private void addAtom( ResonatingCavity tube, int numEnergyLevels ) {
        Rectangle2D tubeBounds = tube.getBounds();

        AtomicState[] states = new AtomicStateFactory().createAtomicStates( numEnergyLevels );
        atom = new DischargeLampAtom( (LaserModel)getModel(), states );
        atom.setPosition( tubeBounds.getX() + tubeBounds.getWidth() / 2,
                          tubeBounds.getY() + tubeBounds.getHeight() / 2 );
        AtomGraphic atomGraphic = addAtom( atom );
        // The graphic may have been put behind the circuit graphic (it is randomly put in front of or behind
        // the circuit in DischargeLampModule.addAtom(). We need to make sure it is above the circuit graphic
        // so that we can get at it with the mouse
        getApparatusPanel().removeGraphic( atomGraphic );
        getApparatusPanel().addGraphic( atomGraphic, DischargeLampsConfig.CIRCUIT_LAYER + 1 );

        // Make the atom movable with the mouse within the bounds of the tube
        Rectangle2D atomBounds = new Rectangle2D.Double( tubeBounds.getMinX() + atom.getRadius(),
                                                         tubeBounds.getMinY() + atom.getRadius(),
                                                         tubeBounds.getWidth() - atom.getRadius() * 2,
                                                         tubeBounds.getHeight() - atom.getRadius() * 2 );
        atomGraphic.setIsMouseable( true, atomBounds );
        atomGraphic.setCursorHand();

        atom.addPhotonEmittedListener( getSpectrometer() );
        getEneregyLevelsMonitorPanel().reset();
    }

}
