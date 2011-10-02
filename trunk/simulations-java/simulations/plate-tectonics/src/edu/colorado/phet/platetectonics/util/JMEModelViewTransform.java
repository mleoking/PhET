// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.util;

import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;

/**
 * JME 3D version of our ModelViewTransform
 * TODO: add normal transformations! see http://www.opengl.org/documentation/specs/version1.1/glspec1.1/node26.html
 * TODO: flesh it out and document
 */
public class JMEModelViewTransform {
    private Matrix4f transform;
    private Matrix4f inverseTransform;

    public JMEModelViewTransform( Matrix4f transform ) {
        setTransform( transform );
    }

    public void setTransform( Matrix4f transform ) {
        this.transform = transform;
        this.inverseTransform = transform.invert();
    }

    /*---------------------------------------------------------------------------*
    * model to view
    *----------------------------------------------------------------------------*/

    public Vector3f modelToView( Vector3f vector ) {
        return transform.mult( vector );
    }

    public Vector3f modelToViewDelta( Vector3f vector ) {
        return transform.mult( vector ).subtract( transform.mult( Vector3f.ZERO ) );
    }

    public float modelToViewDeltaX( float x ) {
        return modelToViewDelta( new Vector3f( x, 0, 0 ) ).x;
    }

    public float modelToViewDeltaY( float y ) {
        return modelToViewDelta( new Vector3f( 0, y, 0 ) ).y;
    }

    public float modelToViewDeltaZ( float z ) {
        return modelToViewDelta( new Vector3f( 0, 0, z ) ).z;
    }

    /*---------------------------------------------------------------------------*
    * view to model
    *----------------------------------------------------------------------------*/

    public Vector3f viewToModel( Vector3f vector ) {
        return inverseTransform.mult( vector );
    }

    public Vector3f viewToModelDelta( Vector3f vector ) {
        return inverseTransform.mult( vector ).subtract( inverseTransform.mult( Vector3f.ZERO ) );
    }

    public float viewToModelDeltaX( float x ) {
        return viewToModelDelta( new Vector3f( x, 0, 0 ) ).x;
    }

    public float viewToModelDeltaY( float y ) {
        return viewToModelDelta( new Vector3f( 0, y, 0 ) ).y;
    }

    public float viewToModelDeltaZ( float z ) {
        return viewToModelDelta( new Vector3f( 0, 0, z ) ).z;
    }
}
