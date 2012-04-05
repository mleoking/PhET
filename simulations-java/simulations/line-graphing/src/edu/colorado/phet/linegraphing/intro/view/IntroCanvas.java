// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.LGConstants;
import edu.colorado.phet.linegraphing.common.view.LGCanvas;
import edu.colorado.phet.linegraphing.intro.model.IntroModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Intro" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroCanvas extends LGCanvas implements Resettable {

    private final InteractiveLineGraphNode lineGraphNode;

    public IntroCanvas( final IntroModel model ) {
        setBackground( new Color( 255, 255, 225 ) );

        lineGraphNode = new InteractiveLineGraphNode( model.graph, model.mvt, model.interactiveLine,
                                                      IntroModel.RISE_RANGE, IntroModel.RUN_RANGE, IntroModel.INTERCEPT_RANGE,
                                                      model.pointToolLocation );
        PNode graphNode = new ZeroOffsetNode( lineGraphNode );
        PNode equationControls = new EquationControls( model.interactiveLine, lineGraphNode, IntroModel.RISE_RANGE, IntroModel.RUN_RANGE, IntroModel.INTERCEPT_RANGE );
        PNode graphControls = new GraphControls( lineGraphNode );
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
            final double xMargin = 20;
            final double yMargin = 20;
            graphNode.setOffset( xMargin, yMargin );

            // upper-right of graph
            equationControls.setOffset( graphNode.getFullBoundsReference().getMaxX() + 10, 35 );
            // centered below equation
            graphControls.setOffset( equationControls.getFullBoundsReference().getCenterX() - ( graphControls.getFullBoundsReference().getWidth() / 2 ),
                                          equationControls.getFullBoundsReference().getMaxY() + 25 );
            // buttons centered below control panel
            resetAllButtonNode.setOffset( graphControls.getFullBoundsReference().getCenterX() - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 ),
                                     getStageSize().getHeight() - yMargin - resetAllButtonNode.getFullBoundsReference().getHeight() );
        }
    }

    public void reset() {
        lineGraphNode.reset();
    }
}
