/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.potentials;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:23:06 AM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class HorizontalDoubleSlit {
    public CompositePotential createDoubleSlit( int gridWidth, int gridHeight,
                                                int y, int height, int slitSize, int slitSeparation, double potential ) {
        CompositePotential compositePotential = new CompositePotential();
        int barWidth = ( gridWidth - 2 * slitSize - slitSeparation ) / 2;

        Rectangle leftBar = new Rectangle( 0, y, barWidth, height );
        Rectangle midBar = new Rectangle( barWidth + slitSize, y, slitSeparation, height );
        Rectangle rightBar = new Rectangle( barWidth + slitSize * 2 + slitSeparation, y, barWidth, height );

        compositePotential.addPotential( new BarrierPotential( leftBar, potential ) );
        compositePotential.addPotential( new BarrierPotential( midBar, potential ) );
        compositePotential.addPotential( new BarrierPotential( rightBar, potential ) );
        return compositePotential;
    }
}
