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
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.flourescent.model.DischargeLampAtom;
import edu.colorado.phet.flourescent.model.Electron;
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

        // Set the current control to an appropriate maximum value
        getCurrentSlider().setMaximum( 0.01 );
    }

    /**
     * todo: clean this up
     */
    private void addControls() {

        // Put the current slider in a set of controls with the Fire button
        final ModelSlider currentSlider = getCurrentSlider();
        getControlPanel().remove( currentSlider );

        JPanel electronProductionControlPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 0, 0,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        ButtonGroup electronProductionBtnGrp = new ButtonGroup();

        // Add a button for firing a single electron. This also tells the energy level panel that if an
        // electron has been produced
        final MultipleAtomModule.DischargeLampEnergyMonitorPanel2 elmp = super.getEneregyLevelsMonitorPanel();
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
                currentSlider.setVisible( true );
                getCathode().setElectronsPerSecond( currentSlider.getValue() );
                singleShotBtn.setVisible( false );
            }
        } );
        JRadioButton singleShotRB = new JRadioButton( new AbstractAction( "Single" ) {
            public void actionPerformed( ActionEvent e ) {
                currentSlider.setVisible( false );
                getCathode().setElectronsPerSecond( 0 );
                singleShotBtn.setVisible( true );
            }
        } );
        electronProductionBtnGrp.add( continuousRB );
        electronProductionBtnGrp.add( singleShotRB );

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
                    getClock().setDt( FluorescentLightsConfig.DT / 5 );
                }
                else {
                    getClock().setDt( FluorescentLightsConfig.DT );
                }
            }
        } );
        getControlPanel().add( slowMotionCB );
    }

    /**
     * Adds some atoms and their graphics
     *
     * @param tube
     * @param numEnergyLevels
     */
    private void addAtom( ResonatingCavity tube, int numEnergyLevels ) {
        Rectangle2D tubeBounds = tube.getBounds();

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

        atom.addPhotonEmittedListener( getSpectrometer() );
        getEneregyLevelsMonitorPanel().reset();
    }
}
