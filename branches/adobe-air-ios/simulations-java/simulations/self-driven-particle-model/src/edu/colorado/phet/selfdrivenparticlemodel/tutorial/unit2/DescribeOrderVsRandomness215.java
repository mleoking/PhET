// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;

public class DescribeOrderVsRandomness215 extends DescribeOrderVsRandomness200 {

    public DescribeOrderVsRandomness215( BasicTutorialCanvas page ) {
        super( page );
        setText( "The phase transition in water from solid to liquid at 0 degrees Celsius is a " +
                 "first order transition, " +
                 "and comes with a latent heat.  The phase transition in the Self-Driven " +
                 "Particle Model is 2nd order-" +
                 "there is no latent heat, but the system exhibits power law behavior at the critical point.\n\t" +
                 " In the next page, we search for the 'critical point', the " +
                 "value of the randomness parameter at which the order parameter drastically drops." );
    }

}
