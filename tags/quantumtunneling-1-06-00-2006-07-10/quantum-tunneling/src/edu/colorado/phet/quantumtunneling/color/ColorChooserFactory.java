/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.color;

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
 * ColorChooserFactory creates non-modal color chooser dialogs.
 *
 * @author Sam Reid, Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ColorChooserFactory {

    /**
     * ColorDialog.Listener is the interface that all client must implement.
     */
    public interface Listener {
        /** Called when the user selects a color. */
        void colorChanged( Color color );
        /** Called when the user presses the OK button. */
        void ok( Color color );
        /** Called when the user pressed the Cancel button. */
        void cancelled( Color originalColor );
    }
    
    /* Not intended for instantiation */
    private ColorChooserFactory() {}
    
    /**
     * Creates a color chooser dialog.
     * 
     * @param title
     * @param parent
     * @param initialColor
     * @param listener
     */
    public static JDialog createDialog( String title, Component parent, 
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
        return dialog;
    }
    
    /**
     * Creates a color chooser dialog and makes it visible.
     * 
     * @param title
     * @param parent
     * @param initialColor
     * @param listener
     */
    public static void showDialog( String title, Component parent, 
            final Color initialColor, final Listener listener ) {
        JDialog dialog = ColorChooserFactory.createDialog( title, parent, initialColor, listener );
        dialog.show();
    }
}