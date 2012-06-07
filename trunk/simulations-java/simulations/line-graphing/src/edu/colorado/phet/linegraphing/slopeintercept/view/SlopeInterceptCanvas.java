// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.view.GraphControls;
import edu.colorado.phet.linegraphing.common.view.LGCanvas;
import edu.colorado.phet.linegraphing.common.view.PointToolNode;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Intro" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptCanvas extends LGCanvas {

    private final Property<Boolean> linesVisible = new Property<Boolean>( true );
    private final Property<Boolean> interactiveLineVisible = new Property<Boolean>( true );
    private final Property<Boolean> interactiveEquationVisible = new Property<Boolean>( true );
    private final Property<Boolean> slopeVisible = new Property<Boolean>( true );

    public SlopeInterceptCanvas( final SlopeInterceptModel model ) {

        PNode graphNode = new SlopeInterceptGraphNode( model.graph, model.mvt, model.interactiveLine, model.savedLines, model.standardLines,
                                                       model.riseRange, model.runRange, model.interceptRange,
                                                       interactiveEquationVisible, linesVisible, interactiveLineVisible, slopeVisible );
        PNode pointTool1 = new PointToolNode( model.pointTool1, model.mvt, model.graph, getStageBounds(), linesVisible );
        PNode pointTool2 = new PointToolNode( model.pointTool2, model.mvt, model.graph, getStageBounds(), linesVisible );
        SlopeInterceptEquationControls equationControls = new SlopeInterceptEquationControls( interactiveEquationVisible, model.interactiveLine,
                                                                                              model.riseRange, model.runRange, model.interceptRange,
                                                                                              model.savedLines, linesVisible );
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
            // upper-right of graph
            equationControls.setOffset( graphNode.getFullBoundsReference().getMaxX() + 20, 50 );
            // centered below equation controls
            graphControls.setOffset( equationControls.getFullBoundsReference().getCenterX() - ( graphControls.getFullBoundsReference().getWidth() / 2 ),
                                     equationControls.getFullBoundsReference().getMaxY() + 25 );
            // centered below graph controls
            resetAllButtonNode.setOffset( graphControls.getFullBoundsReference().getCenterX() - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 ),
                                          graphControls.getFullBoundsReference().getMaxY() + 25 );
        }
        centerRootNodeOnStage();
    }

    public void reset() {
        interactiveLineVisible.reset();
        linesVisible.reset();
        slopeVisible.reset();
    }
}
