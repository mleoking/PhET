// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.geom.Point2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PadBoundsNode;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.colorado.phet.linegraphing.intro.view.LineManipulatorDragHandler.InterceptDragHandler;
import edu.colorado.phet.linegraphing.intro.view.LineManipulatorDragHandler.SlopeDragHandler;
import edu.colorado.phet.linegraphing.intro.view.RiseRunBracketNode.Direction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Graph that displays static lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LineGraphNode extends GraphNode implements Resettable {

    public final Property<Boolean> linesVisible = new Property<Boolean>( true );
    private final Property<Boolean> interactiveLineVisible = new Property<Boolean>( true );
    public final Property<Boolean> slopeVisible = new Property<Boolean>( true );

    private final LineGraph graph;
    private final ModelViewTransform mvt;
    private final Property<Boolean> interactiveEquationVisible;
    private final PNode savedLinesParentNode, standardLinesParentNode; // intermediate nodes, for consistent rendering order
    private final PNode interactiveLineParentNode, bracketsParentNode;
    private final PNode slopeManipulatorNode, interceptManipulatorNode;
    private SlopeInterceptLineNode interactiveLineNode;

    public LineGraphNode( final LineGraph graph, final ModelViewTransform mvt,
                          Property<SlopeInterceptLine> interactiveLine,
                          ObservableList<SlopeInterceptLine> savedLines,
                          ObservableList<SlopeInterceptLine> standardLines,
                          IntegerRange riseRange, IntegerRange runRange, IntegerRange interceptRange,
                          Property<Boolean> interactiveEquationVisible ) {
        super( graph, mvt );

        this.graph = graph;
        this.mvt = mvt;
        this.interactiveEquationVisible = interactiveEquationVisible;

        // Parent nodes for each category of line (standard, saved, interactive) to maintain rendering order
        standardLinesParentNode = new PComposite();
        savedLinesParentNode = new PNode();
        interactiveLineParentNode = new PComposite();

        // Manipulators for the interactive line
        final double manipulatorDiameter = mvt.modelToViewDeltaX( 0.75 );
        slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE_COLOR );
        interceptManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.INTERCEPT_COLOR );

        // Rise and run brackets for the interactive line
        bracketsParentNode = new PComposite();

        // Rendering order
        addChild( interactiveLineParentNode );
        addChild( savedLinesParentNode );
        addChild( standardLinesParentNode );
        addChild( bracketsParentNode );
        addChild( interceptManipulatorNode );
        addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0

        // Add/remove standard lines
        standardLines.addElementAddedObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                addStandardLine( line );
            }
        } );
        standardLines.addElementRemovedObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                removeStandardLine( line );
            }
        } );

        // Add/remove saved lines
        savedLines.addElementAddedObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                addSavedLine( line );
            }
        } );
        savedLines.addElementRemovedObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                removeSavedLine( line );
            }
        } );

        // When the interactive line changes, update the graph.
        interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                updateInteractiveLine( line, graph, mvt );
            }
        } );

        // Visibility of lines
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                updateLinesVisibility();
            }
        };
        visibilityObserver.observe( interactiveLineVisible, slopeVisible, linesVisible );

        // Visibility of the equation on the interactive line
        interactiveEquationVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                if ( interactiveLineNode != null ) {
                    interactiveLineNode.setEquationVisible( visible );
                }
            }
        } );

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
        slopeVisible.reset();
        eraseSavedLines();
    }

    private void addStandardLine( SlopeInterceptLine line ) {
        standardLinesParentNode.addChild( new SlopeInterceptLineNode( line, graph, mvt ) );
    }

    private void removeStandardLine( SlopeInterceptLine line ) {
        removeLine( line, standardLinesParentNode );
    }

    private void addSavedLine( SlopeInterceptLine line ) {
        savedLinesParentNode.addChild( new SlopeInterceptLineNode( line, graph, mvt ) );
    }

    private void removeSavedLine( SlopeInterceptLine line ) {
        removeLine( line, savedLinesParentNode );
    }

    private static void removeLine( SlopeInterceptLine line, PNode parent ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode node = parent.getChild( i );
            if ( node instanceof SlopeInterceptLineNode ) {
                SlopeInterceptLineNode lineNode = (SlopeInterceptLineNode)node;
                if ( lineNode.line == line ) {
                    parent.removeChild( node );
                    break;
                }
            }
        }
    }

    // Erases all of the "saved" lines
    public void eraseSavedLines() {
        savedLinesParentNode.removeAllChildren();
    }

    protected void updateLinesVisibility() {

        savedLinesParentNode.setVisible( linesVisible.get() );
        standardLinesParentNode.setVisible( linesVisible.get() );

        if ( interactiveLineParentNode != null ) {
            interactiveLineParentNode.setVisible( linesVisible.get() && interactiveLineVisible.get() );
            slopeManipulatorNode.setVisible( linesVisible.get() && interactiveLineVisible.get() );
            interceptManipulatorNode.setVisible( linesVisible.get() && interactiveLineVisible.get() );
        }

        if ( bracketsParentNode != null ) {
            bracketsParentNode.setVisible( slopeVisible.get() && linesVisible.get() && interactiveLineVisible.get() );
        }
    }

    // Updates the line and its associated decorations
    private void updateInteractiveLine( final SlopeInterceptLine line, final LineGraph graph, final ModelViewTransform mvt ) {

        // replace the line node
        interactiveLineParentNode.removeAllChildren();
        interactiveLineNode = new SlopeInterceptLineNode( line, graph, mvt );
        interactiveLineNode.setEquationVisible( interactiveEquationVisible.get() );
        interactiveLineParentNode.addChild( interactiveLineNode );

        // replace the rise/run brackets
        bracketsParentNode.removeAllChildren();
        if ( line.isDefined() ) {

            // run bracket
            final Direction runDirection = line.rise >= 0 ? Direction.UP : Direction.DOWN;
            final RiseRunBracketNode runBracketNode = new RiseRunBracketNode( runDirection, mvt.modelToViewDeltaX( line.run ), line.run );
            bracketsParentNode.addChild( runBracketNode );
            runBracketNode.setOffset( mvt.modelToViewX( 0 ), mvt.modelToViewY( line.intercept ) );

            // rise bracket
            if ( line.rise != 0 ) {
                final Direction riseDirection = line.run > 0 ? Direction.LEFT : Direction.RIGHT;
                final RiseRunBracketNode riseBracket = new RiseRunBracketNode( riseDirection, mvt.modelToViewDeltaX( line.rise ), line.rise );
                bracketsParentNode.addChild( riseBracket );
                riseBracket.setOffset( mvt.modelToViewX( line.run ), mvt.modelToViewY( line.intercept ) );
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
        slopeManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( x, y ) ) );
        interceptManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( 0, line.intercept ) ) );
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
        ObservableList<SlopeInterceptLine> standardLines = new ObservableList<SlopeInterceptLine>();
        LineGraphNode graphNode = new LineGraphNode( new LineGraph( -3, 3, -3, 3 ),
                                                     ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 15, -15 ),
                                                     new Property<SlopeInterceptLine>( new SlopeInterceptLine( 1, 1, 1, LGColors.INTERACTIVE_LINE ) ),
                                                     new ObservableList<SlopeInterceptLine>(),
                                                     standardLines,
                                                     new IntegerRange( -1, 1 ),
                                                     new IntegerRange( -1, 1 ),
                                                     new IntegerRange( -1, 1 ),
                                                     new Property<Boolean>( false ) );
        if ( yEqualsXVisible ) {
            standardLines.add( SlopeInterceptLine.Y_EQUALS_X_LINE );
        }
        if ( yEqualsNegativeXVisible ) {
            standardLines.add( SlopeInterceptLine.Y_EQUALS_NEGATIVE_X_LINE );
        }
        graphNode.interactiveLineVisible.set( false );
        graphNode.scale( width / graphNode.getFullBoundsReference().getWidth() );
        return new ImageIcon( new PadBoundsNode( graphNode ).toImage() );
    }
}
