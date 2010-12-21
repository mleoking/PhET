/* Copyright 2008-2010, University of Colorado */ 

package edu.colorado.phet.common.phetcommon.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * Swing version of the "Reset All" button.
 * When it's pressed, requests confirmation.
 * If confirmation is affirmative, then all Resettables are reset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ResetAllButton extends JButton {
    
    private final ResetAllDelegate delegate; // delegate that implements Reset All behavior

    /**
     * @param parent parent component for the confirmation dialog
     */
    public ResetAllButton( final Component parent ) {
        this( new Resettable[0], parent );
    }

    /**
     * 
     * @param resettable thing to reset
     * @param parent parent component for the confirmation dialog
     */
    public ResetAllButton( final Resettable resettable, final Component parent ) {
        this( new Resettable[] { resettable }, parent );
    }
    
    /**
     * 
     * @param resettables things to reset
     * @param parent parent component for the confirmation dialog
     */
    public ResetAllButton( final Resettable[] resettables, final Component parent ) {
        super( PhetCommonResources.getInstance().getLocalizedString( PhetCommonResources.STRING_RESET_ALL ) );
        this.delegate = new ResetAllDelegate( resettables, parent );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                delegate.resetAll();
            }
        } );
    }

    public void addResettable( Resettable resettable ) {
        delegate.addResettable( resettable );
    }
    
    public void removeResettable( Resettable resettable ) {
        delegate.removeResettable( resettable );
    }
    
    public static void main( String[] args ) {

        Resettable resettable1 = new Resettable() {
            public void reset() {
                System.out.println( "Reset 1" );
            }
        };
        Resettable resettable2 = new Resettable() {
            public void reset() {
                System.out.println( "Reset 2" );
            }
        };

        ResetAllButton resetAllButton = new ResetAllButton( resettable1, null );
        resetAllButton.addResettable( resettable2 );

        JFrame frame = new JFrame();
        frame.setContentPane( resetAllButton );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
