/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 12:57:06 AM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

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
