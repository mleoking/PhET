package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Water;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;

/**
 * Water equilibrium expression: Kw = [H3O+][OH-] = 1.0 x 10^-14
 */
class WaterEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    private boolean scaleEnabled;
    
    public WaterEquilibriumExpressionNode( AqueousSolution solution ) {
        super( false /* hasDenominator */ );
        setKLabel( ABSSymbols.Kw );
        setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
        setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
        setKValue( Water.getEquilibriumConstant() );
        
        this.solution = solution;
        scaleEnabled = false;
        
        solutionListener = new SolutionListener() {

            public void soluteChanged() {
                updateView();
            }

            public void concentrationChanged() {
                updateView();
            }

            public void strengthChanged() {
                updateView();
            }
        };
        solution.addSolutionListener( solutionListener );
        
        updateView();
    }
    
    public void cleanup() {
        solution.removeSolutionListener( solutionListener );
    }
    
    public void setScaleEnabled( boolean enabled ) {
        if ( enabled != this.scaleEnabled ) {
            this.scaleEnabled = enabled;
            updateView();
        }
    }

    public boolean isScaleEnabled() {
        return scaleEnabled;
    }
    
    private void updateView() {
        if ( isScaleEnabled() ) {
            scaleLeftNumeratorToConcentration( solution.getH3OConcentration() );
            scaleRightNumeratorToConcentration( solution.getOHConcentration() );
        }
        else {
            scaleAllTerms( 1.0 );
        }
    }
}