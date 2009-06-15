/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.comparing;

import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSModel;

/**
 * ComparingModel is the model for ComparingModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingModel extends ABSModel {
    
    private final AqueousSolution solutionLeft, solutionRight;
    
    public ComparingModel( ABSClock clock ) {
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
