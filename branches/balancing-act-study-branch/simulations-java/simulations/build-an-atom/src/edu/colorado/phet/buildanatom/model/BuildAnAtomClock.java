// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.model;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * The clock for this simulation.
 * The simulation time change (dt) on each clock tick is constant,
 * regardless of when (in wall time) the ticks actually happen.
 */
public class BuildAnAtomClock extends ConstantDtClock {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomClock() {
        super( 1000 / BuildAnAtomDefaults.CLOCK_FRAME_RATE, BuildAnAtomDefaults.CLOCK_DT );
    }
}
