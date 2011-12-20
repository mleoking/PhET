// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.util;

import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;

/**
 * LWJGL 3D version of our ModelViewTransform
 * TODO: add normal transformations! see http://www.opengl.org/documentation/specs/version1.1/glspec1.1/node26.html
 * TODO: flesh it out and document
 */
public class LWJGLModelViewTransform {
    private ImmutableMatrix4F transform;
    private ImmutableMatrix4F inverseTransform;

    public LWJGLModelViewTransform( ImmutableMatrix4F transform ) {
        setTransform( transform );
    }

    public void setTransform( ImmutableMatrix4F transform ) {
        this.transform = transform;
        this.inverseTransform = transform.inverted();
    }

    /*---------------------------------------------------------------------------*
    * model to view
    *----------------------------------------------------------------------------*/

    public ImmutableVector3F modelToView( ImmutableVector3F vector ) {
        return transform.times( vector );
    }

    public ImmutableVector3F modelToViewDelta( ImmutableVector3F vector ) {
        return transform.times( vector ).minus( transform.times( ImmutableVector3F.ZERO ) );
    }

    public float modelToViewDeltaX( float x ) {
        return modelToViewDelta( new ImmutableVector3F( x, 0, 0 ) ).x;
    }

    public float modelToViewDeltaY( float y ) {
        return modelToViewDelta( new ImmutableVector3F( 0, y, 0 ) ).y;
    }

    public float modelToViewDeltaZ( float z ) {
        return modelToViewDelta( new ImmutableVector3F( 0, 0, z ) ).z;
    }

    /*---------------------------------------------------------------------------*
    * view to model
    *----------------------------------------------------------------------------*/

    public ImmutableVector3F viewToModel( ImmutableVector3F vector ) {
        return inverseTransform.times( vector );
    }

    public ImmutableVector3F viewToModelDelta( ImmutableVector3F vector ) {
        return inverseTransform.times( vector ).minus( inverseTransform.times( ImmutableVector3F.ZERO ) );
    }

    public float viewToModelDeltaX( float x ) {
        return viewToModelDelta( new ImmutableVector3F( x, 0, 0 ) ).x;
    }

    public float viewToModelDeltaY( float y ) {
        return viewToModelDelta( new ImmutableVector3F( 0, y, 0 ) ).y;
    }

    public float viewToModelDeltaZ( float z ) {
        return viewToModelDelta( new ImmutableVector3F( 0, 0, z ) ).z;
    }
}
