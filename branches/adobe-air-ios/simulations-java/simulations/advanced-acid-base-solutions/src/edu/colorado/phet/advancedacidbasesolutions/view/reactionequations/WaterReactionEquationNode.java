// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.view.reactionequations;

import java.awt.Color;

import edu.colorado.phet.advancedacidbasesolutions.AABSColors;
import edu.colorado.phet.advancedacidbasesolutions.AABSImages;
import edu.colorado.phet.advancedacidbasesolutions.AABSSymbols;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.advancedacidbasesolutions.model.Solute.ICustomSolute;

/**
 * Water reaction equation: H2O + H2O <-> H3O+ + OH-
 */
public class WaterReactionEquationNode extends AbstractReactionEquationNode {
    
    private static final int H2O_LEFT_INDEX = 0;
    private static final int H2O_RIGHT_INDEX = 1;
    private static final int H3O_PLUS_INDEX = 2;
    private static final int OH_MINUS_INDEX = 3;
    
    private final AqueousSolution solution;
    
    public WaterReactionEquationNode( AqueousSolution solution ) {
        super();
        this.solution = solution;
        setTerm( H2O_LEFT_INDEX, AABSSymbols.H2O, AABSColors.H2O_EQUATION, AABSImages.H2O_STRUCTURE );
        setTerm( H2O_RIGHT_INDEX, AABSSymbols.H2O, AABSColors.H2O_EQUATION, AABSImages.H2O_STRUCTURE );
        setTerm( H3O_PLUS_INDEX, AABSSymbols.H3O_PLUS, AABSColors.H3O_PLUS, AABSImages.H3O_PLUS_STRUCTURE );
        setTerm( OH_MINUS_INDEX, AABSSymbols.OH_MINUS, AABSColors.OH_MINUS, AABSImages.OH_MINUS_STRUCTURE );
        setBidirectional( true );
        update();
        updateH2OColor();
    }
    
    public void update() {

        updateH2OColor();

        // concentration scaling
        scaleTermToConcentration( H3O_PLUS_INDEX, solution.getH3OConcentration() );
        scaleTermToConcentration( OH_MINUS_INDEX, solution.getOHConcentration() );

        // Lewis structure diagrams
        setStructuresVisible( solution.getSolute() instanceof ICustomSolute );
    }
    
    protected void updateH2OColor() {
        // H2O does not scale, use black text when scaling is enabled
        Color waterColor = ( isScalingEnabled() ? Color.BLACK : AABSColors.H2O_EQUATION );
        setTermColor( H2O_LEFT_INDEX, waterColor );
        setTermColor( H2O_RIGHT_INDEX, waterColor );
    }
}