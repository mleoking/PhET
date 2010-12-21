/* Copyright 2010, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;

/**
 * Piccolo version of the phetcommon "Reset All" button.
 * TODO: make ResetAllButton and ResetAllButtonNode use a shared class that encapsulates shared functionality.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ResetAllButtonNode extends ButtonNode {
    
    private final Vector<Resettable> resettables;

    public ResetAllButtonNode( final Resettable resettable, final Component parent, int fontSize, Color textColor, Color backgroundColor ) {
        this( new Resettable[] { resettable }, parent, fontSize, textColor, backgroundColor );
    }

    public ResetAllButtonNode( final Resettable[] resettables, final Component parent, int fontSize, Color textColor, Color backgroundColor  ) {
        super( PhetCommonResources.getString( PhetCommonResources.STRING_RESET_ALL ), fontSize, textColor, backgroundColor );
        
        this.resettables = new Vector<Resettable>();
        for ( Resettable resettable : resettables ) {
            this.resettables.add( resettable );
        }
        
        // When the button is pressed, request confirmation and reset all Resettables
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String message = PhetCommonResources.getInstance().getLocalizedString( "ControlPanel.message.confirmResetAll" );
                String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
                int option = PhetOptionPane.showYesNoDialog( parent, message, title );
                if ( option == JOptionPane.YES_OPTION ) {
                    for ( Resettable resettable : ResetAllButtonNode.this.resettables ) {
                        resettable.reset();
                    }
                }
            }
        } );
    }

    public void addResettable( Resettable resettable ) {
        resettables.add( resettable );
    }
    
    public void removeResettable( Resettable resettable ) {
        resettables.remove( resettable );
    }
}
