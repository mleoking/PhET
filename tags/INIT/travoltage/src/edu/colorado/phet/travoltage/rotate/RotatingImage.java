package edu.colorado.phet.travoltage.rotate;

import edu.colorado.phet.common.gui.Painter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class RotatingImage implements MouseMotionListener, MouseListener, Painter {
    boolean isSelecting = false;
    Vector list = new Vector();
    BufferedImage im;
    double angle;
    int x;
    int y;
    Component paintMe;
    double range;
    int xCM;
    int yCM;
    double offsetAngle;

    public RotatingImage( BufferedImage im, double angle, int x, int y, Component paintMe, double range, int xCM, int yCM, double offsetAngle ) {
        this.offsetAngle = offsetAngle;
        this.xCM = xCM;
        this.yCM = yCM;
        this.range = range;
        this.x = x;
        this.y = y;
        this.im = im;
        this.angle = angle;
        this.paintMe = paintMe;
    }

    public double getAngle() {
        return angle;
    }

    public Point getPivot() {
        return new Point( x, y );
    }

    public void addAngleListener( AngleListener al ) {
        list.add( al );
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( im, getTransform() );
    }

    public AffineTransform getTransform() {
        AffineTransform at = AffineTransform.getRotateInstance( angle, x + xCM, y + yCM );//g,x,y);
        AffineTransform move = AffineTransform.getTranslateInstance( x, y );
        at.concatenate( move );
        return at;
    }

    public void mouseExited( MouseEvent me ) {
    }

    public void mouseClicked( MouseEvent me ) {
    }

    public void mouseMoved( MouseEvent me ) {
//        Shape s = getShape();
//        if( s.contains( me.getX(), me.getY() ) ) {
//            me.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
//        }
//        else {
//            me.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
//        }
    }

    public void mouseEntered( MouseEvent me ) {
    }

    public Shape getShape() {
        /*Deterimine whether the user is to start selecting...*/
        Shape s = new Rectangle( -10, -10, im.getWidth() + 20, im.getHeight() + 20 );

        Shape s2 = getTransform().createTransformedShape( s );
        return s2;
    }

    public void mousePressed( MouseEvent me ) {
        /*Deterimine whether the user is to start selecting...*/
        Shape s = new Rectangle( -10, -10, im.getWidth() + 20, im.getHeight() + 20 );

        Shape s2 = getTransform().createTransformedShape( s );
        //edu.colorado.phet.common.util.Debug.traceln("Transformed shape: "+s2);
        this.isSelecting = ( s2.contains( me.getX(), me.getY() ) );
        //edu.colorado.phet.common.util.Debug.traceln("Set isSelecting="+isSelecting);
    }

    public void mouseReleased( MouseEvent me ) {
    }

    public void mouseDragged( MouseEvent me ) {
        if( !isSelecting ) {
            return;
        }
        double dx = x - me.getX() + xCM;
        double dy = y - me.getY() + yCM;
//  	if (Math.sqrt(dx*dx+dy*dy)>range)
//  	    return;
        this.angle = Math.atan2( dy, dx ) + offsetAngle;
        for( int i = 0; i < list.size(); i++ ) {
            ( (AngleListener)list.get( i ) ).angleChanged( angle );
        }
        //edu.colorado.phet.common.util.Debug.traceln("Changing angle: "+angle);
        paintMe.repaint();
    }
}
