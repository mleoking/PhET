package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Water;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.equilibrium.EquilibriumModel;

/**
 * Water equilibrium expression: Kw = [H3O+][OH-] = 1.0 x 10^-14
 */
class WaterEquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {
    
    private final AqueousSolution solution;
    private boolean scaleEnabled;
    
    public WaterEquilibriumExpressionNode( AqueousSolution solution ) {
        super();
        setKLabel( ABSSymbols.Kw );
        setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
        setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
        setDenominatorVisible( false );
        setKValue( Water.getEquilibriumConstant() );
        
        this.solution = solution;
        scaleEnabled = false;
        
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
        if ( isScaleEnabled() ) {
            EquilibriumModel equilibriumModel = solution.getEquilibriumModel();
            scaleLeftNumeratorToConcentration( equilibriumModel.getH3OConcentration() );
            scaleRightNumeratorToConcentration( equilibriumModel.getOHConcentration() );
        }
        else {
            setScaleAll( 1.0 );
        }
    }
}