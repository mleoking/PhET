/* Copyright 2009, University of Colorado */
package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Water;

class WaterEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    private final AqueousSolution solution;
    
    public WaterEquilibriumExpressionNode( AqueousSolution solution ) {
        super( false );
        this.solution = solution;
        setKLabel( ABSSymbols.Kw );
        setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
        setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
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