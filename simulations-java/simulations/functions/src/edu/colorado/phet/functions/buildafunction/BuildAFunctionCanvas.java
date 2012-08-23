package edu.colorado.phet.functions.buildafunction;

import java.awt.Color;

import edu.colorado.phet.functions.intro.ChallengeProgressionCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class BuildAFunctionCanvas extends ChallengeProgressionCanvas {

    public static final Color BACKGROUND_COLOR = new Color( 236, 251, 251 );
    private BuildAFunctionScene scene;

    public BuildAFunctionCanvas() {

        //Set a really light blue because there is a lot of white everywhere
        setBackground( BACKGROUND_COLOR );

        scene = new BuildAFunctionScene();
        addChild( scene );
    }

    @Override protected void nextButtonPressed() {
    }

    @Override protected void finishAnimation( final PNode newScene ) {
    }
}