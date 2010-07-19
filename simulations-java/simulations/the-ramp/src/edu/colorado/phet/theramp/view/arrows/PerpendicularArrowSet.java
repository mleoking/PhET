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
public class PerpendicularArrowSet extends AbstractArrowSet {

    public PerpendicularArrowSet( final RampPanel component, BlockGraphic blockGraphic ) {
        super( component, blockGraphic );
        RampLookAndFeel ralf = new RampLookAndFeel();
//        String sub="perp";
        String sub = "<U>|</U>";
        final RampPhysicalModel rampPhysicalModel = component.getRampModule().getRampPhysicalModel();
        ForceArrowGraphic forceArrowGraphic = new ForceArrowGraphic( component, APPLIED, ralf.getAppliedForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampPhysicalModel.ForceVector appliedForce = rampPhysicalModel.getAppliedForce();
                return appliedForce.toPerpendicularVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic totalArrowGraphic = new ForceArrowGraphic( component, TOTAL, ralf.getNetForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public Vector2D getForce() {
                RampPhysicalModel.ForceVector totalForce = rampPhysicalModel.getTotalForce();
                return totalForce.toPerpendicularVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic frictionArrowGraphic = new ForceArrowGraphic( component, FRICTION, ralf.getFrictionForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampPhysicalModel.ForceVector totalForce = rampPhysicalModel.getFrictionForce();
                return totalForce.toPerpendicularVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic gravityArrowGraphic = new ForceArrowGraphic( component, WEIGHT, ralf.getWeightColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampPhysicalModel.ForceVector totalForce = rampPhysicalModel.getGravityForce();
                return totalForce.toPerpendicularVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic normalArrowGraphic = new ForceArrowGraphic( component, NORMAL, ralf.getNormalColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampPhysicalModel.ForceVector totalForce = rampPhysicalModel.getNormalForce();
                return totalForce.toPerpendicularVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic wallArrowGraphic = new ForceArrowGraphic( component, WALL, ralf.getWallForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public Vector2D getForce() {
                RampPhysicalModel.ForceVector totalForce = rampPhysicalModel.getWallForce();
                return totalForce.toPerpendicularVector();
            }
        }, getBlockGraphic(), sub );

        addForceArrowGraphic( gravityArrowGraphic );
        addForceArrowGraphic( normalArrowGraphic );

        addForceArrowGraphic( frictionArrowGraphic );
        addForceArrowGraphic( forceArrowGraphic );
        addForceArrowGraphic( wallArrowGraphic );

        addForceArrowGraphic( totalArrowGraphic );
        //setIgnoreMouse( true );
    }

}
