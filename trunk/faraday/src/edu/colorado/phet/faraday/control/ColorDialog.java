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


/**
 * ColorDialog is a color chooser and associated listener.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ColorDialog {

    public interface Listener {

        void colorChanged( Color color );

        void cancelled( Color orig );

        void ok( Color color );
    }
    
    public static void showDialog( String title, Component component, final Color initialColor, final Listener listener ) {
        final JColorChooser jcc = new JColorChooser( initialColor );
        jcc.getSelectionModel().addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                listener.colorChanged( jcc.getColor() );
            }
        } );
        JDialog dialog = JColorChooser.createDialog( component, title, false, jcc, new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                listener.ok( jcc.getColor() );
            }
        }, new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                listener.cancelled( initialColor );
            }
        } );
       // SwingUtils.centerDialogInParent( dialog );//XXX
        dialog.setVisible( true );
    }
}