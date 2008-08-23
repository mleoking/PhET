package edu.colorado.phet.forces1d.common;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.forces1d.phetcommon.view.util.SwingUtils;

/**
 * User: Sam Reid
 * Date: Jul 19, 2004
 * Time: 9:55:48 PM
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
        SwingUtils.centerDialogInParent( dialog );
        dialog.setVisible( true );
    }
}
