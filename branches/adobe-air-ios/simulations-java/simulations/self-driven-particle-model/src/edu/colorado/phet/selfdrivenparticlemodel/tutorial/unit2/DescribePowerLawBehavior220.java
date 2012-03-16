// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;

public class DescribePowerLawBehavior220 extends Page {
    public DescribePowerLawBehavior220( BasicTutorialCanvas page ) {
        super( page );
        setText( "In systems close to the critical point, the order parameter exhibits power law behavior.  " +
                 "The order parameter varies as the deviation from the critical value raised to some power.  " +
                 "This power is called the critical exponent.  " +
                 "Formally, p=k(c-r)^B, where p is the order parameter, " +
                 "k is a proportionality constant, c is the critical randomness," +
                 " r is the randomness and B is the critical exponent." );
        artificialAdvance();
        showNextButton();
    }
}
