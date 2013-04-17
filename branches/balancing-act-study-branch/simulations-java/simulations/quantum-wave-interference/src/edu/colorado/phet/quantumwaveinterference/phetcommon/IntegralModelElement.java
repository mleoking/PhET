// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.phetcommon;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jun 27, 2005
 * Time: 1:39:24 PM
 */

public class IntegralModelElement implements ModelElement {
    private int step = 0;
    private ModelElement modelElement;
    private int interval = 0;

    public IntegralModelElement( ModelElement modelElement, int interval ) {
        this.modelElement = modelElement;
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval( int interval ) {
        this.interval = interval;
    }

    public void stepInTime( double dt ) {
        step++;
        if( step >= interval ) {
            modelElement.stepInTime( dt );
            step = 0;
        }
    }
}
