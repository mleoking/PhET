package edu.colorado.phet.graphics.gauges;

import edu.colorado.phet.physics.body.Particle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;


public class Gauge extends IGauge {
    int numMajorTicks;
    int numMinorTicks;
    float  needleLength;
    float  min;
    float  max;
    float  amount;
    int x;
    int y;
    int width;
    int height;
    String text = "Pressure";

    public Gauge( int x, int y, float  min, float  max, float  amount, float  length ) {
        this( x, y, (int)length, (int)length, min, max, amount, length / 2, 5, 9 );
    }

    //
    // Abstract methods
    //

    /**
     * TODO: There should be a different superclass whose position is not bound to the
     * body that it observes
     */
    protected void setPosition( Particle body ) {
    }

    public void update( Observable observable, Object o ) {
    }

    public void setText( String text ) {
        this.text = text;
    }

    public void setNumMaj( int numMajorTicks ) {
        this.numMajorTicks = numMajorTicks;
    }

    public void setNumMin( int numMinorTicks ) {
        this.numMinorTicks = numMinorTicks;
    }

    public Gauge( int x, int y, int width, int height,
                  float  min, float  max,
                  float  amount, float  needleLength,
                  int numMajorTicks, int numMinorTicks ) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.min = min;
        this.max = max;
        this.amount = amount;
        this.needleLength = needleLength;
        this.numMajorTicks = numMajorTicks;
        this.numMinorTicks = numMinorTicks;
    }

    public void setMax( float  max ) {
        this.max = max;
    }

    public void setMin( float  min ) {
        this.min = min;
    }

    public void setValue( float  amount ) {
        this.amount = amount;
    }

    private Point2D.Float getPoint( int length, float  angle ) {
        int i = (int)( length * Math.sin( angle ) + x + width / 2 );
        int j = (int)( length * Math.cos( angle ) + y + height / 2 );
        return new Point2D.Float( i, j );
    }

    public void paint( Graphics2D g ) {
        g.setColor( Color.white );
        g.fillRect( x, y - 10, width, height / 2 + 20 );
        drawText( g );
        drawNeedle( g );
        drawMinorTicks( g );
        drawTicks( g );
        drawArc( g );
        drawButton( g );
    }

    public void drawText( Graphics2D g ) {
        Font f = g.getFont();
        g.setColor( Color.black );
        Point2D.Float needle = getPoint( 10, (float)( Math.PI / 2 + Math.PI / 4 ));
        Rectangle2D rect = f.getStringBounds( text, g.getFontRenderContext() );
        int w = (int)rect.getWidth();
        int startX = x + width / 2 - w / 2;
        int startY = y + height / 3;
        g.drawString( text, startX, startY );
    }

    public void drawButton( Graphics2D g ) {
        g.setColor( Color.blue );
        Point2D.Float p = getPoint( 0, 0 );
        int buttonWidth = 5;
        //g.fillOval(p.x-buttonWidth,p.y-buttonWidth,buttonWidth*2,buttonWidth*2);
        g.fillRect( (int)p.x - buttonWidth,
                    (int)p.y - buttonWidth,
                    buttonWidth * 2,
                    buttonWidth * 2 );
    }

    protected float  getBoundedValue() {
        float  amt = Math.min( amount, max );
        amt = Math.max( amt, min );
        return amt;
    }

    public void drawNeedle( Graphics2D g ) {
        float  range = max - min;
        float  amt = getBoundedValue();
        float  theta = amt / ( max - min ) * (float)Math.PI;
        //util.Debug.traceln("max="+max+", min="+min+", range="+range+", value="+amount+", theta="+theta);

        //util.Debug.traceln("theta="+theta);
        float  xD = needleLength * (float)Math.sin( theta );
        float  yD = needleLength * (float)Math.cos( theta );
        //util.Debug.traceln("XD="+xD);
        //util.Debug.traceln("yD="+yD);

        int needleX = x + width / 2;
        int needleY = y + height / 2;

        int needleEndX = needleX + (int)xD;
        int needleEndY = needleY - (int)yD;
        //util.Debug.traceln(helper.ArraysHelper.toString(new int[]{x,y,endX,endY}));
        if( amt == max || amt == min )
            g.setColor( Color.red );
        else
            g.setColor( new Color( 200, 0, 180 ) );
        g.setStroke( new BasicStroke( 5 ) );
        g.drawLine( needleX, needleY, needleEndX, needleEndY );
    }

    public void drawArc( Graphics2D g ) {
        g.setStroke( new BasicStroke( 5 ) );
        g.setColor( Color.black );
        g.drawArc( x, y, width, height, 0, 180 );
        g.setColor( Color.blue );
        g.drawRect( x, y - 10, width, height / 2 + 20 );
    }

    public void drawTicks( Graphics2D g ) {
        g.setStroke( new BasicStroke( 3 ) );
        float  dTheta = (float)Math.PI / 2 / (float )( numMajorTicks - 1 );
        float  ang = (float)Math.PI;//straight up
        g.setColor( Color.black );
        int tickSize = 10;
        /*Paint the ticks.*/
        for( int i = 0; i < numMajorTicks; i++ ) {
            Point2D.Float start = getPoint( width / 2 - tickSize, ang );
            Point2D.Float end = getPoint( width / 2, ang );
            //System.out.println("ang="+ang+", start="+start+", end="+end);
            g.drawLine( (int)start.x, (int)start.y, (int)end.x, (int)end.y );
            ang += dTheta;
        }
        ang = (float)Math.PI / 2;
        for( int i = 0; i < numMajorTicks; i++ ) {
            Point2D.Float start = getPoint( width / 2 - tickSize, ang );
            Point2D.Float end = getPoint( width / 2, ang );
            //System.out.println("ang="+ang+", start="+start+", end="+end);
            g.drawLine( (int)start.x, (int)start.y, (int)end.x, (int)end.y );
            ang += dTheta;
        }
    }

    public void drawMinorTicks( Graphics2D g ) {
        g.setStroke( new BasicStroke( 1 ) );
        float  dTheta = (float)Math.PI / 2 / (float )( numMinorTicks - 1 );
        float  ang = (float)Math.PI;//straight up
        g.setColor( Color.green );
        int tickSize = 7;
        /*Paint the ticks.*/
        for( int i = 0; i < numMinorTicks; i++ ) {
            Point2D.Float start = getPoint( width / 2 - tickSize, ang );
            Point2D.Float end = getPoint( width / 2, ang );
            //System.out.println("ang="+ang+", start="+start+", end="+end);
            g.drawLine( (int)start.x, (int)start.y, (int)end.x, (int)end.y );
            ang += dTheta;
        }
        ang = (float)Math.PI / 2;
        for( int i = 0; i < numMinorTicks; i++ ) {
            Point2D.Float start = getPoint( width / 2 - tickSize, ang );
            Point2D.Float end = getPoint( width / 2, ang );
            //System.out.println("ang="+ang+", start="+start+", end="+end);
            g.drawLine( (int)start.x, (int)start.y, (int)end.x, (int)end.y );
            ang += dTheta;
        }
    }
}






