/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view.bargraphs;

import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:17:06 PM
 *
 */

public class WorkBarGraphSet extends BarGraphSet {
    public WorkBarGraphSet( EnergySkateParkSimulationPanel energySkaterSimulationPanel, EnergySkateParkModel energySkateParkModel, ModelViewTransform1D transform1D ) {
        super( energySkaterSimulationPanel, energySkateParkModel, EnergySkateParkStrings.getString( "work" ), transform1D );
        ValueAccessor[] workAccess = new ValueAccessor[]{
//            new ValueAccessor.TotalWork( getLookAndFeel() ),
//            new ValueAccessor.GravityWork( getLookAndFeel() ),
//            new ValueAccessor.FrictiveWork( getLookAndFeel() ),
//            new ValueAccessor.AppliedWork( getLookAndFeel() )
        };
        super.finishInit( workAccess );
    }
}
