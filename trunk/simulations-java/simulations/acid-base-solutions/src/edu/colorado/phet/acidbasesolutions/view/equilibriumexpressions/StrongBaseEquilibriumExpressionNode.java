package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

/**
 * Strong base equilibrium expression: Kb = [M+][OH-] / [MOH] = Large
 */
public class StrongBaseEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    public StrongBaseEquilibriumExpressionNode( String baseSymbol, String metalSymbol ) {
        super();
        setKLabel( ABSSymbols.Kb );
        setLeftNumeratorProperties( metalSymbol, ABSConstants.M_COLOR );
        setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
        setDenominatorProperties( baseSymbol, ABSConstants.MOH_COLOR );
        setLargeValueVisible( true );
    }
    
    public void setBaseScale( double scale ) {
        setDenominatorScale( scale );
    }
    
    public void setMetalScale( double scale ) {
        setLeftNumeratorScale( scale );
    }
    
    public void setOHScale( double scale ) {
        setRightNumeratorScale( scale );
    }

}