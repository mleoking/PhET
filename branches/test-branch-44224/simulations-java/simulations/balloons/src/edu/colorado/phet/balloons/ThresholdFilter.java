package edu.colorado.phet.balloons;

import java.awt.*;
import java.util.Vector;

import edu.colorado.phet.common.phetcommon.math.DoubleSeries;

public class ThresholdFilter implements BalloonDragListener {
    DoubleSeries x;
    DoubleSeries y;
    long lastSampleTime = -1;
    Point lastPos;
    double thresh;
    Vector list = new Vector();

    public void addBalloonDragListener( BalloonDragListener bdl ) {
        list.add( bdl );
    }

    public ThresholdFilter( int xsize, int ysize, double thresh ) {
        x = new DoubleSeries( xsize );
        y = new DoubleSeries( ysize );
        this.thresh = thresh;
    }

    public void balloonDragged( BalloonPainter p ) {
        Point pos = p.getPosition();
        if ( lastSampleTime == -1 || lastPos == null ) {
            lastSampleTime = System.currentTimeMillis();
            lastPos = pos;
            return;
        }
        Point diff = new Point( pos.x - lastPos.x, pos.y - lastPos.y );
        long curTime = System.currentTimeMillis();
        long dt = curTime - lastSampleTime;
        if ( dt > 0 ) {
            double xd = ( (double) diff.x ) / ( (double) dt );
            double yd = ( (double) diff.y ) / ( (double) dt );
            x.add( xd * xd );
            y.add( yd * yd );
            double xavg = x.average();
            double yavg = y.average();
            double val = Math.sqrt( xavg + yavg );//xavg*xavg+yavg*yavg);
            //System.err.println("Diff="+diff+", value="+val);
            //System.err.println("dt="+dt+", diff="+diff+", xavg="+xavg+", yavg="+yavg+"value="+val);
            //System.err.println("value="+val);
            lastSampleTime = curTime;
            if ( val >= thresh ) {
                for ( int i = 0; i < list.size(); i++ ) {
                    ( (BalloonDragListener) list.get( i ) ).balloonDragged( p );
                }
            }
        }
//  	else
//  	    {
//  		//System.err.println("dt="+dt);
//  	    }
        lastPos = pos;
    }
}
