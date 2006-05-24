/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.phetcommon;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.nodes.ConnectorGraphic;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jan 23, 2006
 * Time: 9:56:53 AM
 * Copyright (c) Jan 23, 2006 by Sam Reid
 */

public class VerticalConnector extends ConnectorGraphic {
    int width = 20;

    public VerticalConnector( PNode src, PNode dst ) {
        super( src, dst );
        BufferedImage txtr = null;
        try {
            txtr = ImageLoader.loadBufferedImage( "images/wire.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        setTexture( txtr );
    }

    protected void updateShape( Point2D r1c, Point2D r2c ) {
        double yMin = Math.min( r1c.getY(), r2c.getY() );
        double yMax = Math.max( r1c.getY(), r2c.getY() );
        double height = yMax - yMin;
        Rectangle2D.Double rect = new Rectangle2D.Double( r1c.getX(), yMin, width, height );
        super.setPathTo( rect );
    }

    public void setConnectorWidth( int width ) {
        this.width = width;
    }
}
