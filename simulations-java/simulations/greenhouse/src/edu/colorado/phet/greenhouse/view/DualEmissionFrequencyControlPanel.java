/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This is a control panel that is intended for use in the play area and
 * that allows the setting of 2 different emission frequencies: IR and
 * visible.
 *
 * @author John Blanco
 */
public class DualEmissionFrequencyControlPanel extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final Font LABEL_FONT = new PhetFont( 24 );

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final PhotonAbsorptionModel model;
    private final JRadioButton infraredPhotonRadioButton;
    private final JRadioButton visiblePhotonRadioButton;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public DualEmissionFrequencyControlPanel( final PhotonAbsorptionModel model ) {

        this.model = model;

        model.addListener( new PhotonAbsorptionModel.Adapter() {
            @Override
            public void emittedPhotonWavelengthChanged() {
                updateFrequencySelectButtons();
            }
        } );

        // Create and add the buttons.  Each one includes an image.
        JPanel infraredButtonPanel = new JPanel();
        infraredPhotonRadioButton = new JRadioButton( GreenhouseResources.getString( "PhotonEmitterNode.Infrared" ) );
        infraredPhotonRadioButton.setFont( LABEL_FONT );
        infraredPhotonRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setEmittedPhotonWavelength( GreenhouseConfig.irWavelength );
            }
        } );
        infraredButtonPanel.add( infraredPhotonRadioButton );
        infraredButtonPanel.add( new JLabel( new ImageIcon( GreenhouseResources.getImage( "photon-660.png" ) ) ) );

        JPanel visibleButtonPanel = new JPanel();
        visiblePhotonRadioButton = new JRadioButton( GreenhouseResources.getString( "PhotonEmitterNode.Visible" ) );
        visiblePhotonRadioButton.setFont( LABEL_FONT );
        visiblePhotonRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setEmittedPhotonWavelength( GreenhouseConfig.visibleWaveLength );
            }
        } );
        visibleButtonPanel.add( visiblePhotonRadioButton );
        visibleButtonPanel.add( new JLabel( new ImageIcon( GreenhouseResources.getImage( "thin2.png" ) ) ) );

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( infraredPhotonRadioButton );
        buttonGroup.add( visiblePhotonRadioButton );

        // Put the buttons on to a vertical panel.
        JPanel emissionTypeSelectionPanel = new JPanel();
        emissionTypeSelectionPanel.setBorder( BorderFactory.createRaisedBevelBorder() );
        emissionTypeSelectionPanel.setLayout( new GridLayout( 2, 1 ) );
        emissionTypeSelectionPanel.add( infraredButtonPanel );
        emissionTypeSelectionPanel.add( visibleButtonPanel );

        // Wrap the button panel in a PSwing.
        PSwing emissionTypeSelectionPanelNode = new PSwing( emissionTypeSelectionPanel );

        // Add the panel node.
        addChild( emissionTypeSelectionPanelNode );

        // Get the buttons in the correct initial state.
        updateFrequencySelectButtons();
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    private void updateFrequencySelectButtons() {
        if ( model.getEmittedPhotonWavelength() == GreenhouseConfig.irWavelength ) {
            if ( !infraredPhotonRadioButton.isSelected() ) {
                infraredPhotonRadioButton.setSelected( true );
            }
        }
        else {
            if ( !visiblePhotonRadioButton.isSelected() ) {
                visiblePhotonRadioButton.setSelected( true );
            }
        }
    }
}
