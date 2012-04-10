// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.LGConstants;
import edu.colorado.phet.linegraphing.common.view.LGCanvas;
import edu.colorado.phet.linegraphing.intro.model.IntroModel;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Intro" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroCanvas extends LGCanvas implements Resettable {

    private final LineGraphNode lineGraphNode;

    public IntroCanvas( final IntroModel model ) {
        setBackground( new Color( 255, 255, 225 ) );

        final Property<Boolean> interactiveLineVisible = new Property<Boolean>( true );
        lineGraphNode = new LineGraphNode( model.graph, model.mvt, model.interactiveLine,
                                           IntroModel.RISE_RANGE, IntroModel.RUN_RANGE, IntroModel.INTERCEPT_RANGE,
                                           interactiveLineVisible );
        PNode pointTool = new PointToolNode( model.pointToolLocation, model.mvt, model.graph, getStageBounds() );
        InteractiveLineControls lineControls =
                new InteractiveLineControls( model.interactiveLine,
                                             IntroModel.RISE_RANGE, IntroModel.RUN_RANGE, IntroModel.INTERCEPT_RANGE,
                                             new VoidFunction1<SlopeInterceptLine>() {
                                                 public void apply( SlopeInterceptLine slopeInterceptLine ) {
                                                     lineGraphNode.saveLine( slopeInterceptLine );
                                                 }
                                             },
                                             new VoidFunction0() {
                                                 public void apply() {
                                                     lineGraphNode.eraseLines();
                                                 }
                                             },
                                             interactiveLineVisible );
        PNode graphControls = new GraphControls( lineGraphNode );
        PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, null, LGConstants.CONTROL_FONT_SIZE, Color.BLACK, LGColors.RESET_ALL_BUTTON ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( lineGraphNode );
            addChild( lineControls );
            addChild( graphControls );
            addChild( resetAllButtonNode );
            addChild( pointTool );
        }

        // layout
        {
            // NOTE: Nodes that have corresponding model elements handle their own offsets.
            final double yMargin = 20;

            // upper-right of graph
            lineControls.setOffset( lineGraphNode.getFullBoundsReference().getMaxX(), 35 );
            // centered below equation
            graphControls.setOffset( lineControls.getFullBoundsReference().getCenterX() - ( graphControls.getFullBoundsReference().getWidth() / 2 ),
                                          lineControls.getFullBoundsReference().getMaxY() + 25 );
            // buttons centered below control panel
            resetAllButtonNode.setOffset( graphControls.getFullBoundsReference().getCenterX() - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 ),
                                     getStageSize().getHeight() - yMargin - resetAllButtonNode.getFullBoundsReference().getHeight() );
        }
    }

    public void reset() {
        lineGraphNode.reset();
    }
}
