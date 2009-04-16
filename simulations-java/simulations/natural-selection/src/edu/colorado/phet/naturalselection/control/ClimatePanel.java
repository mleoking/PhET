package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.util.ImagePanel;

public class ClimatePanel extends JPanel {
    private JRadioButton equatorButton;
    private JRadioButton arcticButton;

    public ClimatePanel() {

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        JPanel equatorPanel = new JPanel();
        equatorPanel.setLayout( new BoxLayout( equatorPanel, BoxLayout.X_AXIS ) );

        equatorButton = new JRadioButton( "Equator" );
        equatorPanel.add( equatorButton );

        equatorPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );

        ImagePanel equatorImage = new ImagePanel( "equator-environment.png" );
        equatorPanel.add( equatorImage );

        JPanel arcticPanel = new JPanel();
        arcticPanel.setLayout( new BoxLayout( arcticPanel, BoxLayout.X_AXIS ) );

        arcticButton = new JRadioButton( "Arctic" );
        arcticPanel.add( arcticButton );

        arcticPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );

        ImagePanel arcticImage = new ImagePanel( "arctic-environment.png" );
        arcticPanel.add( arcticImage );

        JLabel label = new JLabel( "Environment" );

        equatorPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        arcticPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        label.setAlignmentX( Component.LEFT_ALIGNMENT );

        add( label );
        add( equatorPanel );
        add( arcticPanel );

        ButtonGroup climateGroup = new ButtonGroup();
        climateGroup.add( equatorButton );
        climateGroup.add( arcticButton );

        equatorButton.setSelected( true );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        equatorButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        equatorPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        equatorImage.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        arcticButton.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        arcticPanel.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        arcticImage.setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
    }

    public void reset() {
        equatorButton.setSelected( true );
    }
}
