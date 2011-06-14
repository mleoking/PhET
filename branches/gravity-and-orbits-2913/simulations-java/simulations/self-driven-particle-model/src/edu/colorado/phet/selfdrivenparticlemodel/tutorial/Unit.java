// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;

public class Unit {
    ArrayList pages = new ArrayList();
    TutorialCanvas currentTutorialCanvas;

    protected void setCanvases( TutorialCanvas[] pages ) {
        this.pages.addAll( Arrays.asList( pages ) );
        currentTutorialCanvas = pages[0];
    }

    public void teardown( SelfDrivenParticleModelApplication tutorialApplication ) {
        getCurrentPage().teardown( tutorialApplication );
    }

    public void start( SelfDrivenParticleModelApplication tutorialApplication ) {
        getCurrentPage().start( tutorialApplication );
    }

    public TutorialCanvas getCurrentPage() {
        return currentTutorialCanvas;
    }

    public void moveRight() {
        currentTutorialCanvas.moveRight();
    }

    public void moveLeft() {
        currentTutorialCanvas.moveLeft();
    }
}
