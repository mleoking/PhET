package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

/**
 * Weak base equilibrium expression: Kb = [BH+][OH-] / [B] = value
 */
class WeakBaseEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    public WeakBaseEquilibriumExpressionNode( String baseSymbol, String acidSymbol ) {
        super();
        setKLabel( ABSSymbols.Kb );
        setLeftNumeratorProperties( acidSymbol, ABSConstants.BH_COLOR );
        setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
        setDenominatorProperties( baseSymbol, ABSConstants.B_COLOR );
        setKValue( 0 );
    }
    
    public void setBaseScale( double scale ) {
        setDenominatorScale( scale );
    }
    
    public void setAcidScale( double scale ) {
        setLeftNumeratorScale( scale );
    }
    
    public void setOHScale( double scale ) {
        setRightNumeratorScale( scale );
    }
}