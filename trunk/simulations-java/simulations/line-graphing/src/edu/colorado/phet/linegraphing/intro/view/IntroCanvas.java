// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.LGConstants;
import edu.colorado.phet.linegraphing.LGResources.Strings;
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
    private final Property<Boolean> pointToolVisible = new Property<Boolean>( false );
    private final Property<Boolean> riseOverRunVisible = new Property<Boolean>( false );

    private final InteractiveLineGraphNode lineGraphNode;

    public IntroCanvas( final IntroModel model ) {

        lineGraphNode = new InteractiveLineGraphNode( model.graph, model.mvt, model.interactiveLine, model.yEqualsXLine, model.yEqualsNegativeXLine );
        PNode graphNode = new ZeroOffsetNode( lineGraphNode );
        final TextButtonNode eraseLinesButton = new TextButtonNode( Strings.ERASE_LINES, LGConstants.CONTROL_FONT, new Color( 0, 255, 255 ) ) {{
            setEnabled( false );
        }};
        PNode equationControlPanel = new EquationControlPanel( model.interactiveLine, lineGraphNode, eraseLinesButton );
        PNode visibilityControls = new VisibilityControls( lineGraphNode.yEqualsXVisible, lineGraphNode.yEqualsNegativeXVisible, pointToolVisible, riseOverRunVisible, lineGraphNode.linesVisible );
        PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, null, LGConstants.CONTROL_FONT_SIZE, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( graphNode );
            addChild( equationControlPanel );
            addChild( eraseLinesButton );
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
            equationControlPanel.setOffset( graphNode.getFullBoundsReference().getMaxX() + 20,
                                            50 );
            // centered below equation
            visibilityControls.setOffset( equationControlPanel.getFullBoundsReference().getCenterX() - ( visibilityControls.getFullBoundsReference().getWidth() / 2 ),
                                          equationControlPanel.getFullBoundsReference().getMaxY() + 50 );
            // lower right
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - yMargin );
            // left of Rest All button
            eraseLinesButton.setOffset( resetAllButtonNode.getFullBoundsReference().getMinX() - eraseLinesButton.getFullBoundsReference().getWidth() - 10,
                                        resetAllButtonNode.getYOffset() );
        }

        // Erase all lines that have been saved.
        eraseLinesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                eraseLinesButton.setEnabled( false );
                lineGraphNode.eraseLines();
            }
        } );
    }

    public void reset() {
        lineGraphNode.reset();
        pointToolVisible.reset();
        riseOverRunVisible.reset();
    }
}
