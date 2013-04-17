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
    
    public AcidReactionEquationNode( AqueousSolution solution ) {
        super();
        this.solution = solution;
        Solute solute = solution.getSolute();
        setTerm( REACTANT_INDEX, solute.getSymbol(), solute.getColor(), solute.getStructure() );
        setTerm( H2O_INDEX, AABSSymbols.H2O, AABSColors.H2O_EQUATION, AABSImages.H2O_STRUCTURE );
        setTerm( PRODUCT_INDEX, solute.getConjugateSymbol(), solute.getConjugateColor(), solute.getConjugateStructure() );
        setTerm( H3O_PLUS_INDEX, AABSSymbols.H3O_PLUS, AABSColors.H3O_PLUS, AABSImages.H3O_PLUS_STRUCTURE );
        update();
        updateH2OColor();
    }
    
    public void update() {
        
        // strong vs weak acid
        setBidirectional( !solution.getSolute().isStrong() );
        
        // concentration scaling
        scaleTermToConcentration( REACTANT_INDEX, solution.getReactantConcentration() );
        scaleTermToConcentration( H3O_PLUS_INDEX, solution.getH3OConcentration() );
        scaleTermToConcentration( PRODUCT_INDEX, solution.getProductConcentration() );

        // Lewis structure diagrams
        setStructuresVisible( solution.getSolute() instanceof ICustomSolute );
    }
    
    protected void updateH2OColor() {
        // H2O does not scale, use black text when scaling is enabled
        Color waterColor = ( isScalingEnabled() ? Color.BLACK : AABSColors.H2O_EQUATION );
        setTermColor( H2O_INDEX, waterColor );
    }
}