package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Color;
import java.awt.Shape;
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

    //Color for the charge of the atom, red = positive, yellow = neutral, blue = negative
    public Color chargeColor;

    //Color to use for neutrally charged objects
    private static final Color NEUTRAL_COLOR = Color.yellow;
    private static final Color POSITIVE_COLOR = Color.red;
    private static final Color NEGATIVE_COLOR = Color.blue;

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
            super( 37, SphericalParticle.NEUTRAL_COLOR, white );
        }
    }

    public static class CarbonIonParticle extends SphericalParticle {
        public CarbonIonParticle() {
            super( 77, SphericalParticle.NEUTRAL_COLOR, gray );
        }
    }

    public static class NitrogenIonParticle extends SphericalParticle {
        public NitrogenIonParticle() {
            super( 75, SphericalParticle.NEGATIVE_COLOR, blue );
        }
    }

    //Abstract since oxygen ions and oxygen in sucrose must have different colors
    public abstract static class OxygenIonParticle extends SphericalParticle {
        public OxygenIonParticle( Color chargeColor ) {
            super( 73, chargeColor, red );
        }
    }

    //Free oxygen atoms have a negative charge
    public static class FreeOxygenIonParticle extends OxygenIonParticle {
        public FreeOxygenIonParticle() {
            super( Color.blue );
        }
    }

    //When participating in sucrose, oxygen atoms are neutral
    public static class SucroseOxygenParticle extends OxygenIonParticle {
        public SucroseOxygenParticle() {
            super( SphericalParticle.NEUTRAL_COLOR );
        }
    }


    public static class SodiumIonParticle extends SphericalParticle {
        public SodiumIonParticle() {
            super( 102, SphericalParticle.POSITIVE_COLOR, magenta );
        }
    }

    public static class ChlorideIonParticle extends SphericalParticle {
        public ChlorideIonParticle() {
            super( 181, SphericalParticle.NEGATIVE_COLOR, green );
        }
    }

    public static class CalciumIonParticle extends SphericalParticle {
        public CalciumIonParticle() {
            //Calcium should be a dark green
            super( 100, SphericalParticle.POSITIVE_COLOR, new Color( 6, 98, 23 ) );
        }
    }
}