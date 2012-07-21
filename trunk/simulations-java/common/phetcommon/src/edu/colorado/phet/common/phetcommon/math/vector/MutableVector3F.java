// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.math.vector;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Provides a mutable version of AbstractVector3F
 *
 * @author Jonathan Olson
 */
public @EqualsAndHashCode(callSuper = false) @ToString class MutableVector3F extends AbstractVector3F {
    private float x;
    private float y;
    private float z;

    public MutableVector3F() {}

    public MutableVector3F( AbstractVector3F v ) { this( v.getX(), v.getY(), v.getZ() ); }

    public MutableVector3F( float x, float y, float z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override public float getX() { return x; }

    @Override public float getY() { return y; }

    @Override public float getZ() { return z; }

    public MutableVector3F add( AbstractVector3F v ) {
        setX( getX() + v.getX() );
        setY( getY() + v.getY() );
        setZ( getZ() + v.getZ() );
        return this;
    }

    public MutableVector3F normalize() {
        float magnitude = getMagnitude();
        if ( magnitude == 0 ) {
            throw new UnsupportedOperationException( "Cannot normalize a zero-magnitude vector." );
        }
        return scale( 1f / magnitude );
    }

    public MutableVector3F scale( float scale ) {
        setX( getX() * scale );
        setY( getY() * scale );
        setZ( getZ() * scale );
        return this;
    }

    public void setX( float x ) { this.x = x; }

    public void setY( float y ) { this.y = y; }

    public void setZ( float z ) { this.z = z; }

    // The reason that this seemingly redundant override exists is to make
    // this method public.
    public void setComponents( float x, float y, float z ) {
        setX( x );
        setY( y );
        setY( z );
    }

    public void setValue( AbstractVector3F value ) { setComponents( value.getX(), value.getY(), value.getZ() ); }

    public void setMagnitude( float magnitude ) {
        normalize();
        scale( magnitude );
    }

    public MutableVector3F subtract( AbstractVector3F v ) {
        setX( getX() - v.getX() );
        setY( getY() - v.getY() );
        setZ( getZ() - v.getZ() );
        return this;
    }
}