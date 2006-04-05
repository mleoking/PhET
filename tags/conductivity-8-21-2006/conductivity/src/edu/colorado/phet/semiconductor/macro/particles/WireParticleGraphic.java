// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.particles;

import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor.common.TransformGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.particles:
//            WireParticle

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
        edu.colorado.phet.common.math.PhetVector phetvector = particle.getPosition();
        java.awt.Point point = getTransform().modelToView( phetvector );
        imageGraphic.setPosition( point );
        imageGraphic.paint( graphics2d );
    }

    WireParticle particle;
    SimpleBufferedImageGraphic imageGraphic;
}
