// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.PButton;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;

public class EndIntroduction extends Page {
    private PButton nextUnit;
    private SelfDrivenParticleModelApplication tutorialApplication;

    public EndIntroduction( BasicTutorialCanvas page, SelfDrivenParticleModelApplication tutorialApplication ) {
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
