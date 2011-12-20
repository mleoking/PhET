// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.math;

/**
 * LWJGL 3D version of our ModelViewTransform
 * TODO: add normal transformations! see http://www.opengl.org/documentation/specs/version1.1/glspec1.1/node26.html
 * TODO: flesh it out and document
 */
public class LWJGLModelViewTransform extends LWJGLTransform {

    public LWJGLModelViewTransform( ImmutableMatrix4F transform ) {
        super( transform );
    }

    /*---------------------------------------------------------------------------*
    * model to view
    *----------------------------------------------------------------------------*/

    public ImmutableVector3F modelToView( ImmutableVector3F vector ) {
        return getMatrix().times( vector );
    }

    public ImmutableVector3F modelToViewDelta( ImmutableVector3F vector ) {
        return getMatrix().times( vector ).minus( getMatrix().times( ImmutableVector3F.ZERO ) );
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
        return getInverse().times( vector );
    }

    public ImmutableVector3F viewToModelDelta( ImmutableVector3F vector ) {
        return getInverse().times( vector ).minus( getInverse().times( ImmutableVector3F.ZERO ) );
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
