package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.util.ImagePanel;

public class SelectionPanel extends JPanel {

    public JRadioButton wolvesButton;
    public JRadioButton foodButton;
    public JRadioButton noneButton;

    private NaturalSelectionModel model;

    public SelectionPanel( NaturalSelectionModel model ) {
        this.model = model;

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

        JLabel label = new JLabel( NaturalSelectionStrings.ENVIRONMENT );

        wolvesPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        foodPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        noneButton.setAlignmentX( Component.LEFT_ALIGNMENT );

        add( label );
        add( wolvesPanel );
        add( foodPanel );
        add( noneButton );

        ButtonGroup selectionGroup = new ButtonGroup();
        selectionGroup.add( wolvesButton );
        selectionGroup.add( foodButton );
        selectionGroup.add( noneButton );

        selectDefaultSelectionFactor();

        // make sure everything has the correct background color
        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        wolvesButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        wolvesPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        wolvesImage.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        foodButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        foodPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        foodImage.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        noneButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    public void selectDefaultSelectionFactor() {
        int selection = NaturalSelectionDefaults.DEFAULT_CLIMATE;
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
}
