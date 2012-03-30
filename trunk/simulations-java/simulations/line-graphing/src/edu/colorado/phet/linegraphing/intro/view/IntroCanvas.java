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
    public final Property<ImmutableVector2D> point = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );//TODO move to model

    private final InteractiveLineGraphNode lineGraphNode;

    public IntroCanvas( final IntroModel model ) {

        lineGraphNode = new InteractiveLineGraphNode( model.graph, model.mvt, model.interactiveLine, model.yEqualsXLine, model.yEqualsNegativeXLine, linesVisible, point );
        PNode graphNode = new ZeroOffsetNode( lineGraphNode );
        final TextButtonNode eraseLinesButton = new TextButtonNode( Strings.ERASE_LINES, LGConstants.CONTROL_FONT, new Color( 0, 255, 255 ) ) {{
            setEnabled( false );
        }};
        PNode equationControlPanel = new EquationControlPanel( model.interactiveLine, lineGraphNode, eraseLinesButton,
                                                               IntroModel.RISE_RANGE, IntroModel.RUN_RANGE, IntroModel.INTERCEPT_RANGE );
        PNode visibilityControls = new VisibilityControls( linesVisible, lineGraphNode.riseOverRunVisible, lineGraphNode.yEqualsXVisible, lineGraphNode.yEqualsNegativeXVisible,
                                                           lineGraphNode.pointToolVisible );
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
    }
}
