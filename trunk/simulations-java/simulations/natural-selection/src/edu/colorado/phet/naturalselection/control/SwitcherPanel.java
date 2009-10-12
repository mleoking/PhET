package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

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
        super( new GridLayout( 2, 1 ) );

        statisticsRadioButton = new JRadioButton( NaturalSelectionStrings.STATS_POPULATION );
        pedigreeRadioButton = new JRadioButton( NaturalSelectionStrings.PEDIGREE_CHART );

        statisticsRadioButton.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );
        pedigreeRadioButton.setBackground( NaturalSelectionConstants.COLOR_ACCESSIBLE_CONTROL_PANEL );

        ButtonGroup group = new ButtonGroup();
        group.add( statisticsRadioButton );
        group.add( pedigreeRadioButton );

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
