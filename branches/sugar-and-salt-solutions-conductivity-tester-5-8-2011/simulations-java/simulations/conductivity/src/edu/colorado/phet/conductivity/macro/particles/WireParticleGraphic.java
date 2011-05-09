// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.conductivity.macro.particles;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.conductivity.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.conductivity.common.TransformGraphic;

public class WireParticleGraphic extends TransformGraphic {

    public WireParticleGraphic( WireParticle wireparticle, ModelViewTransform2D modelviewtransform2d, BufferedImage bufferedimage ) {
        super( modelviewtransform2d );
        particle = wireparticle;
        imageGraphic = new SimpleBufferedImageGraphic( bufferedimage );
        wireparticle.addObserver( new SimpleObserver() {

            public void update() {
                WireParticleGraphic.this.update();
            }

        } );
    }

    public void update() {
    }

    public void paint( Graphics2D graphics2d ) {
        ImmutableVector2D phetvector = particle.getPosition();
        java.awt.Point point = getTransform().modelToView( new Point2D.Double( phetvector.getX(), phetvector.getY() ) );
        imageGraphic.setPosition( point );
        imageGraphic.paint( graphics2d );
    }

    WireParticle particle;
    SimpleBufferedImageGraphic imageGraphic;
}
