/** Sam Reid*/
package edu.colorado.phet.theramp.view.arrows;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.view.BlockGraphic;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.theramp.view.RampPanel;


/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 11:29:31 AM
 * Copyright (c) Nov 27, 2004 by Sam Reid
 */
public class ParallelArrowSet extends AbstractArrowSet {

    public ParallelArrowSet( final RampPanel component, BlockGraphic blockGraphic ) {
        super( component, blockGraphic );
        RampLookAndFeel ralf = new RampLookAndFeel();
        final RampModel rampModel = component.getRampModule().getRampModel();
        String sub = "||";
        ForceArrowGraphic forceArrowGraphic = new ForceArrowGraphic( component, APPLIED, ralf.getAppliedForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector appliedForce = rampModel.getAppliedForce();
                return appliedForce.toParallelVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic totalArrowGraphic = new ForceArrowGraphic( component, TOTAL, ralf.getNetForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getTotalForce();
                return totalForce.toParallelVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic frictionArrowGraphic = new ForceArrowGraphic( component, FRICTION, ralf.getFrictionForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getFrictionForce();
                return totalForce.toParallelVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic gravityArrowGraphic = new ForceArrowGraphic( component, WEIGHT, ralf.getWeightColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getGravityForce();
                return totalForce.toParallelVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic normalArrowGraphic = new ForceArrowGraphic( component, NORMAL, ralf.getNormalColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getNormalForce();
                return totalForce.toParallelVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic wallArrowGraphic = new ForceArrowGraphic( component, WALL, ralf.getWallForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getWallForce();
                return totalForce.toParallelVector();
            }
        }, getBlockGraphic(), sub );

        addForceArrowGraphic( gravityArrowGraphic );
        addForceArrowGraphic( normalArrowGraphic );

        addForceArrowGraphic( frictionArrowGraphic );
        addForceArrowGraphic( forceArrowGraphic );
        addForceArrowGraphic( wallArrowGraphic );

        addForceArrowGraphic( totalArrowGraphic );

        setIgnoreMouse( true );
    }

}
