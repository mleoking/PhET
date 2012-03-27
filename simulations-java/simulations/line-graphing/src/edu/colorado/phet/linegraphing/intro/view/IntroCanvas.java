// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
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

    //TODO relocate some (all?) of these properties into model
    private final Property<Boolean> yEqualsXVisible = new Property<Boolean>( false );
    private final Property<Boolean> yEqualsNegativeXVisible = new Property<Boolean>( false );
    private final Property<Boolean> pointToolVisible = new Property<Boolean>( false );
    private final Property<Boolean> riseOverRunVisible = new Property<Boolean>( false );
    private final Property<Boolean> graphLinesVisible = new Property<Boolean>( false );

    public IntroCanvas( IntroModel model ) {

        PNode graphNode = new GraphNode();
        PNode equationNode = new SlopeInterceptEquationNode();
        PNode savedLinesControl = new SavedLinesControl();
        PNode visibilityControls = new VisibilityControls( yEqualsXVisible, yEqualsNegativeXVisible, pointToolVisible, riseOverRunVisible, graphLinesVisible );
        PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, null, LGConstants.CONTROL_FONT_SIZE, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( graphNode );
            addChild( equationNode );
            addChild( savedLinesControl );
            addChild( visibilityControls );
            addChild( resetAllButtonNode );
        }

        // layout
        {
            // NOTE: Nodes that have corresponding model elements handle their own offsets.
            final double xMargin = 20;
            final double yMargin = 20;
            graphNode.setOffset( xMargin, yMargin );
            // upper-right of graph
            savedLinesControl.setOffset( graphNode.getFullBoundsReference().getMaxX() + 20,
                                         graphNode.getFullBoundsReference().getMinY() );
            // center-right of graph
            equationNode.setOffset( graphNode.getFullBoundsReference().getMaxX() + 20,
                                                 savedLinesControl.getFullBoundsReference().getMaxY() + 60 );
            // bottom-right of graph
            visibilityControls.setOffset( graphNode.getFullBoundsReference().getMaxX() + 20,
                                          graphNode.getFullBoundsReference().getMaxY() - visibilityControls.getFullBoundsReference().getHeight() );
            // lower right
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - yMargin );
        }
    }

    public void reset() {
        yEqualsXVisible.reset();
        yEqualsNegativeXVisible.reset();
        pointToolVisible.reset();
        riseOverRunVisible.reset();
        graphLinesVisible.reset();
        //TODO erase lines
    }
}
