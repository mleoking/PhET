// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

import edu.colorado.phet.selfdrivenparticlemodel.SelfDrivenParticleModelApplication;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2.Unit2;

public class EmergenceUnit extends Unit {
    public EmergenceUnit( SelfDrivenParticleModelApplication tutorialApplication ) {
        super();
        TutorialCanvas[] pages = new TutorialCanvas[] { new BasicTutorialCanvas( tutorialApplication, new Unit2( tutorialApplication ) ) };
        setCanvases( pages );
    }
}
