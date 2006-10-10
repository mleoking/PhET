/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.view.bargraphs;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.energyskatepark.EC3Canvas;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergyConservationModel;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:17:06 PM
 * Copyright (c) Jun 6, 2005 by Sam Reid
 */

public class WorkBarGraphSet extends BarGraphSet {
    public WorkBarGraphSet( EC3Canvas eC3Canvas, EnergyConservationModel energyConservationModel, ModelViewTransform1D transform1D ) {
        super( eC3Canvas, energyConservationModel, EnergySkateParkStrings.getString( "work" ), transform1D );
        ValueAccessor[] workAccess = new ValueAccessor[]{
//            new ValueAccessor.TotalWork( getLookAndFeel() ),
//            new ValueAccessor.GravityWork( getLookAndFeel() ),
//            new ValueAccessor.FrictiveWork( getLookAndFeel() ),
//            new ValueAccessor.AppliedWork( getLookAndFeel() )
        };
        super.finishInit( workAccess );
    }
}
