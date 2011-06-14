// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Tool node that is created when the user drags the conductivity tester from its toolbox.
 * This is modeled using composition since we must inherit from both ToolNode and ConductivityTesterNode
 *
 * @author Sam Reid
 */
public class ConductivityTesterToolNode extends ToolNode {
    private final SugarAndSaltSolutionsConductivityTesterNode node;

    //Reuse the same ConductivityTesterNode instead of creating new ones each time we drag out of the toolbox.
    public ConductivityTesterToolNode( SugarAndSaltSolutionsConductivityTesterNode node ) {
        this.node = node;
        addChild( node );
    }

    //Drag all parts of the conductivity tester when dragged out of the toolbox
    @Override public void dragAll( PDimension viewDelta ) {
        node.dragAll( viewDelta );
    }

    //Identify parts that can be dropped back in the toolbox, in this case, the light bulb
    @Override public PNode[] getDroppableComponents() {
        return node.getDroppableComponents();
    }
}
