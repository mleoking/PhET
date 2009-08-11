package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;

/**
 * Dialog that displays the K equilibrium expression for a solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class KExpressionDialog extends EquilibriumExpressionsDialog {
    
    public KExpressionDialog( Frame owner, final AqueousSolution solution ) {
        super( owner, solution, false /* showWaterExpression */ );
        setTitle( ABSStrings.TITLE_EQUILIBRIUM_EXPRESSION );
    }
}
