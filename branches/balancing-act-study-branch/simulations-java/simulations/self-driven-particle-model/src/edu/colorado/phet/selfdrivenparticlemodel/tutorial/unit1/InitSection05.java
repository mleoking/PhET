// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;

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
