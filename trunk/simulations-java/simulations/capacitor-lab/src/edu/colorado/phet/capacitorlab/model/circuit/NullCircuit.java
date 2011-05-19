// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

//TODO delete this when all circuits are implemented

/**
 * A circuit that does nothing, used as a placeholder for things that haven't been implemented.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NullCircuit extends AbstractCircuit {

    public NullCircuit( String displayName, IClock clock, CLModelViewTransform3D mvt, Point3D batteryLocation ) {
        super( displayName, clock, mvt, batteryLocation );
    }

    public ArrayList<Capacitor> getCapacitors() {
        return new ArrayList<Capacitor>();
    }

    public double getTotalCapacitance() {
        return 0;
    }

    public double getTotalCharge() {
        return 0;
    }

    public double getVoltageAt( Shape shape ) {
        return 0;
    }
}
