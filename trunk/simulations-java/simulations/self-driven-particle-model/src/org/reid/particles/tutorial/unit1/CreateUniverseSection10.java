/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit1;

import edu.umd.cs.piccolox.pswing.PSwing;
import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.Page;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Aug 23, 2005
 * Time: 2:19:33 AM
 * Copyright (c) Aug 23, 2005 by Sam Reid
 */

public class CreateUniverseSection10 extends Page {
    private PSwing newUniverseButton;

    public CreateUniverseSection10( BasicTutorialCanvas basicPage ) {
        super( basicPage );
        String initText2 = "Distributed, localized systems can exhibit organized, complex and dynamical behavior.  " +
                           "In this tutorial, we examine the Self-Driven Particle Model [1] " +
                           "and some of its emergent properties." +
                           "\nThis model takes place in a continuous square universe with periodic boundary conditions.";
        setText( initText2 );
//        setFinishText( "  Wel." );
        JButton newUniverse = new JButton( "Initialize Universe" );
        newUniverse.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBasePage().createUniverse();
                getBasePage().removeChild( newUniverseButton );
                advance();
            }
        } );

        newUniverseButton = new PSwing( basicPage, newUniverse );
    }

    public void init() {
        super.init();
//        getBasePage().topLeft( newUniverseButton );
        newUniverseButton.setOffset( getBasePage().getWidth() / 2, getBasePage().getHeight() / 2 );
        getBasePage().addChild( newUniverseButton );
    }

    public void teardown() {
        getBasePage().createUniverse();
        removeChild( newUniverseButton );
    }

}
