// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;

/**
 * @author Sam Reid
 */
public class EqualsSignNode extends RichPNode {
    public EqualsSignNode() {
        addChild( new PhetPText( "=", FractionNumberNode.NUMBER_FONT ) );
    }
}