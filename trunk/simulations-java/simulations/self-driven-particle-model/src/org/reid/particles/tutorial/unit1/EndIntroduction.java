/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit1;

import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.PButton;
import org.reid.particles.tutorial.Page;
import org.reid.particles.tutorial.SelfDrivenParticleApplication;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 10:02:18 PM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

public class EndIntroduction extends Page {
    private PButton nextUnit;
    private SelfDrivenParticleApplication tutorialApplication;

    public EndIntroduction( BasicTutorialCanvas page, SelfDrivenParticleApplication tutorialApplication ) {
        super( page );
        this.tutorialApplication = tutorialApplication;
        setText( "Well Done.  You have completed the introduction to the Self-Driven Particle Model.  The next section will discuss emergent properties of this model." );
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
        getBasePage().hideNextButton();
    }

    public void showNextSectionButton() {
        nextUnit.setOffset( getBasePage().getWidth() - nextUnit.getFullBounds().getWidth() - 2, getBasePage().getNextButtonLocation().getY() );
//        nextUnit.setOffset( );
        addChild( nextUnit );
    }

    public void teardown() {
        super.teardown();
        removeChild( nextUnit );
    }
}
