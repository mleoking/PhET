/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;

/**
 * User: Sam Reid
 * Date: Aug 26, 2005
 * Time: 1:36:08 AM
 * Copyright (c) Aug 26, 2005 by Sam Reid
 */

public class DescribeOrderVsRandomness210 extends DescribeOrderVsRandomness200 {

    public DescribeOrderVsRandomness210( BasicTutorialCanvas page ) {
        super( page );
        setText( "Be aware that the critical value is dependent " +
                 "on the other model parameters, " +
                 "so if you change the number of particles or " +
                 "interaction range, you should reset the plot.\n" +
                 "Here, as in any numerical simulation, the " +
                 "results are more accurate with a higher particle count." );
    }

}
