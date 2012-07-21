// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.CircularShapeFunction;
import edu.umd.cs.piccolo.PNode;

/**
 * Some copied from NumberNode, may need to be remerged.
 *
 * @author Sam Reid
 */
public class SimpleContainerNode extends PNode {

    static final double scale = 1.7;
    public static final double width = 130 * scale;
    public static final double height = 55 * scale;

    public SimpleContainerNode( final int number, final Color fill ) {
        final PNode content = new PNode() {{
            for ( int i = 0; i < number; i++ ) {
                final double pieceWidth = width / number;
                if ( fill != null ) {
                    addChild( new PhetPPath( new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, height ), fill, new BasicStroke( 1 ), Color.black ) );
                }
                else {
                    addChild( new PhetPPath( new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, height ), new BasicStroke( 1 ), Color.black ) );
                }
            }
            //Thicker outer stroke
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), new BasicStroke( 2 ), Color.black ) );
        }};

        addChild( content );
    }

    public static Rectangle2D.Double createRect( int number ) {
        final double pieceWidth = width / number;
        return new Rectangle2D.Double( pieceWidth * number, 0, pieceWidth, height );
    }

    public static Shape createPieSlice( int number ) {
        final double extent = Math.PI * 2.0 / number;
        return new CircularShapeFunction( extent, 100 ).createShape( Vector2D.ZERO, 0.0 );
    }
}