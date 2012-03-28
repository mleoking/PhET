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
    private final Property<Boolean> yEqualsXVisible = new Property<Boolean>( false );
    private final Property<Boolean> yEqualsNegativeXVisible = new Property<Boolean>( false );
    private final Property<Boolean> pointToolVisible = new Property<Boolean>( false );
    private final Property<Boolean> riseOverRunVisible = new Property<Boolean>( false );
    private final Property<Boolean> graphLinesVisible = new Property<Boolean>( false );

    public IntroCanvas( IntroModel model ) {

        PNode graphNode = new ZeroOffsetNode( new LineGraphNode( model.graph, model.mvt ) );
        PNode equationNode = new SlopeInterceptEquationNode();
        TextButtonNode saveLineButton = new TextButtonNode( Strings.SAVE_LINE, LGConstants.CONTROL_FONT, new Color( 0, 255, 255 ) );
        final TextButtonNode eraseLinesButton = new TextButtonNode( Strings.ERASE_LINES, LGConstants.CONTROL_FONT, new Color( 0, 255, 255 ) ) {{
            setEnabled( false );
        }};
        PNode visibilityControls = new VisibilityControls( yEqualsXVisible, yEqualsNegativeXVisible, pointToolVisible, riseOverRunVisible, graphLinesVisible );
        PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, null, LGConstants.CONTROL_FONT_SIZE, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( graphNode );
            addChild( equationNode );
            addChild( saveLineButton );
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
            saveLineButton.setOffset( graphNode.getFullBoundsReference().getMaxX() + 20,
                                         graphNode.getFullBoundsReference().getMinY() );
            // center-right of graph
            equationNode.setOffset( graphNode.getFullBoundsReference().getMaxX() + 20,
                                                 saveLineButton.getFullBoundsReference().getMaxY() + 60 );
            // bottom-right of graph
            visibilityControls.setOffset( graphNode.getFullBoundsReference().getMaxX() + 20,
                                          graphNode.getFullBoundsReference().getMaxY() - visibilityControls.getFullBoundsReference().getHeight() );
            // lower right
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - yMargin );
            // above Rest All button
            eraseLinesButton.setOffset( resetAllButtonNode.getFullBoundsReference().getMaxX() - eraseLinesButton.getFullBoundsReference().getWidth(),
                                        resetAllButtonNode.getFullBoundsReference().getMinY() - eraseLinesButton.getFullBoundsReference().getHeight() - 5 );
        }

        saveLineButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                eraseLinesButton.setEnabled( true );
                //TODO save the current line
            }
        } );

        eraseLinesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                eraseLinesButton.setEnabled( false );
                //TODO erase the saved lines
            }
        } );
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
