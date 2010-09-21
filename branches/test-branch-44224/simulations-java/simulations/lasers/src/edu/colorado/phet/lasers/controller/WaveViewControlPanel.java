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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.lasers.LasersResources;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.view.PhotonGraphic;
import edu.colorado.phet.lasers.view.util.ViewUtils;

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

    public WaveViewControlPanel( BaseLaserModule module ) {
        this.module = module;
        setLayout( new GridBagLayout() );

        // Controls to set view of lasing photons
        JPanel lasingViewPanel = new JPanel( new GridBagLayout() );
        ViewUtils.setBorder( lasingViewPanel, LasersResources.getString( "WaveViewControlPanel.lasingPanelTitle" ) );
        lasingPhotonBG = new ButtonGroup();
        lasingPhotonViewRB = new JRadioButton( LasersResources.getString( "WaveViewControlPanel.photonView" ) );
        lasingWaveViewRB = new JRadioButton( LasersResources.getString( "WaveViewControlPanel.waveView" ) );
        lasingPhotonBG.add( lasingPhotonViewRB );
        lasingPhotonBG.add( lasingWaveViewRB );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        lasingViewPanel.add( lasingPhotonViewRB, gbc );
        gbc.gridy++;
        lasingViewPanel.add( lasingWaveViewRB, gbc );
        lasingPhotonViewRB.addActionListener( new LasingPhotonRBListener() );
        lasingWaveViewRB.addActionListener( new LasingPhotonRBListener() );

        // Controls to set view of pump beam
        gbc.gridy = 0;
        gbc.gridx++;
        this.add( lasingViewPanel, gbc );

        // Set initial conditions
        module.setPumpingPhotonView( BaseLaserModule.PHOTON_CURTAIN );
        lasingPhotonViewRB.setSelected( true );
        module.setLasingPhotonView( BaseLaserModule.PHOTON_DISCRETE );
    }

    public void setUpperTransitionView( int viewType ) {
        module.setPumpingPhotonView( viewType );
        if ( viewType == BaseLaserModule.PHOTON_DISCRETE ) {
            PhotonGraphic.setAllVisible( true, module.getPumpingBeam().getWavelength() );
        }
        if ( viewType == BaseLaserModule.PHOTON_CURTAIN ) {
            PhotonGraphic.setAllVisible( false, module.getPumpingBeam().getWavelength() );
        }
    }

    private class LasingPhotonRBListener implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            JRadioButton selection = SwingUtils.getSelection( lasingPhotonBG );
            double de = module.getLaserModel().getMiddleEnergyState().getEnergyLevel()
                        - module.getLaserModel().getGroundState().getEnergyLevel();
            if ( selection == lasingPhotonViewRB ) {
                PhotonGraphic.setAllVisible( true, PhysicsUtil.energyToWavelength( de ) );
                module.setLasingPhotonView( BaseLaserModule.PHOTON_DISCRETE );
            }
            if ( selection == lasingWaveViewRB ) {
                PhotonGraphic.setAllVisible( false, PhysicsUtil.energyToWavelength( de ) );
                module.setLasingPhotonView( BaseLaserModule.PHOTON_WAVE );
            }
        }
    }
}
