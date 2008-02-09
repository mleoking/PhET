/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;

/**
 * User: Sam Reid
 * Date: Aug 28, 2005
 * Time: 8:59:21 PM
 * Copyright (c) Aug 28, 2005 by Sam Reid
 */

public class FinalUnit extends Unit {
    public FinalUnit( SelfDrivenParticleModelApplication tutorialApplication ) {
        super();
        TutorialCanvas[] pages = new TutorialCanvas[]{new BasicTutorialCanvas( tutorialApplication, new Unit3( tutorialApplication ) )};
        setCanvases( pages );
    }
}
