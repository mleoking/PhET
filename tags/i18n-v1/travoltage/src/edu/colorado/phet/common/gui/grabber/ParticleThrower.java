package edu.colorado.phet.common.gui.grabber;

import edu.colorado.phet.common.gui.ParticlePanel;
import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.System2D;
import edu.colorado.phet.common.phys2d.SystemRunner;
import edu.colorado.phet.common.utils.TruncatedSeries;

import java.awt.event.MouseEvent;
import java.util.Vector;

public class ParticleThrower extends ParticleGrabber {
    TruncatedSeries ts;
    int maxSeriesSize;
    double scale;

    public ParticleThrower( ParticlePanel pp, System2D sys, SystemRunner run, int maxSeriesSize, double scale ) {
        super( pp, sys, run );
        this.scale = scale;
        this.ts = new TruncatedSeries( maxSeriesSize );
        this.maxSeriesSize = maxSeriesSize;
    }

    private DoublePoint getAverageVelocity( Vector data )//from the Vector<DoublePoint>
    {
        if( data.size() == 0 || data.size() == 1 )//no derivatives.
        {
            return new DoublePoint( 0, 0 );
        }

        DoublePoint[] dp = (DoublePoint[])data.toArray( new DoublePoint[data.size()] );
        Vector diff = new Vector();
        for( int i = 0; i < dp.length - 1; i++ ) {
            diff.add( dp[i + 1].subtract( dp[i] ) );
        }

        DoublePoint[] vel = (DoublePoint[])diff.toArray( new DoublePoint[diff.size()] );
        DoublePoint avg = DoublePoint.average( vel );
        return avg;
    }

    private DoublePoint timedPointsToVelocity( Vector data ) {
        long threshold = 100;//time to move the grabber.
        Vector doublePoints = new Vector();
        TimedPoint[] dp = (TimedPoint[])data.toArray( new TimedPoint[data.size()] );
        long now = System.currentTimeMillis();
        for( int i = 0; i < dp.length; i++ ) {
            if( dp[i].getAge( now ) < threshold ) {
                doublePoints.add( dp[i].getDoublePoint() );
            }
        }
        return getAverageVelocity( doublePoints );
    }

    public void mouseReleased( MouseEvent me ) {
        if( selected == null ) {
            return;
        }
        //edu.colorado.phet.common.util.Debug.traceln("Throwing with: "+ts);
        Vector data = ts.get();
        DoublePoint avg = timedPointsToVelocity( data );//getAverageVelocity(data);
        //edu.colorado.phet.common.util.Debug.traceln("Average velocity: "+avg);

        if( Double.isNaN( avg.getX() ) ) {
            avg = new DoublePoint( 0, avg.getY() );
        }
        if( Double.isNaN( avg.getX() ) ) {
            avg = new DoublePoint( avg.getX(), 0 );
        }

        avg = avg.multiply( scale );
        selected.setVelocity( avg );

        super.mouseReleased( me );
    }

    /**
     * Grab the topmost edu.colorado.phet.common under the grabber, if any.
     */
    public void mousePressed( MouseEvent me ) {
        super.mousePressed( me );
        this.ts = new TruncatedSeries( maxSeriesSize );
    }

    /**
     * Motion.
     */
    public void mouseDragged( MouseEvent me ) {
        super.mouseDragged( me );
        if( selected == null ) {
            return;
        }
        DoublePoint dp = ( new DoublePoint( me.getX(), me.getY() ) );
        TimedPoint tp = new TimedPoint( dp, System.currentTimeMillis() );
        ts.add( tp );
        if( run.isActiveAndRunning() ) {
        }
        else {
            pp.repaint();
        }
    }
}
