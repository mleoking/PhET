package edu.colorado.phet.semiconductor_semi.macro.circuit.particles;

import edu.colorado.phet.common_semiconductor.math.PhetVector;
import edu.colorado.phet.common_semiconductor.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common_semiconductor.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor_semi.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor_semi.common.TransformGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 16, 2004
 * Time: 12:55:13 AM
 *
 */
public class WireParticleGraphic extends TransformGraphic {
    WireParticle particle;
    SimpleBufferedImageGraphic imageGraphic;

    public WireParticleGraphic( WireParticle particle, ModelViewTransform2D transform, BufferedImage image ) {
        super( transform );
        this.particle = particle;
        this.imageGraphic = new SimpleBufferedImageGraphic( image );
        particle.addObserver( new SimpleObserver() {
            public void update() {
                WireParticleGraphic.this.update();
            }
        } );
    }

    public void update() {
//        PhetVector modelLoc=particle.getPosition();
//        Point pt=getTransform().modelToView(modelLoc);
//        particle.
    }

    public void paint( Graphics2D graphics2D ) {
        PhetVector modelLoc = particle.getPosition();
        if( modelLoc != null ) {
            Point pt = getTransform().modelToView( modelLoc );
            imageGraphic.setPosition( pt );
            imageGraphic.paint( graphics2D );
        }
    }
}
