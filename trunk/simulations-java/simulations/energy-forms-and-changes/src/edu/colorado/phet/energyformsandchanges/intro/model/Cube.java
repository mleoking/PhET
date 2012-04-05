// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * @author John Blanco
 */
public abstract class Cube extends UserMovableModelElement {

    // Height and width of the face, which is square.
    public static final double FACE_SIZE = 0.05; // In meters.

    // Flattened size of the top and visible side.
    private static final double EDGE_AND_TOP_SIZE = FACE_SIZE * 0.25;

    public abstract Color getColor();

    public abstract String getLabel();

    public void setOnSurface( Point2D surfaceLocation ) {
        // TODO
    }

    /**
     * Get the "raw shape" that should be used for depicting this cube in the
     * view.  In this context, "raw" means that it is untranslated.  By
     * convention for this simulation, the point (0, 0) is the lower left
     * corner of the cube.
     *
     * @return
     */
    public static Shape getRawShape() {
        DoubleGeneralPath path = new DoubleGeneralPath( new Point2D.Double( 0, 0 ) );
        // Set up variables that will be used throughout the drawing process.
        ImmutableVector2D upperLeftCornerOfFace = new ImmutableVector2D( 0, FACE_SIZE );
        ImmutableVector2D upperRightCornerOfFace = new ImmutableVector2D( FACE_SIZE, FACE_SIZE );
        ImmutableVector2D lowerRightCornerOfFace = new ImmutableVector2D( FACE_SIZE, 0 );
        // Draw the face.
        path.lineTo( upperLeftCornerOfFace );
        path.lineTo( upperRightCornerOfFace );
        path.lineTo( lowerRightCornerOfFace );
        // Draw the top.
        path.moveTo( upperLeftCornerOfFace );
        path.lineTo( upperLeftCornerOfFace.getAddedInstance( EDGE_AND_TOP_SIZE, EDGE_AND_TOP_SIZE ) );
        path.lineTo( upperRightCornerOfFace.getAddedInstance( EDGE_AND_TOP_SIZE, EDGE_AND_TOP_SIZE ) );
        path.lineTo( upperRightCornerOfFace );
        path.lineTo( upperLeftCornerOfFace );
        // Draw the side.
        path.moveTo( upperRightCornerOfFace );
        path.lineTo( upperRightCornerOfFace.getAddedInstance( EDGE_AND_TOP_SIZE, EDGE_AND_TOP_SIZE ) );
        path.lineTo( lowerRightCornerOfFace.getAddedInstance( EDGE_AND_TOP_SIZE, EDGE_AND_TOP_SIZE ) );
        path.lineTo( lowerRightCornerOfFace );
        path.lineTo( upperRightCornerOfFace );

        return path.getGeneralPath();
    }

    @Override public IUserComponentType getUserComponentType() {
        // Movable elements are considered sprites.
        return UserComponentTypes.sprite;
    }
}
