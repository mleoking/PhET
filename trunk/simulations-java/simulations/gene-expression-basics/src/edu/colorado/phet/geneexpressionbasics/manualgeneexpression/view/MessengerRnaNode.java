// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRna;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ShapeSegment;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

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

        // Add placement hints that show where ribosomes and mRNA destroyers
        // could be attached.
        addChild( new PlacementHintNode( mvt, messengerRna.ribosomePlacementHint ) );
        addChild( new PlacementHintNode( mvt, messengerRna.mRnaDestroyerPlacementHint ) );

        // Add the label. This fades in during synthesis, then fades out.
        final FadeLabel label = new FadeLabel( "mRNA", false, messengerRna.existenceStrength );
        addChild( label );
        messengerRna.beingSynthesized.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean beingSynthesized ) {
                if ( beingSynthesized ) {
                    label.startFadeIn( 3000 ); // Fade time chosen empirically.
                }
                else if ( !beingSynthesized ) {
                    label.startFadeOut( 1000 ); // Fade time chosen empirically.
                }
            }
        } );

        // Update the label position as the shape changes.
        messengerRna.addShapeChangeObserver( new VoidFunction1<Shape>() {
            public void apply( Shape shape ) {
                Point2D upperRightCornerPos = mvt.modelToView( new Point2D.Double( messengerRna.getShape().getBounds2D().getMaxX(), messengerRna.getShape().getBounds().getMaxY() ) );
                label.setOffset( upperRightCornerPos.getX(), upperRightCornerPos.getY() );
            }
        } );

        if ( SHOW_SHAPE_SEGMENTS ) {

            // Add a layer for the segment shapes.
            final PNode segmentShapeLayer = new PNode();
            addChild( segmentShapeLayer );
            segmentShapeLayer.moveToBack();

            // Observe the mRNA and add new shape segments as they come into existence.
            messengerRna.shapeSegments.addElementAddedObserver( new VoidFunction1<ShapeSegment>() {

                public void apply( final ShapeSegment addedShapeSegment ) {
                    final ShapeSegmentNode shapeSegmentNode = new ShapeSegmentNode( addedShapeSegment, mvt );
                    segmentShapeLayer.addChild( shapeSegmentNode );

                    // Watch for removal of this shape segment.  If it goes
                    // away, remove the corresponding node.
                    messengerRna.shapeSegments.addElementRemovedObserver( new VoidFunction1<ShapeSegment>() {
                        public void apply( ShapeSegment removedShapeSegment ) {
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

        private ShapeSegmentNode( final ShapeSegment shapeSegment, final ModelViewTransform mvt ) {

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

    /**
     * PNode that is a textual label that can fade in and out.
     */
    private static class FadeLabel extends PNode {
        private static final Font FONT = new PhetFont( 14 );
        private static final int TIMER_DELAY = 100; // In milliseconds.

        private final Timer fadeInTimer;
        private final Timer fadeOutTimer;

        private double opacity;
        private double fadeDelta;
        private final Property<Double> existenceStrength;

        public FadeLabel( String text, boolean initiallyVisible, final Property<Double> existenceStrength ) {
            this.existenceStrength = existenceStrength;
            final PNode label = new PText( text ) {{ setFont( FONT ); }};
            addChild( label );
            if ( !initiallyVisible ) {
                setTransparency( 0 );
                opacity = 0;
            }
            else {
                opacity = 1;
            }

            // Create the timers that will be used for fading in and out.
            fadeInTimer = new Timer( TIMER_DELAY, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    opacity = Math.min( opacity + fadeDelta, existenceStrength.get() );
                    updateTransparency();
                    if ( opacity >= 1 ) {
                        fadeInTimer.stop();
                    }
                }
            } );
            fadeOutTimer = new Timer( TIMER_DELAY, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    opacity = Math.min( Math.max( opacity - fadeDelta, 0 ), existenceStrength.get() );
                    updateTransparency();
                    if ( opacity <= 0 ) {
                        fadeOutTimer.stop();
                    }
                }
            } );

            // Update if existence strength changes.
            existenceStrength.addObserver( new SimpleObserver() {
                public void update() {
                    updateTransparency();
                }
            } );
        }

        public void startFadeIn( double time ) {
            if ( fadeOutTimer.isRunning() ) {
                fadeOutTimer.stop();
            }
            fadeDelta = TIMER_DELAY / time;
            fadeInTimer.restart();
        }

        public void startFadeOut( double time ) {
            if ( fadeInTimer.isRunning() ) {
                fadeInTimer.stop();
            }
            fadeDelta = TIMER_DELAY / time;
            fadeOutTimer.restart();
        }

        private void updateTransparency() {
            setTransparency( (float) Math.min( existenceStrength.get(), opacity ) );
        }
    }
}
