
package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.Acid;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Base;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.equilibrium.EquilibriumModel;

/**
 * Equilibrium expression for acids and bases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AcidBaseEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {

    private final AqueousSolution solution;
    private boolean scaleEnabled;

    public AcidBaseEquilibriumExpressionNode( AqueousSolution solution ) {
        super( true /* hasDenominator */ );
        
        this.solution = solution;
        this.scaleEnabled = false;
        
        solution.addSolutionListener( new SolutionListener() {

            public void soluteChanged() {
                updateView();
            }

            public void concentrationChanged() {
                updateView();
            }

            public void strengthChanged() {
                updateView();
            }
        } );

        updateView();
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

        setVisible( !solution.isPureWater() );

        // labels and colors
        if ( solute instanceof Acid ) {
            // Ka = [H3O+][A-] / [HA] = value
            setKLabel( solute.getStrengthSymbol() );
            setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
            setRightNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
            setDenominatorProperties( solute.getSymbol(), solute.getColor() );
        }
        else if ( solute instanceof Base ) {
            // Kb = [BH+][OH-] / [B] = value
            setKLabel( solute.getStrengthSymbol() );
            setLeftNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
            setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
            setDenominatorProperties( solute.getSymbol(), solute.getColor() );
        }
        else {
            throw new UnsupportedOperationException( "unsupported solute type: " + solute.getClass().getName() );
        }

        // K value
        setKValue( solute.getStrength() );
        setLargeValueVisible( solute.isStrong() );

        // set scale
        if ( scaleEnabled ) {
            EquilibriumModel equilibriumModel = solution.getEquilibriumModel();
            if ( solute instanceof Acid ) {
                scaleLeftNumeratorToConcentration( equilibriumModel.getH3OConcentration() );
                scaleRightNumeratorToConcentration( equilibriumModel.getProductConcentration() );
                scaleDenominatorToConcentration( equilibriumModel.getReactantConcentration() );
            }
            else if ( solute instanceof Base ) {
                scaleLeftNumeratorToConcentration( equilibriumModel.getProductConcentration() );
                scaleRightNumeratorToConcentration( equilibriumModel.getOHConcentration() );
                scaleDenominatorToConcentration( equilibriumModel.getReactantConcentration() );
            }
            else {
                throw new UnsupportedOperationException( "unsupported solute type: " + solute.getClass().getName() );
            }
        }
        else {
            setScaleAll( 1 );
        }
    }
}
