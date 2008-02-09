/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;

/**
 * User: Sam Reid
 * Date: Aug 26, 2005
 * Time: 2:25:10 AM
 * Copyright (c) Aug 26, 2005 by Sam Reid
 */

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
