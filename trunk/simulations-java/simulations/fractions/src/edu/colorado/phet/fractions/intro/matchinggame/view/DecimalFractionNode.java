// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import java.text.DecimalFormat;

import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractions.intro.matchinggame.model.DecimalFraction;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class DecimalFractionNode extends PNode {
    public DecimalFractionNode( DecimalFraction decimalFraction ) {
        addChild( new PhetPText( new DecimalFormat( "0.00" ).format( decimalFraction.fraction.getValue() ) ) );
    }
}