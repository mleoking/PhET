// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.maketheequation;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for the graph node in all "Make the Equation" (MTE) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MTE_GraphNode extends GraphNode {

    private final PNode guessNodeParent;

    public MTE_GraphNode( final Graph graph,
                          Property<Line> guessLine,
                          Line answerLine,
                          final ModelViewTransform mvt ) {
        super( graph, mvt );

        // In MTE challenges, the graph is never interactive
        setPickable( false );
        setChildrenPickable( false );

        // parent of the user's guess, to maintain rendering order.
        guessNodeParent = new PComposite();
        guessNodeParent.setVisible( false );

        // the correct answer
        LineNode answerNode = createAnswerLineNode( answerLine, graph, mvt );
        answerNode.setEquationVisible( false );

        // Rendering order
        addChild( guessNodeParent );
        addChild( answerNode );

        // Show the user's current guess, initially hidden
        guessLine.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                // draw the line
                guessNodeParent.removeAllChildren();
                LineNode guessNode = createGuessLineNode( line, graph, mvt );
                guessNode.setEquationVisible( false );
                guessNodeParent.addChild( guessNode );
            }
        } );
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
