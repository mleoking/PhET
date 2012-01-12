// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;

public class DescribePowerLawBehavior230 extends Page {
    public DescribePowerLawBehavior230( BasicTutorialCanvas page ) {
        super( page );
        setText( "To determine the critical exponent, one therefore plots " +
                 "the logarithm of the order parameter vs. the logarithm of (c-r)/c.\n" +
                 "That is, plotting ln(p) against ln[(c-r)/c] " +
                 "should yield a straight line with the slope of B, the critical exponent." );
        artificialAdvance();
        showNextButton();
    }
}
