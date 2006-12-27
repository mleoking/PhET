/** Sam Reid*/
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.theramp.model.RampModel;


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
        ForceArrowGraphic forceArrowGraphic = new ForceArrowGraphic( component, "Applied Force", ralf.getAppliedForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector appliedForce = rampModel.getAppliedForce();
                return appliedForce;
            }
        }, component.getBlockGraphic() );

        ForceArrowGraphic totalArrowGraphic = new ForceArrowGraphic( component, "Total Force", ralf.getNetForceColor(), 45, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getTotalForce();
                return totalForce;
            }
        }, component.getBlockGraphic() );

        ForceArrowGraphic frictionArrowGraphic = new ForceArrowGraphic( component, "Friction Force", ralf.getFrictionForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getFrictionForce();
                return totalForce;
            }
        }, component.getBlockGraphic() );

        ForceArrowGraphic gravityArrowGraphic = new ForceArrowGraphic( component, "Gravitational Force", ralf.getGravityParallelColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getGravityForce();
                return totalForce;
            }
        }, component.getBlockGraphic() );

        ForceArrowGraphic normalArrowGraphic = new ForceArrowGraphic( component, "Normal Force", ralf.getNormalColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                return rampModel.getNormalForce();
            }
        }, component.getBlockGraphic() );
        addForceArrowGraphic( forceArrowGraphic );
        addForceArrowGraphic( totalArrowGraphic );
        addForceArrowGraphic( frictionArrowGraphic );
        addForceArrowGraphic( gravityArrowGraphic );
        addForceArrowGraphic( normalArrowGraphic );
        setIgnoreMouse( true );
    }

}
