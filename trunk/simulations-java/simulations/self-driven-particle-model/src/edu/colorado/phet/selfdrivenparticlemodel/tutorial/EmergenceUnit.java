/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2.Unit2;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 10:24:16 PM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

public class EmergenceUnit extends Unit {
    public EmergenceUnit( SelfDrivenParticleModelApplication tutorialApplication ) {
        super();
        TutorialCanvas[] pages = new TutorialCanvas[]{new BasicTutorialCanvas( tutorialApplication, new Unit2( tutorialApplication ) )};
        setCanvases( pages );
    }
}
