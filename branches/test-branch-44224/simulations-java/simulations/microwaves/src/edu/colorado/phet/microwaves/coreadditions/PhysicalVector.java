/*
 * Class: PhysicalVector
 * Package: edu.colorado.phet.common.physics
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 3:50:24 PM
 */
package edu.colorado.phet.microwaves.coreadditions;


/**
 * This class represents mathmatical vectors. It has package visibility,
 * and is intended to be subclassed to represent vectors of specific
 * dimensionalities.
 *
 * @see edu.colorado.phet.microwaves.coreadditions.Vector2D
 */
public class PhysicalVector {

    private float[] scalars;

    protected PhysicalVector( int numDimensions ) {
        scalars = new float[numDimensions];
    }

    protected float getScalarAt( int idx ) {
        return scalars[idx];
    }

    protected void setScalarAt( int idx, float value ) {
        scalars[idx] = value;
    }

    public boolean equals( Object obj ) {
        boolean result = true;
        if ( this.getClass() != obj.getClass() ) {
            result = false;
        }
        else {
            PhysicalVector that = (PhysicalVector) obj;
            for ( int i = 0; result == true && i < scalars.length; i++ ) {
                if ( this.scalars[i] != that.scalars[i] ) {
                    result = false;
                }
            }
        }
        return result;
    }

    public float getMagnitudeSq() {
        float sum = 0;
        for ( int i = 0; i < scalars.length; i++ ) {
            sum += scalars[i] * scalars[i];
        }
        return sum;
    }

    public float getMagnitude() {
        return (float) Math.sqrt( getMagnitudeSq() );
    }

    public float getLength() {
        return getMagnitude();
    }

    protected PhysicalVector add( PhysicalVector that, PhysicalVector result ) {

        // TODO check that vectors are the same length, or class
        for ( int i = 0; i < scalars.length; i++ ) {
            result.scalars[i] = this.scalars[i] + that.scalars[i];
        }
        return result;
    }

    protected PhysicalVector generalNormalize() {
        float length = getLength();
        return multiply( 1.0f / length, this );
    }

    protected PhysicalVector multiply( float scale, PhysicalVector result ) {
        for ( int i = 0; i < scalars.length; i++ ) {
            result.scalars[i] = scalars[i] * scale;
        }
        return result;
    }

    public float dot( PhysicalVector that ) {

        // TODO check that vectors are the same length, or class

        float result = 0;
        for ( int i = 0; i < scalars.length; i++ ) {
            result += this.scalars[i] * that.scalars[i];
        }
        return result;
    }

    public float distance( PhysicalVector that ) {
        return (float) Math.sqrt( distanceSquared( that ) );
    }

    public float distanceSquared( PhysicalVector that ) {

        // Check that operation can be done
        if ( this.scalars.length != that.scalars.length ) {
            throw new RuntimeException( "Vectors of different dimensionalities set to PhysicalVector.distanceSquared" );
        }
        float result = 0;
        for ( int i = 0; i < scalars.length; i++ ) {
            float diff = this.scalars[i] - that.scalars[i];
            result += diff * diff;
        }
        return result;
    }

    protected PhysicalVector subtract( PhysicalVector that, PhysicalVector result ) {

        // TODO check that vectors are the same length, or class

        for ( int i = 0; i < scalars.length; i++ ) {
            result.scalars[i] = this.scalars[i] - that.scalars[i];
        }
        return result;
    }

    /*
    public static Vector2D average( Vector2D[] dp ) {
        phys2d.imaging.Average x = new phys2d.imaging.Average();
        phys2d.imaging.Average y = new phys2d.imaging.Average();
        for( int i = 0; i < dp.length; i++ ) {
            x.layoutPane( dp[i].getX() );
            y.layoutPane( dp[i].getY() );
        }
        return new Vector2D( x.value(), y.value() );
    }
    */
}
