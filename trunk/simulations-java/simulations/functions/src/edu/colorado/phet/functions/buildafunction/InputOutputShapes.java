// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.buildafunction;

import fj.data.List;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.colorado.phet.functions.model.Type;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.v;
import static edu.colorado.phet.functions.buildafunction.Constants.ellipseWidth;

/**
 * @author Sam Reid
 */
public class InputOutputShapes {

    public static Area roundedLeftSide( Vector2D attachmentPoint ) {
        return new Area( new Ellipse2D.Double( attachmentPoint.x - ellipseWidth / 2, attachmentPoint.y - ellipseWidth / 2, ellipseWidth, ellipseWidth ) );
    }

    public static Area roundedRightSide( Vector2D attachmentPoint ) {
        return new Area( new Ellipse2D.Double( attachmentPoint.x - ellipseWidth / 2, attachmentPoint.y - ellipseWidth / 2, ellipseWidth, ellipseWidth ) );
    }

    public static Area angledRightSide( Vector2D attachmentPoint ) {
        return new Area( ShapeUtils.createShapeFromPoints( List.list( v( attachmentPoint.x, attachmentPoint.y - ellipseWidth / 2 ),
                                                                      v( attachmentPoint.x + ellipseWidth / 2, attachmentPoint.y ),
                                                                      v( attachmentPoint.x, attachmentPoint.y + ellipseWidth / 2 ) ) ) );
    }

    public static Area angledLeftSide( Vector2D attachmentPoint ) {
        return new Area( ShapeUtils.createShapeFromPoints( List.list( v( attachmentPoint.x, attachmentPoint.y - ellipseWidth / 2 ),
                                                                      v( attachmentPoint.x - ellipseWidth / 2, attachmentPoint.y ),
                                                                      v( attachmentPoint.x, attachmentPoint.y + ellipseWidth / 2 ) ) ) );
    }

    public static Area squareLeftSide( Vector2D attachmentPoint ) {
        return new Area( ShapeUtils.createShapeFromPoints( List.list( v( attachmentPoint.x, attachmentPoint.y - ellipseWidth / 2 ),
                                                                      v( attachmentPoint.x - ellipseWidth / 2, attachmentPoint.y - ellipseWidth / 2 ),
                                                                      v( attachmentPoint.x - ellipseWidth / 2, attachmentPoint.y + ellipseWidth / 2 ),
                                                                      v( attachmentPoint.x, attachmentPoint.y + ellipseWidth / 2 ) ) ) );
    }

    public static Area squareRightSide( Vector2D attachmentPoint ) {
        return new Area( ShapeUtils.createShapeFromPoints( List.list( v( attachmentPoint.x, attachmentPoint.y - ellipseWidth / 2 ),
                                                                      v( attachmentPoint.x + ellipseWidth / 2, attachmentPoint.y - ellipseWidth / 2 ),
                                                                      v( attachmentPoint.x + ellipseWidth / 2, attachmentPoint.y + ellipseWidth / 2 ),
                                                                      v( attachmentPoint.x, attachmentPoint.y + ellipseWidth / 2 ) ) ) );
    }

    //Shapes are for unary functions, subtracted from left and added to the right.
    public static Area getLeftSide( final Type type, Vector2D attachmentPoint ) {
        return type == Type.SHAPE ? roundedLeftSide( attachmentPoint ) :
               type == Type.TEXT ? angledLeftSide( attachmentPoint ) :
               type == Type.NUMBER ? squareLeftSide( attachmentPoint ) :
               null;
    }

    public static Area getRightSide( final Type type, Vector2D attachmentPoint ) {
        return type == Type.SHAPE ? roundedRightSide( attachmentPoint ) :
               type == Type.TEXT ? angledRightSide( attachmentPoint ) :
               type == Type.NUMBER ? squareRightSide( attachmentPoint ) :
               null;
    }
}