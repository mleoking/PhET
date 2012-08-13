// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.math.Ray3F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F.MatrixType;

import static org.lwjgl.opengl.GL11.glMultMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * Allows forward and inverse transforms with the power of full 4x4 matrices, allowing
 * affine, perspective, and other transformations.
 * <p/>
 * Has separate functions for transforming position, deltas, and normals (as they are all different)
 */
public class LWJGLTransform {
    public final Notifier<LWJGLTransform> changed = new ValueNotifier<LWJGLTransform>( this );

    private ImmutableMatrix4F matrix;
    private ImmutableMatrix4F inverse;

    // various buffers for quick handling of the model-view transform
    private FloatBuffer transformBuffer = BufferUtils.createFloatBuffer( 16 );
    private FloatBuffer inverseTransformBuffer = BufferUtils.createFloatBuffer( 16 );

    public LWJGLTransform() {
        set( ImmutableMatrix4F.IDENTITY );
    }

    public LWJGLTransform( ImmutableMatrix4F matrix ) {
        set( matrix );
    }

    public void set( ImmutableMatrix4F matrix ) {
        this.matrix = matrix;
        // store the inverse matrix
        inverse = matrix.inverted();

        // store both into buffers so they can be sent to OpenGL with less overhead
        matrix.writeToBuffer( transformBuffer );
        inverse.writeToBuffer( inverseTransformBuffer );

        changed.updateListeners( this );
    }

    public void prepend( ImmutableMatrix4F matrix ) {
        set( matrix.times( this.matrix ) );
    }

    public void append( ImmutableMatrix4F matrix ) {
        set( this.matrix.times( matrix ) );
    }

    public void append( LWJGLTransform transform ) {
        append( transform.matrix );
    }

    public ImmutableMatrix4F getMatrix() {
        return matrix;
    }

    public ImmutableMatrix4F getInverse() {
        return inverse;
    }

    public void apply() {
        switch( matrix.type ) {
            case SCALING:
                glScalef( matrix.m00, matrix.m11, matrix.m22 );
                break;
            case TRANSLATION:
                glTranslatef( matrix.m30, matrix.m31, matrix.m32 );
                break;
            default:
                // fall back to sending the entire matrix
                transformBuffer.rewind();
                glMultMatrix( transformBuffer );
        }
    }

    public boolean isIdentity() {
        return matrix.type == MatrixType.IDENTITY;
    }

    /*---------------------------------------------------------------------------*
    * forward transforms
    *----------------------------------------------------------------------------*/

    // transform a position (includes translation)
    public Vector3F transformPosition( Vector3F position ) {
        return matrix.times( position );
    }

    // transform a vector (excludes translation, so positional offsets are maintained). use this for transform(B-A)
    public Vector3F transformDelta( Vector3F vector ) {
        return matrix.timesVector( vector );
    }

    // transform a normal vector (different from the others)
    public Vector3F transformNormal( Vector3F normal ) {
        return inverse.timesTranspose( normal );
    }

    public float transformDeltaX( float x ) {
        return transformDelta( new Vector3F( x, 0, 0 ) ).x;
    }

    public float transformDeltaY( float y ) {
        return transformDelta( new Vector3F( 0, y, 0 ) ).y;
    }

    public float transformDeltaZ( float z ) {
        return transformDelta( new Vector3F( 0, 0, z ) ).z;
    }

    public Ray3F transformRay( Ray3F ray ) {
        return new Ray3F( transformPosition( ray.pos ), transformPosition( ray.pos.plus( ray.dir ) ).minus( transformPosition( ray.pos ) ) );
    }

    /*---------------------------------------------------------------------------*
    * inverse transforms
    *----------------------------------------------------------------------------*/

    // inverse transform a position (includes translation)
    public Vector3F inversePosition( Vector3F position ) {
        return inverse.times( position );
    }

    // inverse transform a vector (excludes translation, so positional offsets are maintained). use this for transform(B-A)
    public Vector3F inverseDelta( Vector3F vector ) {
        return inversePosition( vector ).minus( inversePosition( new Vector3F() ) );
    }

    // inverse transform a normal vector (different from the others)
    public Vector3F inverseNormal( Vector3F normal ) {
        return matrix.timesTranspose( normal );
    }

    public float inverseDeltaX( float x ) {
        return inverseDelta( new Vector3F( x, 0, 0 ) ).x;
    }

    public float inverseDeltaY( float y ) {
        return inverseDelta( new Vector3F( 0, y, 0 ) ).y;
    }

    public float inverseDeltaZ( float z ) {
        return inverseDelta( new Vector3F( 0, 0, z ) ).z;
    }

    public Ray3F inverseRay( Ray3F ray ) {
        return new Ray3F( inversePosition( ray.pos ), inversePosition( ray.pos.plus( ray.dir ) ).minus( inversePosition( ray.pos ) ) );
    }
}
