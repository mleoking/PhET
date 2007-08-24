package edu.colorado.phet.semiconductor_semi.macro.energy;

import edu.colorado.phet.common_semiconductor.math.PhetVector;
import edu.colorado.phet.common_semiconductor.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor_semi.common.ParticleGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

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
