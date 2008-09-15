package edu.colorado.phet.balloons;

import java.awt.*;

import edu.colorado.phet.balloons.common.paint.FixedImagePainter;
import edu.colorado.phet.balloons.common.phys2d.DoublePoint;
import edu.colorado.phet.balloons.common.phys2d.Law;
import edu.colorado.phet.balloons.common.phys2d.System2D;

public class BalloonForces implements Law {
    BalloonPainter a;
    BalloonPainter b;
    Sweater s;
    Rectangle bounds;
    int w2;
    int h2;
    int wallX;
    Wall wall;

    public BalloonForces( BalloonPainter a, BalloonPainter b, Sweater s, Rectangle bounds, int wallX, Wall wall ) {
        this.wall = wall;
        this.wallX = wallX;
        this.bounds = bounds;
        this.a = a;
        this.b = b;
        this.s = s;
        this.w2 = a.getFixedPainter().getImage().getWidth() / 2;
        this.h2 = a.getFixedPainter().getImage().getHeight() / 2;
    }

    public DoublePoint getCenter( BalloonPainter bp ) {
        FixedImagePainter fp = bp.getFixedPainter();

        Point ax = bp.getPosition();
        DoublePoint apt = new DoublePoint( ax.x + w2, ax.y + h2 );
        return apt;
    }

    double k = 10.00;

    public void iterate( double dt, System2D sys ) {
        if ( a.isVisible() && !a.isHeld() ) {
            DoublePoint fa = getForce( a, b );
            applyForce( dt, fa, a );
        }
        if ( b.isVisible() && !b.isHeld() ) {
            DoublePoint fb = getForce( b, a );
            applyForce( dt, fb, b );
        }
    }

    public DoublePoint getForce( BalloonPainter me, BalloonPainter other ) {
        DoublePoint sweaterForce = getSweaterForce( me );
        DoublePoint otherForce = getOtherForce( me, other );
        if ( wall.isVisible() ) {
            double distFromWall = wallX - me.getPosition().getX();
            double charge = me.getCharge();
            if ( charge > 5 ) {
                double relDist = distFromWall - 140;
                //if (count++%disp==0)
                //System.err.println("dist="+distFromWall+", charge="+charge);
                double fright = .3;
                if ( relDist <= 20 + charge / 8 ) {
                    return new DoublePoint( fright * charge / 20.0, 0 );
                }
            }
        }
        return sweaterForce.add( otherForce );
    }

    //      public DoublePoint getWallForce(BalloonPainter bp)
//      {
//  	double x=wallX-bp.getPosition().getX();
//  	double k=100000/6;
//  	double kqq=bp.getCharge()*k;
//  	double pow=x*x*x;
//  	//System.err.println("x="+x+", kqq="+kqq+", pow="+pow);
//  	double f=kqq/pow;
//  	//System.err.println("f="+f);
//  	return new DoublePoint(f,0);
//      }

    public DoublePoint getSweaterForce( BalloonPainter bp ) {
        DoublePoint sweater = s.getCenter();
        DoublePoint x = getCenter( bp );
        double qSweater = a.getCharge() + b.getCharge();
        double qBalloon = bp.getCharge();
        return getForce( sweater, x, k * qSweater * qBalloon );
    }

    public static DoublePoint getForce( DoublePoint m, DoublePoint n, double kqq ) {
        DoublePoint diff = m.subtract( n );
        double r = diff.length();
        if ( r == 0 ) {
            return new DoublePoint();
        }
        DoublePoint fa = diff.multiply( kqq / ( r * r * r ) );
        return fa;
    }

    public static DoublePoint getForce( DoublePoint m, DoublePoint n, double kqq, double power ) {
        DoublePoint diff = m.subtract( n );
        double r = diff.length();
        if ( r == 0 ) {
            return new DoublePoint();
        }
        DoublePoint fa = diff.multiply( kqq / ( Math.pow( r, power + 1 ) ) );
        return fa;
    }

    public DoublePoint getOtherForce( BalloonPainter me, BalloonPainter other ) {
        if ( me.isHeld() || !me.isVisible() || !other.isVisible() ) {
            return new DoublePoint();
        }
        double kqq = k * me.getCharge() * other.getCharge();
        DoublePoint meC = getCenter( me );
        DoublePoint oC = getCenter( other );
        return getForce( meC, oC, kqq );
    }

    public Point makeLegal( Point p ) {
        return BalloonDragger.getInsideBounds( p, bounds );
    }

    public void applyForce( double dt, DoublePoint f, BalloonPainter bp ) {
        DoublePoint v0 = bp.getVelocity();
        DoublePoint v = v0.add( f.multiply( dt ) );
        Point x0 = bp.getPosition();
        DoublePoint x00 = new DoublePoint( x0.x, x0.y );
        DoublePoint xnew = x00.add( v.multiply( dt ) );
        Point newPt = new Point( (int) xnew.getX(), (int) xnew.getY() );
        Point newRhs = new Point( newPt.x + w2 * 2, newPt.y + h2 * 2 );
        Point newpt = makeLegal( newPt );
        if ( bounds.contains( newPt ) && bounds.contains( newRhs ) ) {
            bp.setVelocity( v );
            bp.getFixedPainter().setPosition( newPt );
        }
        else {
            bp.setVelocity( new DoublePoint() );
        }
    }
}
