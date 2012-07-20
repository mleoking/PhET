// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;

import static edu.colorado.phet.common.phetcommon.math.Vector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.view.PhetColorScheme.RED_COLORBLIND;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.Units.picometersToMeters;
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
    public final Color chargeColor;

    //The charge of the atom
    private final double charge;

    //Color to use for neutrally charged objects
    public static final Color NEUTRAL_COLOR = Color.yellow;
    public static final Color POSITIVE_COLOR = RED_COLORBLIND;
    public static final Color NEGATIVE_COLOR = Color.blue;

    //This constructor matches the table given in the design doc and to-do doc,
    public SphericalParticle( double radiusInPM, Color chargeColor, Color atomColor, double charge ) {
        this( picometersToMeters( radiusInPM ) * SugarAndSaltSolutionsApplication.sizeScale.get(), ZERO, atomColor, charge, chargeColor );
    }

    public SphericalParticle( double radius, Vector2D position, Color color, double charge ) {
        this( radius, position, color, charge, null );
    }

    private SphericalParticle( double radius, Vector2D position, Color color, double charge, Color chargeColor ) {
        super( position );
        this.radius = radius;
        this.color = color;
        this.charge = charge;
        this.chargeColor = chargeColor;
    }

    @Override public Shape getShape() {
        return new Ellipse2D.Double( getPosition().getX() - radius, getPosition().getY() - radius, radius * 2, radius * 2 );
    }

    public double getCharge() {
        return charge;
    }

    //Get the value to use for showing partial charge.  Necessary to support showing a subset of particle charges for sucrose: http://www.chemistryland.com/CHM130W/LabHelp/Experiment10/Exp10.html
    public double getPartialChargeDisplayValue() {
        return getCharge();
    }

    //These classes contains state information for particulars particles and ions and permit matching in MicroModel for particle counting.
    public static class Hydrogen extends SphericalParticle {

        public Hydrogen() {
            this( SphericalParticle.NEUTRAL_COLOR, +1 );
        }

        // Constructor for use by clients that need to support other partial charge models.
        protected Hydrogen( Color chargeColor, double charge ) {
            super( 37, chargeColor, white, charge );
        }
    }

    public static class Carbon extends SphericalParticle {
        public Carbon() {
            super( 77, SphericalParticle.NEUTRAL_COLOR, gray, 0 );
        }
    }

    public static class Nitrogen extends SphericalParticle {
        public Nitrogen() {
            super( 75, SphericalParticle.NEGATIVE_COLOR, blue, -1 );
        }
    }

    //Abstract since oxygen ions and oxygen in sucrose/glucose must have different colors
    public abstract static class Oxygen extends SphericalParticle {

        public Oxygen( Color chargeColor ) {
            this( chargeColor, -2 );
        }

        // Constructor for use by clients that need to support other partial charge models.
        protected Oxygen( Color chargeColor, double charge ) {
            super( 73, chargeColor, RED_COLORBLIND, charge );
        }
    }

    //Free oxygen atoms have a negative charge
    public static class FreeOxygen extends Oxygen {
        public FreeOxygen() {
            super( Color.blue );
        }
    }

    //When participating in sucrose or glucose or other neutral crystals oxygen atoms should be shown as neutral
    public static class NeutralOxygen extends Oxygen {
        public NeutralOxygen() {
            super( SphericalParticle.NEUTRAL_COLOR );
        }
    }

    public static class Sodium extends SphericalParticle {
        public Sodium() {
            super( 102, SphericalParticle.POSITIVE_COLOR, magenta, +1 );
        }
    }

    public static class Chloride extends SphericalParticle {
        public Chloride() {
            super( 181, SphericalParticle.NEGATIVE_COLOR, green, -1 );
        }
    }

    public static class Calcium extends SphericalParticle {
        public Calcium() {
            //Calcium should be a dark green
            super( 100, SphericalParticle.POSITIVE_COLOR, new Color( 6, 98, 23 ), +1 );
        }
    }
}