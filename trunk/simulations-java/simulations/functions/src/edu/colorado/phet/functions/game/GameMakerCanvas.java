// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.game;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.CenteredStageCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.functions.buildafunction.BuildAFunctionCanvas;
import edu.colorado.phet.functions.buildafunction.BuildAFunctionScene;

/**
 * @author Sam Reid
 */
public class GameMakerCanvas extends CenteredStageCanvas {
    public GameMakerCanvas() {
        setBackground( BuildAFunctionCanvas.BACKGROUND_COLOR );
        final PlayAreaNode playAreaNode = new PlayAreaNode();
        addChild( playAreaNode );
        HTMLImageButtonNode runButton = new HTMLImageButtonNode( "Run" ) {{
            setBackground( Color.red );
            setFont( BuildAFunctionScene.BUTTON_FONT );
            setEnabled( false );
            setOffset( playAreaNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, playAreaNode.getFullBounds().getMaxY() + 2 );
        }};
        addChild( runButton );
    }
}