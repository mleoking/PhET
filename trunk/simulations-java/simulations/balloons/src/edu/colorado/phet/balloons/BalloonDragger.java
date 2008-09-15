package edu.colorado.phet.balloons;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import edu.colorado.phet.balloons.common.paint.Painter;
import edu.colorado.phet.balloons.common.phys2d.DoublePoint;

public class BalloonDragger implements MouseMotionListener, MouseListener, Painter {
    BalloonPainter[] p;
    Component paintMe;
    Point balloonStart;
    Point mouseStart;
    BalloonPainter sel;
    Vector list = new Vector();
    Rectangle bounds;

    public void addBalloonDragListener( BalloonDragListener bdl ) {
        list.add( bdl );
    }

    public BalloonDragger( BalloonPainter[] p, Component paintMe, Rectangle bounds ) {
        this.bounds = bounds;
        this.p = p;
        this.paintMe = paintMe;
    }

    public void paint( Graphics2D g ) {
        for ( int i = 0; i < p.length; i++ ) {
            if ( sel != p[i] ) {
                p[i].paint( g );
            }
        }
        if ( sel != null ) {
            sel.paint( g );
        }
    }

    public static Point getInsideBounds( Point a, Rectangle bounds ) {
        int x = 0;
        int y = 0;
        if ( a.x > bounds.x + bounds.width ) {
            x = bounds.x + bounds.width;
        }
        else if ( a.x < bounds.x ) {
            x = bounds.x;
        }
        else {
            x = a.x;
        }
        if ( a.y > bounds.y + bounds.height ) {
            y = bounds.y + bounds.height;
        }
        else if ( a.y < bounds.y ) {
            y = bounds.y;
        }
        else {
            y = a.y;
        }
        return new Point( x, y );
    }

    public synchronized void mouseDragged( MouseEvent me ) {
        if ( mouseStart == null ) {
            return;
        }
        synchronized( paintMe ) {
            Point newPt = me.getPoint();
            Point diff = new Point( newPt.x - mouseStart.x, newPt.y - mouseStart.y );
            Point newBallonPos = new Point( balloonStart.x + diff.x, balloonStart.y + diff.y );
            newBallonPos = getInsideBounds( newBallonPos, bounds );
            sel.getFixedPainter().setPosition( newBallonPos );
            sel.setIsHeld( true );
            for ( int i = 0; i < list.size(); i++ ) {
                ( (BalloonDragListener) list.get( i ) ).balloonDragged( sel );
            }
            //if (i++%n==0)
            paintMe.repaint();
        }
    }

    //int i=0;
    //int n=10;
    public void mouseEntered( MouseEvent me ) {


    }

    public synchronized void mousePressed( MouseEvent me ) {
        /*Choose the topmost to drag.*/
        BalloonPainter old = sel;
        this.sel = getHit( me.getPoint() );
        if ( sel == null ) {
            return;
        }
        sel.setIsHeld( true );
        if ( old != null ) {
            old.setIsHeld( false );
        }
        this.balloonStart = sel.getFixedPainter().getPosition();
        this.mouseStart = me.getPoint();
        paintMe.repaint();
    }

    private BalloonPainter getHit( Point mouse ) {
        if ( sel != null ) {
            if ( isHit( mouse, sel ) ) {
                return sel;
            }
        }
        for ( int i = 0; i < p.length; i++ ) {
            if ( isHit( mouse, p[i] ) ) {
                return p[i];
            }
        }
        return null;
    }

    public boolean isHit( Point mouse, BalloonPainter p ) {
        BufferedImage target = p.getFixedPainter().getImage();
        Point px = p.getFixedPainter().getPosition();
        Rectangle r = new Rectangle( px.x, px.y, target.getWidth(), target.getHeight() );
        if ( r.contains( mouse ) && p.isVisible() ) {
            return true;
        }
        return false;
    }

    public void mouseReleased( MouseEvent me ) {
        mouseStart = null;
        if ( sel != null ) {
            this.sel.setIsHeld( false );
            sel.setVelocity( new DoublePoint() );
        }
    }

    public void mouseClicked( MouseEvent me ) {
    }

    public void mouseMoved( MouseEvent me ) {
        if ( getHit( me.getPoint() ) != null ) {
            me.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }
        else {
            me.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
        }
    }

    public void mouseExited( MouseEvent me ) {

    }
}
