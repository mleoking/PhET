// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;

import static org.lwjgl.opengl.GL11.*;

/**
 * Displays a quad-based polygonal grid tesselated with triangles. Think of it as a table of
 * vertices, each connected to its neighbors.
 */
public class GridMesh {
    private int rows;
    private int columns;
    private ImmutableVector3D[] positions;

    /**
     * @param rows      How many rows of vertices there are in positions
     * @param columns   How many columns of vertices  there are in positions
     * @param positions Vertices. Should be rows*columns of them
     */
    public GridMesh( int rows, int columns, ImmutableVector3D[] positions ) {
        this.rows = rows;
        this.columns = columns;
        this.positions = positions;
    }

    public void render() {
        // renders in a series of triangle strips. quad strips rejected since we can't guarantee they will be planar
        for ( int row = 0; row < rows - 1; row++ ) {
            glBegin( GL_TRIANGLE_STRIP );
//            glBegin( GL_POINTS );
            for ( int col = 0; col < columns; col++ ) {
                // top point
                LWJGLUtils.texCoord2d( getTextureCoordinate( row, col ) );
                LWJGLUtils.normal3d( getNormal( row, col ) );
                LWJGLUtils.vertex3d( getPosition( row, col ) );

                // bottom point
                LWJGLUtils.texCoord2d( getTextureCoordinate( row + 1, col ) );
                LWJGLUtils.normal3d( getNormal( row + 1, col ) );
                LWJGLUtils.vertex3d( getPosition( row + 1, col ) );
            }
            glEnd();
        }
    }

    public ImmutableVector3D getPosition( int row, int col ) {
        return positions[row * columns + col];
    }

    public ImmutableVector2D getTextureCoordinate( int row, int col ) {
        // return a default mapping that covers the entire texture
        return new ImmutableVector2D( ( (float) row ) / ( (float) ( rows - 1 ) ),
                                      ( (float) col ) / ( (float) ( columns - 1 ) ) );
    }

    public ImmutableVector3D getNormal( int row, int col ) {
        // basic approximation based on neighbors.
        ImmutableVector3D position = getPosition( row, col );
        ImmutableVector3D up, down, left, right;

        // calculate up/down vectors
        if ( row > 0 ) {
            up = getPosition( row - 1, col ).minus( position );
            down = ( row < rows - 1 ) ? getPosition( row + 1, col ).minus( position ) : up.negated();
        }
        else {
            down = getPosition( row + 1, col ).minus( position );
            up = down.negated();
        }

        // calculate left/right vectors
        if ( col > 0 ) {
            left = getPosition( row, col - 1 ).minus( position );
            right = ( col < columns - 1 ) ? getPosition( row, col + 1 ).minus( position ) : left.negated();
        }
        else {
            right = getPosition( row, col + 1 ).minus( position );
            left = right.negated();
        }

        ImmutableVector3D normal = new ImmutableVector3D();
        // basically, sum up the normals of each quad this vertex is part of, and take the average
        normal = normal.plus( right.cross( up ).normalized() );
        normal = normal.plus( up.cross( left ).normalized() );
        normal = normal.plus( left.cross( down ).normalized() );
        normal = normal.plus( down.cross( right ).normalized() );
        return normal.normalized();
    }
}
