package edu.colorado.phet.fractionsintro.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

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
    private int stage = 0;

    public BuildAFractionCanvas( final BuildAFractionModel model ) {
        this.model = model;
        currentNumberSceneLevel = new PNode();
        currentNumberSceneLevel.addChild( new NumberSceneNode( rootNode, model, STAGE_SIZE, this ) );
        addChild( new VisibilityNode( model.selectedScene.valueEquals( Scene.numbers ), currentNumberSceneLevel ) );
    }

    public void goToNext() {
        stage = stage + 1;
        currentNumberSceneLevel.addChild( new NumberSceneNode( rootNode, model, STAGE_SIZE, this ) {{
            setOffset( STAGE_SIZE.getWidth() * stage, 0 );
        }} );
        currentNumberSceneLevel.animateToTransform( AffineTransform.getTranslateInstance( -STAGE_SIZE.getWidth() * stage, 0 ), 1000 );
    }
}