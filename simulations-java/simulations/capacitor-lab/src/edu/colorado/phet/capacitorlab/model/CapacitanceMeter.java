// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Model element for the Capacitance meter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitanceMeter extends BarMeter {

    public CapacitanceMeter( final ICircuit circuit, World world, Point3D location, boolean visible ) {
        super( circuit, world, location, visible, new Function1<ICircuit, Double>() {
            public Double apply( ICircuit iCircuit ) {
                return circuit.getTotalCapacitance();
            }
        } );
    }
}
