/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.components.WIStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 24, 2006
 * Time: 9:44:40 PM
 * Copyright (c) Jun 24, 2006 by Sam Reid
 */

public class ResetDynamicsButton extends JButton {
    private CCK3Module module;

    public ResetDynamicsButton( final CCK3Module module ) {
        super( WIStrings.getString( "reset.dynamics" ) );
        this.module = module;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCircuit().resetDynamics();
            }
        } );
    }
}
