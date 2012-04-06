// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.geom.Point2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PadBoundsNode;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.colorado.phet.linegraphing.intro.view.BracketValueNode.Direction;
import edu.colorado.phet.linegraphing.intro.view.LineManipulatorDragHandler.InterceptDragHandler;
import edu.colorado.phet.linegraphing.intro.view.LineManipulatorDragHandler.SlopeDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Graph that displays static lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LineGraphNode extends GraphNode implements Resettable {

    public final Property<Boolean> linesVisible = new Property<Boolean>( true );
    public final Property<Boolean> yEqualsXVisible = new Property<Boolean>( false );
    public final Property<Boolean> yEqualsNegativeXVisible = new Property<Boolean>( false );
    private final Property<Boolean> interactiveLineVisible = new Property<Boolean>( true );
    public final Property<Boolean> riseOverRunVisible = new Property<Boolean>( false );
    public final Property<Boolean> pointToolVisible = new Property<Boolean>( true );

    private final LineGraph graph;
    private final ModelViewTransform mvt;
    private final SlopeInterceptLineNode yEqualsXLineNode, yEqualsNegativeXLineNode;
    private final PNode savedLinesParentNode, standardLinesParentNode; // intermediate nodes, for consistent rendering order
    private final PNode interactiveLineParentNode, bracketsParentNode;
    private final PNode slopeManipulatorNode, interceptManipulatorNode;

    public LineGraphNode( final LineGraph graph, final ModelViewTransform mvt,
                          Property<SlopeInterceptLine> interactiveLine,
                          IntegerRange riseRange, IntegerRange runRange, IntegerRange interceptRange,
                          Property<ImmutableVector2D> pointToolLocation ) {
        super( graph, mvt );

        this.graph = graph;
        this.mvt = mvt;

        // Standard lines
        standardLinesParentNode = new PComposite();
        yEqualsXLineNode = new SlopeInterceptLineNode( new SlopeInterceptLine( 1, 1, 0 ), graph, mvt, LGColors.Y_EQUALS_X );
        standardLinesParentNode.addChild( yEqualsXLineNode );
        yEqualsNegativeXLineNode = new SlopeInterceptLineNode( new SlopeInterceptLine( 1, -1, 0 ), graph, mvt, LGColors.Y_EQUALS_NEGATIVE_X );
        standardLinesParentNode.addChild( yEqualsNegativeXLineNode );

        // Saved lines
        savedLinesParentNode = new PNode();

        // Interactive line
        interactiveLineParentNode = new PComposite();
        interactiveLineParentNode.setVisible( linesVisible.get() );

        // Manipulators
        final double manipulatorDiameter = mvt.modelToViewDeltaX( 0.75 );
        slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE_COLOR );
        interceptManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.INTERCEPT_COLOR );

        // Rise and run brackets
        bracketsParentNode = new PComposite();

        // Point tool
        final PNode pointTool = new PointToolNode( pointToolLocation, mvt, graph );

        // Rendering order
        addChild( standardLinesParentNode );
        addChild( savedLinesParentNode );
        addChild( interactiveLineParentNode );
        addChild( bracketsParentNode );
        addChild( interceptManipulatorNode );
        addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0
        addChild( pointTool );

        // Visibility of lines
        yEqualsXVisible.addObserver( new SimpleObserver() {
            public void update() {
                updateLinesVisibility();
            }
        } );
        yEqualsNegativeXVisible.addObserver( new SimpleObserver() {
            public void update() {
                updateLinesVisibility();
            }
        } );
        linesVisible.addObserver( new SimpleObserver() {
            public void update() {
                updateLinesVisibility();
            }
        } );

        // Visibility of point tool
        pointToolVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                pointTool.setVisible( visible );
            }
        } );

        // When the interactive line changes, update the graph.
        interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                updateInteractiveLine( line, graph, mvt );
            }
        } );

        // Visibility
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                updateLinesVisibility();
            }
        };
        visibilityObserver.observe( interactiveLineVisible, riseOverRunVisible, linesVisible );

        // interactivity for slope manipulator
        slopeManipulatorNode.addInputEventListener( new CursorHandler() );
        slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                          slopeManipulatorNode, mvt, interactiveLine, riseRange, runRange ) );

        // interactivity for intercept manipulator
        interceptManipulatorNode.addInputEventListener( new CursorHandler() );
        interceptManipulatorNode.addInputEventListener( new InterceptDragHandler( UserComponents.interceptManipulator, UserComponentTypes.sprite,
                                                                                  interceptManipulatorNode, mvt, interactiveLine, interceptRange ) );
    }

    public void reset() {
        linesVisible.reset();
        yEqualsXVisible.reset();
        yEqualsNegativeXVisible.reset();
        riseOverRunVisible.reset();
        eraseLines();
    }

    // "Saves" a line, displaying it on the graph.
    public void saveLine( SlopeInterceptLine line ) {
        savedLinesParentNode.addChild( new SlopeInterceptLineNode( line, graph, mvt, LGColors.SAVED_LINE_NORMAL, LGColors.SAVED_LINE_HIGHLIGHT ) );
    }

    // Erases all of the "saved" lines
    public void eraseLines() {
        savedLinesParentNode.removeAllChildren();
    }

    protected void updateLinesVisibility() {

        savedLinesParentNode.setVisible( linesVisible.get() );

        standardLinesParentNode.setVisible( linesVisible.get() );
        yEqualsXLineNode.setVisible( yEqualsXVisible.get() );
        yEqualsNegativeXLineNode.setVisible( yEqualsNegativeXVisible.get() );

        if ( interactiveLineParentNode != null ) {
            interactiveLineParentNode.setVisible( linesVisible.get() && interactiveLineVisible.get() );
            slopeManipulatorNode.setVisible( linesVisible.get() && interactiveLineVisible.get() );
            interceptManipulatorNode.setVisible( linesVisible.get() && interactiveLineVisible.get() );
        }

        if ( bracketsParentNode != null ) {
            bracketsParentNode.setVisible( riseOverRunVisible.get() && linesVisible.get() && interactiveLineVisible.get() );
        }
    }

    // Updates the line and its associated decorations
    private void updateInteractiveLine( final SlopeInterceptLine line, final LineGraph graph, final ModelViewTransform mvt ) {

        // replace the line node
        interactiveLineParentNode.removeAllChildren();
        interactiveLineParentNode.addChild( new SlopeInterceptLineNode( line, graph, mvt, LGColors.INTERACTIVE_LINE ) );

        // replace the rise/run brackets
        bracketsParentNode.removeAllChildren();
        if ( line.isDefined() ) {

            // run bracket
            final Direction runDirection = line.rise >= 0 ? Direction.UP : Direction.DOWN;
            final BracketValueNode runBracketNode = new BracketValueNode( runDirection, mvt.modelToViewDeltaX( line.run ), line.run );
            bracketsParentNode.addChild( runBracketNode );
            runBracketNode.setOffset( mvt.modelToViewDeltaX( 0 ), mvt.modelToViewDeltaY( line.intercept ) );

            // rise bracket
            if ( line.rise != 0 ) {
                final Direction riseDirection = line.run > 0 ? Direction.LEFT : Direction.RIGHT;
                final BracketValueNode riseBracket = new BracketValueNode( riseDirection, mvt.modelToViewDeltaX( line.rise ), line.rise );
                bracketsParentNode.addChild( riseBracket );
                riseBracket.setOffset( mvt.modelToViewDeltaX( line.run ), mvt.modelToViewDeltaY( line.intercept ) );
            }
        }

        // move the manipulators
        final double y = line.rise + line.intercept;
        double x;
        if ( line.run == 0 ) {
            x = 0;
        }
        else if ( line.rise == 0 ) {
            x = line.run;
        }
        else {
            x = line.solveX( y );
        }
        slopeManipulatorNode.setOffset( mvt.modelToView( x, y ) );
        interceptManipulatorNode.setOffset( mvt.modelToView( 0, line.intercept ) );
    }

    // Creates an icon for the "y = +x" feature
    public static Icon createYEqualsXIcon( double width ) {
        return createIcon( width, true, false );
    }

    // Creates an icon for the "y = -x" feature
    public static Icon createYEqualsNegativeXIcon( double width ) {
        return createIcon( width, false, true );
    }

    // Icon creation
    private static Icon createIcon( double width, boolean yEqualsXVisible, boolean yEqualsNegativeXVisible ) {
        LineGraphNode graphNode = new LineGraphNode( new LineGraph( -3, 3, -3, 3 ),
                                                     ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 15, -15 ),
                                                     new Property<SlopeInterceptLine>( new SlopeInterceptLine( 1, 1, 1 ) ),
                                                     new IntegerRange( -1, 1 ),
                                                     new IntegerRange( -1, 1 ),
                                                     new IntegerRange( -1, 1 ),
                                                     new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) ) );
        graphNode.yEqualsXVisible.set( yEqualsXVisible );
        graphNode.yEqualsNegativeXVisible.set( yEqualsNegativeXVisible );
        graphNode.interactiveLineVisible.set( false );
        graphNode.pointToolVisible.set( false );
        graphNode.scale( width / graphNode.getFullBoundsReference().getWidth() );
        return new ImageIcon( new PadBoundsNode( graphNode ).toImage() );
    }
}
