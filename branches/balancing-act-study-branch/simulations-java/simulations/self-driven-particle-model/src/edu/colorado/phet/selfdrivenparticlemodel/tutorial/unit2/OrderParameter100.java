// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;

public class OrderParameter100 extends OrderParameter90 {
    public OrderParameter100( BasicTutorialCanvas page ) {
        super( page );
        setText( "Now try to reduce the order parameter to < 0.1" );
    }

    protected boolean isOrderParamaterAwesome() {
        return getParticleModel().getOrderParameter() < 0.1;
    }

    protected double getInitRandomness() {
        return getParticleModel().getRandomness();
    }
}
