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
 * Reaction equation for acids.
 * 
 * Weak acid: HA + H2O <-> H3O+ + A-
 * Strong acid: HA + H2O -> H3O+ + A-
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AcidReactionEquationNode extends AbstractReactionEquationNode {
    
    private static final int REACTANT_INDEX = 0; // HA
    private static final int H2O_INDEX = 1;
    private static final int H3O_PLUS_INDEX = 2;
    private static final int PRODUCT_INDEX = 3; // A-
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    
    public AcidReactionEquationNode( AqueousSolution solution ) {
        super();
        
        setTerm( H3O_PLUS_INDEX, ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR, ABSImages.H3O_PLUS_STRUCTURE );
        
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
        
        // strong vs weak acid
        setBidirectional( !solution.getSolute().isStrong() );
        
        // concentration scaling
        scaleTermToConcentration( REACTANT_INDEX, solution.getReactantConcentration() );
        scaleTermToConcentration( H3O_PLUS_INDEX, solution.getH3OConcentration() );
        scaleTermToConcentration( PRODUCT_INDEX, solution.getProductConcentration() );

        // Lewis structure diagrams
        setAllStructuresVisible( solution.getSolute() instanceof ICustomSolute );
    }
}