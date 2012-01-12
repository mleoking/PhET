// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.TutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Unit;

public class IntroductionUnit extends Unit {
    public IntroductionUnit( SelfDrivenParticleModelApplication tutorialApplication ) {
        TutorialCanvas[] pages = new TutorialCanvas[] { new BasicTutorialCanvas( tutorialApplication, new Unit1( tutorialApplication ) ) };
        setCanvases( pages );
    }
}
