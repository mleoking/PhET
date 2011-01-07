// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.advancedacidbasesolutions.AABSColors;
import edu.colorado.phet.advancedacidbasesolutions.AABSSymbols;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.advancedacidbasesolutions.model.Solute;

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
        setKLabel( AABSSymbols.Ka );
        setLeftNumeratorProperties( AABSSymbols.H3O_PLUS, AABSColors.H3O_PLUS );
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