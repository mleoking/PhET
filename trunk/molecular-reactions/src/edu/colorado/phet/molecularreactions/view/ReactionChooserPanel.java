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
import edu.colorado.phet.molecularreactions.controller.SelectReactionAction;
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

    private AbstractAction selectionAction;
    private JRadioButton r1RB;
    private JRadioButton r2RB;
    private JRadioButton r3RB;
    private JRadioButton designYourOwnRB;


    public ReactionChooserPanel( MRModule module ) {
        super( new GridBagLayout() );

        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "ExperimentSetup.reactionSelector" ) ) );

        ButtonGroup bg = new ButtonGroup();
        r1RB = new JRadioButton();
        r2RB = new JRadioButton();
        r3RB = new JRadioButton();
        designYourOwnRB = new JRadioButton();
        bg.add( r1RB );
        bg.add( r2RB );
        bg.add( r3RB );
        bg.add( designYourOwnRB );

        selectionAction = new SelectReactionAction( module, r1RB, r2RB, r3RB, designYourOwnRB );
        r1RB.addActionListener( selectionAction  );
        r2RB.addActionListener( selectionAction );
        r3RB.addActionListener( selectionAction  );
        designYourOwnRB.addActionListener( selectionAction  );

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
        add( r1RB, gbc );
        add( r2RB, gbc );
        add( r3RB, gbc );
        add( designYourOwnRB, gbc );
        gbc.gridy = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridx = 1;
        gbc.anchor = iconAnchor;

        add( iconA, gbc );
        add( iconBC, gbc );
        add( iconAB, gbc );
        add( designYourOwnLbl, gbc );

        r1RB.setSelected( true );
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
            selectionAction.actionPerformed( new ActionEvent( e.getSource(), e.getID(), "" ) );
        }
    }
}
