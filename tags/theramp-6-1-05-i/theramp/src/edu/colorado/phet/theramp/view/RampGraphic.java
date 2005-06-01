/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.theramp.model.Surface;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:17:00 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampGraphic extends SurfaceGraphic {
    private PhetGraphic arrowGraphic;

    public RampGraphic( RampPanel rampPanel, Surface ramp ) {
        super( rampPanel, ramp );
        arrowGraphic = createArrowGraphic();
        addGraphic( arrowGraphic );

        getSurfaceGraphic().addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseMotionListener
            public void mouseDragged( MouseEvent e ) {
                System.out.println( "RampGraphic.mouseDragged" );
                arrowGraphic.setVisible( false );
            }
        } );
        updateArrowGraphic();
    }

    private void updateArrowGraphic() {
        Point pt = getViewLocation( getSurface().getLocation( getSurface().getLength() * 0.8 ) );
        arrowGraphic.setLocation( pt.x - arrowGraphic.getWidth() / 2, pt.y - arrowGraphic.getHeight() / 2 );
    }

    private PhetGraphic createArrowGraphic() {
        String imageResourceName = "images/arrow-2.png";
        BufferedImage image = null;
        try {
            image = ImageLoader.loadBufferedImage( imageResourceName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        image = BufferedImageUtils.rescaleYMaintainAspectRatio( getComponent(), image, 100 );
        PhetImageGraphic phetImageGraphic = new PhetImageGraphic( getComponent(), image );
        phetImageGraphic.setIgnoreMouse( true );
        return phetImageGraphic;
    }
}