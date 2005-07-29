/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.qm.view.SchrodingerPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 27, 2005
 * Time: 11:33:39 AM
 * Copyright (c) Jul 27, 2005 by Sam Reid
 */

public class SchrodingerMenu extends JMenu {
    private SchrodingerModule schrodingerModule;

    public SchrodingerMenu( SchrodingerModule schrodingerModule ) {
        super( "Schrodinger" );
        this.schrodingerModule = schrodingerModule;
//        JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem();

        final JCheckBoxMenuItem x = new JCheckBoxMenuItem( "Show Observable <X>" );
        x.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayXExpectation( x.isSelected() );
            }
        } );
        add( x );

        final JCheckBoxMenuItem y = new JCheckBoxMenuItem( "Show Observable <Y>" );
        y.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayYExpectation( y.isSelected() );
            }
        } );
        add( y );
    }

    private SchrodingerPanel getSchrodingerPanel() {
        return schrodingerModule.getSchrodingerPanel();
    }
}
