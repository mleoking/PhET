// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class canvas for the 2 tabs that deal with line forms (slope-intercept and point-slope).
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
     * @param linesVisible are lines visible on the graph?
     * @param interactiveLineVisible is the interactive line visible visible on the graph?
     * @param interactiveEquationVisible is the interactive equation visible?
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

        // nodes
        PNode pointTool1 = new PointToolNode( model.pointTool1, model.mvt, model.graph, getStageBounds(), linesVisible );
        PNode pointTool2 = new PointToolNode( model.pointTool2, model.mvt, model.graph, getStageBounds(), linesVisible );
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
            addChild( pointTool1 );
            addChild( pointTool2 );
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
    }

    @Override public void reset() {
        super.reset();
        linesVisible.reset();
        interactiveLineVisible.reset();
        interactiveEquationVisible.reset();
        slopeVisible.reset();
    }
}
