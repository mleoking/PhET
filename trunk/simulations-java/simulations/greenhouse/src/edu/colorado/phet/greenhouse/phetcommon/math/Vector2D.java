/*
 * Class: Vector2D
 * Package: edu.colorado.phet.common.physics
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 3:50:24 PM
 */
package edu.colorado.phet.greenhouse.phetcommon.math;


/**
 * A class for 2 dimensional mathmatical vectors
 */
public class Vector2D extends PhysicalVector {

    public Vector2D() {
        this(0, 0);
    }

    public Vector2D(Vector2D vector) {
        this(vector.getX(), vector.getY());
    }

    public Vector2D(float x, float y) {
        super(NUM_DIMENSIONS);
        this.setX(x);
        this.setY(y);
        if (Double.isNaN(x))
        //throw new RuntimeException( "x was NaN" );
            System.out.println("Vector2D constructor: x was NaN");
        if (Double.isNaN(y))
        //throw new RuntimeException( "Y was NaN" );
            System.out.println("Vector2D constructor: y was NaN");
    }

    public String toString() {
        java.util.Vector v = new java.util.Vector();
        v.add(new Double(this.getX()));
        v.add(new Double(this.getY()));
        return v.toString();
    }

    public Vector2D setComponents( float x, float y ) {
        this.setX( x );
        this.setY( y );
        return this;
    };

    public Vector2D setComponents( Vector2D that ) {
        this.setComponents( that.getX(), that.getY() );
        return this;
    };

    public float getX() {
        return getScalarAt(X);
    }

    public void setX(float x) {
        setScalarAt(X, x);
    }

    public float getY() {
        return getScalarAt(Y);
    }

    public void setY(float y) {
        setScalarAt(Y, y);
    }

    public Vector2D add(Vector2D that) {
        return (Vector2D) super.add(that, this);
    }

    public Vector2D normalize() {
        return (Vector2D) super.generalNormalize();
    }

    public Vector2D multiply(float scale) {
        return (Vector2D) super.multiply(scale, this);
    }

    public Vector2D subtract(Vector2D that) {
        return (Vector2D) super.subtract(that, this);
    }

    public Vector2D subtract(float x, float y) {
        Vector2D temp = new Vector2D(x, y);
        return this.subtract(temp);
    }

    public Vector2D normalVector() {
        return new Vector2D(this.getY(), -this.getX());
    }

    //
    // Static fields and methods
    //
    private final static int X = 0;
    private final static int Y = 1;
    private final static int NUM_DIMENSIONS = 2;
}
