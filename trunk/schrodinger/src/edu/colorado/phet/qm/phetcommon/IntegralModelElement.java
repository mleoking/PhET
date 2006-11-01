/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jun 27, 2005
 * Time: 1:39:24 PM
 * Copyright (c) Jun 27, 2005 by Sam Reid
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
        System.out.println( "step = " + step + ", interval=" + interval );
        step++;
        if( step >= interval ) {
            modelElement.stepInTime( dt );
            step = 0;
        }
    }
}
