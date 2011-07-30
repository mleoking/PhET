package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.OpenSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;

/**
 * This crystal for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SodiumChlorideCrystal extends Crystal<SphericalParticle> {

    public SodiumChlorideCrystal( ImmutableVector2D position, double angle ) {
        super( position, new Chloride().radius + new Sodium().radius, angle );
    }

    public SphericalParticle createSeed() {
        return random.nextBoolean() ? new Sodium() : new Chloride();
    }

    @Override public ArrayList<OpenSite<SphericalParticle>> getOpenSites() {
        ArrayList<OpenSite<SphericalParticle>> bondingSites = new ArrayList<OpenSite<SphericalParticle>>();
        for ( final Constituent<SphericalParticle> constituent : new ArrayList<Constituent<SphericalParticle>>( constituents ) ) {
            for ( ImmutableVector2D direction : new ImmutableVector2D[] { NORTH, SOUTH, EAST, WEST } ) {
                ImmutableVector2D relativePosition = constituent.relativePosition.plus( direction );
                if ( !isOccupied( relativePosition ) ) {
                    SphericalParticle opposite = ( constituent.particle instanceof Sodium ) ? new Chloride() : new Sodium();
                    ImmutableVector2D absolutePosition = relativePosition.plus( getPosition() );
                    opposite.setPosition( absolutePosition );
                    bondingSites.add( new OpenSite<SphericalParticle>( relativePosition, opposite.getShape(), new Function0<SphericalParticle>() {
                        public SphericalParticle apply() {
                            return ( constituent.particle instanceof Sodium ) ? new Chloride() : new Sodium();
                        }
                    }, absolutePosition ) );
                }
            }
        }

        return bondingSites;
    }

    private final Random random = new Random();

    private boolean isOccupied( ImmutableVector2D location ) {
        for ( Constituent<SphericalParticle> constituent : constituents ) {
            if ( constituent.relativePosition.minus( location ).getMagnitude() < 1E-12 ) {
                return true;
            }
        }
        return false;
    }
}