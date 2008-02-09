/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Aug 22, 2005
 * Time: 11:57:01 PM
 * Copyright (c) Aug 22, 2005 by Sam Reid
 */

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
