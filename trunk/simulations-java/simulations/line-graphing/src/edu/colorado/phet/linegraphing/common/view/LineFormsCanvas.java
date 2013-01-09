// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.CenteredStageCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class canvas for the "Slope", "Slope-Intercept" and "Point-Slope" tabs.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LineFormsCanvas extends CenteredStageCanvas {

    private final LineFormsViewProperties viewProperties;

    /**
     * Constructor
     *
     * @param model            model container
     * @param viewProperties
     * @param graphNode
     * @param equationControls
     * @param graphControls
     */
    protected LineFormsCanvas( LineFormsModel model, LineFormsViewProperties viewProperties, PNode graphNode, PNode equationControls, PNode graphControls ) {
        setBackground( LGColors.CANVAS );

        this.viewProperties = viewProperties;

        // Put all control panels under a common parent, horizontally centered, origin at upper-left
        PNode controlPanelsParent = new PNode();
        {
            PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, null, LGConstants.CONTROL_FONT_SIZE, Color.BLACK, LGColors.RESET_ALL_BUTTON ) {{
                setConfirmationEnabled( false );
            }};

            // rendering order
            controlPanelsParent.addChild( equationControls );
            controlPanelsParent.addChild( graphControls );
            controlPanelsParent.addChild( resetAllButtonNode );

            // layout
            final int ySpacing = 25;
            // determine which control panel is wider
            final double maxControlPanelWidth = Math.max( equationControls.getFullBoundsReference().getWidth(), graphControls.getFullBoundsReference().getWidth() );
            // determine the center line for the control panels
            final double centerX = maxControlPanelWidth / 2;
            equationControls.setOffset( centerX - ( equationControls.getFullBoundsReference().getWidth() / 2 ), 0 );
            graphControls.setOffset( centerX - ( graphControls.getFullBoundsReference().getWidth() / 2 ),
                                     equationControls.getFullBoundsReference().getMaxY() + ySpacing );
            // centered below graph controls
            resetAllButtonNode.setOffset( centerX - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 ),
                                          graphControls.getFullBoundsReference().getMaxY() + ySpacing );
        }

        // rendering order
        addChild( controlPanelsParent );
        addChild( graphNode );

        // layout
        {
            // position of graphNode is determined by model

            // position of control panels:
            final double xMargin = 5;
            // get the amount of horizontal space that's left for the control panels
            final double availableControlPanelWidth = getStageWidth() - graphNode.getFullBoundsReference().getMaxX() - ( 2 * xMargin );
            // if the available width is less than the width of the control panels, scale the control panels
            if ( controlPanelsParent.getFullBoundsReference().getWidth() > availableControlPanelWidth ) {
                controlPanelsParent.scale( availableControlPanelWidth / controlPanelsParent.getFullBoundsReference().getWidth() );
            }
            // horizontally center controls in the available space
            controlPanelsParent.setOffset( graphNode.getFullBoundsReference().getMaxX() + xMargin + ( availableControlPanelWidth / 2 ) - ( controlPanelsParent.getFullBoundsReference().getWidth() / 2 ),
                                           50 );
        }

        // Point tools, added after centering root node, so that we can compute drag bounds.
        {
            // Create a dummy point tool, so we know its height for the purposes of computing drag bounds.
            final double height = new PointToolNode( model.pointTool1, model.mvt, model.graph, getStageBounds(), viewProperties.linesVisible ).getFullBoundsReference().getHeight();

            // Compute drag bounds such that 50% of the point tool node is always inside the stage. Bounds differ based on tool orientation.
            Rectangle2D stageBounds = getStageBounds();
            Rectangle2D upDragBounds = new Rectangle2D.Double( stageBounds.getX(), stageBounds.getY() - ( height / 2 ), stageBounds.getWidth(), stageBounds.getHeight() );
            Rectangle2D downDragBounds = new Rectangle2D.Double( upDragBounds.getX(), stageBounds.getY() + ( height / 2 ), upDragBounds.getWidth(), upDragBounds.getHeight() );

            // Create point tool nodes
            PNode pointTool1 = new PointToolNode( model.pointTool1, model.mvt, model.graph, model.pointTool1.orientation == Orientation.UP ? upDragBounds : downDragBounds, viewProperties.linesVisible );
            PNode pointTool2 = new PointToolNode( model.pointTool2, model.mvt, model.graph, model.pointTool2.orientation == Orientation.UP ? upDragBounds : downDragBounds, viewProperties.linesVisible );

            // Point tools moveToFront when dragged, so we give them a common parent to preserve rendering order of the reset of the scenegraph.
            PNode pointToolParent = new PNode();
            pointToolParent.addChild( pointTool1 );
            pointToolParent.addChild( pointTool2 );
            addChild( pointToolParent );
        }
    }

    @Override public void reset() {
        super.reset();
        viewProperties.reset();
    }
}
