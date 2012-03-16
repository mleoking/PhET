// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.persistence.NaturalSelectionConfig;
import edu.colorado.phet.naturalselection.util.ImagePanel;

/**
 * Panel that allows the user to select between two climates (equator and arctic)
 *
 * @author Jonathan Olson
 */
public class ClimatePanel extends JPanel {

    // radio buttons
    private JRadioButton equatorButton;
    private JRadioButton arcticButton;

    public ClimatePanel() {
        // ugly layout code to follow

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        JPanel equatorPanel = new JPanel();
        equatorPanel.setLayout( new BoxLayout( equatorPanel, BoxLayout.X_AXIS ) );

        equatorButton = new JRadioButton( NaturalSelectionStrings.EQUATOR );
        equatorPanel.add( equatorButton );

        equatorPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );

        ImagePanel equatorImage = new ImagePanel( NaturalSelectionConstants.IMAGE_EQUATOR_ENVIRONMENT );
        equatorPanel.add( equatorImage );

        JPanel arcticPanel = new JPanel();
        arcticPanel.setLayout( new BoxLayout( arcticPanel, BoxLayout.X_AXIS ) );

        arcticButton = new JRadioButton( NaturalSelectionStrings.ARCTIC );
        arcticPanel.add( arcticButton );

        arcticPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );

        ImagePanel arcticImage = new ImagePanel( NaturalSelectionConstants.IMAGE_ARCTIC_ENVIRONMENT );
        arcticPanel.add( arcticImage );

        JLabel label = new JLabel( NaturalSelectionStrings.ENVIRONMENT );
        label.setFont( new PhetFont( 12, true ) );

        equatorPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        arcticPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        label.setAlignmentX( Component.LEFT_ALIGNMENT );

        add( label );
        add( Box.createRigidArea( new Dimension( 5, 2 ) ) );
        add( equatorPanel );
        add( arcticPanel );

        ButtonGroup climateGroup = new ButtonGroup();
        climateGroup.add( equatorButton );
        climateGroup.add( arcticButton );

        selectDefaultClimate();

        // make sure everything has the correct background color
        setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        equatorButton.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        equatorPanel.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        equatorImage.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        arcticButton.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        arcticPanel.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        arcticImage.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
    }

    public void selectDefaultClimate() {
        int climate = NaturalSelectionDefaults.DEFAULT_CLIMATE;
        setClimate( climate );
    }

    private void setClimate( int climate ) {
        if ( climate == NaturalSelectionModel.CLIMATE_EQUATOR ) {
            equatorButton.setSelected( true );
        }
        else if ( climate == NaturalSelectionModel.CLIMATE_ARCTIC ) {
            arcticButton.setSelected( true );
        }
    }

    public void reset() {
        selectDefaultClimate();
    }

    public void load( NaturalSelectionConfig config ) {
        setClimate( config.getClimate() );
    }

    public JRadioButton getEquatorButton() {
        return equatorButton;
    }

    public JRadioButton getArcticButton() {
        return arcticButton;
    }
}
