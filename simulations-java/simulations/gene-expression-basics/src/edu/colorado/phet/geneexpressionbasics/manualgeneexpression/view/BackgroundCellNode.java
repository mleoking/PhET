// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A Piccolo node that looks like a cell (as in the biology concept of a cell).
 * This is used when cells are needed in the background and have no
 * corresponding model component.
 *
 * @author John Blanco
 */
public class BackgroundCellNode extends PNode {

    public static final Dimension2D DEFAULT_SIZE = new PDimension( DnaMolecule.MOLECULE_LENGTH * 1.5, DnaMolecule.MOLECULE_LENGTH * 0.75 ); // In screen coordinates, which are roughly the same as pixels.
    private static final Color CELL_INTERIOR_COLOR = new Color( 190, 231, 251 );

    public BackgroundCellNode( Point2D centerLocation, int seed ) {
        addChild( new PhetPPath( createShape( centerLocation, DEFAULT_SIZE, seed ),
                                 CELL_INTERIOR_COLOR,
                                 new BasicStroke( 100f ), // This is big because the cell is only ever shown when zoomed way out.
                                 Color.BLACK ) );
    }

    //    private static Shape createShape( Point2D initialPosition, Dimension2D size, long seed ) {
//        return BioShapeUtils.createCurvyEnclosedShape( new Rectangle2D.Double( initialPosition.getX() - size.getWidth() / 2,
//                                                                               initialPosition.getY() - size.getHeight() / 2,
//                                                                               size.getWidth(),
//                                                                               size.getHeight() ),
//                                                       0.4,
//                                                       seed );
//    }
    private static Shape createShape( Point2D centerPosition, Dimension2D size, long seed ) {
        assert size.getWidth() > size.getHeight(); // Param checking.  Can't create the needed shape if this isn't true.

        // Tweakable parameters that affect number of points used to define the
        // shape.
        final int numPointsPerLineSegment = 3;
        final int numPointsPerCurvedSegment = 3;
        int currentIndex = 0;

        // The list of points that will define the shape.
        List<Point2D> pointList = new ArrayList<Point2D>();

        // Variables needed for the calculations.
        double curveRadius = size.getHeight() / 2;
        double lineLength = size.getWidth() - size.getHeight();
        double rightCurveCenterX = centerPosition.getX() + size.getHeight() / 2;
        double leftCurveCenterX = centerPosition.getX() - size.getHeight() / 2;
        double centerY = centerPosition.getY();

        // Create a shape that is like E. Coli.  Start at the left side of the
        // line that defines the top edge and move around the shape in a
        // clockwise direction.

        // Add points for the top line.
        for ( int i = 0; i < numPointsPerLineSegment; i++ ) {
            pointList.add( new Point2D.Double( leftCurveCenterX + i * ( lineLength / ( numPointsPerLineSegment - 1 ) ), centerY - size.getHeight() / 2 ) );
        }
        // Add points that define the right curved edge.  Skip what would be
        // the first point, because it would overlap with the previous segment.
        for ( int i = 1; i < numPointsPerCurvedSegment; i++ ) {
            double angle = -Math.PI / 2 + i * ( Math.PI / ( numPointsPerCurvedSegment - 1 ) );
            pointList.add( new Point2D.Double( rightCurveCenterX + curveRadius * Math.cos( angle ), curveRadius * Math.sin( angle ) ) );
        }
        // Add points that define the bottom line.  Skip what would be
        // the first point, because it would overlap with the previous segment.
        for ( int i = 1; i < numPointsPerLineSegment; i++ ) {
            pointList.add( new Point2D.Double( rightCurveCenterX - i * ( lineLength / ( numPointsPerLineSegment - 1 ) ), centerY + size.getHeight() / 2 ) );
        }
        // Add points that define the left curved side.  Skip what would be
        // the first point and last points, because the would overlap with the
        // previous and next segment (respectively).
        for ( int i = 1; i < numPointsPerCurvedSegment - 1; i++ ) {
            double angle = Math.PI / 2 + i * ( Math.PI / ( numPointsPerCurvedSegment - 1 ) );
            pointList.add( new Point2D.Double( leftCurveCenterX + curveRadius * Math.cos( angle ), curveRadius * Math.sin( angle ) ) );
        }

//        pointList.add( new Point2D.Double( rightCurveCenterX + curveRadius, centerY ) );
//        pointList.add( new Point2D.Double( centerPosition.getX(), centerY + size.getHeight() / 2) );
//        pointList.add( new Point2D.Double( leftCurveCenterX - curveRadius, centerY ) );

        // Create a shape that is like E. Coli.  Start at leftmost edge and add
        // points as though moving clockwise around the shape.
//        pointList.add( new Point2D.Double( centerX - size.getWidth() / 2, centerY ) );
//        pointList.add( new Point2D.Double( centerX - size.getWidth() / 2 + size.getHeight() / 2, centerY - size.getHeight() / 2 ) );
//        pointList.add( new Point2D.Double( centerX, centerY - size.getHeight() / 2 ) );
//        pointList.add( new Point2D.Double( centerX + size.getWidth() / 2 - size.getHeight() / 2, centerY - size.getHeight() / 2 ) );
//        pointList.add( new Point2D.Double( centerX + size.getWidth() / 2, centerY ) ); // Rightmost point.
//        pointList.add( new Point2D.Double( centerX + size.getWidth() / 2 - size.getHeight() / 2, centerY + size.getHeight() / 2 ) );
//        pointList.add( new Point2D.Double( centerX, centerY + size.getHeight() / 2 ) );
//        pointList.add( new Point2D.Double( centerX - size.getWidth() / 2 + size.getHeight() / 2, centerY + size.getHeight() / 2 ) );

        return BioShapeUtils.createRoundedShapeFromPoints( pointList );
    }
}
