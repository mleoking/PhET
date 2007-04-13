/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor_semi.common;

import edu.colorado.phet.common_semiconductor.math.PhetVector;
import edu.colorado.phet.common_semiconductor.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 1:54:53 PM
 * Copyright (c) Jan 18, 2004 by Sam Reid
 */
public class ParticleGraphic extends TransformGraphic {
    protected Particle particle;
    private BufferedImage image;
    protected SimpleBufferedImageGraphic graphic;

    public ParticleGraphic( Particle particle, ModelViewTransform2D transform, BufferedImage image ) {
        super( transform );
        this.particle = particle;
        this.image = image;
        this.graphic = new SimpleBufferedImageGraphic( image );
        update();
    }

    public BufferedImage getImage() {
        return image;
    }

    public void paint( Graphics2D graphics2D ) {
        graphic.paint( graphics2D );
    }

    public void update() {
        PhetVector modelLoc = particle.getPosition();
        Point pt = getTransform().modelToView( modelLoc );
        graphic.setPosition( pt );
    }
}
