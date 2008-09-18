package edu.colorado.phet.batteryvoltage.common.electron.man;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;

import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;

/*A kinematic chain.*/

public class Node {
    Vector children;
    double x;
    double y;

    public Node( double x, double y ) {
        children = new Vector();
        this.x = x;
        this.y = y;
    }

    public DoublePoint getPosition() {
        return new DoublePoint( x, y );
    }

    public void addChildRelative( double dx, double dy ) {
        addChild( new Node( x + dx, y + dy ) );
    }

    public void addChild( Node ch ) {
        children.add( ch );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int numChildren() {
        return children.size();
    }

    public void rotate( double anchorX, double anchorY, double angle ) {
        AffineTransform at = AffineTransform.getRotateInstance( angle, anchorX, anchorY );
        transform( at );
    }

    public void rotate( double angle ) {
        for ( int i = 0; i < numChildren(); i++ ) {
            childAt( i ).rotate( x, y, angle );
        }
    }

    public void transform( AffineTransform t ) {
        Point2D.Double pt = new Point2D.Double( x, y );
        Point2D.Double out = (Point2D.Double) t.transform( pt, null );
        x = out.x;
        y = out.y;
        for ( int i = 0; i < children.size(); i++ ) {
            childAt( i ).transform( t );
        }
    }

    public void translate( double dx, double dy ) {
        x += dx;
        y += dy;
        for ( int i = 0; i < children.size(); i++ ) {
            childAt( i ).translate( dx, dy );
        }
    }

    public Node childAt( int i ) {
        return (Node) children.get( i );
    }

}
