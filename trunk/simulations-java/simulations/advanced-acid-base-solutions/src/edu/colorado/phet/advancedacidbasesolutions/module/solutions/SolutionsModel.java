// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.module.solutions;

import edu.colorado.phet.advancedacidbasesolutions.model.AABSClock;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.advancedacidbasesolutions.module.AABSModel;

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
