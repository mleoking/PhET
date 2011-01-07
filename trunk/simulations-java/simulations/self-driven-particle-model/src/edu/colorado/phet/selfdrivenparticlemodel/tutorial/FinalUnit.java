// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;

public class FinalUnit extends Unit {
    public FinalUnit( SelfDrivenParticleModelApplication tutorialApplication ) {
        super();
        TutorialCanvas[] pages = new TutorialCanvas[]{new BasicTutorialCanvas( tutorialApplication, new Unit3( tutorialApplication ) )};
        setCanvases( pages );
    }
}
