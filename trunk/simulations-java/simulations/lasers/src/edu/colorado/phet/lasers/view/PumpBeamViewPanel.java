/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.lasers.LasersResources;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;

/**
 * PumpBeamViewPanel
 * <p/>
 * Provides controls that set the pumping beam to be showns as either photons or a colored area
 * ("curtain view").
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PumpBeamViewPanel extends JPanel {
    private ButtonGroup pumpPhotonBG;
    private JRadioButton pumpPhotonViewRB;
    private JRadioButton pumpCurtainViewRB;
    private BaseLaserModule module;

    /**
     * @param module
     */
    public PumpBeamViewPanel( BaseLaserModule module ) {
        super( new GridBagLayout() );
        this.module = module;
        JPanel pumpViewPanel = this;
        Border pumpPanelBorder = BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                                                                   LasersResources.getString( "WaveViewControlPanel.pumpPanelTitle" ) );
        pumpViewPanel.setBorder( pumpPanelBorder );
        pumpPhotonBG = new ButtonGroup();
        pumpPhotonViewRB = new JRadioButton( LasersResources.getString( "WaveViewControlPanel.photonView" ) );
        pumpCurtainViewRB = new JRadioButton( LasersResources.getString( "WaveViewControlPanel.curtainView" ) );
        pumpPhotonBG.add( pumpPhotonViewRB );
        pumpPhotonBG.add( pumpCurtainViewRB );

        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        pumpViewPanel.add( pumpPhotonViewRB, gbc );
        pumpViewPanel.add( pumpCurtainViewRB, gbc );
        pumpPhotonViewRB.addActionListener( new PumpPhotonRBListener() );
        pumpCurtainViewRB.addActionListener( new PumpPhotonRBListener() );

        // Set initial condition
        pumpCurtainViewRB.setSelected( true );

    }

    public void setUpperTransitionView( int viewType ) {
        pumpPhotonViewRB.setSelected( viewType == BaseLaserModule.PHOTON_DISCRETE );
        pumpCurtainViewRB.setSelected( viewType == BaseLaserModule.PHOTON_CURTAIN );
    }

    //----------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------

    private class PumpPhotonRBListener implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            JRadioButton selection = SwingUtils.getSelection( pumpPhotonBG );
            if ( selection == pumpPhotonViewRB ) {
                PhotonGraphic.setAllVisible( true, module.getPumpingBeam().getWavelength() );
                module.setPumpingPhotonView( BaseLaserModule.PHOTON_DISCRETE );
            }
            if ( selection == pumpCurtainViewRB ) {
                PhotonGraphic.setAllVisible( false, module.getPumpingBeam().getWavelength() );
                module.setPumpingPhotonView( BaseLaserModule.PHOTON_CURTAIN );
            }
        }
    }

}
