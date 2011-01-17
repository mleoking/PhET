// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;
import edu.umd.cs.piccolox.pswing.PSwing;

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
