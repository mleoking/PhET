/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSColors;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;

/**
 * Equilibrium expression for acids.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AcidEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    private final AqueousSolution solution;
    
    public AcidEquilibriumExpressionNode( AqueousSolution solution ) {
        super( true, true );
        assert( solution.isAcidic() );
        this.solution = solution;
        Solute solute = solution.getSolute();
        setKLabel( ABSSymbols.Ka );
        setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSColors.H3O_PLUS );
        setRightNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
        setDenominatorProperties( solute.getSymbol(), solute.getColor() );
        update();
    }

    public void update() {
        assert( solution.isAcidic() );
        
        // K value
        Solute solute = solution.getSolute();
        setKValue( solute.getStrength() );

        // concentration scaling
        scaleLeftNumeratorToConcentration( solution.getH3OConcentration() );
        scaleRightNumeratorToConcentration( solution.getProductConcentration() );
        scaleDenominatorToConcentration( solution.getReactantConcentration() );
    }
}