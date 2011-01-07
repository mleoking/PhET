// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.semiconductor.macro.circuit.particles;

import java.awt.*;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor.common.TransformGraphic;


/**
 * User: Sam Reid
 * Date: Jan 16, 2004
 * Time: 12:55:13 AM
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
//        Vector2D.Double modelLoc=particle.getPosition();
//        Point pt=getTransform().modelToView(modelLoc);
//        particle.
    }

    public void paint( Graphics2D graphics2D ) {
        ImmutableVector2D modelLoc = particle.getPosition();
        if ( modelLoc != null ) {
            Point pt = getTransform().modelToView( modelLoc );
            imageGraphic.setPosition( pt );
            imageGraphic.paint( graphics2D );
        }
    }
}
