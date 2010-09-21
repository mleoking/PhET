package edu.colorado.phet.batteryvoltage.common.electron.components;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.batteryvoltage.BatteryVoltageResources;

public class Gauge implements IGauge {
    int numMajorTicks;
    int numMinorTicks;
    double needleLength;
    double min;
    double max;
    double amount;
    int x;
    int y;
    int width;
    int height;
    String text = BatteryVoltageResources.getString( "Gauge.DefaultText" );

    public Gauge( int x, int y, double min, double max, double amount, double length ) {
        this( x, y, (int) length, (int) length, min, max, amount, length / 2, 5, 9 );
    }

    public void setText( String text ) {
        this.text = text;
    }

    public Gauge( int x, int y, int width, int height, double min, double max, double amount, double needleLength, int numMajorTicks, int numMinorTicks ) {
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

    public void setValue( double amount ) {
        this.amount = amount;
    }

    private Point getPoint( int length, double angle ) {
        int i = (int) ( length * Math.sin( angle ) + x + width / 2 );
        int j = (int) ( length * Math.cos( angle ) + y + height / 2 );
        return new Point( i, j );
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
        Point needle = getPoint( 10, Math.PI / 2 + Math.PI / 4 );
        Rectangle2D rect = f.getStringBounds( text, g.getFontRenderContext() );
        int w = (int) rect.getWidth();
        int startX = x + width / 2 - w / 2;
        int startY = y + height / 3;
        g.drawString( text, startX, startY );
    }

    public void drawButton( Graphics2D g ) {
        g.setColor( Color.blue );
        Point p = getPoint( 0, 0 );
        int buttonWidth = 5;
        //g.fillOval(p.x-buttonWidth,p.y-buttonWidth,buttonWidth*2,buttonWidth*2);
        g.fillRect( p.x - buttonWidth, p.y - buttonWidth, buttonWidth * 2, buttonWidth * 2 );
    }

    protected double getBoundedValue() {
        double amt = Math.min( amount, max );
        amt = Math.max( amt, min );
        return amt;
    }

    public void drawNeedle( Graphics2D g ) {
        double range = max - min;
        double amt = getBoundedValue();
        double theta = amt / ( max - min ) * Math.PI;
        //util.Debug.traceln("max="+max+", min="+min+", range="+range+", value="+amount+", theta="+theta);

        //util.Debug.traceln("theta="+theta);
        double xD = needleLength * Math.sin( theta );
        double yD = needleLength * Math.cos( theta );
        //util.Debug.traceln("XD="+xD);
        //util.Debug.traceln("yD="+yD);

        int needleX = x + width / 2;
        int needleY = y + height / 2;

        int needleEndX = needleX + (int) xD;
        int needleEndY = needleY - (int) yD;
        //util.Debug.traceln(helper.ArraysHelper.toString(new int[]{x,y,endX,endY}));
        if ( amt == max || amt == min ) {
            g.setColor( Color.red );
        }
        else {
            g.setColor( new Color( 200, 0, 180 ) );
        }
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
        double dTheta = Math.PI / 2 / (double) ( numMajorTicks - 1 );
        double ang = Math.PI;//straight up
        g.setColor( Color.black );
        int tickSize = 10;
        /*Paint the ticks.*/
        for ( int i = 0; i < numMajorTicks; i++ ) {
            Point start = getPoint( width / 2 - tickSize, ang );
            Point end = getPoint( width / 2, ang );
            //System.out.println("ang="+ang+", start="+start+", end="+end);
            g.drawLine( start.x, start.y, end.x, end.y );
            ang += dTheta;
        }
        ang = Math.PI / 2;
        for ( int i = 0; i < numMajorTicks; i++ ) {
            Point start = getPoint( width / 2 - tickSize, ang );
            Point end = getPoint( width / 2, ang );
            //System.out.println("ang="+ang+", start="+start+", end="+end);
            g.drawLine( start.x, start.y, end.x, end.y );
            ang += dTheta;
        }
    }

    public void drawMinorTicks( Graphics2D g ) {
        g.setStroke( new BasicStroke( 1 ) );
        double dTheta = Math.PI / 2 / (double) ( numMinorTicks - 1 );
        double ang = Math.PI;//straight up
        g.setColor( Color.green );
        int tickSize = 7;
        /*Paint the ticks.*/
        for ( int i = 0; i < numMinorTicks; i++ ) {
            Point start = getPoint( width / 2 - tickSize, ang );
            Point end = getPoint( width / 2, ang );
            //System.out.println("ang="+ang+", start="+start+", end="+end);
            g.drawLine( start.x, start.y, end.x, end.y );
            ang += dTheta;
        }
        ang = Math.PI / 2;
        for ( int i = 0; i < numMinorTicks; i++ ) {
            Point start = getPoint( width / 2 - tickSize, ang );
            Point end = getPoint( width / 2, ang );
            //System.out.println("ang="+ang+", start="+start+", end="+end);
            g.drawLine( start.x, start.y, end.x, end.y );
            ang += dTheta;
        }
    }
}






