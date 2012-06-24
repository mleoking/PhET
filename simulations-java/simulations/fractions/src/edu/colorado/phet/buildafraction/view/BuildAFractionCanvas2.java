package edu.colorado.phet.buildafraction.view;

import java.awt.Color;

import edu.colorado.phet.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.buildafraction.view.WorldSelectionScreen.Context;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the build a fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas2 extends AbstractFractionsCanvas implements Context, LevelSelectionScreen.Context {
    private final PNode startScreen;
    private final LevelSelectionScreen shapesLevelSelectionScreen;
    private final LevelSelectionScreen numbersLevelSelectionScreen;

    public BuildAFractionCanvas2( final BuildAFractionModel model, final boolean dev ) {
        startScreen = new PNode() {{

            //Title text, only shown when the user is choosing a level
            PNode titleText = new PNode() {{
                addChild( new PhetPText( "Build a Fraction", new PhetFont( 38, true ) ) );
            }};

            WorldSelectionScreen worldSelectionScreen = new WorldSelectionScreen( BuildAFractionCanvas2.this ) {{
                centerFullBoundsOnPoint( STAGE_SIZE.width / 2, STAGE_SIZE.height / 2 );
            }};
            addChild( worldSelectionScreen );
            titleText.centerFullBoundsOnPoint( STAGE_SIZE.width / 2, INSET + titleText.getFullBounds().getHeight() / 2 );
            addChild( titleText );
        }};
        addChild( startScreen );

        //Set a really light blue because there is a lot of white everywhere
        setBackground( new Color( 236, 251, 251 ) );

        shapesLevelSelectionScreen = new LevelSelectionScreen( "Build a Fraction: Shapes", this ) {{
            setInitialPosition( STAGE_SIZE.width + STAGE_SIZE.width / 2 - getFullBounds().getWidth() / 2,
                                INSET );
        }};
        addChild( shapesLevelSelectionScreen );

        numbersLevelSelectionScreen = new LevelSelectionScreen( "Build a Fraction: Numbers", this ) {{
            setInitialPosition( STAGE_SIZE.width + STAGE_SIZE.width / 2 - getFullBounds().getWidth() / 2,
                                INSET );
        }};
        addChild( numbersLevelSelectionScreen );
    }

    public void startShapesLevelSelection() {
        startScreen.animateToPositionScaleRotation( startScreen.getXOffset() - STAGE_SIZE.width, startScreen.getYOffset(), 1, 0, 400 );
        shapesLevelSelectionScreen.animateToPositionScaleRotation( 0, 0, 1, 0, 400 );
    }

    public void startNumberLevelSelection() {
        startScreen.animateToPositionScaleRotation( startScreen.getXOffset() - STAGE_SIZE.width, startScreen.getYOffset(), 1, 0, 400 );
        numbersLevelSelectionScreen.animateToPositionScaleRotation( 0, 0, 1, 0, 400 );
    }

    public void homeButtonPressed() {
        startScreen.animateToPositionScaleRotation( 0, 0, 1, 0, 400 );
        shapesLevelSelectionScreen.animateHome( 400 );
        numbersLevelSelectionScreen.animateHome( 400 );
    }
}