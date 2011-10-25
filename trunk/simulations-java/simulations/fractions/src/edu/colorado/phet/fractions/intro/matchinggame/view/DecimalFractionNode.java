// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.colorado.phet.fractions.intro.intro.view.ObservableFractionNumberNode;

/**
 * @author Sam Reid
 */
public class DecimalFractionNode extends RepresentationNode {
    public DecimalFractionNode( ModelViewTransform transform, Fraction decimalFraction ) {
        super( transform, decimalFraction );
        addChild( new PhetPText( new DecimalFormat( "0.00" ).format( fraction.getValue() ), ObservableFractionNumberNode.FONT ) );
        scale( 0.5 );
    }
}