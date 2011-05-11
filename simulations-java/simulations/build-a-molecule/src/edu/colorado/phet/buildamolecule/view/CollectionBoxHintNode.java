//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;

import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

public class CollectionBoxHintNode extends PNode {
    public CollectionBoxHintNode( final ModelViewTransform mvt, PBounds moleculeDestinationBounds, final CollectionBox box ) {

        // at the end, where our tip and tail should be
        final ImmutableVector2D tipTarget = mvt.modelToView( new ImmutableVector2D( box.getDropBounds().getMinX() - 20, box.getDropBounds().getCenterY() ) );
        final ImmutableVector2D tailTarget;

        final Rectangle2D moleculeViewBounds = mvt.modelToViewRectangle( moleculeDestinationBounds );
        final Rectangle2D boxViewBounds = mvt.modelToView( box.getDropBounds() ).getBounds2D();

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

        addChild( new PNode() {
            {
                PText textNode = new PText( "Drag your molecule here" ) {{
                    setFont( new PhetFont( 14, true ) );
                    setTextPaint( Color.BLACK );
                }};
                double verticalPad = 5;
                double horizontalPad = 5;
                PBounds textBounds = textNode.getFullBounds();
                addChild( new PhetPPath( new RoundRectangle2D.Double( textBounds.x - horizontalPad, textBounds.y - verticalPad, textBounds.getWidth() + horizontalPad * 2, textBounds.getHeight() + verticalPad * 2, 15, 15 ) ) {{
                    setPaint( Color.WHITE );
                    setStrokePaint( Color.BLACK );
                }} );
                addChild( textNode );
                blueArrow.addPropertyChangeListener( new PropertyChangeListener() {
                    public void propertyChange( PropertyChangeEvent evt ) {
                        updatePosition();
                    }
                } );
                updatePosition();
            }

            public void updatePosition() {
                centerFullBoundsOnPoint( blueArrow.getTailLocation().getX(), blueArrow.getTailLocation().getY() );
//                centerFullBoundsOnPoint( boxViewBounds.getCenterX(), boxViewBounds.getMaxY() );
                repaint();
            }
        } );

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
