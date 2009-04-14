package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.util.ImagePanel;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;

public class ClimatePanel extends JPanel {
    public ClimatePanel() {

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        JPanel equatorPanel = new JPanel();
        equatorPanel.setLayout( new BoxLayout( equatorPanel, BoxLayout.X_AXIS ) );

        JRadioButton equatorButton = new JRadioButton( "Equator" );
        equatorPanel.add( equatorButton );

        equatorPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );

        ImagePanel equatorImage = new ImagePanel( "equator-environment.png" );
        equatorImage.setPreferredSize( new Dimension( 32, 32 ) );
        equatorPanel.add( equatorImage );

        JPanel arcticPanel = new JPanel();
        arcticPanel.setLayout( new BoxLayout( arcticPanel, BoxLayout.X_AXIS ) );

        JRadioButton arcticButton = new JRadioButton( "Arctic" );
        arcticPanel.add( arcticButton );

        arcticPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );

        ImagePanel arcticImage = new ImagePanel( "arctic-environment.png" );
        arcticImage.setPreferredSize( new Dimension( 32, 32 ) );
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

        setBackground( NaturalSelectionConstants.COLOR_TRANSPARENT );
        equatorButton.setBackground( NaturalSelectionConstants.COLOR_TRANSPARENT );
        equatorPanel.setBackground( NaturalSelectionConstants.COLOR_TRANSPARENT );
        equatorImage.setBackground( NaturalSelectionConstants.COLOR_TRANSPARENT );
        arcticButton.setBackground( NaturalSelectionConstants.COLOR_TRANSPARENT );
        arcticPanel.setBackground( NaturalSelectionConstants.COLOR_TRANSPARENT );
        arcticImage.setBackground( NaturalSelectionConstants.COLOR_TRANSPARENT );
    }
}
