/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit1;

import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.Page;

/**
 * User: Sam Reid
 * Date: Aug 23, 2005
 * Time: 2:07:37 AM
 * Copyright (c) Aug 23, 2005 by Sam Reid
 */

public class InitSection05 extends Page {

    public InitSection05( final BasicTutorialCanvas basicPage ) {
        super( basicPage );
        String initText = "For further reading on the Self-Driven Particle Model, please see:\n" +
                          "The original paper and results [1], " +
                          "more realistic models and frameworks [3], " +
                          "results dealing with intermittency and clustering [4], and " +
                          "convergence proofs for this model [5].\n" +
                          "References for this tutorial are posted online at: \n" +
                          "http://www.colorado.edu/physics/pion/srr/particles/" +
                          "";
        super.setText( initText );
        super.artificialAdvance();
        super.showNextButton();
    }

}
