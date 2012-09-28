// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.graphtheline;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.linegame.model.graphtheline.GTL_Challenge;

/**
 * Base class for the graph node in all "Graph the Line" (GTL) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GTL_GraphNode extends GraphNode {

    public GTL_GraphNode( GTL_Challenge challenge ) {
        super( challenge.graph, challenge.mvt );

        // To reduce brain damage during development, show the answer as a translucent gray line.
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            LineNode answerNode = createAnswerLineNode( challenge.answer.withColor( new Color( 0, 0, 0, 25 ) ), challenge.graph, challenge.mvt );
            answerNode.setEquationVisible( false );
            addChild( answerNode );
        }
    }

    // Creates the node that corresponds to the "answer" line.
    protected abstract LineNode createAnswerLineNode( Line line, Graph graph, ModelViewTransform mvt );

    // Creates the node that corresponds to the "guess" line.
    protected abstract LineNode createGuessLineNode( Line line, Graph graph, ModelViewTransform mvt );

    // Changes the visibility of the "answer" line.
    public abstract void setAnswerVisible( boolean visible );
}
