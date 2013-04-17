// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;

/**
 * Node for a number that gets dragged out of the toolbox.
 *
 * @author Sam Reid
 */
public class NumberNode extends RichPNode {
    public final int number;
    private final PhetPText textNode;

    public NumberNode( final int number ) {
        this.number = number;
        textNode = new PhetPText( number + "", new PhetFont( 64, true ) );
        addChild( textNode );
    }

    //Make the number non-bold when it expands into the "whole" part of a mixed fraction
    public void setBoldFont( boolean boldFont ) { textNode.setFont( new PhetFont( 64, boldFont ) ); }
}