// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.conductivity.view.graphics.Graphic;
import edu.colorado.phet.common.conductivity.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.conductivity.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.conductivity.common.SimpleBufferedImageGraphic;

// Referenced classes of package edu.colorado.phet.semiconductor.flashlight:
//            Flashlight

public class FlashlightGraphic implements Graphic {

    private Flashlight light;
    private BufferedImage lightImage;
    private ModelViewTransform2D transform;
    private SimpleBufferedImageGraphic imageGraphic;
    private boolean visible;

    public FlashlightGraphic( Flashlight flashlight, BufferedImage bufferedimage, ModelViewTransform2D modelviewtransform2d ) {
        light = flashlight;
        lightImage = bufferedimage;
        transform = modelviewtransform2d;
        imageGraphic = new SimpleBufferedImageGraphic( bufferedimage );
        modelviewtransform2d.addTransformListener( new TransformListener() {

            public void transformChanged( ModelViewTransform2D modelviewtransform2d1 ) {
                doUpdate();
            }

        } );
        flashlight.addObserver( new SimpleObserver() {
            public void update() {
                doUpdate();
            }
        } );
    }

    private void doUpdate() {
        java.awt.Point point = transform.modelToView( light.getPosition() );
        imageGraphic.setPosition( point );
        AffineTransform affinetransform = imageGraphic.getTransform();
        double d = light.getAngle();
        affinetransform.rotate( d, lightImage.getWidth() / 2, lightImage.getHeight() / 2 );
        imageGraphic.setTransform( affinetransform );
    }

    public void paint( Graphics2D graphics2d ) {
        if ( visible ) {
            imageGraphic.paint( graphics2d );
        }
    }

    public void setVisible( boolean flag ) {
        visible = flag;
    }

}
