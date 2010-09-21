/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial;

/**
 * User: Sam Reid
 * Date: Aug 28, 2005
 * Time: 11:15:52 PM
 * Copyright (c) Aug 28, 2005 by Sam Reid
 */

public class FinalCommentsPage310 extends Page {
    public FinalCommentsPage310( BasicTutorialCanvas page ) {
        super( page );
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
