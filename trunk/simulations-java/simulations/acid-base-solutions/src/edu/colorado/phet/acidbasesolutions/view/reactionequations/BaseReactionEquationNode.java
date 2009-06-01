/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.equilibrium.EquilibriumModel;

/**
 * Reaction equation for bases.
 * 
 * Weak base:  B + H2O <-> BH+ + OH-
 * Strong base: MOH -> M+ + OH-
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BaseReactionEquationNode extends AbstractReactionEquationNode {
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    private boolean scaleEnabled;
    
    public BaseReactionEquationNode( AqueousSolution solution ) {
        super();
        
        setTerm( 1, ABSSymbols.H2O, ABSConstants.H2O_COLOR, ABSImages.H2O_STRUCTURE );
        setTerm( 3, ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR, ABSImages.OH_MINUS_STRUCTURE );
        
        this.solution = solution;
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
        setTerm( 0, solute.getSymbol(), solute.getColor() );
        setTerm( 2, solute.getConjugateSymbol(), solute.getConjugateColor() );
        
        // H2O does not scale, use black text when scaling is enabled
        Color waterColor = ( isScaleEnabled() ? Color.BLACK : ABSConstants.H2O_COLOR );
        setTerm( 1, ABSSymbols.H2O, waterColor, ABSImages.H2O_STRUCTURE );
        
        // strong vs weak base
        final boolean isStrong = solution.getSolute().isStrong();
        setBidirectional( !isStrong );
        setTerm1Visible( !isStrong );
        
        // concentration scaling
        if ( scaleEnabled ) {
            EquilibriumModel equilibriumModel = solution.getEquilibriumModel();
            scaleTermToConcentration( 0, equilibriumModel.getReactantConcentration() );
            scaleTermToConcentration( 1, equilibriumModel.getH2OConcentration() );
            scaleTermToConcentration( 2, equilibriumModel.getProductConcentration() );
            scaleTermToConcentration( 3, equilibriumModel.getOHConcentration() );
        }
        else {
            scaleAllTerms( 1 );
        }
    }
}