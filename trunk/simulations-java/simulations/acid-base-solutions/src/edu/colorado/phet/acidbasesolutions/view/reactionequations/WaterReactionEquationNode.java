package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute.ICustomSolute;

/**
 * Water reaction equation: H2O + H2O <-> H3O+ + OH-
 */
class WaterReactionEquationNode extends AbstractReactionEquationNode {
    
    private static final int H2O_LEFT_INDEX = 0;
    private static final int H2O_RIGHT_INDEX = 1;
    private static final int H3O_PLUS_INDEX = 2;
    private static final int OH_MINUS_INDEX = 3;
    
    private final AqueousSolution solution;
    
    public WaterReactionEquationNode( AqueousSolution solution ) {
        super();
        this.solution = solution;
        setTerm( H2O_LEFT_INDEX, ABSSymbols.H2O, ABSConstants.H2O_EQUATION_COLOR, ABSImages.H2O_STRUCTURE );
        setTerm( H2O_RIGHT_INDEX, ABSSymbols.H2O, ABSConstants.H2O_EQUATION_COLOR, ABSImages.H2O_STRUCTURE );
        setTerm( H3O_PLUS_INDEX, ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR, ABSImages.H3O_PLUS_STRUCTURE );
        setTerm( OH_MINUS_INDEX, ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR, ABSImages.OH_MINUS_STRUCTURE );
        setBidirectional( true );
        update();
        updateH2OColor();
    }
    
    public void setScalingEnabled( boolean enabled ) {
        super.setScalingEnabled( enabled );
        updateH2OColor();
    }
    
    public void update() {

        updateH2OColor();

        // concentration scaling
        scaleTermToConcentration( H3O_PLUS_INDEX, solution.getH3OConcentration() );
        scaleTermToConcentration( OH_MINUS_INDEX, solution.getOHConcentration() );

        // Lewis structure diagrams
        setAllStructuresVisible( solution.getSolute() instanceof ICustomSolute );
    }
    
    protected void updateH2OColor() {
        // H2O does not scale, use black text when scaling is enabled
        Color waterColor = ( isScalingEnabled() ? Color.BLACK : ABSConstants.H2O_EQUATION_COLOR );
        setTermColor( H2O_LEFT_INDEX, waterColor );
        setTermColor( H2O_RIGHT_INDEX, waterColor );
    }
}