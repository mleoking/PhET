// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.intro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.intro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractions.intro.intro.view.representationcontrolpanel.RepresentationControlPanel;

/**
 * Canvas for "Fractions Intro" sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroCanvas extends AbstractFractionsCanvas {

    public FractionsIntroCanvas( final FractionsIntroModel model ) {

        final RepresentationControlPanel representationControlPanel = new RepresentationControlPanel( model.representation ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullWidth() / 2, INSET );
        }};
        addChild( representationControlPanel );

        final RepresentationArea representationArea = new RepresentationArea( model.representation, model.numerator, model.denominator, model.containerState ) {{
            setOffset( INSET, representationControlPanel.getFullBounds().getMaxY() + 100 );
        }};
        addChild( representationArea );

        ZeroOffsetNode fractionEqualityPanel = new ZeroOffsetNode( new FractionEqualityPanel( model ) ) {{
            setOffset( 35, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
        addChild( fractionEqualityPanel );

        EqualityDisplayOptionsControlPanel equalityDisplayOptionsControlPanel = new EqualityDisplayOptionsControlPanel( model.showReduced, model.showMixed );
        addChild( equalityDisplayOptionsControlPanel );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setConfirmationEnabled( false );
        }};
        addChild( resetAllButtonNode );

        equalityDisplayOptionsControlPanel.setOffset( STAGE_SIZE.width - equalityDisplayOptionsControlPanel.getFullBounds().getWidth() - INSET, STAGE_SIZE.height - resetAllButtonNode.getFullBounds().getHeight() - INSET - equalityDisplayOptionsControlPanel.getFullBounds().getHeight() - INSET );
        resetAllButtonNode.setOffset( equalityDisplayOptionsControlPanel.getFullBounds().getCenterX() - resetAllButtonNode.getFullBounds().getWidth() / 2, STAGE_SIZE.height - resetAllButtonNode.getFullBounds().getHeight() - INSET );
    }
}