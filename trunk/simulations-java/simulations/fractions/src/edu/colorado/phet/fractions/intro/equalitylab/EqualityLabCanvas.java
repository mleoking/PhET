// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.equalitylab;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.fractions.intro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.intro.intro.view.RepresentationArea;
import edu.colorado.phet.fractions.intro.intro.view.RepresentationControlPanel;

/**
 * Canvas for "Fractions Intro" sim.
 *
 * @author Sam Reid
 */
public class EqualityLabCanvas extends AbstractFractionsCanvas {

    public EqualityLabCanvas( final EqualityLabModel model ) {

        final RepresentationControlPanel representationControlPanel = new RepresentationControlPanel( model.representation ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, INSET );
        }};
        addChild( representationControlPanel );

        final RepresentationArea representationArea = new RepresentationArea( model.representation, model.numerator, model.denominator ) {{
            setOffset( INSET, representationControlPanel.getFullBounds().getMaxY() + 100 );
        }};
        addChild( representationArea );

        addChild( new ControlPanelNode( new PropertyCheckBox( "Separate", new BooleanProperty( true ) ) {{
            setFont( CONTROL_FONT );
        }} ) {{
            setOffset( representationControlPanel.getFullBounds().getMaxX() + INSET, representationControlPanel.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
                resetAll();
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setConfirmationEnabled( false );
        }};
        addChild( resetAllButtonNode );

        resetAllButtonNode.setOffset( STAGE_SIZE.getWidth() - resetAllButtonNode.getFullBounds().getWidth(), STAGE_SIZE.getHeight() - resetAllButtonNode.getFullBounds().getHeight() );
    }

    private void resetAll() {

    }
}