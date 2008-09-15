package edu.colorado.phet.balloons;

import java.awt.*;
import java.util.Vector;

import javax.swing.*;

import edu.colorado.phet.balloons.common.paint.Painter;
import edu.colorado.phet.balloons.common.paint.ParticlePainter;
import edu.colorado.phet.balloons.common.phys2d.DoublePoint;
import edu.colorado.phet.balloons.common.phys2d.Law;
import edu.colorado.phet.balloons.common.phys2d.System2D;

public class Wall implements Painter, Law {
    JCheckBox show;
    Rectangle bounds;
    Painter background;
    Vector d = new Vector();
    BalloonPainter blu;
    BalloonPainter yel;
    int w;
    int h;

    public void iterate( double dt, System2D sys ) {
        Point pos = blu.getPosition();
        Point pos2 = yel.getPosition();
        DoublePoint blue = new DoublePoint( pos.x + w / 2, pos.y + h / 2 );
        boolean blueVis = blu.isVisible();
        DoublePoint yellow = new DoublePoint( pos2.x + w / 2, pos2.y + h / 2 );
        boolean yelVis = yel.isVisible();
        double k = 1;
        for ( int i = 0; i < d.size(); i++ ) {
            Dipole dip = ( (Dipole) d.get( i ) );
            Charge p = dip.p();
            Charge m = dip.m();
//  		DoublePoint fx=getForce(p,blueVis,blue,blu.getCharge()).add(getForce(p,yelVis,yellow,yel.getCharge()));
//  		DoublePoint newPos=(p.getInitialPosition().add(fx));
//  		if (newPos.getX()<bounds.x)
//  		    newPos=new DoublePoint(bounds.x,newPos.getY());
//  		p.setPosition(newPos);

            DoublePoint fx = getForce( m, blueVis, blue, blu.getCharge() ).add( getForce( m, yelVis, yellow, yel.getCharge() ) );
            DoublePoint newPos = ( m.getInitialPosition().add( fx ) );
            if ( newPos.getX() < bounds.x ) {
                newPos = new DoublePoint( bounds.x, newPos.getY() );
            }
            m.setPosition( newPos );
        }
    }

    public static final DoublePoint ZERO = new DoublePoint();
    public static final double max = 40;

    public DoublePoint getForce( Charge ch, boolean vis, DoublePoint ctr, int c ) {
        if ( !vis ) {
            return ZERO;
        }
        //double k=-100000/4;
        double k = -1000000 / 5;
        double kqq = k * ch.getCharge() * c;
        DoublePoint force = BalloonForces.getForce( ch.getPosition(), ctr, kqq, 2.7 );
        if ( force.length() <= 2 ) {
            return ZERO;
        }
        if ( force.length() > max ) {
            return force.normalize().multiply( max );
        }
        return force;
    }

    public boolean isVisible() {
        return show.isSelected();
    }

    public Wall( JCheckBox show, Rectangle bounds, Painter background, ParticlePainter plussy, ParticlePainter minnie, BalloonPainter blu, BalloonPainter yel ) {
        this.w = blu.getImage().getWidth();
        this.h = blu.getImage().getHeight();
        this.blu = blu;
        this.yel = yel;
        this.show = show;
        this.bounds = bounds;
        this.background = background;

        int X = 3;
        int Y = 18;
        int dx = bounds.width / X + 4;
        int dy = bounds.height / Y;
        int y0 = 0;
        for ( int i = 0; i < X; i++ ) {
            for ( int k = 0; k < Y; k++ ) {
                if ( i % 2 == 0 ) {
                    y0 = dy / 2;
                }
                else {
                    y0 = 0;
                }
                int x = i * dx + bounds.x;
                int y = k * dy + bounds.y + y0;
//  		}
//  	for (int i=0;i<numDipoles;i++)
//  	    {
                //int x=r.nextInt(bounds.width)+bounds.x;
                //int y=r.nextInt(bounds.height)+bounds.y;
                //int x2=r.nextInt(bounds.width)+bounds.x;
                //int y2=r.nextInt(bounds.height)+bounds.y;
                Dipole dip = new Dipole( x, y, x, y, plussy, minnie );
                d.add( dip );
            }
        }
    }

    public void paint( Graphics2D g ) {
        if ( !show.isSelected() ) {
            return;
        }
        background.paint( g );
        for ( int i = 0; i < d.size(); i++ ) {
            ( (Painter) d.get( i ) ).paint( g );
        }
    }
}
