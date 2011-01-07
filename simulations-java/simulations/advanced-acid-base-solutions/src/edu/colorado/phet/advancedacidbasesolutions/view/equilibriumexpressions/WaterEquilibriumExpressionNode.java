// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.advancedacidbasesolutions.AABSColors;
import edu.colorado.phet.advancedacidbasesolutions.AABSSymbols;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.advancedacidbasesolutions.model.Water;

/**
 * Equilibrium expression for water.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WaterEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    private final AqueousSolution solution;
    
    public WaterEquilibriumExpressionNode( AqueousSolution solution ) {
        super( false, false );
        this.solution = solution;
        setKLabel( AABSSymbols.Kw );
        setLeftNumeratorProperties( AABSSymbols.H3O_PLUS, AABSColors.H3O_PLUS );
        setRightNumeratorProperties( AABSSymbols.OH_MINUS, AABSColors.OH_MINUS );
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