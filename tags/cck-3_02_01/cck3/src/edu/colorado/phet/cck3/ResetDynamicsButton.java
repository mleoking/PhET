/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.components.CCKStrings;

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
    private CCKModule module;

    public ResetDynamicsButton( final CCKModule module ) {
        super( CCKStrings.getString( "reset.dynamics" ) );
        this.module = module;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCircuit().resetDynamics();
            }
        } );
    }
}
