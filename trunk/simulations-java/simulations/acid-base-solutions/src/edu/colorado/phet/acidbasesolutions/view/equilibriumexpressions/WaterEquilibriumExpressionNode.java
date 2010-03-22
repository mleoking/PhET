/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.AABSColors;
import edu.colorado.phet.acidbasesolutions.AABSSymbols;
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