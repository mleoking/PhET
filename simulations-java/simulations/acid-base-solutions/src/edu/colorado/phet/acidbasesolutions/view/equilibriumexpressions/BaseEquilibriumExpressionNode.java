/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;

class BaseEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    private final AqueousSolution solution;
    
    public BaseEquilibriumExpressionNode( AqueousSolution solution ) {
        super( true );
        assert( solution.isBasic() );
        this.solution = solution;
        Solute solute = solution.getSolute();
        setKLabel( ABSSymbols.Kb );
        setLeftNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
        setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
        setDenominatorProperties( solute.getSymbol(), solute.getColor() );
        update();
    }

    public void update() {
        assert( solution.isBasic() );
        
        Solute solute = solution.getSolute();
        
        // strong vs weak base
        setLeftNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
        setDenominatorProperties( solute.getSymbol(), solute.getColor() );

        // K value
        setKValue( solute.getStrength() );
        setLargeValueVisible( solute.isStrong() );

        // concentration scaling
        scaleLeftNumeratorToConcentration( solution.getProductConcentration() );
        scaleRightNumeratorToConcentration( solution.getOHConcentration() );
        scaleDenominatorToConcentration( solution.getReactantConcentration() );
    }
}