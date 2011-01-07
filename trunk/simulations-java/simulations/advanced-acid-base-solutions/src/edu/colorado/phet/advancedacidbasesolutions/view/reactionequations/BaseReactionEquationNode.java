// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.view.reactionequations;

import java.awt.Color;

import edu.colorado.phet.advancedacidbasesolutions.AABSColors;
import edu.colorado.phet.advancedacidbasesolutions.AABSImages;
import edu.colorado.phet.advancedacidbasesolutions.AABSSymbols;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.advancedacidbasesolutions.model.Solute;
import edu.colorado.phet.advancedacidbasesolutions.model.Solute.ICustomSolute;

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
    
    public BaseReactionEquationNode( AqueousSolution solution ) {
        super();
        this.solution = solution;
        setTerm( H2O_INDEX, AABSSymbols.H2O, AABSColors.H2O_EQUATION, AABSImages.H2O_STRUCTURE );
        setTerm( OH_MINUS_INDEX, AABSSymbols.OH_MINUS, AABSColors.OH_MINUS, AABSImages.OH_MINUS_STRUCTURE );
        update();
        updateH2OColor();
    }
    
    public void update() {
        
        Solute solute = solution.getSolute();
        
        // symbols and colors (strong vs weak)
        setTerm( REACTANT_INDEX, solute.getSymbol(), solute.getColor(), solute.getStructure() );
        setTerm( PRODUCT_INDEX, solute.getConjugateSymbol(), solute.getConjugateColor(), solute.getConjugateStructure() );
        
        // strong vs weak base
        final boolean isStrong = solution.getSolute().isStrong();
        setBidirectional( !isStrong );
        setTerm1Visible( !isStrong );
        
        // concentration scaling
        scaleTermToConcentration( REACTANT_INDEX, solution.getReactantConcentration() );
        scaleTermToConcentration( PRODUCT_INDEX, solution.getProductConcentration() );
        scaleTermToConcentration( OH_MINUS_INDEX, solution.getOHConcentration() );

        // Lewis structure diagrams
        setStructuresVisible( solution.getSolute() instanceof ICustomSolute );
    }
    
    protected void updateH2OColor() {
        // H2O does not scale, use black text when scaling is enabled
        Color waterColor = ( isScalingEnabled() ? Color.BLACK : AABSColors.H2O_EQUATION );
        setTermColor( H2O_INDEX, waterColor );
    }
}