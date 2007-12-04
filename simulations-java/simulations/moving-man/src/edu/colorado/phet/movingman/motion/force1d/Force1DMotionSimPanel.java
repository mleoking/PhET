package edu.colorado.phet.movingman.motion.force1d;

import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.movingman.motion.movingman.MovingManNode;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:29:00 PM
 */
public class Force1DMotionSimPanel extends PhetPCanvas {
    private MovingManNode movingManNode;

    public Force1DMotionSimPanel( ConstantDtClock clock ) {
        movingManNode = new MovingManNode( new SingleBodyMotionModel( clock ) );
        addScreenChild( movingManNode );
    }

    protected void updateLayout() {
        super.updateLayout();
        movingManNode.setTransform( 22.0, getWidth() );
    }
}
