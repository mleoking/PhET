package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.Water;

/**
 * Water equilibrium expression: Kw = [H3O+][OH-] = 1.0 x 10^-14
 */
class WaterEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    public WaterEquilibriumExpressionNode() {
        super();
        setKLabel( ABSSymbols.Kw );
        setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
        setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
        setDenominatorVisible( false );
        setKValue( Water.getEquilibriumConstant() );
    }
    
    public void setH3OScale( double scale ) {
        setLeftNumeratorScale( scale );
    }
    
    public void setOHScale( double scale ) {
        setRightNumeratorScale( scale );
    }
}