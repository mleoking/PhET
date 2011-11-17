// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.intro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.intro.intro.model.FractionsIntroModel;

/**
 * Canvas for "Fractions Intro" sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroCanvas extends AbstractFractionsCanvas {

    public static final Font CONTROL_FONT = new PhetFont( 16 );

    public FractionsIntroCanvas( final FractionsIntroModel model ) {

        RepresentationControlPanel representationControlPanel = new RepresentationControlPanel( new Property<RepresentationControlPanel.Fill>( RepresentationControlPanel.Fill.SEQUENTIAL ) ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2 - 100, INSET );
        }};
        addChild( representationControlPanel );

        ZeroOffsetNode fractionEqualityPanel = new ZeroOffsetNode( new FractionEqualityPanel( model ) ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2 - 100, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
        addChild( fractionEqualityPanel );

        OptionsControlPanel optionsControlPanel = new OptionsControlPanel( new Property<Visualization>( Visualization.FRACTION ) );
        addChild( optionsControlPanel );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
                resetAll();
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setConfirmationEnabled( false );
        }};
        addChild( resetAllButtonNode );

        optionsControlPanel.setOffset( STAGE_SIZE.width - optionsControlPanel.getFullBounds().getWidth() - INSET, STAGE_SIZE.height - resetAllButtonNode.getFullBounds().getHeight() - INSET - optionsControlPanel.getFullBounds().getHeight() - INSET );
        resetAllButtonNode.setOffset( optionsControlPanel.getFullBounds().getCenterX() - resetAllButtonNode.getFullBounds().getWidth() / 2, STAGE_SIZE.height - resetAllButtonNode.getFullBounds().getHeight() - INSET );
    }

    private void resetAll() {

    }
}