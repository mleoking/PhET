// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows a small caret ^ under the center of the play area, to indicate the line that must be crossed in order for one team to win.
 *
 * @author Sam Reid
 */
class CaretNode extends PNode {
    public CaretNode() {
        addChild( new PhetPPath( createUnclosedShapeFromPoints( List.list( Vector2D.v( -10, 10 ), Vector2D.v( 0, 0 ), Vector2D.v( 10, 10 ) ) ), new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL ), Color.black ) );
    }

    private static Shape createUnclosedShapeFromPoints( Iterable<Vector2D> points ) {
        Iterator<Vector2D> iterator = points.iterator();
        DoubleGeneralPath path = new DoubleGeneralPath( iterator.next() );
        while ( iterator.hasNext() ) {
            path.lineTo( iterator.next() );
        }
        return path.getGeneralPath();
    }
}