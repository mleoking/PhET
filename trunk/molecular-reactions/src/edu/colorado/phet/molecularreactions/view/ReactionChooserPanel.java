/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ReactionChooser
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReactionChooserPanel extends JPanel {

    private static EnergyProfile r1Profile = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                100 );

    private static EnergyProfile r2Profile = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .7,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .4,
                                                                100 );

    private static EnergyProfile r3Profile = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .7,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .7,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                100 );
    private MRModule module;

    private static class Reaction {
        EnergyProfile energyProfile;
        public Reaction( EnergyProfile energyProfile ) {
            this.energyProfile = energyProfile;
        }

        public EnergyProfile getEnergyProfile() {
            return energyProfile;
        }
    }

    private static Reaction R1 = new Reaction( r1Profile );
    private static Reaction R2 = new Reaction( r2Profile );
    private static Reaction R3 = new Reaction( r3Profile );


    private JRadioButton r1RB;
    private JRadioButton r2RB;
    private JRadioButton r3RB;
    private Reaction currentReaction;


    public ReactionChooserPanel( MRModule module ) {
        super( new GridBagLayout() );
        this.module = module;

        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "ExperimentSetup.reactionSelector" ) ) );

        ButtonGroup bg = new ButtonGroup();
        r1RB = new JRadioButton();
        r2RB = new JRadioButton();
        r3RB = new JRadioButton();
        bg.add( r1RB );
        bg.add( r2RB );
        bg.add( r3RB );

        r1RB.addActionListener( new ReactionSelectorRBAction() );
        r2RB.addActionListener( new ReactionSelectorRBAction() );
        r3RB.addActionListener( new ReactionSelectorRBAction() );

        JLabel iconA = new JLabel( new MoleculeIcon( MoleculeA.class ) );
        iconA.addMouseListener( new MoleculeIconMouseAdapter( r1RB ) );
        JLabel iconBC = new JLabel( new MoleculeIcon( MoleculeBC.class ) );
        iconBC.addMouseListener( new MoleculeIconMouseAdapter( r2RB ) );
        JLabel iconAB = new JLabel( new MoleculeIcon( MoleculeAB.class ) );
        iconAB.addMouseListener( new MoleculeIconMouseAdapter( r3RB ) );

        setLayout( new GridBagLayout() );
        int rbAnchor = GridBagConstraints.EAST;
        int iconAnchor = GridBagConstraints.WEST;
        Insets insets = new Insets( 3, 3, 3, 3 );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         rbAnchor,
                                                         GridBagConstraints.NONE,
                                                         insets, 0, 0 );
        add( r1RB, gbc );
        add( r2RB, gbc );
        add( r3RB, gbc );
        gbc.gridy = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridx = 1;
        gbc.anchor = iconAnchor;

        add( iconA, gbc );
        add( iconBC, gbc );
        add( iconAB, gbc );

        r1RB.setSelected( true );
        currentReaction = R1;
    }

    private class ReactionSelectorRBAction extends AbstractAction {

        public void actionPerformed( ActionEvent e ) {
            setReaction();
        }
    }

    private void setReaction() {
        if( r1RB.isSelected() ) {
            currentReaction = R1;
        }
        if( r2RB.isSelected() ) {
            currentReaction = R2;
        }
        if( r3RB.isSelected() ) {
            currentReaction = R3;
        }

        module.getMRModel().getReaction().setEnergyProfile( currentReaction.getEnergyProfile() );
    }

    public void reset() {
        r1RB.setSelected( true );
        currentReaction = R1;
    }

    private class MoleculeIconMouseAdapter extends MouseAdapter {
        JToggleButton toggleBtn;

        public MoleculeIconMouseAdapter( JToggleButton toggleBtn ) {
            this.toggleBtn = toggleBtn;
        }

        public void mouseClicked( MouseEvent e ) {
            toggleBtn.setSelected( true );
            setReaction();
        }
    }

}
