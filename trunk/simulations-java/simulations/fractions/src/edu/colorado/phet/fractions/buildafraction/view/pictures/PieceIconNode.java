// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

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

import static edu.colorado.phet.fractions.buildafraction.view.pictures.SimpleContainerNode.*;

/**
 * Shows an icon in the toolbox with empty pieces.  The draggable pieces are stacked on top.
 *
 * @author Sam Reid
 */
public class PieceIconNode extends PNode {

    public static final double TINY_SCALE = 0.43;

    public PieceIconNode( int divisions, ShapeType shapeType ) {
        for ( int i = 0; i < divisions; i++ ) {
            addChild( new PhetPPath( shapeType == ShapeType.BAR ? barShape( divisions, i ) : pieShape( divisions, i ), Color.white, new BasicStroke( 1 ), Color.black ) );
        }
        //Thicker outer stroke
        addChild( new PhetPPath( shapeType == ShapeType.BAR ? new Rectangle2D.Double( 0, 0, rectangleWidth, rectangleHeight ) :
                                 new Ellipse2D.Double( 0, 0, circleDiameter, circleDiameter ), new BasicStroke( 2 ), Color.black ) );

        scale( TINY_SCALE );
    }

    private Shape pieShape( final int divisions, final int i ) {return new CircularShapeFunction( Math.PI * 2 / divisions, circleDiameter / 2 ).createShape( Vector2D.v( circleDiameter / 2, circleDiameter / 2 ), Math.PI * 2 / divisions * i );}

    private Rectangle2D.Double barShape( final int divisions, final int i ) {
        final double pieceWidth = rectangleWidth / divisions;
        return new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, rectangleHeight );
    }
}