// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.linegraphing.LGConstants;
import edu.colorado.phet.linegraphing.common.view.LGCanvas;
import edu.colorado.phet.linegraphing.intro.model.IntroModel;
import edu.colorado.phet.linegraphing.intro.view.SlopeInterceptFormControl.SlopeInterceptForm;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Intro" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroCanvas extends LGCanvas implements Resettable {

    private final Property<SlopeInterceptForm> slopeInterceptForm = new Property<SlopeInterceptForm>( SlopeInterceptForm.Y_FORM ); //TODO relocate
    private final Property<Boolean> unitPositiveSlopeVisible = new Property<Boolean>( false ); //TODO relocate
    private final Property<Boolean> unitNegativeSlopeVisible = new Property<Boolean>( false ); //TODO relocate

    public IntroCanvas( IntroModel model ) {

        PNode graphNode = new GraphNode();
        PNode equationNode = new SlopeInterceptEquationNode();
        PNode savedLinesControl = new SavedLinesControl();
        PNode slopeInterceptFormControl = new SlopeInterceptFormControl( slopeInterceptForm );
        PNode standardEquationsControl = new StandardEquationsControl( unitPositiveSlopeVisible, unitNegativeSlopeVisible );
        PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, null, LGConstants.CONTROL_FONT_SIZE, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( graphNode );
            addChild( equationNode );
            addChild( savedLinesControl );
            addChild( slopeInterceptFormControl );
            addChild( standardEquationsControl );
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
            slopeInterceptFormControl.setOffset( graphNode.getFullBoundsReference().getMaxX() + 20,
                                                 savedLinesControl.getFullBoundsReference().getMaxY() + 60 );
            equationNode.setOffset( slopeInterceptFormControl.getXOffset(),
                                    slopeInterceptFormControl.getFullBoundsReference().getMaxY() + 20 );
            // bottom-right of graph
            standardEquationsControl.setOffset( graphNode.getFullBoundsReference().getMaxX() + 20,
                                                graphNode.getFullBoundsReference().getMaxY() - standardEquationsControl.getFullBoundsReference().getHeight() );
            // lower right
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - yMargin );
        }
    }

    public void reset() {
        slopeInterceptForm.reset();
        unitPositiveSlopeVisible.reset();
        unitNegativeSlopeVisible.reset();
    }
}
