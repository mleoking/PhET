// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.conductivity.macro.bands;

import java.awt.*;
import java.awt.image.BufferedImage;


import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;
import edu.colorado.phet.conductivity.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.conductivity.common.TransformGraphic;

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
        Vector2D phetvector = bandParticle.getPosition();
        java.awt.Point point = getTransform().modelToView( phetvector );
        graphic.setPosition( point );
        graphic.paint( graphics2d );
    }

    public void update() {
    }

    BandParticle bandParticle;
    SimpleBufferedImageGraphic graphic;
}
