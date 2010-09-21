/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.TutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Unit;

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
