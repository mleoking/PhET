/*  */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: May 30, 2005
 * Time: 10:20:33 PM
 */

public abstract class BarrierGraphic extends PNode {
    private RampPanel rampPanel;
    private SurfaceGraphic surfaceGraphic;
    protected PImage imageGraphic;

    public BarrierGraphic( Component component, RampPanel rampPanel, SurfaceGraphic surfaceGraphic ) {
        super();
        this.rampPanel = rampPanel;
        this.surfaceGraphic = surfaceGraphic;
        try {
            imageGraphic = new PImage( ImageLoader.loadBufferedImage( "the-ramp/images/barrier2.jpg" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        addChild( imageGraphic );
        surfaceGraphic.getSurface().addObserver( new SimpleObserver() {
            public void update() {
                BarrierGraphic.this.update();
            }
        } );
        update();
    }

    private AffineTransform createTransform( double position, double scaleX, double fracSize ) {
        return surfaceGraphic.createTransform( position, new Dimension( (int)( imageGraphic.getImage().getWidth( null ) * scaleX ), (int)( imageGraphic.getImage().getHeight( null ) * fracSize ) ) );
    }

    private void update() {
//        setAutorepaint( false );
        AffineTransform transform = createTransform( getBarrierPosition(), 1, 1 );
        transform.concatenate( AffineTransform.getTranslateInstance( 0, getYOffset() ) );
        transform.concatenate( AffineTransform.getTranslateInstance( getOffsetX(), 0 ) );
        imageGraphic.setTransform( transform );
//        repaint();
    }

    protected abstract int getOffsetX();

    protected abstract double getBarrierPosition();

    public RampPanel getRampPanel() {
        return rampPanel;
    }

    public SurfaceGraphic getRampGraphic() {
        return surfaceGraphic;
    }
}
