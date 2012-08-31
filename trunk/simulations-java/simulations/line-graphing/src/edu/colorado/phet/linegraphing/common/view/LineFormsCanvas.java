// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class canvas for the "Slope-Intercept" and "Point-Slope" modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineFormsCanvas extends LGCanvas {

    // properties that control visibility
    private final Property<Boolean> linesVisible;
    private final Property<Boolean> interactiveLineVisible;
    private final Property<Boolean> interactiveEquationVisible;
    private final Property<Boolean> slopeVisible;

    /**
     * Constructor
     * @param model model container
     * @param linesVisible are lines visible on the graph? (applies to all lines)
     * @param interactiveLineVisible is the interactive line visible visible on the graph?
     * @param interactiveEquationVisible is the equation visible on the interactive line?
     * @param slopeVisible are the slope (rise/run) brackets visible on the graphed line?
     * @param graphNode
     * @param equationControls
     */
    public LineFormsCanvas( LineFormsModel model,
                            Property<Boolean> linesVisible, Property<Boolean> interactiveLineVisible,
                            Property<Boolean> interactiveEquationVisible, Property<Boolean> slopeVisible,
                            PNode graphNode, PNode equationControls ) {

        this.linesVisible = linesVisible;
        this.interactiveLineVisible = interactiveLineVisible;
        this.interactiveEquationVisible = interactiveEquationVisible;
        this.slopeVisible = slopeVisible;

        PNode graphControls = new GraphControls( linesVisible, slopeVisible, model.standardLines );
        PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, null, LGConstants.CONTROL_FONT_SIZE, Color.BLACK, LGColors.RESET_ALL_BUTTON ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( graphNode );
            addChild( equationControls );
            addChild( graphControls );
            addChild( resetAllButtonNode );

        }

        // layout
        {
            // NOTE: Nodes that have corresponding model elements handle their own offsets.
            final int ySpacing = 25;
            // upper-right of graph
            equationControls.setOffset( graphNode.getFullBoundsReference().getMaxX(), 50 );
            // centered below equation controls
            graphControls.setOffset( equationControls.getFullBoundsReference().getCenterX() - ( graphControls.getFullBoundsReference().getWidth() / 2 ),
                                     equationControls.getFullBoundsReference().getMaxY() + ySpacing );
            // centered below graph controls
            resetAllButtonNode.setOffset( equationControls.getFullBoundsReference().getCenterX() - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 ),
                                          graphControls.getFullBoundsReference().getMaxY() + ySpacing );
        }
        centerRootNodeOnStage();

        // Point tools, added after centering root node, so that we can compute drag bounds.
        {
            // Create a dummy point tool, so we know its height for the purposes of computing drag bounds.
            final double height = new PointToolNode( model.pointTool1, model.mvt, model.graph, getStageBounds(), linesVisible ).getFullBoundsReference().getHeight();

            // Compute drag bounds such that 50% of the point tool is always inside the stage. Differs based on tool orientation.
            Rectangle2D stageBounds = getStageBounds();
            Rectangle2D upDragBounds = new Rectangle2D.Double( stageBounds.getX(), stageBounds.getY() - ( height / 2 ), stageBounds.getWidth(), stageBounds.getHeight() );
            Rectangle2D downDragBounds = new Rectangle2D.Double( upDragBounds.getX(), stageBounds.getY() + ( height / 2 ), upDragBounds.getWidth(), upDragBounds.getHeight() );

            // Create point tool nodes
            PNode pointTool1 = new PointToolNode( model.pointTool1, model.mvt, model.graph, model.pointTool1.orientation == Orientation.UP ? upDragBounds : downDragBounds, linesVisible );
            PNode pointTool2 = new PointToolNode( model.pointTool2, model.mvt, model.graph, model.pointTool2.orientation == Orientation.UP ? upDragBounds : downDragBounds, linesVisible );
            addChild( pointTool1 );
            addChild( pointTool2 );
        }
    }

    @Override public void reset() {
        super.reset();
        linesVisible.reset();
        interactiveLineVisible.reset();
        interactiveEquationVisible.reset();
        slopeVisible.reset();
    }
}
