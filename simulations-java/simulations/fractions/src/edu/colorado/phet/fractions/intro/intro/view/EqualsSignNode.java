// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class EqualsSignNode extends PNode {
    public EqualsSignNode() {
        addChild( new PhetPText( "=", FractionNumberControlNode.NUMBER_FONT ) );
    }
}