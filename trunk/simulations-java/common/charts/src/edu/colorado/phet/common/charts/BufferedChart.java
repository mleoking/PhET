/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.charts;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.util.BasicGraphicsSetup;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 6, 2005
 * Time: 9:33:16 PM
 * Copyright (c) Apr 6, 2005 by Sam Reid
 */

public class BufferedChart extends PhetImageGraphic {
    private Chart chart;
    private ModelViewTransform2D transform2D;

    public BufferedChart( Component component, Chart chart ) {
        super( component );
        this.chart = chart;
        BufferedImage bufferedImage = createBuffer( chart, new BasicGraphicsSetup(),
                                                    BufferedImage.TYPE_INT_RGB, component.getBackground() );
        setImage( bufferedImage );
        fixTransform();
    }

    private void fixTransform() {
        transform2D = new ModelViewTransform2D( chart.getModelViewTransform().getModelBounds(), getChartArea() );
    }


    public ModelViewTransform2D getTransform2D() {
        return transform2D;
    }

    private BufferedImage createBuffer( Chart chart, BasicGraphicsSetup setup, int imageType, Color background ) {
        Rectangle bounds = chart.getBounds();
        BufferedImage im = new BufferedImage( bounds.width, bounds.height, imageType );
        Graphics2D g2 = im.createGraphics();
        setup.setup( g2 );
        g2.setPaint( background );
        g2.translate( -bounds.x, -bounds.y );
        g2.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
        chart.paint( g2 );
        return im;
    }

    public Point toBufferCoordinates( Point2D modelPoint ) {
        return transform2D.modelToView( modelPoint );
    }

    public Graphics2D createGraphics() {
        return super.getImage().createGraphics();
    }

    private Point getChartPosition() {
        return new Point( -chart.getBounds().x, -chart.getBounds().y );
    }

    public Rectangle getChartArea() {
        return new Rectangle( getChartPosition(), chart.getChartSize() );
    }

}
