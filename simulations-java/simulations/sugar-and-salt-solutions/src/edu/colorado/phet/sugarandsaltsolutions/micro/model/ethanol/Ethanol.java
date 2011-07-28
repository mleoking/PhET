// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.ethanol;

import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Carbon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Hydrogen;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NeutralOxygen;
import edu.colorado.phet.sugarandsaltsolutions.water.model.EthanolPositions;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static java.lang.Double.parseDouble;

/**
 * This represents an EthanolMolecule, composed of its constituent C,H,O atoms.  See EthanolPositions for how the atoms are located
 *
 * @author Sam Reid
 */
public class Ethanol extends Compound<SphericalParticle> {
    public Ethanol() {
        this( ZERO, Math.random() * 2 * Math.PI );
    }

    public Ethanol( ImmutableVector2D position, double angle ) {
        super( position, angle );

        //Parse the locations and use the specified z-ordering so it will look more realistic
        StringTokenizer lines = new StringTokenizer( EthanolPositions.positions, "\n" );
        while ( lines.hasMoreElements() ) {
            String line = lines.nextToken();
            StringTokenizer elements = new StringTokenizer( line, " " );
            char type = elements.nextToken().charAt( 0 );
            ImmutableVector2D location = EthanolPositions.normalize( new ImmutableVector2D( parseDouble( elements.nextToken() ), parseDouble( elements.nextToken() ) ) );

            //Create the Carbon, Hydrogen and Oxygen atoms at the right relative locations, but at an arbitrary angle so the molecules won't all be aligned
            //It shouldn't look too regular since it is a fluid
            switch( type ) {
                case 'H':
                    constituents.add( new Constituent<SphericalParticle>( new Hydrogen(), location.getRotatedInstance( angle ) ) );
                    break;
                case 'C':
                    constituents.add( new Constituent<SphericalParticle>( new Carbon(), location.getRotatedInstance( angle ) ) );
                    break;
                case 'O':
                    constituents.add( new Constituent<SphericalParticle>( new NeutralOxygen(), location.getRotatedInstance( angle ) ) );
                    break;
            }
        }
    }
}
