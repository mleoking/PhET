/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

/**
 * Acid equilibrium expression: Ka = [H3O+][A-] / [HA] = value
 */
class AbstractAcidEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    public AbstractAcidEquilibriumExpressionNode( String acidSymbol, String baseSymbol ) {
        super();
        setKLabel( ABSSymbols.Ka );
        setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
        setRightNumeratorProperties( baseSymbol, ABSConstants.A_COLOR );
        setDenominatorProperties( acidSymbol, ABSConstants.HA_COLOR );
        setKValue( 0 );
    }
    
    public void setAcidScale( double scale ) {
        setDenominatorScale( scale );
    }
    
    public void setBaseScale( double scale ) {
        setRightNumeratorScale( scale );
    }
    
    public void setH3OScale( double scale ) {
        setLeftNumeratorScale( scale );
    }
}