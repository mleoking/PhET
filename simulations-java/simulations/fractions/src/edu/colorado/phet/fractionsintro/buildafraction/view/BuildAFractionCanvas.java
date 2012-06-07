package edu.colorado.phet.fractionsintro.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.Scene;
import edu.colorado.phet.fractionsintro.buildafraction.view.numbers.NumberSceneContext;
import edu.colorado.phet.fractionsintro.buildafraction.view.numbers.NumberSceneNode;
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
    private boolean inited = false;

    public BuildAFractionCanvas( final BuildAFractionModel model ) {
        this.model = model;
        addChild( sceneLayer );
        pictureScene.addChild( new NumberSceneNode( model.level.get(), rootNode, model, STAGE_SIZE, this ) );
        numberScene.addChild( new NumberSceneNode( model.level.get(), rootNode, model, STAGE_SIZE, this ) {{
            setOffset( 0, STAGE_SIZE.height );
        }} );
        sceneLayer.addChild( numberScene );
        sceneLayer.addChild( pictureScene );

        //Add reset button to a layer that won't pan
        addChild( new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
                numberScene.reset();
                numberScene.addChild( new NumberSceneNode( model.level.get(), rootNode, model, STAGE_SIZE, BuildAFractionCanvas.this ) {{
                    setOffset( 0, STAGE_SIZE.height );
                }} );
                pictureScene.reset();
                pictureScene.addChild( new NumberSceneNode( model.level.get(), rootNode, model, STAGE_SIZE, BuildAFractionCanvas.this ) );

            }
        }, this, 18, Color.black, Color.orange ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - INSET, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - INSET );
        }} );

        addChild( new HBox( new PSwing( radioButton( model, "Pictures", Scene.pictures ) ),
                            new PSwing( radioButton( model, "Numbers", Scene.numbers ) ) ) );

        model.selectedScene.addObserver( new VoidFunction1<Scene>() {
            public void apply( final Scene scene ) {
                if ( scene == Scene.pictures ) {
                    numberScene.animateToTransparency( 0.0f, 1000 );
                    pictureScene.animateToTransparency( 1f, 1000 );
                    sceneLayer.animateToPositionScaleRotation( 0, 0, 1, 0, inited ? 1000 : 0 );
                }
                else if ( scene == Scene.numbers ) {
                    numberScene.animateToTransparency( 1.0f, 1000 );
                    pictureScene.animateToTransparency( 0.0f, 1000 );
                    sceneLayer.animateToPositionScaleRotation( 0, -STAGE_SIZE.height, 1, 0, inited ? 1000 : 0 );
                }
            }
        } );
        inited = true;
    }

    private PropertyRadioButton<Scene> radioButton( final BuildAFractionModel model, final String text, Scene scene ) {
        return new PropertyRadioButton<Scene>( null, text, model.selectedScene, scene ) {{
            setFont( new PhetFont( 24 ) );
            setOpaque( false );
        }};
    }

    public void goToNext() {
        model.nextLevel();
        numberScene.addChild( new NumberSceneNode( model.level.get(), rootNode, model, STAGE_SIZE, this ) {{
            setOffset( STAGE_SIZE.width * model.level.get(), STAGE_SIZE.height );
        }} );
        numberScene.animateToTransform( AffineTransform.getTranslateInstance( -STAGE_SIZE.getWidth() * model.level.get(), 0 ), 1000 );
    }
}