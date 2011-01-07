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
 * Panel that allows the user to change the selection factor.
 */
public class SelectionPanel extends JPanel {

    private JRadioButton wolvesButton;
    private JRadioButton foodButton;
    private JRadioButton noneButton;

    public SelectionPanel() {

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        JPanel wolvesPanel = new JPanel();
        wolvesPanel.setLayout( new BoxLayout( wolvesPanel, BoxLayout.X_AXIS ) );

        wolvesButton = new JRadioButton( NaturalSelectionStrings.WOLVES );
        wolvesPanel.add( wolvesButton );

        wolvesPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );

        ImagePanel wolvesImage = new ImagePanel( NaturalSelectionConstants.IMAGE_SELECTION_WOLVES );
        wolvesPanel.add( wolvesImage );

        JPanel foodPanel = new JPanel();
        foodPanel.setLayout( new BoxLayout( foodPanel, BoxLayout.X_AXIS ) );

        foodButton = new JRadioButton( NaturalSelectionStrings.FOOD );
        foodPanel.add( foodButton );

        foodPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );

        ImagePanel foodImage = new ImagePanel( NaturalSelectionConstants.IMAGE_SELECTION_FOOD );
        foodPanel.add( foodImage );

        noneButton = new JRadioButton( NaturalSelectionStrings.NONE );

        JLabel label = new JLabel( NaturalSelectionStrings.SELECTION_FACTOR );
        label.setFont( new PhetFont( 12, true ) );

        wolvesPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        foodPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        noneButton.setAlignmentX( Component.LEFT_ALIGNMENT );

        add( label );
        add( Box.createRigidArea( new Dimension( 5, 2 ) ) );
        add( wolvesPanel );
        add( foodPanel );
        add( noneButton );

        ButtonGroup selectionGroup = new ButtonGroup();
        selectionGroup.add( wolvesButton );
        selectionGroup.add( foodButton );
        selectionGroup.add( noneButton );

        selectDefaultSelectionFactor();

        // make sure everything has the correct background color
        setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        wolvesButton.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        wolvesPanel.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        wolvesImage.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        foodButton.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        foodPanel.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        foodImage.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        noneButton.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
    }

    public void selectDefaultSelectionFactor() {
        int selection = NaturalSelectionDefaults.DEFAULT_CLIMATE;
        setSelectionFactor( selection );
    }

    private void setSelectionFactor( int selection ) {
        if ( selection == NaturalSelectionModel.SELECTION_WOLVES ) {
            wolvesButton.setSelected( true );
        }
        else if ( selection == NaturalSelectionModel.SELECTION_FOOD ) {
            foodButton.setSelected( true );
        }
        else if ( selection == NaturalSelectionModel.SELECTION_NONE ) {
            noneButton.setSelected( true );
        }
    }

    public void reset() {
        selectDefaultSelectionFactor();
    }

    public void load( NaturalSelectionConfig config ) {
        setSelectionFactor( config.getSelectionFactor() );
    }

    public JRadioButton getWolvesButton() {
        return wolvesButton;
    }

    public JRadioButton getFoodButton() {
        return foodButton;
    }

    public JRadioButton getNoneButton() {
        return noneButton;
    }
}
