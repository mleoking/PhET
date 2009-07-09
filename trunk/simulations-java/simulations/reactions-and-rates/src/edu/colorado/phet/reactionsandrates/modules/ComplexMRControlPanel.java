/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.modules;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.MRModel;
import edu.colorado.phet.reactionsandrates.util.ControlBorderFactory;
import edu.colorado.phet.reactionsandrates.view.InitialTemperaturePanel;
import edu.colorado.phet.reactionsandrates.view.MoleculeInstanceControlPanel;
import edu.colorado.phet.reactionsandrates.view.ReactionChooserComboBox;
import edu.colorado.phet.reactionsandrates.view.charts.ChartOptionsPanel;

/**
 * ComplexMRControlPanel
 * <p/>
 * The control panel for the ComplexModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ComplexMRControlPanel extends MRControlPanel {
    private MoleculeInstanceControlPanel moleculeInstanceControlPanel;
    private ChartOptionsPanel optionsPanel;
    private static InitialTemperaturePanel initialTemperaturePanel;

    /**
     * @param module
     */
    public ComplexMRControlPanel( final ComplexModule module ) {
        super( new GridBagLayout() );

        final MRModel model = (MRModel)module.getModel();
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 5, 0, 0, 0 ), 0, 0 );

        // Reaction selection controls
        JPanel reactionSelectionPanel = createReactionSelectionPanel( module );

        // Controls for adding and removing molecules
        moleculeInstanceControlPanel = new MoleculeInstanceControlPanel( model );

        // Options
        optionsPanel = new ChartOptionsPanel( module );

        // Reset button
        ResetAllButton resetAllBtn = new ResetAllButton( this );
        resetAllBtn.addResettable( new Resettable() {
            public void reset() {
                module.reset();
            }
        });

        // Lay out the controls
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add( reactionSelectionPanel, gbc );
        gbc.anchor = GridBagConstraints.CENTER;
        add( moleculeInstanceControlPanel, gbc );
        add( optionsPanel, gbc );

        gbc.fill = GridBagConstraints.NONE;
        add( resetAllBtn, gbc );
    }

    private static JPanel createReactionSelectionPanel( ComplexModule module ) {
        // TODO: There's a lot in common with ExperimentSetupPanel; factor out common class
        JPanel reactionSelectionPanel = new JPanel( new GridBagLayout() );

        reactionSelectionPanel.setBorder( ControlBorderFactory.createPrimaryBorder( MRConfig.RESOURCES.getLocalizedString( "Control.initialConditions" ) ) );

        GridBagConstraints c = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                       1, 1, 1, 1,
                                                       GridBagConstraints.WEST,
                                                       GridBagConstraints.NONE,
                                                       new Insets( 2, 3, 3, 3 ),
                                                       0, 0 );

        c.gridx = 0;
        c.gridwidth = 4;
        c.anchor = GridBagConstraints.WEST;

        reactionSelectionPanel.add( new JLabel( MRConfig.RESOURCES.getLocalizedString( "Control.selectReaction" ) ), c );

        c.anchor = GridBagConstraints.CENTER;

        reactionSelectionPanel.add( new ReactionChooserComboBox( module ), c );

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = GridBagConstraints.REMAINDER;

        initialTemperaturePanel = new InitialTemperaturePanel( module );

        reactionSelectionPanel.add( initialTemperaturePanel, c );

        return reactionSelectionPanel;
    }

    public MoleculeInstanceControlPanel getMoleculeInstanceControlPanel() {
        return moleculeInstanceControlPanel;
    }

    public void reset() {
        initialTemperaturePanel.reset();
        optionsPanel.reset();
    }

    public boolean isTemperatureBeingAdjusted() {
        boolean adjusting = super.isTemperatureBeingAdjusted();

        if( !adjusting ) {
            adjusting = initialTemperaturePanel.isTemperatureBeingAdjusted();
        }

        return adjusting;
    }
}
