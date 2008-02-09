/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit2;

import org.reid.particles.SelfDrivenParticleModelApplication;
import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.PButton;
import org.reid.particles.tutorial.Page;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Aug 28, 2005
 * Time: 8:55:22 PM
 * Copyright (c) Aug 28, 2005 by Sam Reid
 */

public class FinishedUnit2 extends Page {
    private PButton nextUnit;
    private SelfDrivenParticleModelApplication tutorialApplication;

    public FinishedUnit2( BasicTutorialCanvas page, SelfDrivenParticleModelApplication tutorialApplication ) {
        super( page );
        this.tutorialApplication = tutorialApplication;
        setText( "Well Done.  You have computed the critical point and critical exponent " +
                 "for the Self-Driven Particle Model.  " +
                 "\nIn the next and final unit, we mention supplementary results, " +
                 "and give a full-featured platform for " +
                 "experimentation with the model." );
        artificialAdvance();

        playApplause();

        nextUnit = new PButton( page, "Next Unit" );
        nextUnit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                nextUnit();
            }

        } );

    }

    private void nextUnit() {
        tutorialApplication.nextUnit();
    }

    public void init() {
        super.init();
        showNextSectionButton();
    }

    public void showNextSectionButton() {
        nextUnit.setOffset( getBasePage().getWidth() - nextUnit.getFullBounds().getWidth() - 2, getBasePage().getNextButtonLocation().getY() );
//        nextUnit.setOffset( );
        addChild( nextUnit );
    }
}
