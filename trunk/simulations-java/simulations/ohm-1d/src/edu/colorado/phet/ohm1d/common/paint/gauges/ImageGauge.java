package edu.colorado.phet.ohm1d.common.paint.gauges;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageGauge implements IGauge {
    double min;
    double max;
    double amount;
    double length;
    BufferedImage image;
    int x;
    int y;
    int lineX;
    int lineY;

    public ImageGauge( BufferedImage image, int x, int y, int lineX, int lineY, double length ) {
        this( -1, 1, 0, length, image, x, y, lineX, lineY );
    }

    public ImageGauge( double min, double max, double amount, double length, BufferedImage image, int x, int y, int lineX, int lineY ) {
        this.min = min;
        this.max = max;
        this.amount = amount;
        this.length = length;
        this.image = image;
        this.x = x;
        this.y = y;
        this.lineX = lineX;
        this.lineY = lineY;
    }

    public void setMax( double max ) {
        this.max = max;
    }

    public void setMin( double min ) {
        this.min = min;
    }

    public void setValue( double amount ) {
        this.amount = amount;
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( image, AffineTransform.getTranslateInstance( x, y ) );
        double theta = amount / ( max - min ) * Math.PI;
        //util.Debug.traceln("theta="+theta);
        double xD = length * Math.sin( theta );
        double yD = length * Math.cos( theta );
        //util.Debug.traceln("XD="+xD);
        //util.Debug.traceln("yD="+yD);
        int endX = x + (int) xD + lineX;
        int endY = y - (int) yD + lineY;
        //util.Debug.traceln(helper.ArraysHelper.toString(new int[]{x,y,endX,endY}));
        g.setColor( new Color( 240, 20, 120 ) );
        g.setStroke( new BasicStroke( 5 ) );
        int dotWidth = 8;
        int dotHeight = 8;
        g.drawLine( x + lineX, y + lineY, endX, endY - dotHeight / 2 );

        g.setColor( Color.blue );
        g.fillOval( x + lineX - dotWidth / 2, y + lineY - dotHeight / 2, dotWidth, dotHeight );
    }
}
