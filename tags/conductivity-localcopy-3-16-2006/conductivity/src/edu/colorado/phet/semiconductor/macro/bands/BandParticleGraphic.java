// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.bands;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.semiconductor.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor.common.TransformGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            BandParticle

public class BandParticleGraphic extends TransformGraphic {

    public BandParticleGraphic( BandParticle bandparticle, ModelViewTransform2D modelviewtransform2d, BufferedImage bufferedimage ) {
        super( modelviewtransform2d );
        bandParticle = bandparticle;
        graphic = new SimpleBufferedImageGraphic( bufferedimage );
        modelviewtransform2d.addTransformListener( new TransformListener() {

            public void transformChanged( ModelViewTransform2D modelviewtransform2d1 ) {
                update();
            }

        } );
    }

    public void paint( Graphics2D graphics2d ) {
        edu.colorado.phet.common.math.PhetVector phetvector = bandParticle.getPosition();
        java.awt.Point point = getTransform().modelToView( phetvector );
        graphic.setPosition( point );
        graphic.paint( graphics2d );
    }

    public void update() {
    }

    BandParticle bandParticle;
    SimpleBufferedImageGraphic graphic;
}
