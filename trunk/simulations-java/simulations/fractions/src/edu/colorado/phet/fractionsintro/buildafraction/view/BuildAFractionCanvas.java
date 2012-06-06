package edu.colorado.phet.fractionsintro.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.model.Scene;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.view.VisibilityNode;

/**
 * Canvas for the build a fraction tab.
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas {
    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    public static final Stroke controlPanelStroke = new BasicStroke( 2 );

    public BuildAFractionCanvas( final BuildAFractionModel model ) {
        addChild( new VisibilityNode( model.selectedScene.valueEquals( Scene.numbers ), new NumberSceneNode( rootNode, model, STAGE_SIZE ) ) );
    }
}