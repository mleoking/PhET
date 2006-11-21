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
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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
    private JRadioButton defaultRB;
    private JRadioButton r1RB;
    private JRadioButton r2RB;
    private JRadioButton r3RB;
    private JRadioButton designYourOwnRB;

    /**
     * 
     * @param module
     */
    public ReactionChooserComboBox( MRModule module ) {

        setRenderer( new DefaultListCellRenderer() );
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

        selectionAction = new SelectReactionAction( module );
        defaultRB.addActionListener( selectionAction  );
        r1RB.addActionListener( selectionAction  );
        r2RB.addActionListener( selectionAction );
        r3RB.addActionListener( selectionAction  );
        designYourOwnRB.addActionListener( selectionAction  );

        EnergyProfile profile = module.getMRModel().getEnergyProfile();
        
        ImageIcon ii = new MoleculeIcon( MoleculeA.class, profile );
//        final ImageIcon r1Item = new MoleculeIcon( MoleculeA.class );

        final ImageIcon defaultItem = new MoleculeIcon( MoleculeA.class, profile );
        BufferedImage bi = (BufferedImage)ii.getImage();
        MakeDuotoneImageOp imgOp = new MakeDuotoneImageOp( new Color( 0, 90, 0 ));
        bi = imgOp.filter( bi, null );
        final ImageIcon r1Item = new ImageIcon( bi );


        final ImageIcon r2Item = new MoleculeIcon( MoleculeBC.class, profile );
        final ImageIcon r3Item = new MoleculeIcon( MoleculeAB.class, profile );
        final String designYourOwnItem = SimStrings.get( "ExperimentSetup.designYourOwn" );

        addItem( defaultItem );
        addItem( r1Item );
        addItem( r2Item );
        addItem( r3Item );
        addItem( designYourOwnItem );

        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Object selection = ReactionChooserComboBox.this.getSelectedItem();
                String command = "";
                if( selection == defaultItem ) {
                    command = SelectReactionAction.DEFAULT_ACTION;
                }
                if( selection == r1Item ) {
                    command = SelectReactionAction.R1_ACTION;
                }
                if( selection == r2Item ) {
                    command = SelectReactionAction.R2_ACTION;
                }
                if( selection == r3Item ) {
                    command = SelectReactionAction.R3_ACTION;

                }
                if( selection == designYourOwnItem ) {
                    command = SelectReactionAction.DESIGN_YOUR_OWN_ACTION;
                }
                selectionAction.actionPerformed( new ActionEvent( e.getSource(), e.getID(), command ) );
            }
        } );

        defaultRB.setSelected( true );
    }

    public void reset() {
        r1RB.setSelected( true );
    }
}
