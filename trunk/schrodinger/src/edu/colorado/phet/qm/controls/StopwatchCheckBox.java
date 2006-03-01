/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.qm.view.SchrodingerPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 1, 2006
 * Time: 7:30:43 AM
 * Copyright (c) Mar 1, 2006 by Sam Reid
 */

public class StopwatchCheckBox extends JCheckBox {
    private SchrodingerPanel schrodingerPanel;

    public StopwatchCheckBox( SchrodingerPanel schrodingerPanel ) {
        super( "Stopwatch" );
        this.schrodingerPanel = schrodingerPanel;

//        final JCheckBox stopwatchCheckBox = new JCheckBox( "Stopwatch" );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().setStopwatchVisible( isSelected() );
            }

        } );
        new Timer( 500, new ActionListener() {//todo why does this drag the application if time < 30 ms?

            public void actionPerformed( ActionEvent e ) {
                setEnabled( !getSchrodingerPanel().isPhotonMode() );
            }
        } ).start();
    }

    private SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }
}
