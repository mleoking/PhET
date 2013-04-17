// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.theramp.view.bars;

import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampPanel;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:17:06 PM
 */

public class WorkBarGraphSet extends BarGraphSet {
    public WorkBarGraphSet( RampPanel rampPanel, RampPhysicalModel rampPhysicalModel, ModelViewTransform1D transform1D ) {
        super( rampPanel, rampPhysicalModel, TheRampStrings.getString( "energy.work" ), transform1D );
        ValueAccessor[] workAccess = new ValueAccessor[] {
                new ValueAccessor.TotalWork( getLookAndFeel() ),
                new ValueAccessor.GravityWork( getLookAndFeel() ),
                new ValueAccessor.FrictiveWork( getLookAndFeel() ),
                new ValueAccessor.AppliedWork( getLookAndFeel() )
        };
        super.finishInit( workAccess );
    }
}
