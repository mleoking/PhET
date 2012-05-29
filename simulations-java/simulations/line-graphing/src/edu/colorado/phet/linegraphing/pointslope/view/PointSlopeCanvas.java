// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.view.GraphControls;
import edu.colorado.phet.linegraphing.common.view.LGCanvas;
import edu.colorado.phet.linegraphing.common.view.PointToolNode;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

//TODO lots of duplication with SlopeInterceptCanvas

/**
 * Canvas for the "Point Slope" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeCanvas extends LGCanvas {

    private final Property<Boolean> linesVisible = new Property<Boolean>( true );
    private final Property<Boolean> interactiveLineVisible = new Property<Boolean>( true );
    private final Property<Boolean> interactiveEquationVisible = new Property<Boolean>( true );
    private final Property<Boolean> slopeVisible = new Property<Boolean>( true );

    public PointSlopeCanvas( final PointSlopeModel model ) {

        //TODO create PointSlopeLineGraphNode
        PNode graphNode = new PointSlopeGraphNode( model.graph, model.mvt, model.interactiveLine, model.savedLines, model.standardLines,
                                                   model.riseRange, model.runRange, model.x1Range, model.y1Range,
                                                   interactiveEquationVisible, linesVisible, interactiveLineVisible, slopeVisible );
        PNode pointTool1 = new PointToolNode( model.pointTool1, model.mvt, model.graph, getStageBounds(), linesVisible );
        PNode pointTool2 = new PointToolNode( model.pointTool2, model.mvt, model.graph, getStageBounds(), linesVisible );
        //TODO create PointSlopeEquationControls
        PNode equationControls = new PPath( new Rectangle2D.Double( 0, 0, 300, 200 ) );
//        SlopeInterceptEquationControls equationControls = new SlopeInterceptEquationControls( interactiveEquationVisible, model.interactiveLine,
//                                                                                              model.riseRange, model.runRange, model.interceptRange,
//                                                                                              model.savedLines, linesVisible );
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
            final double yMargin = 20;

            // upper-right of graph
            equationControls.setOffset( graphNode.getFullBoundsReference().getMaxX(), 35 );
            // right aligned below equation
            graphControls.setOffset( equationControls.getFullBoundsReference().getMaxX() - graphControls.getFullBoundsReference().getWidth(),
                                     equationControls.getFullBoundsReference().getMaxY() + 25 );
            // buttons centered below control panel
            resetAllButtonNode.setOffset( graphControls.getFullBoundsReference().getCenterX() - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 ),
                                          getStageSize().getHeight() - yMargin - resetAllButtonNode.getFullBoundsReference().getHeight() );
        }
        centerRootNodeOnStage();
    }

    public void reset() {
        interactiveLineVisible.reset();
        linesVisible.reset();
        slopeVisible.reset();
    }
}
