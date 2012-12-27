// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.SlopeToolNode;
import edu.colorado.phet.linegraphing.linegame.model.MTE_Challenge;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Graph node in all "Make the Equation" (MTE) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MTE_GraphNode extends GraphNode {

    private final PNode guessNodeParent;
    private final PNode slopeToolNode;

    public MTE_GraphNode( final MTE_Challenge challenge ) {
        super( challenge.graph, challenge.mvt );

        // In MTE challenges, the graph is never interactive
        setPickable( false );
        setChildrenPickable( false );

        // parent of the user's guess, to maintain rendering order.
        guessNodeParent = new PComposite();
        guessNodeParent.setVisible( false );

        // the correct answer
        LineNode answerNode = new LineNode( challenge.answer, challenge.graph, challenge.mvt );

        // Slope tool
        slopeToolNode = new SlopeToolNode( challenge.guess, challenge.mvt );

        // Rendering order
        addChild( guessNodeParent );
        addChild( answerNode );
        addChild( slopeToolNode );

        // Show the user's current guess, initially hidden
        challenge.guess.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                // draw the line
                guessNodeParent.removeAllChildren();
                LineNode guessNode = new LineNode( line, challenge.graph, challenge.mvt );
                guessNodeParent.addChild( guessNode );
            }
        } );
    }

    public void setSlopeToolVisible( boolean visible ) {
        slopeToolNode.setVisible( visible );
    }

    // Changes the visibility of the "guess" line.
    public void setGuessVisible( boolean visible ) {
        guessNodeParent.setVisible( visible );
    }
}
