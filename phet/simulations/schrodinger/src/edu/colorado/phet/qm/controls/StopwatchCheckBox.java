/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.view.QWIPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Mar 1, 2006
 * Time: 7:30:43 AM
 * Copyright (c) Mar 1, 2006 by Sam Reid
 */

//public class StopwatchCheckBox extends JCheckBox {
public class StopwatchCheckBox extends HorizontalLayoutPanel {
    private QWIPanel QWIPanel;

    public StopwatchCheckBox( QWIPanel QWIPanel ) {
        super();
        final JCheckBox checkBox = new JCheckBox( QWIStrings.getString( "stopwatch" ) );
        this.QWIPanel = QWIPanel;

//        final JCheckBox stopwatchCheckBox = new JCheckBox( "Stopwatch" );
        checkBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().setStopwatchVisible( checkBox.isSelected() );
            }

        } );
        new Timer( 500, new ActionListener() {//todo why does this drag the application if time < 30 ms?

            public void actionPerformed( ActionEvent e ) {
                setEnabled( !getSchrodingerPanel().isPhotonMode() );
            }
        } ).start();
        add( checkBox );
        try {
            add( new JLabel( new ImageIcon( edu.colorado.phet.common.view.util.ImageLoader.loadBufferedImage( "images/stopwatch.png" ) ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private QWIPanel getSchrodingerPanel() {
        return QWIPanel;
    }
}
