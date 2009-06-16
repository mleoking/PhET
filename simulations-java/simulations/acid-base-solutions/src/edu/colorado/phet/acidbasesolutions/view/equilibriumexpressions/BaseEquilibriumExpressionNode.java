/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSColors;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;

/**
 * Equilibrium expression for bases.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BaseEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    private final AqueousSolution solution;
    
    public BaseEquilibriumExpressionNode( AqueousSolution solution ) {
        super( true );
        assert( solution.isBasic() );
        this.solution = solution;
        setKLabel( ABSSymbols.Kb );
        setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSColors.OH_MINUS );
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