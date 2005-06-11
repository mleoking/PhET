/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:23:06 AM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class DoubleSlit {
    private CompositePotential createDoubleSlit( int XMESH, int YMESH, int x, int width, int slitHeight, int slitSeparation, double val ) {
        CompositePotential compositePotential = new CompositePotential();
        int barHeight = ( YMESH - 2 * slitHeight - slitSeparation ) / 2;
        Rectangle top = new Rectangle( x, 0, width, barHeight );
        Rectangle mid = new Rectangle( x, barHeight + slitHeight, width, slitSeparation );
        Rectangle bot = new Rectangle( x, barHeight + slitHeight * 2 + slitSeparation, width, barHeight );
        compositePotential.addPotential( new BarrierPotential( top, val ) );
        compositePotential.addPotential( new BarrierPotential( mid, val ) );
        compositePotential.addPotential( new BarrierPotential( bot, val ) );
        return compositePotential;
    }
}
