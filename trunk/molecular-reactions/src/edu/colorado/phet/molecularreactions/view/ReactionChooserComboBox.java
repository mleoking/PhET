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

import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.controller.SelectReactionAction;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeBC;
import edu.colorado.phet.molecularreactions.model.MoleculeAB;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ReactionChooser
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReactionChooserComboBox extends JComboBox {

    private AbstractAction selectionAction;
    private JRadioButton r1RB;
    private JRadioButton r2RB;
    private JRadioButton r3RB;
    private JRadioButton designYourOwnRB;

    private MoleculeIcon r1Item;
    private MoleculeIcon r2Item;
    private MoleculeIcon r3Item;


    public ReactionChooserComboBox( MRModule module ) {

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

        r1Item = new MoleculeIcon( MoleculeA.class );
        r2Item = new MoleculeIcon( MoleculeBC.class );
        r3Item = new MoleculeIcon( MoleculeAB.class );
        addItem( r1Item );
        addItem( r2Item );
        addItem( r3Item );
//        addItem( SimStrings.get( "ExperimentSetup.designYourOwn" ) );

        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Object selection = ReactionChooserComboBox.this.getSelectedItem();
                if( selection == r1Item ) {
                    r1RB.setSelected( true );
                }
                if( selection == r2Item ) {
                    r2RB.setSelected( true );
                }
                if( selection == r3Item ) {
                    r3RB.setSelected( true );
                }
                selectionAction.actionPerformed( new ActionEvent( e.getSource(), e.getID(), "" ) );
            }
        } );

        r1RB.setSelected( true );
    }


    public void reset() {
        r1RB.setSelected( true );
    }
}
