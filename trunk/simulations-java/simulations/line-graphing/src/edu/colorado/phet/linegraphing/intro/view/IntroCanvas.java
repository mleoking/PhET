// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;

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
public class IntroCanvas extends LGCanvas {

    private final Property<SlopeInterceptForm> slopeInterceptForm = new Property<SlopeInterceptForm>( SlopeInterceptForm.Y_FORM ); //TODO relocate

    public IntroCanvas( IntroModel model ) {

        PNode savedLinesControl = new SavedLinesControl();
        PNode slopeInterceptFormControl = new SlopeInterceptFormControl( slopeInterceptForm );
        PNode resetAllButtonNode = new ResetAllButtonNode( model, null, LGConstants.CONTROL_FONT_SIZE, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( savedLinesControl );
            addChild( slopeInterceptFormControl );
            addChild( resetAllButtonNode );
        }

        // layout
        {
            // NOTE: Nodes that have corresponding model elements handle their own offsets.
            final double xMargin = 20;
            final double yMargin = 20;
            savedLinesControl.setOffset( getStageSize().getWidth() - savedLinesControl.getFullBoundsReference().getWidth() - xMargin,
                                       100 );
            slopeInterceptFormControl.setOffset( getStageSize().getWidth() - slopeInterceptFormControl.getFullBoundsReference().getWidth() - xMargin,
                                                 savedLinesControl.getFullBoundsReference().getMaxY() + 20 );
            // lower right
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - yMargin );
        }
    }
}
