
package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;

/**
 * Equilibrium expression bases: Kb = [BH+][OH-] / [B] = value
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BaseEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {

    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    private boolean scaleEnabled;

    public BaseEquilibriumExpressionNode( AqueousSolution solution ) {
        super( true /* hasDenominator */ );
        
        this.solution = solution;
        this.scaleEnabled = false;
        
        setKLabel( ABSSymbols.Kb );
        setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
        
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
        
        Solute solute = solution.getSolute();

        // symbols and colors
        setLeftNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
        setDenominatorProperties( solute.getSymbol(), solute.getColor() );

        // K value
        setKValue( solute.getStrength() );
        setLargeValueVisible( solute.isStrong() );

        // concentration scaling
        if ( scaleEnabled ) {
            scaleLeftNumeratorToConcentration( solution.getProductConcentration() );
            scaleRightNumeratorToConcentration( solution.getOHConcentration() );
            scaleDenominatorToConcentration( solution.getReactantConcentration() );
        }
        else {
            scaleAllTerms( 1 );
        }
    }
}
