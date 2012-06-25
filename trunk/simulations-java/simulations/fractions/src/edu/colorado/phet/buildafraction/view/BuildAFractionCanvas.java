package edu.colorado.phet.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import edu.colorado.phet.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.buildafraction.view.WorldSelectionScreen.Context;
import edu.colorado.phet.buildafraction.view.pictures.PictureSceneContext;
import edu.colorado.phet.buildafraction.view.pictures.PictureSceneNode;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the build a fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas implements Context, edu.colorado.phet.buildafraction.view.Context {

    private static final int rgb = 240;
    public static final Color CONTROL_PANEL_BACKGROUND = new Color( rgb, rgb, rgb );

    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    public static final Stroke controlPanelStroke = new BasicStroke( 2 );

    private final PNode startScreen;
    private final AbstractLevelSelectionNode shapesLevelSelectionScreen;
    private final AbstractLevelSelectionNode numbersLevelSelectionScreen;
    private PictureSceneNode pictureSceneNode;

    public BuildAFractionCanvas( final BuildAFractionModel model, final boolean dev ) {
        startScreen = new PNode() {{

            //Title text, only shown when the user is choosing a level
            PNode titleText = new PNode() {{
                addChild( new PhetPText( "Build a Fraction", new PhetFont( 38, true ) ) );
            }};

            WorldSelectionScreen worldSelectionScreen = new WorldSelectionScreen( BuildAFractionCanvas.this ) {{
                centerFullBoundsOnPoint( STAGE_SIZE.width / 2, STAGE_SIZE.height / 2 );
            }};
            addChild( worldSelectionScreen );
            titleText.centerFullBoundsOnPoint( STAGE_SIZE.width / 2, INSET + titleText.getFullBounds().getHeight() / 2 );
            addChild( titleText );
        }};
        addChild( startScreen );

        //Set a really light blue because there is a lot of white everywhere
        setBackground( new Color( 236, 251, 251 ) );

        shapesLevelSelectionScreen = new ShapesLevelSelectionScreen( "Build a Fraction: Shapes", this ) {{
            setInitialPosition( STAGE_SIZE.width + STAGE_SIZE.width / 2 - getFullBounds().getWidth() / 2,
                                INSET );
        }};
        addChild( shapesLevelSelectionScreen );

        numbersLevelSelectionScreen = new NumbersLevelSelectionScreen( "Build a Fraction: Numbers", this ) {{
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

    public void levelButtonPressed( final AbstractLevelSelectionNode parent, final LevelInfo info ) {
        shapesLevelSelectionScreen.animateToPositionScaleRotation( -STAGE_SIZE.width, 0, 1, 0, 400 );
        numbersLevelSelectionScreen.animateToPositionScaleRotation( -STAGE_SIZE.width, 0, 1, 0, 400 );
        pictureSceneNode = new PictureSceneNode( info.levelIndex, rootNode, new BuildAFractionModel( true ), STAGE_SIZE, new PictureSceneContext() {
            public void nextPictureLevel() {
            }

            public void goToLevelSelectionScreen() {
                numbersLevelSelectionScreen.animateToPositionScaleRotation( 0, 0, 1, 0, 400 );
                numbersLevelSelectionScreen.setTransparency( 0 );
                shapesLevelSelectionScreen.animateToPositionScaleRotation( 0, 0, 1, 0, 400 );
                pictureSceneNode.animateToPositionScaleRotation( STAGE_SIZE.width * 2, 0, 1, 0, 400 );
            }
        } ) {{
            setOffset( STAGE_SIZE.width * 2, 0 );
        }};
        addChild( pictureSceneNode );
        pictureSceneNode.animateToPositionScaleRotation( 0, 0, 1, 0, 400 );
    }
}