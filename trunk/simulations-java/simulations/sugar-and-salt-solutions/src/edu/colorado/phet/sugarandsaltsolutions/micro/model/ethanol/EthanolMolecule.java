// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.ethanol;

import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.CarbonIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.HydrogenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NeutralOxygenParticle;
import edu.colorado.phet.sugarandsaltsolutions.water.model.EthanolPositions;

import static java.lang.Double.parseDouble;

/**
 * This represents an EthanolMolecule, composed of its constituent C,H,O atoms.  See EthanolPositions for how the atoms are located
 *
 * @author Sam Reid
 */
public class EthanolMolecule extends Compound {
    public EthanolMolecule( ImmutableVector2D position ) {
        super( position );

        //Parse the locations and use the specified z-ordering so it will look more realistic
        StringTokenizer lines = new StringTokenizer( EthanolPositions.positions, "\n" );
        while ( lines.hasMoreElements() ) {
            String line = lines.nextToken();
            StringTokenizer elements = new StringTokenizer( line, " " );
            char type = elements.nextToken().charAt( 0 );
            ImmutableVector2D location = EthanolPositions.normalize( new ImmutableVector2D( parseDouble( elements.nextToken() ), parseDouble( elements.nextToken() ) ) );
            switch( type ) {
                case 'H':
                    constituents.add( new Constituent( new HydrogenIonParticle(), location ) );
                    break;
                case 'C':
                    constituents.add( new Constituent( new CarbonIonParticle(), location ) );
                    break;
                case 'O':
                    constituents.add( new Constituent( new NeutralOxygenParticle(), location ) );
                    break;
            }
        }
    }
}
