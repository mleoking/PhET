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
public class YArrowSet extends AbstractArrowSet {

    public YArrowSet( final RampPanel component ) {
        super( component );
        RampLookAndFeel ralf = new RampLookAndFeel();
        String sub = "y";
        final RampModel rampModel = component.getRampModule().getRampModel();
        ForceArrowGraphic forceArrowGraphic = new ForceArrowGraphic( component, "Applied Force", ralf.getAppliedForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector appliedForce = rampModel.getAppliedForce();
                return appliedForce.toYVector();
            }
        }, component.getBlockGraphic(), sub );

        ForceArrowGraphic totalArrowGraphic = new ForceArrowGraphic( component, "Total Force", ralf.getNetForceColor(), 45, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getTotalForce();
                return totalForce.toYVector();
            }
        }, component.getBlockGraphic(), sub );

        ForceArrowGraphic frictionArrowGraphic = new ForceArrowGraphic( component, "Friction Force", ralf.getFrictionForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getFrictionForce();
                return totalForce.toYVector();
            }
        }, component.getBlockGraphic(), sub );

        ForceArrowGraphic gravityArrowGraphic = new ForceArrowGraphic( component, "Gravitational Force", ralf.getGravityParallelColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getGravityForce();
                return totalForce.toYVector();
            }
        }, component.getBlockGraphic(), sub );

        ForceArrowGraphic normalArrowGraphic = new ForceArrowGraphic( component, "Normal Force", ralf.getNormalColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getNormalForce();
                return totalForce.toYVector();
            }
        }, component.getBlockGraphic(), sub );
        addForceArrowGraphic( forceArrowGraphic );
        addForceArrowGraphic( totalArrowGraphic );
        addForceArrowGraphic( frictionArrowGraphic );
        addForceArrowGraphic( gravityArrowGraphic );
        addForceArrowGraphic( normalArrowGraphic );
        setIgnoreMouse( true );
    }

}
