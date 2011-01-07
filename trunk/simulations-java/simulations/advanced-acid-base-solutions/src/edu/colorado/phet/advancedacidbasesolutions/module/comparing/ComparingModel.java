// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.module.comparing;

import edu.colorado.phet.advancedacidbasesolutions.model.AABSClock;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.advancedacidbasesolutions.module.AABSModel;

/**
 * ComparingModel is the model for ComparingModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingModel extends AABSModel {
    
    private final AqueousSolution solutionLeft, solutionRight;
    
    public ComparingModel( AABSClock clock ) {
        super( clock );
        this.solutionLeft = new AqueousSolution();
        this.solutionRight = new AqueousSolution();
    }
    
    public AqueousSolution getSolutionLeft() {
        return solutionLeft;
    }
    
    public AqueousSolution getSolutionRight() {
        return solutionRight;
    }
}
