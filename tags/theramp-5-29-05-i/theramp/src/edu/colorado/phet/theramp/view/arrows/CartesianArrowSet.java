/** Sam Reid*/
package edu.colorado.phet.theramp.view.arrows;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.theramp.view.RampPanel;


/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 11:29:31 AM
 * Copyright (c) Nov 27, 2004 by Sam Reid
 */
public class CartesianArrowSet extends AbstractArrowSet {

    public CartesianArrowSet( final RampPanel component ) {
        super( component );
        RampLookAndFeel ralf = new RampLookAndFeel();

        final RampModel rampModel = component.getRampModule().getRampModel();
        ForceArrowGraphic forceArrowGraphic = new ForceArrowGraphic( component, AbstractArrowSet.APPLIED, ralf.getAppliedForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector appliedForce = rampModel.getAppliedForce();
                return appliedForce;
            }
        }, component.getBlockGraphic() );

        ForceArrowGraphic totalArrowGraphic = new ForceArrowGraphic( component, TOTAL, ralf.getNetForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getTotalForce();
                return totalForce;
            }
        }, component.getBlockGraphic() );

        ForceArrowGraphic frictionArrowGraphic = new ForceArrowGraphic( component, FRICTION, ralf.getFrictionForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getFrictionForce();
                return totalForce;
            }
        }, component.getBlockGraphic() );

        ForceArrowGraphic gravityArrowGraphic = new ForceArrowGraphic( component, WEIGHT, ralf.getGravityParallelColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getGravityForce();
                return totalForce;
            }
        }, component.getBlockGraphic() );

        ForceArrowGraphic normalArrowGraphic = new ForceArrowGraphic( component, NORMAL, ralf.getNormalColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                return rampModel.getNormalForce();
            }
        }, component.getBlockGraphic() );

        ForceArrowGraphic wallArrowGraphic = new ForceArrowGraphic( component, WALL, ralf.getWallForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public Vector2D getForce() {
                return rampModel.getWallForce();
            }
        }, component.getBlockGraphic() );

        addForceArrowGraphic( gravityArrowGraphic );
        addForceArrowGraphic( normalArrowGraphic );

        addForceArrowGraphic( frictionArrowGraphic );
        addForceArrowGraphic( forceArrowGraphic );

        addForceArrowGraphic( wallArrowGraphic );

        addForceArrowGraphic( totalArrowGraphic );
        setIgnoreMouse( true );
    }

}
