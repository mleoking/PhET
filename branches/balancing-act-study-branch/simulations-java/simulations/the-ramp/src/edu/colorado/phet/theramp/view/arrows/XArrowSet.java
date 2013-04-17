// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.theramp.view.arrows;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.view.BlockGraphic;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.theramp.view.RampPanel;


/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 11:29:31 AM
 */
public class XArrowSet extends AbstractArrowSet {

    public XArrowSet( final RampPanel component, BlockGraphic blockGraphic ) {
        super( component, blockGraphic );
        RampLookAndFeel ralf = new RampLookAndFeel();
        String sub = TheRampStrings.getString( "coordinates.x" );
        final RampPhysicalModel rampPhysicalModel = component.getRampModule().getRampPhysicalModel();
        ForceArrowGraphic forceArrowGraphic = new ForceArrowGraphic( component, APPLIED, ralf.getAppliedForceColor(), 0, new ForceComponent() {
            public MutableVector2D getForce() {
                RampPhysicalModel.ForceVector appliedForce = rampPhysicalModel.getAppliedForce();
                return appliedForce.toXVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic totalArrowGraphic = new ForceArrowGraphic( component, TOTAL, ralf.getNetForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public MutableVector2D getForce() {
                RampPhysicalModel.ForceVector totalForce = rampPhysicalModel.getTotalForce();
                return totalForce.toXVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic frictionArrowGraphic = new ForceArrowGraphic( component, FRICTION, ralf.getFrictionForceColor(), 0, new ForceComponent() {
            public MutableVector2D getForce() {
                RampPhysicalModel.ForceVector totalForce = rampPhysicalModel.getFrictionForce();
                return totalForce.toXVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic gravityArrowGraphic = new ForceArrowGraphic( component, WEIGHT, ralf.getWeightColor(), 0, new ForceComponent() {
            public MutableVector2D getForce() {
                RampPhysicalModel.ForceVector totalForce = rampPhysicalModel.getGravityForce();
                return totalForce.toXVector();
            }
        }, getBlockGraphic(), sub );

        ForceArrowGraphic normalArrowGraphic = new ForceArrowGraphic( component, NORMAL, ralf.getNormalColor(), 0, new ForceComponent() {
            public MutableVector2D getForce() {
                RampPhysicalModel.ForceVector totalForce = rampPhysicalModel.getNormalForce();
                return totalForce.toXVector();
            }
        }, getBlockGraphic(), sub );
        ForceArrowGraphic wallArrowGraphic = new ForceArrowGraphic( component, WALL, ralf.getWallForceColor(), getDefaultOffsetDY(), new ForceComponent() {
            public MutableVector2D getForce() {
                RampPhysicalModel.ForceVector totalForce = rampPhysicalModel.getWallForce();
                return totalForce.toXVector();
            }
        }, getBlockGraphic(), sub );
        addForceArrowGraphic( gravityArrowGraphic );
        addForceArrowGraphic( normalArrowGraphic );
        addForceArrowGraphic( frictionArrowGraphic );
        addForceArrowGraphic( forceArrowGraphic );
        addForceArrowGraphic( wallArrowGraphic );
        addForceArrowGraphic( totalArrowGraphic );

        setPickable( false );
        setChildrenPickable( false );
    }

    private Paint createXPaint( ForceArrowGraphic arrowGraphic ) {
        int imageWidth = 6;
        int imageHeight = imageWidth;
        BufferedImage texture = new BufferedImage( imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB );//todo could fail for mac.
        Graphics2D graphics2D = texture.createGraphics();
        Color background = new Color( 255, 255, 255 );

        graphics2D.setColor( background );
        graphics2D.fillRect( 0, 0, imageWidth, imageHeight );

        graphics2D.setColor( arrowGraphic.getBaseColor() );
        int stripeSize = 2;
        graphics2D.fillRect( 0, 0, stripeSize, imageHeight );
        return new TexturePaint( texture, new Rectangle2D.Double( 0, 0, texture.getWidth(), texture.getHeight() ) );
    }

    protected void addForceArrowGraphic( ForceArrowGraphic forceArrowGraphic ) {
        super.addForceArrowGraphic( forceArrowGraphic );
        forceArrowGraphic.setPaint( createXPaint( forceArrowGraphic ) );
    }

}
