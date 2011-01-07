// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.PButton;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CollisionSection30 extends Page {
    private PButton againButton;
    private PButton goButton;
    private double dy;

    public CollisionSection30( BasicTutorialCanvas basicPage ) {
        super( basicPage );
        setText( "Particles can see each other if they are within visual range.  " +
                 "I'll indicate visual range with a yellow highlight.  " +
                 "In the nonrandom case, particles choose their direction of motion as the average of all particles within their visual range (including themselves)." );
        setFinishText( "\nYou can run it again if you like." );
        dy = getParticleModel().getBoxHeight() * 0.2;
    }

    public void init() {
        super.init();
        super.pauseModel();
        super.clearParticles();
        getBasePage().getParticleModel().setRandomness( 0 );

        getBasePage().addParticle( getParticleModel().getBoxWidth() / 2, getParticleModel().getBoxHeight() * 0.9 - dy, -Math.PI / 2, Color.red );
        getBasePage().addParticle( getParticleModel().getBoxWidth() * 0.1, getParticleModel().getBoxHeight() * 0.5 - dy, 0, Color.blue );

        getBasePage().setHalosVisible( true );
        againButton = new PButton( getBasePage(), "Again!" );
        againButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                runAgain();
            }

        } );
        goButton = new PButton( getBasePage(), "Go!" );
        goButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                goPressed();
            }
        } );
        goButton.setOffset( getBasePage().getUniverseGraphic().getFullBounds().getMaxX(), getBasePage().getUniverseGraphic().getFullBounds().getCenterY() );
        addChild( goButton );
    }

    private void goPressed() {
        startModel();
        this.removeChild( goButton );
        addChild( againButton );
        againButton.setOffset( goButton.getFullBounds().getX(), goButton.getFullBounds().getMaxY() );
        advance();
    }

    private void runAgain() {
        pauseModel();
        clearParticles();
        getBasePage().addParticle( getParticleModel().getBoxWidth() / 2, getParticleModel().getBoxHeight() * 0.9 - dy, -Math.PI / 2, Color.red );
        getBasePage().addParticle( getParticleModel().getBoxWidth() * 0.1, getParticleModel().getBoxHeight() * 0.5 - dy, 0, Color.blue );

        getBasePage().addHalos();
        startModel();
    }

    public void teardown() {
        super.teardown();
        removeChild( againButton );
        removeChild( goButton );
        startModel();
    }

}
