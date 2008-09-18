package edu.colorado.phet.batteryvoltage.common.electron.paint.animate;

import java.awt.*;

import edu.colorado.phet.batteryvoltage.common.electron.paint.Painter;

public class RotatingTwinkle2 implements Animation, Painter {
    int numFrames;
    PointSource ps;
    Stroke s;
    int frame;
    double rot;
    double maxWidth;
    double maxHeight;

    public RotatingTwinkle2( PointSource ps, Stroke s, int numFrames, double rot, double maxWidth, double maxHeight ) {
        this.rot = rot;
        this.numFrames = numFrames;
        this.ps = ps;
        this.s = s;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public int numFrames() {
        return numFrames;
    }

    private double getFrameFraction() {
        return ( (double) frame ) / ( (double) numFrames );
    }

    public void paint( Graphics2D g ) {
        if ( frame > numFrames ) {
            return;
        }
        double ff = getFrameFraction();
        int height = (int) ( Math.sin( ff * Math.PI ) * maxHeight );
        int width = (int) ( Math.sin( ff * Math.PI ) * maxWidth );
        Point p = ps.getPoint();
        int x = p.x;
        int y = p.y;
        //o.O.d(""+x+", "+y);
        int maxDotWidth = 10;
        //if (frame<(int)(numFrames*.75))

        g.setColor( Color.white );
        g.setStroke( s );
        //Go Math.PI/2 in all frames.
        double angle = Math.PI / 2 * frame / numFrames;

        //g.drawLine(x,y-height,x,y+height);
        //g.drawLine(x-width,y,x+width,y);

        //o.O.p(""+angle*rot);
        g.drawLine( x, y, x + (int) ( width * Math.cos( angle * rot ) ), y + (int) ( height * Math.sin( angle * rot ) ) );
        g.drawLine( x, y, x - (int) ( width * Math.cos( angle * rot ) ), y - (int) ( height * Math.sin( angle * rot ) ) );
        g.drawLine( x, y, x - (int) ( width * Math.sin( -angle * rot ) ), y - (int) ( height * Math.cos( -angle * rot ) ) );
        g.drawLine( x, y, x + (int) ( width * Math.sin( -angle * rot ) ), y + (int) ( height * Math.cos( -angle * rot ) ) );
        //g.drawLine(x,y,x+width,y);
        //g.drawLine(x,y,x-width,y);

//  	    {
//  		int dotWidth=(int)(Math.sin(ff*Math.PI)*maxDotWidth);//outer dot
//  		g.setColor(Color.blue);
//  		g.fillOval(x-dotWidth/2,y-dotWidth/2,dotWidth,dotWidth);
//  		g.setColor(Color.black);
//  		int dot2=(int)(dotWidth*.8);
//  		g.fillOval(x-dot2/2,y-dot2/2,dot2,dot2);
//  	    }
    }

    public Painter frameAt( int i ) {
        this.frame = i;
        return this;
    }
}
