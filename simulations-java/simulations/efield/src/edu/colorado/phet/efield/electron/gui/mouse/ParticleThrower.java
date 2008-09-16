// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.gui.mouse;

import java.awt.event.MouseEvent;
import java.util.Vector;

import edu.colorado.phet.efield.electron.gui.ParticlePanel;
import edu.colorado.phet.efield.electron.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.electron.phys2d_efield.System2D;
import edu.colorado.phet.efield.electron.phys2d_efield.SystemRunner;
import edu.colorado.phet.efield.electron.utils.TruncatedSeries;

// Referenced classes of package edu.colorado.phet.efield.electron.gui.mouse:
//            ParticleGrabber, TimedPoint

public class ParticleThrower extends ParticleGrabber {

    public ParticleThrower( ParticlePanel particlepanel, System2D system2d, SystemRunner systemrunner, int i, double d ) {
        super( particlepanel, system2d, systemrunner );
        scale = d;
        ts = new TruncatedSeries( i );
        maxSeriesSize = i;
    }

    private DoublePoint getAverageVelocity( Vector vector ) {
        if ( vector.size() == 0 || vector.size() == 1 ) {
            return new DoublePoint( 0.0D, 0.0D );
        }
        DoublePoint adoublepoint[] = (DoublePoint[]) vector.toArray( new DoublePoint[vector.size()] );
        Vector vector1 = new Vector();
        for ( int i = 0; i < adoublepoint.length - 1; i++ ) {
            vector1.add( adoublepoint[i + 1].subtract( adoublepoint[i] ) );
        }

        DoublePoint adoublepoint1[] = (DoublePoint[]) vector1.toArray( new DoublePoint[vector1.size()] );
        DoublePoint doublepoint = DoublePoint.average( adoublepoint1 );
        return doublepoint;
    }

    private DoublePoint timedPointsToVelocity( Vector vector ) {
        long l = 100L;
        Vector vector1 = new Vector();
        TimedPoint atimedpoint[] = (TimedPoint[]) vector.toArray( new TimedPoint[vector.size()] );
        long l1 = System.currentTimeMillis();
        for ( int i = 0; i < atimedpoint.length; i++ ) {
            if ( atimedpoint[i].getAge( l1 ) < l ) {
                vector1.add( atimedpoint[i].getDoublePoint() );
            }
        }

        return getAverageVelocity( vector1 );
    }

    public void mouseReleased( MouseEvent mouseevent ) {
        if ( selected == null ) {
            return;
        }
        Vector vector = ts.get();
        DoublePoint doublepoint = timedPointsToVelocity( vector );
        if ( Double.isNaN( doublepoint.getX() ) ) {
            doublepoint = new DoublePoint( 0.0D, doublepoint.getY() );
        }
        if ( Double.isNaN( doublepoint.getX() ) ) {
            doublepoint = new DoublePoint( doublepoint.getX(), 0.0D );
        }
        doublepoint = doublepoint.multiply( scale );
        selected.setVelocity( doublepoint );
        super.mouseReleased( mouseevent );
    }

    public void mousePressed( MouseEvent mouseevent ) {
        super.mousePressed( mouseevent );
        ts = new TruncatedSeries( maxSeriesSize );
    }

    public void mouseDragged( MouseEvent mouseevent ) {
        super.mouseDragged( mouseevent );
        if ( selected == null ) {
            return;
        }
        DoublePoint doublepoint = new DoublePoint( mouseevent.getX(), mouseevent.getY() );
        TimedPoint timedpoint = new TimedPoint( doublepoint, System.currentTimeMillis() );
        ts.add( timedpoint );
        if ( !run.isActiveAndRunning() ) {
            pp.repaint();
        }
    }

    TruncatedSeries ts;
    int maxSeriesSize;
    double scale;
}
