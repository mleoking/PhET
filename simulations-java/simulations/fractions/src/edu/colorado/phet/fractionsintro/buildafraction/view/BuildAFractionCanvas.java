package edu.colorado.phet.fractionsintro.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.Scene;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.view.VisibilityNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the build a fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas implements NumberSceneContext {
    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    public static final Stroke controlPanelStroke = new BasicStroke( 2 );
    private final PNode currentNumberSceneLevel;
    private final BuildAFractionModel model;

    public BuildAFractionCanvas( final BuildAFractionModel model ) {
        this.model = model;
        currentNumberSceneLevel = new PNode();
        currentNumberSceneLevel.addChild( new NumberSceneNode( model.level.get(), rootNode, model, STAGE_SIZE, this ) );
        addChild( new VisibilityNode( model.selectedScene.valueEquals( Scene.numbers ), currentNumberSceneLevel ) );

        addChild( new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
                currentNumberSceneLevel.setTransform( AffineTransform.getTranslateInstance( 0, 0 ) );
                currentNumberSceneLevel.removeAllChildren();
                currentNumberSceneLevel.addChild( new NumberSceneNode( model.level.get(), rootNode, model, STAGE_SIZE, BuildAFractionCanvas.this ) );
            }
        }, this, 18, Color.black, Color.orange ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - INSET, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - INSET );
        }} );
    }

    public void goToNext() {
        model.nextLevel();
        currentNumberSceneLevel.addChild( new NumberSceneNode( model.level.get(), rootNode, model, STAGE_SIZE, this ) {{
            setOffset( STAGE_SIZE.getWidth() * model.level.get(), 0 );
        }} );
        currentNumberSceneLevel.animateToTransform( AffineTransform.getTranslateInstance( -STAGE_SIZE.getWidth() * model.level.get(), 0 ), 1000 );
    }
}