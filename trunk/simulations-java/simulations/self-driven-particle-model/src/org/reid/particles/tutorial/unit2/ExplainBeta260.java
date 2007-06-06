/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit2;

import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.Page;

/**
 * User: Sam Reid
 * Date: Aug 28, 2005
 * Time: 2:55:12 PM
 * Copyright (c) Aug 28, 2005 by Sam Reid
 */

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
