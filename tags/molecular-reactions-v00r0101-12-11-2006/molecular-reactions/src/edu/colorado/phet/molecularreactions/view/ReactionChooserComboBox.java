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
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.reactions.Profiles;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.view.icons.ReactionSelectorIcons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ReactionChooserComboBox
 * <p>
 * A JComboBox whose items are selectors for the reaction to be used
 * by the model
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReactionChooserComboBox extends JComboBox implements MRModel.ModelListener {

    private AbstractAction selectionAction;
    private JRadioButton defaultRB;
    private JRadioButton r1RB;
    private JRadioButton r2RB;
    private JRadioButton r3RB;
    private JRadioButton designYourOwnRB;
    private ImageIcon defaultItem;
    private ImageIcon r1Item;
    private ImageIcon r2Item;
    private ImageIcon r3Item;
    private String designYourOwnItem;

    /**
     * 
     * @param module
     */
    public ReactionChooserComboBox( MRModule module ) {

        module.getMRModel().addListener( this );
        
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

        defaultItem = ReactionSelectorIcons.getIcon( Profiles.DEFAULT );
        r1Item = ReactionSelectorIcons.getIcon( Profiles.R1 );
        r2Item = ReactionSelectorIcons.getIcon( Profiles.R2 );
        r3Item = ReactionSelectorIcons.getIcon( Profiles.R3 );
        designYourOwnItem = SimStrings.get( "ExperimentSetup.designYourOwn" );

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

    //--------------------------------------------------------------------------------------------------
    // Implementation of MRModel.ModelListener
    //--------------------------------------------------------------------------------------------------

    public void energyProfileChanged( EnergyProfile profile ) {
        Object selectedItem = null;
        selectedItem = ( profile == Profiles.DEFAULT ) ? defaultItem : selectedItem;
        selectedItem = ( profile == Profiles.R1 ) ? r1Item : selectedItem;
        selectedItem = ( profile == Profiles.R2 ) ? r2Item : selectedItem;
        selectedItem = ( profile == Profiles.R3 ) ? r3Item : selectedItem;
        selectedItem = ( profile == Profiles.DYO ) ? designYourOwnItem : selectedItem;
        setSelectedItem( selectedItem );
    }
}
