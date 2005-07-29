/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.potentials;

import edu.colorado.phet.qm.model.Potential;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:23:06 AM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class VerticalDoubleSlit {
    public Potential createDoubleSlit( int gridWidth, int gridHeight,
                                       int x, int width, int slitHeight, int slitSeparation, double potential ) {
        CompositePotential compositePotential = new CompositePotential();
        int barHeight = ( gridHeight - 2 * slitHeight - slitSeparation ) / 2;
        Rectangle top = new Rectangle( x, 0, width, barHeight );
        Rectangle mid = new Rectangle( x, barHeight + slitHeight, width, slitSeparation );
        Rectangle bot = new Rectangle( x, barHeight + slitHeight * 2 + slitSeparation, width, barHeight );
        compositePotential.addPotential( new BarrierPotential( top, potential ) );
        compositePotential.addPotential( new BarrierPotential( mid, potential ) );
        compositePotential.addPotential( new BarrierPotential( bot, potential ) );
        return compositePotential;
    }
}
