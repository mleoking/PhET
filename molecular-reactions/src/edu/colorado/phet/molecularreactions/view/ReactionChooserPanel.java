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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.controller.SelectReactionAction;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeAB;
import edu.colorado.phet.molecularreactions.model.MoleculeBC;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;

/**
 * ReactionChooser
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReactionChooserPanel extends JPanel {

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

        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "ExperimentSetup.reactionSelector" ) ) );

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
        defaultRB.addActionListener( selectionListener  );
        r1RB.addActionListener( selectionListener  );
        r2RB.addActionListener( selectionListener );
        r3RB.addActionListener( selectionListener  );
        designYourOwnRB.addActionListener( selectionListener  );

        JLabel iconDefault = new JLabel( new MoleculeIcon( MoleculeA.class ) );
        iconDefault.addMouseListener( new MoleculeIconMouseAdapter( defaultRB ) );
        JLabel iconA = new JLabel( new MoleculeIcon( MoleculeA.class ) );
        iconA.addMouseListener( new MoleculeIconMouseAdapter( r1RB ) );
        JLabel iconBC = new JLabel( new MoleculeIcon( MoleculeBC.class ) );
        iconBC.addMouseListener( new MoleculeIconMouseAdapter( r2RB ) );
        JLabel iconAB = new JLabel( new MoleculeIcon( MoleculeAB.class ) );
        iconAB.addMouseListener( new MoleculeIconMouseAdapter( r3RB ) );
        JLabel designYourOwnLbl = new JLabel( SimStrings.get( "ExperimentSetup.designYourOwn" ) );
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
}
