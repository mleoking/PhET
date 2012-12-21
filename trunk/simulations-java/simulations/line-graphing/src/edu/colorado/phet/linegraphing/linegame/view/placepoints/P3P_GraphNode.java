// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.placepoints;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.SlopeToolNode;
import edu.colorado.phet.linegraphing.linegame.model.graphtheline.GTL_Challenge;
import edu.colorado.phet.linegraphing.linegame.model.placepoints.P3P_Challenge;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for the graph node in all "Place 3 Points" (P3P) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class P3P_GraphNode extends GraphNode {

    private final PNode linesParent, manipulatorsParent;

    public P3P_GraphNode( P3P_Challenge challenge ) {
        super( challenge.graph, challenge.mvt );

        // To reduce brain damage during development, show the answer as a translucent gray line.
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            LineNode answerNode = createAnswerLineNode( challenge.answer.withColor( new Color( 0, 0, 0, 25 ) ), challenge.graph, challenge.mvt );
            answerNode.setEquationVisible( false );
            addChild( answerNode );
        }

        // parent nodes, for maintaining rendering order
        linesParent = new PNode();
        manipulatorsParent = new PNode();

        // rendering order
        addChild( linesParent );
        addChild( manipulatorsParent );
    }

    protected void addLineNode( PNode node ) {
        linesParent.addChild( node );
    }

    protected void addManipulatorNode( PNode node ) {
        manipulatorsParent.addChild( node );
    }

    // Creates the node that corresponds to the "answer" line.
    protected abstract LineNode createAnswerLineNode( Line line, Graph graph, ModelViewTransform mvt );

    // Creates the node that corresponds to the "guess" line.
    protected abstract LineNode createGuessLineNode( Line line, Graph graph, ModelViewTransform mvt );

    // Changes the visibility of the "answer" line.
    public abstract void setAnswerVisible( boolean visible );
}
