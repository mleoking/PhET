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
 * Reaction equation for acids.
 * 
 * Weak acid: HA + H2O <-> H3O+ + A-
 * Strong acid: HA + H2O -> H3O+ + A-
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AcidReactionEquationNode extends AbstractReactionEquationNode {
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    private boolean scaleEnabled;
    
    public AcidReactionEquationNode( AqueousSolution solution ) {
        super();
        
        setTerm( 0, ABSSymbols.HA, ABSConstants.HA_COLOR, ABSImages.HA_STRUCTURE );
        setTerm( 1, ABSSymbols.H2O, ABSConstants.H2O_COLOR, ABSImages.H2O_STRUCTURE );
        setTerm( 2, ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR, ABSImages.H3O_PLUS_STRUCTURE );
        setTerm( 3, ABSSymbols.A_MINUS, ABSConstants.A_COLOR, ABSImages.A_MINUS_STRUCTURE );
        
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
        setTerm( 3, solute.getConjugateSymbol(), solute.getConjugateColor() );
        
        // H2O does not scale, use black text when scaling is enabled
        Color waterColor = ( isScaleEnabled() ? Color.BLACK : ABSConstants.H2O_COLOR );
        setTerm( 1, ABSSymbols.H2O, waterColor, ABSImages.H2O_STRUCTURE );
        
        // strong vs weak acid
        setBidirectional( !solution.getSolute().isStrong() );
        
        // concentration scaling
        if ( scaleEnabled) {
            EquilibriumModel equilibriumModel = solution.getEquilibriumModel();
            scaleTermToConcentration( 0, equilibriumModel.getReactantConcentration() );
            scaleTermToConcentration( 0, equilibriumModel.getH3OConcentration() );
            scaleTermToConcentration( 0, equilibriumModel.getProductConcentration() );
        }
        else {
            scaleAllTerms( 1 );
        }
    }
}