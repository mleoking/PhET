/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.view.PhotonGraphic;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class: WaveViewControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Nov 22, 2004
 */
public class WaveViewControlPanel extends JPanel {
    private BaseLaserModule module;
    private ButtonGroup lasingPhotonBG;
    private JRadioButton lasingPhotonViewRB;
    private JRadioButton lasingWaveViewRB;
    private ButtonGroup pumpPhotonBG;
    private JRadioButton pumpPhotonViewRB;
    private JRadioButton pumpCurtainViewRB;

    public WaveViewControlPanel( BaseLaserModule module ) {
        this.module = module;
        setLayout( new GridBagLayout() );

        // Controls to set view of lasing photons
        JPanel lasingViewPanel = new JPanel( new GridBagLayout() );
        Border lasingPanelBorder = BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                                                                     SimStrings.get( "WaveViewControlPanel.lasingPanelTitle" ) );
        lasingViewPanel.setBorder( lasingPanelBorder );
        lasingPhotonBG = new ButtonGroup();
        lasingPhotonViewRB = new JRadioButton( SimStrings.get( "WaveViewControlPanel.photonView" ) );
        lasingWaveViewRB = new JRadioButton( SimStrings.get( "WaveViewControlPanel.waveView" ) );
        lasingPhotonBG.add( lasingPhotonViewRB );
        lasingPhotonBG.add( lasingWaveViewRB );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 5, 5, 5, 5 ), 0, 0 );
        lasingViewPanel.add( lasingPhotonViewRB, gbc );
        gbc.gridy++;
        lasingViewPanel.add( lasingWaveViewRB, gbc );
        lasingPhotonViewRB.addActionListener( new LasingPhotonRBListener() );
        lasingWaveViewRB.addActionListener( new LasingPhotonRBListener() );

        // Controls to set view of pump beam
        JPanel pumpViewPanel = new JPanel( new GridBagLayout() );
        Border pumpPanelBorder = BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                                                                   SimStrings.get( "WaveViewControlPanel.pumpPanelTitle" ) );
        pumpViewPanel.setBorder( pumpPanelBorder );
        pumpPhotonBG = new ButtonGroup();
        pumpPhotonViewRB = new JRadioButton( SimStrings.get( "WaveViewControlPanel.photonView" ) );
        pumpCurtainViewRB = new JRadioButton( SimStrings.get( "WaveViewControlPanel.curtainView" ) );
        pumpPhotonBG.add( pumpPhotonViewRB );
        pumpPhotonBG.add( pumpCurtainViewRB );
        gbc.gridy = 0;
        pumpViewPanel.add( pumpPhotonViewRB, gbc );
        gbc.gridy++;
        pumpViewPanel.add( pumpCurtainViewRB, gbc );
        pumpPhotonViewRB.addActionListener( new PumpPhotonRBListener() );
        pumpCurtainViewRB.addActionListener( new PumpPhotonRBListener() );

        gbc.gridy = 0;
        this.add( lasingViewPanel, gbc );
        gbc.gridy++;
        this.add( pumpViewPanel, gbc );

        // Set the initial conditions
        pumpCurtainViewRB.setSelected( true );
        lasingPhotonViewRB.setSelected( true );
    }

    private class LasingPhotonRBListener implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            JRadioButton selection = GraphicsUtil.getSelection( lasingPhotonBG );
            if( selection == lasingPhotonViewRB ) {
                module.setLasingPhotonView( BaseLaserModule.PHOTON_DISCRETE );
                //                module.setPhotonView();
            }
            if( selection == lasingWaveViewRB ) {
                PhotonGraphic.removeAll( module.getApparatusPanel() );
                module.setLasingPhotonView( BaseLaserModule.PHOTON_WAVE );
                //                module.setWaveView();
            }
        }
    }

    private class PumpPhotonRBListener implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            JRadioButton selection = GraphicsUtil.getSelection( pumpPhotonBG );
            if( selection == pumpPhotonViewRB ) {
                module.setPumpingPhotonView( BaseLaserModule.PHOTON_DISCRETE );
                //                module.setPhotonView();
            }
            if( selection == pumpCurtainViewRB ) {
                PhotonGraphic.removeAll( module.getApparatusPanel() );
                module.setPumpingPhotonView( BaseLaserModule.PHOTON_CURTAIN );
                //                module.setWaveView();
            }
        }
    }
}
