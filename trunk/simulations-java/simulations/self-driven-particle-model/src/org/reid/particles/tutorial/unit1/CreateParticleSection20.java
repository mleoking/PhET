/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit1;

import edu.colorado.phet.piccolo.pswing.PSwing;
import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.Page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Aug 23, 2005
 * Time: 2:19:33 AM
 * Copyright (c) Aug 23, 2005 by Sam Reid
 */

public class CreateParticleSection20 extends Page {
    private PSwing createParticle;

    public CreateParticleSection20( final BasicTutorialCanvas basicPage ) {
        super( basicPage );
        String initText2 = "Now add a particle to the universe.  In this model, particles are always running at full speed.";
        setText( initText2 );
        setFinishText( "\n  Well done.  In the nonrandom case, isolated particles travel in straight lines." );
        JButton sdp = new JButton( "Create Particle" );
        sdp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                basicPage.addParticle( getParticleModel().getBoxWidth() / 2.0, getParticleModel().getBoxHeight() / 2.0, 0, Color.blue );
                startModel();
                advance();
            }
        } );
        createParticle = new PSwing( basicPage, sdp );
    }

    public void init() {
        super.init();
        createParticle.setOffset( getBasePage().getUniverseGraphic().getFullBounds().getMaxX(), getBasePage().getUniverseGraphic().getFullBounds().getCenterY() );
        addChild( createParticle );
        getBasePage().setNumberParticles( 0 );
        getBasePage().setHalosVisible( false );
    }

    public void teardown() {
        removeChild( createParticle );
    }

}
