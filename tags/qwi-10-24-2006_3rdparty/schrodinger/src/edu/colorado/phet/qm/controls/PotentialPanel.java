/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.davissongermer.QWIStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 1, 2006
 * Time: 7:22:27 AM
 * Copyright (c) Mar 1, 2006 by Sam Reid
 */

public class PotentialPanel extends VerticalLayoutPanel {
    public PotentialPanel( final QWIModule module ) {

        setFillNone();
        setBorder( BorderFactory.createTitledBorder( QWIStrings.getString( "potential" ) ) );

        JButton clear = new JButton( QWIStrings.getString( "remove.all" ) );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearPotential();
            }
        } );

        JButton newBarrier = new JButton( QWIStrings.getString( "add.barrier" ) );
        newBarrier.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addPotential();
            }
        } );
        add( newBarrier );
        add( clear );

    }
}
