/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SwingUtils;


/**
 * ColorDialog is a color chooser and associated listener.
 * Implementation courtesy of Sam Reid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ColorDialog {

    /**
     * ColorDialog.Listener is the interface that all client must implement.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    public interface Listener {
        /** Called when the user selects a color. */
        void colorChanged( Color color );
        /** Called when the user presses the OK button. */
        void ok( Color color );
        /** Called when the user pressed the Cancel button. */
        void cancelled( Color orig );
    }
    
    /**
     * Shows a color chooser dialog.
     * 
     * @param title
     * @param parent
     * @param initialColor
     * @param listener
     */
    public static void showDialog( String title, Component parent, 
            final Color initialColor, final Listener listener ) {
        
        final JColorChooser jcc = new JColorChooser( initialColor );
        
        jcc.getSelectionModel().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                listener.colorChanged( jcc.getColor() );
            }
        } );
        
        JDialog dialog = JColorChooser.createDialog( parent, title, false, jcc, 
            new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    listener.ok( jcc.getColor() );
                }
            },
            new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    listener.cancelled( initialColor );
                }
            } );
        
        SwingUtils.centerDialogInParent( dialog );
        dialog.setVisible( true );
    }
}