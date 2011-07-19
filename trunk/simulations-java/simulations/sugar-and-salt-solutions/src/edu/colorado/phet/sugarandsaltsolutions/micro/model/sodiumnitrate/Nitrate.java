// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
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
        SphericalParticle o1 = new OxygenIonParticle();
        ImmutableVector2D o1Position = parseAngleAndMagnitude( nitrogenOxygenSpacing, Math.PI * 2 * 0 / 3.0 + angle );
        constituents.add( new Constituent( o1, o1Position ) );

        SphericalParticle o2 = new OxygenIonParticle();
        ImmutableVector2D o2Position = parseAngleAndMagnitude( nitrogenOxygenSpacing, Math.PI * 2 * 1 / 3.0 + angle );
        constituents.add( new Constituent( o2, o2Position ) );

        SphericalParticle o3 = new OxygenIonParticle();
        ImmutableVector2D o3Position = parseAngleAndMagnitude( nitrogenOxygenSpacing, Math.PI * 2 * 2 / 3.0 + angle );
        constituents.add( new Constituent( o3, o3Position ) );

        SphericalParticle nitrogen = new NitrogenIonParticle();
        constituents.add( new Constituent( nitrogen, ZERO ) );
    }

    public Iterator<Constituent> iterator() {
        return constituents.iterator();
    }

    public Constituent getConstituent( int i ) {
        return constituents.get( i );
    }
}
