// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Controls for choosing a real reaction.
 * Gets the collection of real reactions from the model, creates a radio button for each reaction,
 * sets the corresponding reaction when a radio button is selected.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ReactionChoiceNode extends PhetPNode {
    
    
    public ReactionChoiceNode( final RealReactionModel model ) {
        super();
        
        JPanel panel = new JPanel();
        panel.setOpaque( false );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        
        final ArrayList<JRadioButton> radioButtons = new ArrayList<JRadioButton>();
        ButtonGroup group = new ButtonGroup();
        final ChemicalReaction[] reactions = model.getReactions();
        for ( int i = 0; i < reactions.length; i++ ) {
            
            final ChemicalReaction reaction = reactions[i];
            
            // radio button
            JRadioButton radioButton = new JRadioButton( reaction.getName() );
            radioButton.setOpaque( false );
            radioButtons.add( radioButton );
            group.add( radioButton );
            layout.addComponent( radioButton, row++, column );
            if ( i == 0 ) {
                radioButton.setSelected( true );
                model.setReaction( reaction );
            }
            
            // set the desired reaction when this radio button is selected
            radioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.setReaction( reaction );
                }
            } );
        }
        
        addChild( new PSwing( panel ) );
        
        // when the model changes, select the proper radio button
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                for ( int i = 0; i < reactions.length; i++ ) {
                    if ( reactions[i] == model.getReaction() ) {
                        radioButtons.get( i ).setSelected( true );
                        break;
                    }
                }
            }
        });
    }
}
