// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.LGColors;
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

    public final Property<Boolean> linesVisible = new Property<Boolean>( true ); //TODO this property is problematic...

    private final InteractiveLineGraphNode lineGraphNode;

    public IntroCanvas( final IntroModel model ) {

        lineGraphNode = new InteractiveLineGraphNode( model.graph, model.mvt, model.interactiveLine, model.yEqualsXLine, model.yEqualsNegativeXLine, linesVisible, model.pointToolLocation );
        PNode graphNode = new ZeroOffsetNode( lineGraphNode );
        final TextButtonNode eraseLinesButton = new TextButtonNode( Strings.ERASE_LINES, LGConstants.CONTROL_FONT, LGColors.ERASE_LINES_BUTTON ) {{
            setEnabled( false );
        }};
        PNode equationControlPanel = new EquationControlPanel( model.interactiveLine, lineGraphNode, eraseLinesButton,
                                                               IntroModel.RISE_RANGE, IntroModel.RUN_RANGE, IntroModel.INTERCEPT_RANGE );
        PNode visibilityControls = new VisibilityControls( linesVisible, lineGraphNode.riseOverRunVisible, lineGraphNode.yEqualsXVisible, lineGraphNode.yEqualsNegativeXVisible,
                                                           lineGraphNode.pointToolVisible );
        PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, null, LGConstants.CONTROL_FONT_SIZE, Color.BLACK, LGColors.RESET_ALL_BUTTON ) {{
            setConfirmationEnabled( false );
        }};
        PNode buttonsParent = new PNode();

        // rendering order
        {
            addChild( graphNode );
            addChild( equationControlPanel );
            addChild( visibilityControls );
            addChild( buttonsParent );
            buttonsParent.addChild( eraseLinesButton );
            buttonsParent.addChild( resetAllButtonNode );
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
            // buttons centered below control panel
            eraseLinesButton.setOffset( 0, 0 );
            resetAllButtonNode.setOffset( eraseLinesButton.getFullBoundsReference().getWidth() + 10, 0 );
            buttonsParent.setOffset( visibilityControls.getFullBoundsReference().getCenterX() - ( buttonsParent.getFullBoundsReference().getWidth() / 2 ),
                                     getStageSize().getHeight() - yMargin - buttonsParent.getFullBoundsReference().getHeight() );
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
    }
}
