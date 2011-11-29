// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.umd.cs.piccolo.PNode;

/**
 * View representation for messenger RNA.  This is done differently from most
 * if not all of the other mobile biomolecules because it is represented as an
 * unclosed shape.
 *
 * @author John Blanco
 */
public class MessengerRnaNode extends MobileBiomoleculeNode {

    // For debug - turn on to show the enclosing shape segments.
    private static final boolean SHOW_SHAPE_SEGMENTS = false;

    /**
     * Constructor.
     *
     * @param mvt
     * @param messengerRna
     */
    public MessengerRnaNode( final ModelViewTransform mvt, final MessengerRna messengerRna ) {
        super( mvt, messengerRna, new BasicStroke( 2 ) );

        // Add a placement hint that shows where a ribosome could be attached.
        addChild( new PlacementHintNode( mvt, messengerRna.ribosomePlacementHint ) );

        if ( SHOW_SHAPE_SEGMENTS ) {

            // Add a layer for the segment shapes.
            final PNode segmentShapeLayer = new PNode();
            addChild( segmentShapeLayer );
            segmentShapeLayer.moveToBack();

            // Observe the mRNA and add new shape segments as they come into existence.
            messengerRna.shapeSegments.addElementAddedObserver( new VoidFunction1<MessengerRna.ShapeSegment>() {

                public void apply( final MessengerRna.ShapeSegment addedShapeSegment ) {
                    final ShapeSegmentNode shapeSegmentNode = new ShapeSegmentNode( addedShapeSegment, mvt );
                    segmentShapeLayer.addChild( shapeSegmentNode );

                    // Watch for removal of this shape segment.  If it goes
                    // away, remove the corresponding node.
                    messengerRna.shapeSegments.addElementRemovedObserver( new VoidFunction1<MessengerRna.ShapeSegment>() {
                        public void apply( MessengerRna.ShapeSegment removedShapeSegment ) {
                            if ( removedShapeSegment == addedShapeSegment ) {
                                segmentShapeLayer.removeChild( shapeSegmentNode );
                            }
                        }
                    } );
                }
            } );
        }
    }

    /**
     * Class that defines a node that can be used to visualize the shapes that
     * enclose the mRNA strand.  This was created primarily for debugging, and
     * can probably be removed once the shape algorithm is worked out.
     */
    private static class ShapeSegmentNode extends PNode {

        private static final Color FILL_COLOR = Color.ORANGE;
        private static final Color STROKE_COLOR = Color.RED;
        private static final Stroke STROKE = new BasicStroke( 1 );

        // Need to give flat segments an arbitrary height so that they can be
        // visualized.  This is in screen units.
        private static final double FLAT_SEGMENT_NODE_HEIGHT = 5;

        private ShapeSegmentNode( final MessengerRna.ShapeSegment shapeSegment, final ModelViewTransform mvt ) {

            // Create the node that represents the segment.
            final PhetPPath shapeSegmentNode = new PhetPPath( FILL_COLOR, STROKE, STROKE_COLOR );
            addChild( shapeSegmentNode );

            // Set the initial shape and watch for changes.
            shapeSegment.bounds.addObserver( new VoidFunction1<Rectangle2D>() {
                Shape shape;

                public void apply( Rectangle2D bounds ) {
                    if ( bounds.getHeight() == 0 ) {
                        // This is a horizontal (i.e. flat) segment, so create
                        // a rect that matches the width and has an arbitrary
                        // height.
                        shape = new Rectangle2D.Double( mvt.modelToViewX( bounds.getMinX() ),
                                                        mvt.modelToViewY( bounds.getMinY() ) - FLAT_SEGMENT_NODE_HEIGHT / 2,
                                                        mvt.modelToViewDeltaX( bounds.getWidth() ),
                                                        FLAT_SEGMENT_NODE_HEIGHT );
                    }
                    else {
                        // This is a diagonal segment, so make it a rect.
                        shape = mvt.modelToView( bounds );
                    }
                    // Update the shape.
                    shapeSegmentNode.setPathTo( shape );
                }
            } );
        }
    }

    // TODO: Started on this, decided not to use it for now, delete if never used.  It is intended for debugging the mRNA shape.
    private static class PointMassNode {
        private static final int DIAMETER = 5;
        private static final Shape SHAPE = new Ellipse2D.Double( -DIAMETER / 2, -DIAMETER / 2, DIAMETER, DIAMETER );
        private static final Color COLOR = Color.RED;
        public final MessengerRna.PointMass pointMass;
        private final ModelViewTransform mvt;
        private final PNode representation;

        public PointMassNode( MessengerRna.PointMass pointMass, ModelViewTransform mvt ) {
            this.pointMass = pointMass;
            this.mvt = mvt;
            representation = new PhetPPath( SHAPE, COLOR );
            updatePosition();
        }

        public void updatePosition() {
            representation.setOffset( mvt.modelToView( pointMass.getPosition() ) );
        }
    }


}
