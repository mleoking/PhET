package edu.colorado.phet.theramp.view.arrows;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.view.BlockGraphic;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.theramp.view.RampPanel;


/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 11:29:31 AM
 */
public class CartesianArrowSet extends AbstractArrowSet {

    public CartesianArrowSet( final RampPanel rampPanel, BlockGraphic blockGraphic ) {
        super( rampPanel, blockGraphic );
        RampLookAndFeel ralf = new RampLookAndFeel();

        final RampPhysicalModel rampPhysicalModel = rampPanel.getRampModule().getRampPhysicalModel();
        ForceArrowGraphic forceArrowGraphic = new ForceArrowGraphic( rampPanel, AbstractArrowSet.APPLIED, ralf.getAppliedForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                return rampPhysicalModel.getAppliedForce();
            }
        }, getBlockGraphic() );

        ForceArrowGraphic totalArrowGraphic = new ForceArrowGraphic( rampPanel, TOTAL, ralf.getNetForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public Vector2D getForce() {
                return rampPhysicalModel.getTotalForce();
            }
        }, getBlockGraphic() );

        ForceArrowGraphic frictionArrowGraphic = new ForceArrowGraphic( rampPanel, FRICTION, ralf.getFrictionForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                return rampPhysicalModel.getFrictionForce();
            }
        }, getBlockGraphic() );

        ForceArrowGraphic gravityArrowGraphic = new ForceArrowGraphic( rampPanel, WEIGHT, ralf.getWeightColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                return rampPhysicalModel.getGravityForce();
            }
        }, getBlockGraphic() );

        ForceArrowGraphic normalArrowGraphic = new ForceArrowGraphic( rampPanel, NORMAL, ralf.getNormalColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                return rampPhysicalModel.getNormalForce();
            }
        }, getBlockGraphic() );

        ForceArrowGraphic wallArrowGraphic = new ForceArrowGraphic( rampPanel, WALL, ralf.getWallForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public Vector2D getForce() {
                return rampPhysicalModel.getWallForce();
            }
        }, getBlockGraphic() );

        addForceArrowGraphic( gravityArrowGraphic );
        addForceArrowGraphic( normalArrowGraphic );

        addForceArrowGraphic( frictionArrowGraphic );
        addForceArrowGraphic( forceArrowGraphic );

        addForceArrowGraphic( wallArrowGraphic );

        addForceArrowGraphic( totalArrowGraphic );
        //setIgnoreMouse( true );
    }

}
