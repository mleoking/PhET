/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.ec3.view.bargraphs.EnergyBarGraphSet;
import edu.colorado.phet.piccolo.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 2:01:22 PM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class BarGraphCanvas extends PSwingCanvas {
    private EC3Module module;

    public BarGraphCanvas( EC3Module module ) {
        this.module = module;
        EnergyBarGraphSet energyBarGraphSet = new EnergyBarGraphSet( module.getEnergyConservationCanvas(), module.getEnergyConservationModel(), new ModelViewTransform1D( 0, 1000000, 0, 100 ) );
        getLayer().addChild( energyBarGraphSet );
        energyBarGraphSet.translate( 45, 45 );
    }
}
