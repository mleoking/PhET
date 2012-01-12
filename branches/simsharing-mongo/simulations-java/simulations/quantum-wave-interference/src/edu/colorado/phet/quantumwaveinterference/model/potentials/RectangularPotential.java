// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.model.potentials;

import edu.colorado.phet.quantumwaveinterference.model.Potential;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.model.RectangularObject;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:11 PM
 */

public class RectangularPotential extends RectangularObject implements Potential {

    private double potential = Double.MAX_VALUE / 100.0;

    public RectangularPotential( QWIModel QWIModel, int x, int y, int width, int height ) {
        super( QWIModel, x, y, width, height );
    }

    public double getPotential() {
        return potential;
    }

    public void setPotential( double potential ) {
        this.potential = potential;
    }

    public double getPotential( int x, int y, int timestep ) {
        if( getBounds().contains( x, y ) ) {
            return potential;
        }
        else {
            return 0.0;
        }
    }
}
