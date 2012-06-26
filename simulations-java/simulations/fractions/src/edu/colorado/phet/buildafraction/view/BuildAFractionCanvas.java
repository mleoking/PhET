package edu.colorado.phet.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import edu.colorado.phet.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.buildafraction.view.WorldSelectionScreen.Context;
import edu.colorado.phet.buildafraction.view.numbers.NumberSceneNode;
import edu.colorado.phet.buildafraction.view.pictures.PictureSceneNode;
import edu.colorado.phet.buildafraction.view.pictures.SceneContext;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

/**
 * Canvas for the build a fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas implements Context, MainContext, SceneContext {

    private static final int rgb = 240;
    public static final Color CONTROL_PANEL_BACKGROUND = new Color( rgb, rgb, rgb );

    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    public static final Stroke controlPanelStroke = new BasicStroke( 2 );

    private PNode currentScene;

    public static class StartScreen extends PNode {
        public StartScreen( final Context context ) {

            //Title text, only shown when the user is choosing a level
            PNode titleText = new PNode() {{
                addChild( new PhetPText( "Build a Fraction", new PhetFont( 38, true ) ) );
            }};

            WorldSelectionScreen worldSelectionScreen = new WorldSelectionScreen( context ) {{
                centerFullBoundsOnPoint( STAGE_SIZE.width / 2, STAGE_SIZE.height / 2 );
            }};
            addChild( worldSelectionScreen );
            titleText.centerFullBoundsOnPoint( STAGE_SIZE.width / 2, INSET + titleText.getFullBounds().getHeight() / 2 );
            addChild( titleText );
        }
    }

    public BuildAFractionCanvas( final BuildAFractionModel model, final boolean dev ) {
        //Set a really light blue because there is a lot of white everywhere
        setBackground( new Color( 236, 251, 251 ) );

        currentScene = new StartScreen( this );
        addChild( currentScene );
    }

    public void startShapesLevelSelection() { animateTo( new ShapesLevelSelectionScreen( "Build a Fraction: Shapes", this ), true ); }

    public void startNumberLevelSelection() { animateTo( new NumbersLevelSelectionScreen( "Build a Fraction: Numbers", this ), true ); }

    private void animateTo( final PNode node, boolean right ) {
        node.setOffset( right ? STAGE_SIZE.width : -STAGE_SIZE.width, 0 );
        addChild( node );

        final PNode oldNode = currentScene;
        PActivity activity = currentScene.animateToPositionScaleRotation( right ? -STAGE_SIZE.width : STAGE_SIZE.width, 0, 1, 0, 400 );
        activity.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {

                PInterpolatingActivity fade = oldNode.animateToTransparency( 0, 1000 );
                fade.setDelegate( new PActivityDelegate() {
                    public void activityStarted( final PActivity activity ) {
                    }

                    public void activityStepped( final PActivity activity ) {
                    }

                    public void activityFinished( final PActivity activity ) {
                        oldNode.removeFromParent();
                    }
                } );
            }
        } );
        node.animateToPositionScaleRotation( 0, 0, 1, 0, 400 );
        currentScene = node;
    }

    public void goBackToHomeScreen() {
        animateTo( new StartScreen( this ), false );
    }

    public void levelButtonPressed( final AbstractLevelSelectionNode parent, final LevelInfo info ) {
        animateTo( createLevelNode( parent, info.levelIndex ), true );
    }

    private PNode createLevelNode( final AbstractLevelSelectionNode parent, final int levelIndex ) {
        if ( parent instanceof ShapesLevelSelectionScreen ) {
            return new PictureSceneNode( levelIndex, new BuildAFractionModel( true ), STAGE_SIZE, this );
        }
        else {
            return new NumberSceneNode( levelIndex, rootNode, new BuildAFractionModel( true ), STAGE_SIZE, this );
        }
    }

    public void goToNextPictureLevel( final int newLevelIndex ) {
        animateTo( new PictureSceneNode( newLevelIndex, new BuildAFractionModel( true ), STAGE_SIZE, this ), true );
    }

    public void goToNextNumberLevel( final int newLevelIndex ) {
        animateTo( new NumberSceneNode( newLevelIndex, rootNode, new BuildAFractionModel( true ), STAGE_SIZE, this ), true );
    }

    public void goToPictureLevelSelectionScreen() {
        animateTo( new ShapesLevelSelectionScreen( "Build a Fraction: Shapes", this ), false );
    }

    public void goToNumberLevelSelectionScreen() {
        animateTo( new NumbersLevelSelectionScreen( "Build a Fraction: Numbers", this ), false );
    }
}