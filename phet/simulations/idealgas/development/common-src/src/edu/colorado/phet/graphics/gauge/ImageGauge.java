package edu.colorado.phet.graphics.gauge;

import edu.colorado.phet.physics.body.PhysicalEntity;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Observable;

public class ImageGauge extends AbstractGauge {
    float  min;
    float  max;
    float  amount;
    float  length;
    BufferedImage image;
    int x;
    int y;
    int lineX;
    int lineY;

    public void setNumMaj( int num ) {
    }

    public void setNumMin( int num ) {
    }

    public ImageGauge( BufferedImage image, int x, int y, int lineX, int lineY ) {
        this( -1, 1, 0, 40, image, x, y, lineX, lineY );
    }

    public ImageGauge( float  min, float  max,
                       float  amount, float  length,
                       BufferedImage image, int x, int y, int lineX, int lineY ) {
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

    public void setMax( float  max ) {
        this.max = max;
    }

    public void setMin( float  min ) {
        this.min = min;
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
    }

    public void setValue( float  amount ) {
        this.amount = amount;
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( image, AffineTransform.getTranslateInstance( x, y ) );
        float  theta = amount / ( max - min ) * (float)Math.PI;
        //util.Debug.traceln("theta="+theta);
        float  xD = length * (float)Math.sin( theta );
        float  yD = length * (float)Math.cos( theta );
        //util.Debug.traceln("XD="+xD);
        //util.Debug.traceln("yD="+yD);
        int endX = x + (int)xD + lineX;
        int endY = y - (int)yD + lineY;
        //util.Debug.traceln(helper.ArraysHelper.toString(new int[]{x,y,endX,endY}));
        g.setColor( new Color( 200, 0, 180 ) );
        g.setStroke( new BasicStroke( 5 ) );
        int dotWidth = 8;
        int dotHeight = 8;
        g.drawLine( x + lineX, y + lineY, endX, endY - dotHeight / 2 );

        g.setColor( Color.blue );
        g.fillOval( x + lineX - dotWidth / 2, y + lineY - dotHeight / 2, dotWidth, dotHeight );
    }

    //
    // Abstract methods
    //
    protected void setPosition( PhysicalEntity body ) {
    }

    public void update( Observable observable, Object o ) {
    }
}
