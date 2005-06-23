/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:06:32 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class DetectorSheet extends GraphicLayerSet {
    private int width;
    private int height;
    public PhetShapeGraphic phetShapeGraphic;
    public BufferedImage bufferedImage;
    public PhetImageGraphic graphic;

    public DetectorSheet( Component c, int width, int height ) {
        super( c );

        bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        graphic = new PhetImageGraphic( getComponent(), bufferedImage );
        addGraphic( graphic );

        phetShapeGraphic = new PhetShapeGraphic( c, new Rectangle( width, height ), Color.white, new BasicStroke( 1 ), Color.black );
//        addGraphic( phetShapeGraphic );
        phetShapeGraphic.paint( bufferedImage.createGraphics() );

        RenderingHints renderingHints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        setRenderingHints( renderingHints );

        this.width = width;
        this.height = height;
    }

    public void addDetectionEvent( int x, int y ) {
//        addGraphic( new DetectionGraphic( this, x, y ) );
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        new DetectionGraphic( this, x, y ).paint( g2 );
        repaint();
    }

    public void reset() {
        clear();
        bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        phetShapeGraphic.paint( bufferedImage.createGraphics() );
        graphic.setImage( bufferedImage );
        addGraphic( graphic );
    }
}
