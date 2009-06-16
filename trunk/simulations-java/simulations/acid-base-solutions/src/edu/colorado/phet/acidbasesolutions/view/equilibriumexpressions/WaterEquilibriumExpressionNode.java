/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSColors;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Water;

/**
 * Equilibrium expression for water.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WaterEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    private final AqueousSolution solution;
    
    public WaterEquilibriumExpressionNode( AqueousSolution solution ) {
        super( false );
        this.solution = solution;
        setKLabel( ABSSymbols.Kw );
        setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSColors.H3O_PLUS );
        setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSColors.OH_MINUS );
        update();
    }
    
    public void update() {
       
        // K value
        setKValue( Water.getEquilibriumConstant() );
        
        // concentration scaling
        scaleLeftNumeratorToConcentration( solution.getH3OConcentration() );
        scaleRightNumeratorToConcentration( solution.getOHConcentration() );
    }
}