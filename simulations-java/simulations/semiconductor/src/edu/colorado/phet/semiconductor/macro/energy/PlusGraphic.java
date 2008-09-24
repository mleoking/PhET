package edu.colorado.phet.semiconductor.macro.energy;

import java.awt.*;
import java.awt.image.BufferedImage;

import edu.colorado.phet.semiconductor.common.ParticleGraphic;
import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
 * User: Sam Reid
 * Date: Feb 9, 2004
 * Time: 12:26:33 PM
 */
public class PlusGraphic extends ParticleGraphic {
    private PlusCharge plusCharge;

    public PlusGraphic( PlusCharge particle, ModelViewTransform2D transform, BufferedImage image ) {
        super( particle, transform, image );
        this.plusCharge = particle;
    }

    public void update() {
        PhetVector modelLoc = particle.getPosition();
        Point pt = getTransform().modelToView( modelLoc );
        pt = new Point( pt.x + getImage().getWidth(), pt.y );
        graphic.setPosition( pt );
    }

    public PlusCharge getPlusCharge() {
        return plusCharge;
    }
}
