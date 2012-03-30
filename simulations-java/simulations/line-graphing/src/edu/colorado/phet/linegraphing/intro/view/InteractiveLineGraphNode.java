// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Specialization of line graph that adds the ability to directly manipulate a line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class InteractiveLineGraphNode extends LineGraphNode {

    private PNode interactiveLineParentNode, bracketsParentNode;
    private PNode slopeManipulatorNode, interceptManipulatorNode;

    public InteractiveLineGraphNode( final LineGraph graph, final ModelViewTransform mvt, Property<SlopeInterceptLine> interactiveLine, SlopeInterceptLine yEqualsXLine, SlopeInterceptLine yEqualsNegativeXLine ) {
        super( graph, mvt, yEqualsXLine, yEqualsNegativeXLine );

        // Interactive line
        interactiveLineParentNode = new PComposite();
        interactiveLineParentNode.setVisible( linesVisible.get() );

        // Manipulators
        final double manipulatorDiameter = mvt.modelToViewDeltaX( 0.65 );
        slopeManipulatorNode = new SphericalNode( manipulatorDiameter, LGColors.SLOPE_COLOR, new BasicStroke( 1f ), Color.BLACK, false );
        interceptManipulatorNode = new SphericalNode( manipulatorDiameter, LGColors.INTERCEPT_COLOR, new BasicStroke( 1f ), Color.BLACK, false );

        // Rise and run brackets
        bracketsParentNode = new PComposite();

        // rendering order
        addChild( interactiveLineParentNode );
        addChild( bracketsParentNode );
        addChild( slopeManipulatorNode );
        addChild( interceptManipulatorNode );

        // When the interactive line changes, update the graph.
        interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                updateInteractiveLine( line, graph, mvt );
            }
        } );

        slopeManipulatorNode.addInputEventListener( new CursorHandler() );
        //TODO drag handler for slope

        interceptManipulatorNode.addInputEventListener( new CursorHandler() );
        //TODO drag handler for intercept
    }

    // Updates the line and its associated decorations
    private void updateInteractiveLine( final SlopeInterceptLine line, final LineGraph graph, final ModelViewTransform mvt ) {

        // replace the line node
        interactiveLineParentNode.removeAllChildren();
        interactiveLineParentNode.addChild( new SlopeInterceptLineNode( line, graph, mvt, LGColors.INTERACTIVE_LINE ) );

        // replace the rise/run brackets
        bracketsParentNode.removeAllChildren();
        if ( line.run != 0 ) {
            final BracketLabelNode runBracketNode = new BracketLabelNode( String.valueOf( line.run ), Math.abs( mvt.modelToViewDeltaX( line.run ) ), new PhetFont( 12 ),
                                                                          Color.BLACK, Color.BLACK, new BasicStroke( 0.5f ) );
            bracketsParentNode.addChild( runBracketNode );
            final double xOffset = ( line.run > 0 ) ? 0 : line.run;
            runBracketNode.setOffset( mvt.modelToViewDeltaX( xOffset ), mvt.modelToViewDeltaY( line.intercept ) );
        }
        if ( line.rise != 0 ) {

        }

        // move the manipulators
        final int y = line.rise + line.intercept;
        slopeManipulatorNode.setOffset( mvt.modelToView( line.solveX( y ), y ) );
        interceptManipulatorNode.setOffset( mvt.modelToView( 0, line.intercept ) );
    }

    // Updates the visibility of lines in the graph
    @Override protected void updateLinesVisibility() {
        super.updateLinesVisibility();
        if ( interactiveLineParentNode != null ) {
            interactiveLineParentNode.setVisible( linesVisible.get() );
            slopeManipulatorNode.setVisible( linesVisible.get() );
            interceptManipulatorNode.setVisible( linesVisible.get() );
        }
    }
}
