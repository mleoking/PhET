/*  */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.theramp.model.Surface;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:17:00 AM
 */

public class RampGraphic extends SurfaceGraphic {
    private PNode arrowGraphic;

    public RampGraphic( RampPanel rampPanel, Surface ramp ) {
        super( rampPanel, ramp );
        arrowGraphic = createArrowGraphic();
        addChild( arrowGraphic );

        getSurfaceGraphic().addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                arrowGraphic.setVisible( false );
            }
        } );
        getSurface().addObserver( new SimpleObserver() {
            public void update() {
                arrowGraphic.setVisible( false );
            }
        } );

        updateArrowGraphic();
    }

    private void updateArrowGraphic() {
        Point pt = getViewLocation( getSurface().getLocation( getSurface().getLength() * 0.8 ) );
        arrowGraphic.setOffset( pt.x - arrowGraphic.getWidth() / 2, pt.y - arrowGraphic.getHeight() / 2 );
    }

    private PNode createArrowGraphic() {
        String imageResourceName = "the-ramp/images/arrow-2.gif";
        BufferedImage image = null;
        try {
            image = ImageLoader.loadBufferedImage( imageResourceName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        image = BufferedImageUtils.rescaleYMaintainAspectRatio( image, 100 );
        PImage phetImageGraphic = new PImage( image );
        //phetImageGraphic.setIgnoreMouse( true );
        phetImageGraphic.setPickable( false );
        phetImageGraphic.setChildrenPickable( false );
        return phetImageGraphic;
    }
}