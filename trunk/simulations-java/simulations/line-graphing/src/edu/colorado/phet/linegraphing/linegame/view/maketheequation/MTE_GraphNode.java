// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.maketheequation;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.SlopeToolNode;
import edu.colorado.phet.linegraphing.linegame.model.MTE_Challenge;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for the graph node in all "Make the Equation" (MTE) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MTE_GraphNode extends GraphNode {

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
        LineNode answerNode = createAnswerLineNode( challenge.answer, challenge.graph, challenge.mvt );
        answerNode.setEquationVisible( false );

        // Slope tool
        final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( 0.85 );
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
                LineNode guessNode = createGuessLineNode( line, challenge.graph, challenge.mvt );
                guessNode.setEquationVisible( false );
                guessNodeParent.addChild( guessNode );
            }
        } );
    }

    public void setSlopeToolVisible( boolean visible ) {
        slopeToolNode.setVisible( visible );
    }

    // Creates the node that corresponds to the "answer" line.
    protected abstract LineNode createAnswerLineNode( Line line, Graph graph, ModelViewTransform mvt );

    // Creates the node that corresponds to the "guess" line.
    protected abstract LineNode createGuessLineNode( Line line, Graph graph, ModelViewTransform mvt );

    // Changes the visibility of the "guess" line.
    public void setGuessVisible( boolean visible ) {
        guessNodeParent.setVisible( visible );
    }
}
