// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NitrogenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.OxygenIonParticle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;

/**
 * Data structure for a nitrate (NO3) including references to the particles and the locations relative to the central nitrogen.
 *
 * @author Sam Reid
 */
public class Nitrate implements Iterable<Constituent> {
    public ArrayList<Constituent> constituents = new ArrayList<Constituent>();
    public final ImmutableVector2D relativePosition;

    public Nitrate( double nitrogenOxygenSpacing, double angle, ImmutableVector2D relativePosition ) {
        this.relativePosition = relativePosition;
        constituents.add( new Constituent( new OxygenIonParticle(), parseAngleAndMagnitude( nitrogenOxygenSpacing, Math.PI * 2 * 0 / 3.0 + angle ) ) );
        constituents.add( new Constituent( new OxygenIonParticle(), parseAngleAndMagnitude( nitrogenOxygenSpacing, Math.PI * 2 * 1 / 3.0 + angle ) ) );
        constituents.add( new Constituent( new OxygenIonParticle(), parseAngleAndMagnitude( nitrogenOxygenSpacing, Math.PI * 2 * 2 / 3.0 + angle ) ) );
        constituents.add( new Constituent( new NitrogenIonParticle(), ZERO ) );
    }

    public Iterator<Constituent> iterator() {
        return constituents.iterator();
    }
}