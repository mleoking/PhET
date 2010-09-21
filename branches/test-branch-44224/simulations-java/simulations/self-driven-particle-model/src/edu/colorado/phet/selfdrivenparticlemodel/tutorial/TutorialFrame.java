/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import javax.swing.*;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;

/**
 * User: Sam Reid
 * Date: Aug 22, 2005
 * Time: 11:39:00 PM
 * Copyright (c) Aug 22, 2005 by Sam Reid
 */

public class TutorialFrame extends JFrame {
    private SelfDrivenParticleModelApplication tutorialApplication;

    public TutorialFrame( SelfDrivenParticleModelApplication tutorialApplication ) {
        super( "The Self-Driven Particle Model" );
        this.tutorialApplication = tutorialApplication;
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
