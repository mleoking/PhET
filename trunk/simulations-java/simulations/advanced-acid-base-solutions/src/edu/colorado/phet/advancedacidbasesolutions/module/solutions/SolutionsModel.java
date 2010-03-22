/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import edu.colorado.phet.acidbasesolutions.model.AABSClock;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.module.AABSModel;

/**
 * SolutionsModel is the model for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsModel extends AABSModel {
    
    private final AqueousSolution solution;
    
    public SolutionsModel( AABSClock clock ) {
        super( clock );
        this.solution = new AqueousSolution();
    }
    
    public AqueousSolution getSolution() {
        return solution;
    }
}
