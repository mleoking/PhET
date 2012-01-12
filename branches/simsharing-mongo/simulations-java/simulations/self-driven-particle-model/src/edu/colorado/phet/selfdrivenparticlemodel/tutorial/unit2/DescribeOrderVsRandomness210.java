// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;

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
