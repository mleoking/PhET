/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSModel;

/**
 * SolutionsModel is the model for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsModel extends ABSModel {
    
    private final AqueousSolution solution;
    
    public SolutionsModel( ABSClock clock ) {
        super( clock );
        this.solution = new AqueousSolution();
    }
    
    public AqueousSolution getSolution() {
        return solution;
    }
}
