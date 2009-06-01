package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.equilibrium.EquilibriumModel;

/**
 * Water reaction equation: H2O + H2O <-> H3O+ + OH-
 */
public class WaterReactionEquationNode extends AbstractReactionEquationNode {
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    private boolean scaleEnabled;
    
    public WaterReactionEquationNode( AqueousSolution solution ) {
        super();
        
        setTerm( 0, ABSSymbols.H2O, ABSConstants.H2O_COLOR, ABSImages.H2O_STRUCTURE );
        setTerm( 1, ABSSymbols.H2O, ABSConstants.H2O_COLOR, ABSImages.H2O_STRUCTURE );
        setTerm( 2, ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR, ABSImages.H3O_PLUS_STRUCTURE );
        setTerm( 3, ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR, ABSImages.OH_MINUS_STRUCTURE );
        
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
        
        // H2O does not scale, use black text when scaling is enabled
        Color waterColor = ( isScaleEnabled() ? Color.BLACK : ABSConstants.H2O_COLOR );
        setTerm( 0, ABSSymbols.H2O, waterColor, ABSImages.H2O_STRUCTURE );
        setTerm( 1, ABSSymbols.H2O, waterColor, ABSImages.H2O_STRUCTURE );
        
        if ( isScaleEnabled() ) {
            EquilibriumModel equilibriumModel = solution.getEquilibriumModel();
            scaleTermToConcentration( 2, equilibriumModel.getH3OConcentration() );
            scaleTermToConcentration( 3, equilibriumModel.getOHConcentration() );
        }
        else {
            scaleAllTerms( 1.0 );
        }
    }
}