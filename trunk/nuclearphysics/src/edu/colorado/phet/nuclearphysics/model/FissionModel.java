/**
 * Class: FissionModel
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 27, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;

public class FissionModel extends BaseModel {
    public FissionModel( AbstractClock clock ) {
        super();
        clock.addClockTickListener( this );
    }
}
