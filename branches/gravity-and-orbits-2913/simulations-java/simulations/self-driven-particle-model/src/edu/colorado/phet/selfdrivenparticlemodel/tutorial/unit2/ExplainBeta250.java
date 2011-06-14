// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;

public class ExplainBeta250 extends Page {
    public ExplainBeta250( BasicTutorialCanvas page ) {
        super( page );
        setText( "Typically, one of these simulations is run with N=5000 or more" +
                 " to improve numerical results. " +
                 "One can compute the critical point as the point " +
                 "at which the critical exponent plot is straightest.  " +
                 "Using this technique, [1] reports a critical exponent of approximately 0.45." );
        artificialAdvance();
        showNextButton();
    }
}
