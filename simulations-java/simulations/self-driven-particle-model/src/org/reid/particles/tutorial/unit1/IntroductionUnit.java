/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit1;

import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.SelfDrivenParticleModelApplication;
import org.reid.particles.tutorial.TutorialCanvas;
import org.reid.particles.tutorial.Unit;

/**
 * User: Sam Reid
 * Date: Aug 22, 2005
 * Time: 11:59:52 PM
 * Copyright (c) Aug 22, 2005 by Sam Reid
 */

public class IntroductionUnit extends Unit {
    public IntroductionUnit( SelfDrivenParticleModelApplication tutorialApplication ) {
        TutorialCanvas[] pages = new TutorialCanvas[]{new BasicTutorialCanvas( tutorialApplication, new Unit1( tutorialApplication ) )};
        setCanvases( pages );
    }
}
