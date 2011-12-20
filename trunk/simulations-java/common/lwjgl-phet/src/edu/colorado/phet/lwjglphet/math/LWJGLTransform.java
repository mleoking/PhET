// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F.MatrixType;

import static org.lwjgl.opengl.GL11.*;

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
        matrix.store( transformBuffer );
        inverse.store( inverseTransformBuffer );
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
}
