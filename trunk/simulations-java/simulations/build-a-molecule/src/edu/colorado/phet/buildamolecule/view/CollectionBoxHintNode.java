//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Timer;
import java.util.TimerTask;

import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

public class CollectionBoxHintNode extends PNode {
    public CollectionBoxHintNode( ModelViewTransform mvt, PBounds moleculeDestinationBounds, CollectionBox box ) {

        // at the end, where our tip and tail should be
        final ImmutableVector2D tipTarget = mvt.modelToView( new ImmutableVector2D( box.getDropBounds().getMinX() - 20, box.getDropBounds().getCenterY() ) );
        final ImmutableVector2D tailTarget;

        // molecule view bounds on creation
        final Rectangle2D moleculeViewBounds = mvt.modelToViewRectangle( moleculeDestinationBounds );

        double maxArrowLength = 100;
        final ImmutableVector2D tailStart = mvt.modelToView( new ImmutableVector2D( moleculeDestinationBounds.getMaxX() + 20, moleculeDestinationBounds.getCenterY() ) );
        if ( tipTarget.getDistance( tailStart ) > maxArrowLength ) {
            tailTarget = tailStart.getSubtractedInstance( tipTarget ).getNormalizedInstance().getScaledInstance( maxArrowLength ).getAddedInstance( tipTarget );
        }
        else {
            tailTarget = tailStart;
        }

        final ImmutableVector2D tailOffset = tipTarget.getSubtractedInstance( tailTarget );
        final ImmutableVector2D tipStart = tailStart.getAddedInstance( tailOffset );

        final ArrowNode blueArrow = new ArrowNode( tailStart.toPoint2D(), tipStart.toPoint2D(), 30, 40, 20 ) {{
            setPaint( Color.BLUE );
        }};
        addChild( blueArrow );

        // animate the blue arrow, using position = target * a + start * (1-a)
        new Timer() {
            double alpha = 0;
            {
                scheduleAtFixedRate( new TimerTask() {
                                         @Override public void run() {
                                             alpha += 0.05;
                                             if ( alpha > 1 ) {
                                                 alpha = 1;
                                                 cancel(); // prevent timer from calling in the future
                                             }
                                             double heuristicAlpha = Math.pow( alpha, 0.8 );
                                             ImmutableVector2D tip = tipStart.times( 1 - heuristicAlpha ).getAddedInstance( tipTarget.times( heuristicAlpha ) );
                                             ImmutableVector2D tail = tailStart.times( 1 - heuristicAlpha ).getAddedInstance( tailTarget.times( heuristicAlpha ) );
                                             blueArrow.setTipAndTailLocations( tip.toPoint2D(), tail.toPoint2D() );
                                             blueArrow.repaint();
                                         }
                                     }, 30, 30 );
            }
        };
    }

    public void disperse() {
        setVisible( false );
    }
}
