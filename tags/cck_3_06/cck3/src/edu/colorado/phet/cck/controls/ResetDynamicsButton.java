/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.controls;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.CCKStrings;

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
    private ICCKModule module;

    public ResetDynamicsButton( final ICCKModule module ) {
        super( CCKStrings.getString( "reset.dynamics" ) );
        this.module = module;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCircuit().resetDynamics();
            }
        } );
    }
}
