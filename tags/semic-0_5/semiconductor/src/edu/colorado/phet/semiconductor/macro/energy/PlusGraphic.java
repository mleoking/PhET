package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.common.ParticleGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Feb 9, 2004
 * Time: 12:26:33 PM
 * Copyright (c) Feb 9, 2004 by Sam Reid
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
