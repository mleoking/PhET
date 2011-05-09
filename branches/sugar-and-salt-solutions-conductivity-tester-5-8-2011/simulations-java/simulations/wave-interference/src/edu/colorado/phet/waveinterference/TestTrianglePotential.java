// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.Potential;

/**
 * Created by: Sam
 * Feb 7, 2008 at 11:15:17 AM
 */
public class TestTrianglePotential implements Potential {
    public double getPotential( int i, int j, int time ) {
        return i + j > 70 ? 1 : 0;
    }
}
