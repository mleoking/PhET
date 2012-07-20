// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

/**
 * PlaneWaveCycle
 * <p/>
 * A single cycle of a plane wave. This is used to represent the EM emitted from a dipole when it
 * flips to its lower energy state
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PlaneWaveCycle extends RadiowaveSource {
    private double elapsedTime;

    public PlaneWaveCycle( Point2D location, double length, MutableVector2D direction ) {
        super( location, length, direction, Math.PI * 3 / 2 );
    }

    public void stepInTime( double dt ) {
        elapsedTime += dt;
        super.stepInTime( dt );
    }

    public double getValue() {
        double amplitude = 0;
        double period = 100;
        if ( elapsedTime <= period ) {
            amplitude = super.getValue();
        }
        return amplitude;
    }
}
