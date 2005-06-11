/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.view.ControlPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:51:18 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerControlPanel extends ControlPanel {
    private SchrodingerModule module;

    public SchrodingerControlPanel( final SchrodingerModule module ) {
        super( module );
        this.module = module;
        JButton reset = new JButton( "Reset" );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );
        addControl( reset );
    }
}
