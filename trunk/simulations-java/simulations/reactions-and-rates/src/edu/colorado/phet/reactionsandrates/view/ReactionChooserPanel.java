// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view;

import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.controller.SelectReactionAction;
import edu.colorado.phet.reactionsandrates.model.EnergyProfile;
import edu.colorado.phet.reactionsandrates.model.MRModel;
import edu.colorado.phet.reactionsandrates.model.reactions.Profiles;
import edu.colorado.phet.reactionsandrates.modules.MRModule;
import edu.colorado.phet.reactionsandrates.util.ControlBorderFactory;
import edu.colorado.phet.reactionsandrates.view.icons.ReactionSelectorIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ReactionChooserPanel
 * <p/>
 * A JPanel with radio buttons for selecting a reaction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReactionChooserPanel extends JPanel implements MRModel.ModelListener {

    private JRadioButton defaultRB;
    private JRadioButton r1RB;
    private JRadioButton r2RB;
    private JRadioButton r3RB;
    private JRadioButton designYourOwnRB;
    private MRModule module;
    private ActionListener selectionListener;


    public ReactionChooserPanel( MRModule module ) {
        super( new GridBagLayout() );
        this.module = module;
        module.getMRModel().addListener( this );

        setBorder( ControlBorderFactory.createPrimaryBorder( MRConfig.RESOURCES.getLocalizedString( "ExperimentSetup.reactionSelector" ) ) );

        ButtonGroup bg = new ButtonGroup();
        defaultRB = new JRadioButton();
        r1RB = new JRadioButton();
        r2RB = new JRadioButton();
        r3RB = new JRadioButton();
        designYourOwnRB = new JRadioButton();
        bg.add( defaultRB );
        bg.add( r1RB );
        bg.add( r2RB );
        bg.add( r3RB );
        bg.add( designYourOwnRB );

        selectionListener = new SelectionHandler();
        defaultRB.addActionListener( selectionListener );
        r1RB.addActionListener( selectionListener );
        r2RB.addActionListener( selectionListener );
        r3RB.addActionListener( selectionListener );
        designYourOwnRB.addActionListener( selectionListener );

        JLabel iconDefault = new JLabel( ReactionSelectorIcons.getIcon( Profiles.DEFAULT ) );
        iconDefault.addMouseListener( new MoleculeIconMouseAdapter( defaultRB ) );
        JLabel iconA = new JLabel( ReactionSelectorIcons.getIcon( Profiles.R1 ) );
        iconA.addMouseListener( new MoleculeIconMouseAdapter( r1RB ) );
        JLabel iconBC = new JLabel( ReactionSelectorIcons.getIcon( Profiles.R2 ) );
        iconBC.addMouseListener( new MoleculeIconMouseAdapter( r2RB ) );
        JLabel iconAB = new JLabel( ReactionSelectorIcons.getIcon( Profiles.R3 ) );
        iconAB.addMouseListener( new MoleculeIconMouseAdapter( r3RB ) );
        JLabel designYourOwnLbl = new JLabel( MRConfig.RESOURCES.getLocalizedString( "ExperimentSetup.designYourOwn" ) );
        designYourOwnLbl.addMouseListener( new MoleculeIconMouseAdapter( designYourOwnRB ) );

        setLayout( new GridBagLayout() );
        int rbAnchor = GridBagConstraints.EAST;
        int iconAnchor = GridBagConstraints.WEST;
        Insets insets = new Insets( 3, 3, 3, 3 );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         rbAnchor,
                                                         GridBagConstraints.NONE,
                                                         insets, 0, 0 );
        add( defaultRB, gbc );
        add( r1RB, gbc );
        add( r2RB, gbc );
        add( r3RB, gbc );
        add( designYourOwnRB, gbc );

        gbc.gridy = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridx = 1;
        gbc.anchor = iconAnchor;
        add( iconDefault, gbc );
        add( iconA, gbc );
        add( iconBC, gbc );
        add( iconAB, gbc );
        add( designYourOwnLbl, gbc );

        defaultRB.setSelected( true );
    }

    public void reset() {
        r1RB.setSelected( true );
    }

    private class MoleculeIconMouseAdapter extends MouseAdapter {
        JToggleButton toggleBtn;

        public MoleculeIconMouseAdapter( JToggleButton toggleBtn ) {
            this.toggleBtn = toggleBtn;
        }

        public void mouseClicked( MouseEvent e ) {
            toggleBtn.setSelected( true );
            selectionListener.actionPerformed( new ActionEvent( e.getSource(), e.getID(), "" ) );
        }
    }


    private class SelectionHandler implements ActionListener {

        public void actionPerformed( ActionEvent e ) {
            SelectReactionAction action = new SelectReactionAction( module );
            String command = "";
            if( defaultRB.isSelected() ) {
                command = SelectReactionAction.DEFAULT_ACTION;
            }
            if( r1RB.isSelected() ) {
                command = SelectReactionAction.R1_ACTION;
            }
            if( r2RB.isSelected() ) {
                command = SelectReactionAction.R2_ACTION;
            }
            if( r3RB.isSelected() ) {
                command = SelectReactionAction.R3_ACTION;
            }
            if( designYourOwnRB.isSelected() ) {
                command = SelectReactionAction.DESIGN_YOUR_OWN_ACTION;
            }
            action.actionPerformed( new ActionEvent( e.getSource(), e.getID(), command ) );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of MRModel.ModelListener
    //--------------------------------------------------------------------------------------------------

    public void notifyEnergyProfileChanged( EnergyProfile profile ) {
        defaultRB.setSelected( profile == Profiles.DEFAULT );
        r1RB.setSelected( profile == Profiles.R1 );
        r2RB.setSelected( profile == Profiles.R2 );
        r3RB.setSelected( profile == Profiles.R3 );
        designYourOwnRB.setSelected( profile == Profiles.DYO );
    }


    public void notifyDefaultTemperatureChanged( double newInitialTemperature ) {

    }
}
