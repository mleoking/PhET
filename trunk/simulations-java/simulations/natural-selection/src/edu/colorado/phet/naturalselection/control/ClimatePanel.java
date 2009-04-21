/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.util.ImagePanel;

/**
 * Panel that allows the user to select between two climates (equator and arctic)
 *
 * @author Jonathan Olson
 */
public class ClimatePanel extends JPanel {

    // radio buttons
    public JRadioButton equatorButton;
    public JRadioButton arcticButton;

    private NaturalSelectionModel model;

    /**
     * Constructor
     *
     * @param model The Natural Selection model
     */
    public ClimatePanel( NaturalSelectionModel model ) {
        this.model = model;

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

        equatorPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        arcticPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        label.setAlignmentX( Component.LEFT_ALIGNMENT );

        add( label );
        add( equatorPanel );
        add( arcticPanel );

        ButtonGroup climateGroup = new ButtonGroup();
        climateGroup.add( equatorButton );
        climateGroup.add( arcticButton );

        selectDefaultClimate();

        // make sure everything has the correct background color
        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        equatorButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        equatorPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        equatorImage.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        arcticButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        arcticPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        arcticImage.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    public void selectDefaultClimate() {
        int climate = NaturalSelectionDefaults.DEFAULT_CLIMATE;
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
}
