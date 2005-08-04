/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.bars;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampPanel;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:17:25 PM
 * Copyright (c) Jun 6, 2005 by Sam Reid
 */

public class EnergyBarGraphSet extends BarGraphSet {
    public EnergyBarGraphSet( RampPanel rampPanel, RampModel rampModel, ModelViewTransform1D transform1D ) {
        super( rampPanel, rampModel, "Energy", transform1D );
        ValueAccessor[] energyAccess = new ValueAccessor[]{
            new ValueAccessor.KineticEnergy( super.getLookAndFeel() ), new ValueAccessor.PotentialEnergy( getLookAndFeel() ),
            new ValueAccessor.ThermalEnergy( getLookAndFeel() ), new ValueAccessor.TotalEnergy( getLookAndFeel() )
        };
        setAccessors( energyAccess );
    }
}
