/* Copyright 2008, University of Colorado */ 

package edu.colorado.phet.common.phetcommon.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * "Reset All" button.  When it's pressed, requests confirmation.
 * If confirmation is affirmative, then all Resettables are reset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ResetAllButton extends JButton {
    private ArrayList m_resettables;

    public ResetAllButton( final Component parent ) {
        this(new Resettable[0],parent );
    }

    public ResetAllButton( final Resettable resettable, final Component parent ) {
        this( new Resettable[] { resettable }, parent );
    }
    
    public ResetAllButton( final Resettable[] resettables, final Component parent ) {
        super();
        this.m_resettables=new ArrayList( Arrays.asList( resettables ));
        // set text to "Reset All"
        setText( PhetCommonResources.getInstance().getLocalizedString( PhetCommonResources.STRING_RESET_ALL ) );
        
        // When the button is pressed, request confirmation and reset all Resettables
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String message = PhetCommonResources.getInstance().getLocalizedString( "ControlPanel.message.confirmResetAll" );
                String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
                int option = JOptionPane.showConfirmDialog( parent, message, title, JOptionPane.YES_NO_CANCEL_OPTION );
                if ( option == JOptionPane.YES_OPTION ) {
                    for ( int i = 0; i < m_resettables.size(); i++ ) {
                        ((Resettable)m_resettables.get(i)).reset();
                    }
                }
            }
        } );
    }

    public void addResettable( Resettable resettable ) {
        m_resettables.add( resettable );
    }

    public static void main( String[] args ) {
        ResetAllButton resetAllButton=new ResetAllButton( new Resettable(){
            public void reset() {
                System.out.println( "Reset 1" );
            }
        },null);
        resetAllButton.addResettable( new Resettable(){
            public void reset() {
                System.out.println( "Reset 2" );
            }
        } );
        JFrame frame=new JFrame( );
        frame.setContentPane( resetAllButton );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
