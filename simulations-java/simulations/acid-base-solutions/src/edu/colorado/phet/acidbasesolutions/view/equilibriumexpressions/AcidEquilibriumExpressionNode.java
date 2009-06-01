
package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.equilibrium.EquilibriumModel;

/**
 * Equilibrium expression for acids: Ka = [H3O+][A-] / [HA] = value
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AcidEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {

    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    private boolean scaleEnabled;

    public AcidEquilibriumExpressionNode( AqueousSolution solution ) {
        super( true /* hasDenominator */ );
        
        this.solution = solution;
        this.scaleEnabled = false;
        
        setKLabel( ABSSymbols.Ka );
        setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
        
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
        setRightNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
        setDenominatorProperties( solute.getSymbol(), solute.getColor() );

        // K value
        setKValue( solute.getStrength() );
        setLargeValueVisible( solute.isStrong() );

        // concentration scaling
        if ( scaleEnabled ) {
            EquilibriumModel equilibriumModel = solution.getEquilibriumModel();
            scaleLeftNumeratorToConcentration( equilibriumModel.getH3OConcentration() );
            scaleRightNumeratorToConcentration( equilibriumModel.getProductConcentration() );
            scaleDenominatorToConcentration( equilibriumModel.getReactantConcentration() );
        }
        else {
            scaleAllTerms( 1 );
        }
    }
}
