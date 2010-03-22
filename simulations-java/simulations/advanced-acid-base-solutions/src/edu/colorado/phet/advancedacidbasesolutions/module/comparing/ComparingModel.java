/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.comparing;

import edu.colorado.phet.acidbasesolutions.model.AABSClock;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.module.AABSModel;

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
