// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Delegate for "Reset All" behavior.
 * Calling resetAll opens a confirmation dialog.
 * If the user confirms, then all Resettables are reset.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ResetAllDelegate {

    private final Vector<Resettable> resettables;
    private final Component parent;
    private boolean confirmationEnabled;
    private JDialog dialog;

    /**
     * @param resettables things to reset
     * @param parent      parent component for the confirmation dialog
     */
    public ResetAllDelegate( final Resettable[] resettables, final Component parent ) {
        this.resettables = new Vector<Resettable>();
        for ( Resettable resettable : resettables ) {
            this.resettables.add( resettable );
        }
        this.parent = parent;
        this.confirmationEnabled = true;
    }

    public void setConfirmationEnabled( boolean confirmationEnabled ) {
        this.confirmationEnabled = confirmationEnabled;
    }

    public boolean isConfirmationEnabled() {
        return confirmationEnabled;
    }

    public void addResettable( Resettable resettable ) {
        resettables.add( resettable );
    }

    public void removeResettable( Resettable resettable ) {
        resettables.remove( resettable );
    }

    /**
     * Resets all Resettables, with optional confirmation.
     */
    public void resetAll( final Point2D location ) {
        if ( !confirmationEnabled ) {
            for ( Resettable resettable : resettables ) {
                resettable.reset();
            }
        }
        else {
            if ( dialog == null ) {
                //Show a confirmation dialog, which will handle the resetting if it is pressed.
                dialog = new JDialog( (Frame) SwingUtilities.getWindowAncestor( parent ) ) {{
                    setUndecorated( true );
                    setContentPane( new VerticalLayoutPanel() {{
                        final Color background = Color.yellow;
                        setBackground( background );
                        add( new JPanel() {{
                            setBackground( background );
                            add( new JLabel( "Are you sure you want to reset?" ) {{setFont( new PhetFont( 16 ) );}} );
                        }} );
                        add( new JPanel() {{
                            setBackground( background );
                            add( new JButton( "Yes, Reset" ) {{
                                setFont( new PhetFont( 16 ) );
                                addActionListener( new ActionListener() {
                                    public void actionPerformed( ActionEvent e ) {
                                        for ( Resettable resettable : resettables ) {
                                            resettable.reset();
                                            dispose();
                                        }
                                    }
                                } );
                            }} );
                            add( new JButton( "No, don't reset" ) {{
                                setFont( new PhetFont( 16 ) );
                                addActionListener( new ActionListener() {
                                    public void actionPerformed( ActionEvent e ) {
                                        dispose();
                                    }
                                } );
                            }} );
                        }} );
                        setBorder( new LineBorder( Color.blue, 2, true ) );
                    }} );
                    pack();
                    //TODO: if the window goes off the screen, move it back onscreen
                }};
            }
            dialog.setLocation( (int) location.getX() - dialog.getWidth() / 2, (int) location.getY() );
            dialog.setVisible( true );
        }
    }

//    /*
//    * Opens a confirmation dialog, returns true if the user selects "Yes".
//    */
//    private boolean confirmReset() {
////        String message = PhetCommonResources.getInstance().getLocalizedString( "ControlPanel.message.confirmResetAll" );
////        String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
////        int option = PhetOptionPane.showYesNoDialog( parent, message, title );
////        return ( option == JOptionPane.YES_OPTION );
//
//    }
}