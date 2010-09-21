package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.model.Bunny;

/**
 * Panel with two radio buttons that switches between the bunny statistics and the pedigree chart.
 *
 * @author Jonathan Olson
 */
public class SwitcherPanel extends JPanel implements DetachOptionPanel.Listener {
    private JRadioButton statisticsRadioButton;
    private JRadioButton pedigreeRadioButton;

    public SwitcherPanel() {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        statisticsRadioButton = new JRadioButton( NaturalSelectionStrings.STATS_POPULATION );
        pedigreeRadioButton = new JRadioButton( NaturalSelectionStrings.PEDIGREE_CHART );

        statisticsRadioButton.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        pedigreeRadioButton.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );

        ButtonGroup group = new ButtonGroup();
        group.add( statisticsRadioButton );
        group.add( pedigreeRadioButton );

        JLabel label = new JLabel( NaturalSelectionStrings.CHART );
        label.setFont( new PhetFont( 12, true ) );

        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        statisticsRadioButton.setAlignmentX( Component.LEFT_ALIGNMENT );
        pedigreeRadioButton.setAlignmentX( Component.LEFT_ALIGNMENT );

        add( label );
        add( Box.createRigidArea( new Dimension( 5, 2 ) ) );
        add( statisticsRadioButton );
        add( pedigreeRadioButton );

        statisticsRadioButton.setSelected( true );

        setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
    }

    public void onDock() {
        statisticsRadioButton.setEnabled( true );
        pedigreeRadioButton.setEnabled( true );
        pedigreeRadioButton.setSelected( true );
    }

    public void onUndock() {
        statisticsRadioButton.setEnabled( false );
        pedigreeRadioButton.setEnabled( false );
        statisticsRadioButton.setSelected( true );
    }

    public void onClose() {
        statisticsRadioButton.setEnabled( true );
        pedigreeRadioButton.setEnabled( true );
        if ( Bunny.getSelectedBunny() != null ) {
            Bunny.getSelectedBunny().setSelected( false );
        }
    }

    public JRadioButton getStatisticsRadioButton() {
        return statisticsRadioButton;
    }

    public JRadioButton getPedigreeRadioButton() {
        return pedigreeRadioButton;
    }
}
