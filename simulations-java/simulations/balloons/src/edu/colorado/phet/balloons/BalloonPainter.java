package edu.colorado.phet.balloons;

import edu.colorado.phet.balloons.common.paint.FixedImagePainter;
import edu.colorado.phet.balloons.common.paint.Painter;
import edu.colorado.phet.balloons.common.phys2d.DoublePoint;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class BalloonPainter implements Painter {
    Vector charges = new Vector();
    boolean vis = true;
    FixedImagePainter fip;
    Vector v = new Vector();
    Point stringAttach;
    Point stringRoot;
    Stroke stringStroke;
    Color stringColor;
    DoublePoint vel = new DoublePoint();
    Point initPos;
    boolean isHeld = false;

    public BalloonPainter( FixedImagePainter fip, Point stringAttach, Point stringRoot, Stroke string, Color stringColor ) {
        this.fip = fip;
        this.stringAttach = stringAttach;
        this.stringRoot = stringRoot;
        this.stringColor = stringColor;
        this.stringStroke = string;
    }

    public boolean isHeld() {
        return isHeld;
    }

    public void setIsHeld( boolean isHeld ) {
        this.isHeld = isHeld;
    }

    public void setVisible( boolean t ) {
        this.vis = t;
    }

    public boolean isVisible() {
        return vis;
    }

    public void setInitialPosition( Point p ) {
        this.initPos = p;
    }

    public Point getInitialPosition() {
        return initPos;
    }

    public DoublePoint getVelocity() {
        return vel;
    }

    public void setVelocity( DoublePoint vel ) {
        this.vel = vel;
    }

    public int getCharge() {
        return v.size();
    }

    public void removePainters() {
        this.charges = new Vector();
        this.v = new Vector();
    }

    public Charge[] getCharges() {
        return (Charge[])charges.toArray( new Charge[0] );
    }

    public void addPainter( Painter p, Charge ch ) {
        charges.add( ch );
        v.add( p );
    }

    public FixedImagePainter getFixedPainter() {
        return fip;
    }

    public void setPosition( Point p ) {
        fip.setPosition( p );//sel

    }

    public Point getPosition() {
        return fip.getPosition();
    }

    public BufferedImage getImage() {
        return fip.getImage();
    }

    public void paint( Graphics2D g ) {
        if( !vis ) {
            return;
        }
        fip.paint( g );
        for( int i = 0; i < v.size(); i++ ) {
            ( (Painter)v.get( i ) ).paint( g );
        }
        Point p = fip.getPosition();
        g.setColor( stringColor );
        g.setStroke( stringStroke );
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.drawLine( stringAttach.x + p.x, stringAttach.y + p.y, stringRoot.x, stringRoot.y );
    }
}
