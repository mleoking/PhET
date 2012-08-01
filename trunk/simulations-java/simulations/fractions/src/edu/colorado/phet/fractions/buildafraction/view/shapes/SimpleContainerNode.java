// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.CircularShapeFunction;
import edu.umd.cs.piccolo.PNode;

/**
 * Some copied from NumberNode, may need to be remerged.
 *
 * @author Sam Reid
 */
public class SimpleContainerNode extends PNode {

    static final double scale = 1.7;
    public static final double rectangleWidth = 130 * scale;
    public static final double rectangleHeight = 55 * scale;

    public static final double circleDiameter = 100 * scale;

    public SimpleContainerNode( final int number, final Color fill, final ShapeType shapeType ) {
        final PNode content = new PNode() {{
            for ( int i = 0; i < number; i++ ) {
                if ( shapeType == ShapeType.BAR ) {
                    final double pieceWidth = rectangleWidth / number;
                    addChild( new PhetPPath( new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, rectangleHeight ), fill, new BasicStroke( 1 ), Color.black ) );
                }
            }
            //Thicker outer stroke
            addChild( new PhetPPath( shapeType == ShapeType.BAR ? new Rectangle2D.Double( 0, 0, rectangleWidth, rectangleHeight ) :
                                     new Ellipse2D.Double( 0, 0, circleDiameter, circleDiameter ), new BasicStroke( 2 ), Color.black ) );
        }};

        addChild( content );
    }

    public static Shape createRect( int number ) {
        final double pieceWidth = rectangleWidth / number;
        return new Rectangle2D.Double( pieceWidth * number, 0, pieceWidth, rectangleHeight );
    }

    public static Shape createPieSlice( int number ) {
        final double extent = Math.PI * 2.0 / number;
        return new CircularShapeFunction( extent, circleDiameter / 2 ).createShape( Vector2D.ZERO, 0.0 );
    }
}