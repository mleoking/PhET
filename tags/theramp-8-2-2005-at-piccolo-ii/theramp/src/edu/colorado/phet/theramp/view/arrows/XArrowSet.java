/** Sam Reid*/
package edu.colorado.phet.theramp.view.arrows;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.view.BlockGraphic;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.theramp.view.RampPanel;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 11:29:31 AM
 * Copyright (c) Nov 27, 2004 by Sam Reid
 */
public class XArrowSet extends AbstractArrowSet {

    public XArrowSet( final RampPanel component, BlockGraphic blockGraphic ) {
        super( component, blockGraphic );
        RampLookAndFeel ralf = new RampLookAndFeel();
        String sub = "x";
        final RampModel rampModel = component.getRampModule().getRampModel();
        ForceArrowGraphic forceArrowGraphic = new ForceArrowGraphic( component, APPLIED, ralf.getAppliedForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector appliedForce = rampModel.getAppliedForce();
                return appliedForce.toXVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic totalArrowGraphic = new ForceArrowGraphic( component, TOTAL, ralf.getNetForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getTotalForce();
                return totalForce.toXVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic frictionArrowGraphic = new ForceArrowGraphic( component, FRICTION, ralf.getFrictionForceColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getFrictionForce();
                return totalForce.toXVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic gravityArrowGraphic = new ForceArrowGraphic( component, WEIGHT, ralf.getWeightColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getGravityForce();
                return totalForce.toXVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic normalArrowGraphic = new ForceArrowGraphic( component, NORMAL, ralf.getNormalColor(), 0, new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getNormalForce();
                return totalForce.toXVector();
            }
        }, getBlockGraphic(), sub );
        ForceArrowGraphic wallArrowGraphic = new ForceArrowGraphic( component, WALL, ralf.getWallForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public Vector2D getForce() {
                RampModel.ForceVector totalForce = rampModel.getWallForce();
                return totalForce.toXVector();
            }
        }, getBlockGraphic(), sub );
        addForceArrowGraphic( gravityArrowGraphic );
        addForceArrowGraphic( normalArrowGraphic );

        addForceArrowGraphic( frictionArrowGraphic );
        addForceArrowGraphic( forceArrowGraphic );
        addForceArrowGraphic( wallArrowGraphic );

        addForceArrowGraphic( totalArrowGraphic );

        gravityArrowGraphic.setPaint( createXPaint( gravityArrowGraphic ) );
        normalArrowGraphic.setPaint( createXPaint( normalArrowGraphic ) );
        frictionArrowGraphic.setPaint( createXPaint( frictionArrowGraphic ) );
        forceArrowGraphic.setPaint( createXPaint( forceArrowGraphic ) );
        wallArrowGraphic.setPaint( createXPaint( wallArrowGraphic ) );
        totalArrowGraphic.setPaint( createXPaint( totalArrowGraphic ) );
        //setIgnoreMouse( true );
    }

    private Paint createXPaint( ForceArrowGraphic arrowGraphic ) {
        BufferedImage texture = new BufferedImage( 10, 10, BufferedImage.TYPE_INT_ARGB );
        Graphics2D graphics2D = texture.createGraphics();
        graphics2D.setColor( new Color( 255, 255, 255, 0 ) );
        graphics2D.fillRect( 0, 0, 10, 10 );
        graphics2D.setStroke( new BasicStroke( 5 ) );
        graphics2D.setColor( arrowGraphic.getBaseColor() );
        graphics2D.drawLine( 0, 5, 10, 5 );
        TexturePaint texturePaint = new TexturePaint( texture, new Rectangle2D.Double( 0, 0, texture.getWidth(), texture.getHeight() ) );
        return texturePaint;
    }
}
