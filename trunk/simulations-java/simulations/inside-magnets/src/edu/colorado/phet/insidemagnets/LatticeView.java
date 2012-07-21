// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.insidemagnets;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class LatticeView extends PNode {
    public LatticeView( final ModelViewTransform2D modelViewTransform2D, final InsideMagnetsModel model ) {
        model.addStepListener( new SimpleObserver() {
            public void update() {
                removeAllChildren();
                Lattice<Cell> lattice = model.getLattice();
                for ( Point location : lattice.getLocations() ) {
                    Point viewLocation = modelViewTransform2D.modelToView( location );
                    final Vector2D displayValue = lattice.getValue( location ).getSpinVector().times( 30 );

                    final ArrowNode arrowNode = new ArrowNode( new Point2D.Double(), displayValue.toPoint2D(), 10, 10, 4, 0.5, true );
                    arrowNode.setPaint( Color.white );
                    arrowNode.setOffset( viewLocation.getX() - displayValue.getX() / 2, viewLocation.getY() - displayValue.getY() / 2 );
                    addChild( arrowNode );

                    //Draw the cell center for debugging purposes
//                    final PhetPPath cellCenter = new PhetPPath( new Rectangle2D.Double( -1, -1, 2, 2 ), Color.red );
//                    cellCenter.setOffset( viewLocation );
//                    addChild( cellCenter );
                }
            }
        } );
    }
}
