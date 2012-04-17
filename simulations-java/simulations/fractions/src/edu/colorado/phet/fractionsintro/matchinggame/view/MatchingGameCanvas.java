// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the matching game. Uses the immutable model so reconstructs the scene graph any time the model changes.
 *
 * @author Sam Reid
 */
public class MatchingGameCanvas extends AbstractFractionsCanvas {

    public static final double GAME_UI_SCALE = 1.5;

    public MatchingGameCanvas( final boolean showDeveloperControls, final MatchingGameModel model ) {
        addChild( new PNode() {{
            model.state.addObserver( new SimpleObserver() {
                @Override public void update() {
                    removeAllChildren();
                    addChild( new MatchingGameNode( showDeveloperControls, model.state, rootNode ) );
                }
            } );
        }} );
    }
}