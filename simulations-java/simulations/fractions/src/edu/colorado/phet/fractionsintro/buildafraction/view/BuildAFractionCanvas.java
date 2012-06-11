package edu.colorado.phet.fractionsintro.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.view.SpinnerButtonNode;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.Scene;
import edu.colorado.phet.fractionsintro.buildafraction.view.numbers.NumberSceneContext;
import edu.colorado.phet.fractionsintro.buildafraction.view.numbers.NumberSceneNode;
import edu.colorado.phet.fractionsintro.buildafraction.view.pictures.PictureSceneNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the build a fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas implements NumberSceneContext {

    private static final int rgb = 240;
    public static final Color CONTROL_PANEL_BACKGROUND = new Color( rgb, rgb, rgb );

    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    public static final Stroke controlPanelStroke = new BasicStroke( 2 );

    //Layer that is panned over vertically to switch between numbers and pictures.
    //Couldn't use camera for this since it is difficult to make camera objects scale up and down with the canvas but not translate with the screens
    private final PNode sceneLayer = new PNode();
    private final SceneNode numberScene = new SceneNode();
    private final SceneNode pictureScene = new SceneNode();
    private final BuildAFractionModel model;

    //Flag is just used for debugging, so the first scene change is not done in animation
    private boolean initialized = false;
    private static final int FADE_IN_TIME = 200;
    private static final int FADE_OUT_TIME = 1000;
    private static final PhetFont scoreboardFont = new PhetFont( 26, true );

    public BuildAFractionCanvas( final BuildAFractionModel model ) {
        this.model = model;
        addChild( sceneLayer );
        pictureScene.addChild( new PictureSceneNode( model.numberLevel.get(), rootNode, model, STAGE_SIZE, this ) );
        numberScene.addChild( new NumberSceneNode( model.numberLevel.get(), rootNode, model, STAGE_SIZE, this ) {{
            setOffset( 0, STAGE_SIZE.height );
        }} );
        sceneLayer.addChild( numberScene );
        sceneLayer.addChild( pictureScene );

        //Add reset button to a layer that won't pan
        addChild( new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
                numberScene.reset();
                numberScene.addChild( new NumberSceneNode( model.numberLevel.get(), rootNode, model, STAGE_SIZE, BuildAFractionCanvas.this ) {{
                    setOffset( 0, STAGE_SIZE.height );
                }} );
                pictureScene.reset();
                pictureScene.addChild( new PictureSceneNode( model.numberLevel.get(), rootNode, model, STAGE_SIZE, BuildAFractionCanvas.this ) );

            }
        }, this, 18, Color.black, Color.orange ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - INSET, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - INSET );
            setConfirmationEnabled( false );
        }} );

        addChild( new VBox( VBox.LEFT_ALIGNED,
                            new PSwing( radioButton( model, "Pictures", Scene.pictures ) ),
                            new PSwing( radioButton( model, "Numbers", Scene.numbers ) ) ) {{
            setOffset( INSET, INSET / 2 );//Inset manually tuned to line up mode, level and score
        }} );

        final SpinnerButtonNode leftButtonNode = new SpinnerButtonNode( spinnerImage( Images.LEFT_BUTTON_UP ), spinnerImage( Images.LEFT_BUTTON_PRESSED ), spinnerImage( Images.LEFT_BUTTON_GRAY ), new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                goToNumberLevel( model.numberLevel.get() - 1 );
            }
        }, model.numberLevel.greaterThan( 0 ) );
        final SpinnerButtonNode rightButtonNode = new SpinnerButtonNode( spinnerImage( Images.RIGHT_BUTTON_UP ), spinnerImage( Images.RIGHT_BUTTON_PRESSED ), spinnerImage( Images.RIGHT_BUTTON_GRAY ), new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                goToNumberLevel( model.numberLevel.get() + 1 );
            }
        }, model.numberLevel.lessThan( 20 ) );
        addChild( rightButtonNode );

        //Level indicator and navigation buttons for number mode
        addChild( new HBox( 30, leftButtonNode, new PhetPText( "Level 100", scoreboardFont ) {{
            model.numberLevel.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer integer ) {
                    setText( "Level " + ( integer + 1 ) );
                }
            } );
        }}, rightButtonNode ) {{
            setOffset( 300, INSET );
        }} );

        addChild( new PhetPText( "Score 0", scoreboardFont ) {{
            model.numberScore.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer score ) {
                    setText( "Score " + score );
                }
            } );
            setOffset( 600, INSET );
        }} );

        model.selectedScene.addObserver( new VoidFunction1<Scene>() {
            public void apply( final Scene scene ) {
                if ( scene == Scene.pictures ) {
                    numberScene.animateToTransparency( 0.0f, FADE_OUT_TIME );
                    pictureScene.animateToTransparency( 1f, FADE_IN_TIME );
                    sceneLayer.animateToPositionScaleRotation( 0, 0, 1, 0, initialized ? 1000 : 0 );
                }
                else if ( scene == Scene.numbers ) {
                    numberScene.animateToTransparency( 1.0f, FADE_IN_TIME );
                    pictureScene.animateToTransparency( 0.0f, FADE_OUT_TIME );
                    sceneLayer.animateToPositionScaleRotation( 0, -STAGE_SIZE.height, 1, 0, initialized ? 1000 : 0 );
                }
            }
        } );
        initialized = true;
    }

    private BufferedImage spinnerImage( final BufferedImage image ) { return BufferedImageUtils.multiScaleToHeight( image, 30 ); }

    private PropertyRadioButton<Scene> radioButton( final BuildAFractionModel model, final String text, Scene scene ) {
        return new PropertyRadioButton<Scene>( null, text, model.selectedScene, scene ) {{
            setFont( scoreboardFont );
            setOpaque( false );
        }};
    }

    public void goToNumberLevel( int level ) {
        model.goToNumberLevel( level );
        for ( Object node : numberScene.getChildrenReference() ) {
            PNode n2 = (PNode) node;
            //Fade out the other levels, but not the target one (if it already exists)
            if ( n2 != getNumberSceneNode( level ) ) {
                n2.animateToTransparency( 0.0f, FADE_OUT_TIME );
            }
        }
        if ( getNumberSceneNode( level ) == null ) {
            numberScene.addChild( new NumberSceneNode( model.numberLevel.get(), rootNode, model, STAGE_SIZE, this ) {{
                setOffset( STAGE_SIZE.width * model.numberLevel.get(), STAGE_SIZE.height );
            }} );
        }
        else {
            getNumberSceneNode( level ).animateToTransparency( 1.0f, FADE_IN_TIME );
        }
        numberScene.animateToTransform( AffineTransform.getTranslateInstance( -STAGE_SIZE.getWidth() * model.numberLevel.get(), 0 ), 1000 );
    }

    private NumberSceneNode getNumberSceneNode( final int level ) {
        for ( Object child : numberScene.getChildrenReference() ) {
            PNode node = (PNode) child;
            if ( node instanceof NumberSceneNode && ( (NumberSceneNode) node ).level == level ) {
                return (NumberSceneNode) node;
            }
        }
        return null;
    }

    public void nextNumberLevel() { goToNumberLevel( model.numberLevel.get() + 1 ); }
}