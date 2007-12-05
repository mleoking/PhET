package edu.colorado.phet.movingman.motion.force1d;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.movingman.motion.movingman.MovingManMotionModel;

/**
 * Created by: Sam
 * Dec 4, 2007 at 3:35:41 PM
 */
public class Force1DMotionModel extends MovingManMotionModel {
    private ForceModel forceModel;

    public Force1DMotionModel( ConstantDtClock clock ) {
        super( clock );
        forceModel = new ForceModel();
    }

    public ForceModel getForceModel() {
        return forceModel;
    }
}
