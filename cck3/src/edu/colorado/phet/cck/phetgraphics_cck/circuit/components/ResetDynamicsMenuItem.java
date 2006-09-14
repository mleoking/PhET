/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.phetgraphics_cck.circuit.components;

import edu.colorado.phet.cck.model.DynamicBranch;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 2:32:46 AM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class ResetDynamicsMenuItem extends JMenuItem {
    private DynamicBranch branch;

    public ResetDynamicsMenuItem( String text, final DynamicBranch branch ) {
        super( text );
        this.branch = branch;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                branch.resetDynamics();
            }
        } );
    }
}
