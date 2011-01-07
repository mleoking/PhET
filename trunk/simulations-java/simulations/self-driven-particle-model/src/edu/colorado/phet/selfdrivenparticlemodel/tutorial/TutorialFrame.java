// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import javax.swing.*;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;

public class TutorialFrame extends JFrame {
    private SelfDrivenParticleModelApplication tutorialApplication;

    public TutorialFrame( SelfDrivenParticleModelApplication tutorialApplication ) {
        super( "The Self-Driven Particle Model" );
        this.tutorialApplication = tutorialApplication;
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
