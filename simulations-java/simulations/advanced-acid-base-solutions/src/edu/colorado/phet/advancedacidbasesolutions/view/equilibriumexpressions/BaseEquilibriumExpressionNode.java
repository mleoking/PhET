// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.advancedacidbasesolutions.AABSColors;
import edu.colorado.phet.advancedacidbasesolutions.AABSSymbols;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.advancedacidbasesolutions.model.Solute;

/**
 * Equilibrium expression for bases.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BaseEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    private final AqueousSolution solution;
    
    public BaseEquilibriumExpressionNode( AqueousSolution solution ) {
        super( true, true );
        assert( solution.isBasic() );
        this.solution = solution;
        setKLabel( AABSSymbols.Kb );
        setRightNumeratorProperties( AABSSymbols.OH_MINUS, AABSColors.OH_MINUS );
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

        // concentration scaling
        scaleLeftNumeratorToConcentration( solution.getProductConcentration() );
        scaleRightNumeratorToConcentration( solution.getOHConcentration() );
        scaleDenominatorToConcentration( solution.getReactantConcentration() );
    }
}