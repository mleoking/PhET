/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.view.bargraphs;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.energyskatepark.EC3Canvas;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergyConservationModel;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:17:25 PM
 * Copyright (c) Jun 6, 2005 by Sam Reid
 */

public class EnergyBarGraphSet extends BarGraphSet {
    public EnergyBarGraphSet( EC3Canvas canvas, EnergyConservationModel energyConservationModel, ModelViewTransform1D transform1D ) {
        super( canvas, energyConservationModel, EnergySkateParkStrings.getString( "energy" ), transform1D );
        ValueAccessor[] energyAccess = new ValueAccessor[]{
                new ValueAccessor.KineticEnergy( super.getLookAndFeel() ),
                new ValueAccessor.PotentialEnergy( getLookAndFeel() ),
                new ValueAccessor.ThermalEnergy( getLookAndFeel() ),
                new ValueAccessor.TotalEnergy( getLookAndFeel() )
        };
        finishInit( energyAccess );
    }
}
