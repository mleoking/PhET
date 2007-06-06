/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit2;

import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.Page;

/**
 * User: Sam Reid
 * Date: Aug 28, 2005
 * Time: 2:47:55 PM
 * Copyright (c) Aug 28, 2005 by Sam Reid
 */

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
