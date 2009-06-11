/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.Solute.ICustomSolute;

/**
 * Reaction equation for bases.
 * 
 * Weak base:  B + H2O <-> BH+ + OH-
 * Strong base: MOH -> M+ + OH-
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BaseReactionEquationNode extends AbstractReactionEquationNode {
    
    private static final int REACTANT_INDEX = 0; // B or MOH
    private static final int H2O_INDEX = 1;
    private static final int PRODUCT_INDEX = 2; // BH+ or M+
    private static final int OH_MINUS_INDEX = 3;
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    
    public BaseReactionEquationNode( AqueousSolution solution ) {
        super();
        
        setTerm( OH_MINUS_INDEX, ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR, ABSImages.OH_MINUS_STRUCTURE );
        
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

    private void updateView() {
        
        Solute solute = solution.getSolute();
        
        // symbols and colors
        setTerm( REACTANT_INDEX, solute.getSymbol(), solute.getColor(), solute.getStructure() );
        setTerm( PRODUCT_INDEX, solute.getConjugateSymbol(), solute.getConjugateColor(), solute.getConjugateStructure() );
        
        // H2O does not scale, use black text when scaling is enabled
        Color waterColor = ( isScalingEnabled() ? Color.BLACK : ABSConstants.H2O_EQUATION_COLOR );
        setTerm( H2O_INDEX, ABSSymbols.H2O, waterColor, ABSImages.H2O_STRUCTURE );
        
        // strong vs weak base
        final boolean isStrong = solution.getSolute().isStrong();
        setBidirectional( !isStrong );
        setTerm1Visible( !isStrong );
        
        // concentration scaling
        scaleTermToConcentration( REACTANT_INDEX, solution.getReactantConcentration() );
        scaleTermToConcentration( PRODUCT_INDEX, solution.getProductConcentration() );
        scaleTermToConcentration( OH_MINUS_INDEX, solution.getOHConcentration() );

        // Lewis structure diagrams
        setAllStructuresVisible( solution.getSolute() instanceof ICustomSolute );
    }
}