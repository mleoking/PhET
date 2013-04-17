// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;

public class ExplainBeta260 extends Page {
    public ExplainBeta260( BasicTutorialCanvas page ) {
        super( page );
        setText( "Furthermore, the critical randomness can be determined for " +
                 "a universe of infinite extent (assuming the particles have a fixed global density).  " +
                 "This computation is called a finite size scaling analysis, also reported in [1].  " +
                 "They show the critical randomness to be approximately 2.9 for the infinite-extent universe." );
        artificialAdvance();
        showNextButton();
    }
}
