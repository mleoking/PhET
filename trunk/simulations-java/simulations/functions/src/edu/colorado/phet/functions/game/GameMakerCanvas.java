// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.game;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.functions.buildafunction.AbstractFunctionsCanvas;
import edu.colorado.phet.functions.buildafunction.BuildAFunctionCanvas;
import edu.colorado.phet.functions.buildafunction.BuildAFunctionScene;

/**
 * @author Sam Reid
 */
public class GameMakerCanvas extends AbstractFunctionsCanvas {
    public GameMakerCanvas() {
        setBackground( BuildAFunctionCanvas.BACKGROUND_COLOR );
        addChild( new PlayAreaNode() );
        HTMLImageButtonNode runButton = new HTMLImageButtonNode( "Run" ) {{
            setBackground( Color.red );
            setFont( BuildAFunctionScene.BUTTON_FONT );
            setEnabled( false );
            setOffset( STAGE_SIZE.width / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.height / 2 - INSET );
        }};
        addChild( runButton );
    }
}