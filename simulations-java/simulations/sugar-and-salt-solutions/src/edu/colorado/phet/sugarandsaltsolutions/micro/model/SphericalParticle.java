package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.picometersToMeters;
import static java.awt.Color.*;

/**
 * This particle represents a single indivisible spherical particle.
 *
 * @author Sam Reid
 */
public class SphericalParticle extends Particle {
    public final double radius;

    //Color corresponding to the identity of the atom
    public final Color color;

    //Color for the charge of the atom, red = positive, gray = neutral, blue = negative
    public Color chargeColor;

    //This constructor matches the table given in the design doc and to-do doc,
    public SphericalParticle( double radiusInPM, Color chargeColor, Color atomColor ) {
        this( picometersToMeters( radiusInPM ) * MicroModel.sizeScale, ImmutableVector2D.ZERO, atomColor );
        this.chargeColor = chargeColor;
    }

    public SphericalParticle( double radius, ImmutableVector2D position, Color color ) {
        super( position );
        this.radius = radius;
        this.color = color;
    }

    @Override public Shape getShape() {
        return new Ellipse2D.Double( position.get().getX() - radius, position.get().getY() - radius, radius * 2, radius * 2 );
    }

    //These classes contains state information for particulars particles and ions and permit matching in MicroModel for particle counting.
    public static class HydrogenIonParticle extends SphericalParticle {
        public HydrogenIonParticle() {
            super( 37, gray, white );
        }
    }

    public static class CarbonIonParticle extends SphericalParticle {
        public CarbonIonParticle() {
            super( 77, gray, gray );
        }
    }

    public static class NitrogenIonParticle extends SphericalParticle {
        public NitrogenIonParticle() {
            super( 75, gray, blue );
        }
    }

    public static class OxygenIonParticle extends SphericalParticle {
        public OxygenIonParticle() {
            super( 73, gray, red );
        }
    }

    public static class SodiumIonParticle extends SphericalParticle {
        public SodiumIonParticle() {
            super( 102, red, magenta );
        }
    }

    public static class ChlorideIonParticle extends SphericalParticle {
        public ChlorideIonParticle() {
            super( 181, blue, green );
        }
    }

    public static class CalciumIonParticle extends SphericalParticle {
        public CalciumIonParticle() {
            super( 100, red, green.darker() );
        }
    }
}