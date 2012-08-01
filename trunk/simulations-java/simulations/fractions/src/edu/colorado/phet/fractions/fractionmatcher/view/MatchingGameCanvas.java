// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.view;

import fj.data.List;

import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameModel;
import edu.umd.cs.piccolo.PNode;

/**
 * This class shows the graphics and provides user interaction for the matching game.
 * Even though using an immutable model, this class uses a more traditional piccolo approach for graphics,
 * creating nodes once and maintaining them and only updating them when necessary.
 *
 * @author Sam Reid
 */
public class MatchingGameCanvas extends AbstractFractionsCanvas {
    private final HashMap<Integer, GameNode> levelNodes = new HashMap<Integer, GameNode>();

    public MatchingGameCanvas( final boolean dev, final MatchingGameModel model, String title, final List<PNode> patterns ) {

        //Show the start screen when the user is choosing a level.
        addChild( new StartScreen( model, title, patterns ) );

        addChild( new PNode() {{
            model.addLevelStartedListener( new VoidFunction1<Integer>() {
                public void apply( Integer level ) {
                    removeAllChildren();
                    //Things to show during the game (i.e. not when settings dialog showing.)
                    if ( !levelNodes.containsKey( level ) ) {
                        levelNodes.put( level, new GameNode( dev, model, new EmptyBarGraphNode(), rootNode ) );
                    }
                    addChild( levelNodes.get( level ) );
                }
            } );
        }} );
    }
}