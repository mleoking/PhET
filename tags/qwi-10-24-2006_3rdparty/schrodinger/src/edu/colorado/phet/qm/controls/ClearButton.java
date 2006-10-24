/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.view.QWIPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jan 1, 2006
 * Time: 5:47:15 PM
 * Copyright (c) Jan 1, 2006 by Sam Reid
 */

public class ClearButton extends JButton {
    public ClearButton( final QWIPanel qwiPanel ) {
        super( QWIStrings.getString( "clear.wave" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                qwiPanel.clearWavefunction();
            }
        } );
    }
}
