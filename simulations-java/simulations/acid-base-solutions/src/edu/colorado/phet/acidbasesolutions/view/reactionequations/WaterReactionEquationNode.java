package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.image.BufferedImage;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;

/**
 * Water reaction equation: H2O + H2O <-> H3O+ + OH-
 */
public class WaterReactionEquationNode extends AbstractReactionEquationNode {
    
    public WaterReactionEquationNode() {
        setTerm( 0, ABSSymbols.H2O, ABSConstants.H2O_COLOR, ABSImages.H2O_STRUCTURE );
        setTerm( 1, ABSSymbols.H2O, ABSConstants.H2O_COLOR, ABSImages.H2O_STRUCTURE );
        setTerm( 2, ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR, ABSImages.H3O_PLUS_STRUCTURE );
        setTerm( 3, ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR, ABSImages.OH_MINUS_STRUCTURE );
    }
    
    protected BufferedImage getArrowImage() {
        return ABSImages.ARROW_DOUBLE;
    }
}