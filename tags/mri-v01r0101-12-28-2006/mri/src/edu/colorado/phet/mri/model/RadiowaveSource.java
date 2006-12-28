/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.PhysicsUtil;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.util.IScalar;
import edu.colorado.phet.quantum.model.Beam;

import java.awt.geom.Point2D;

/**
 * RadiowaveSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RadiowaveSource extends Beam implements IScalar {
    private static final double MAX_POWER = MriConfig.MAX_POWER;
    private static final double DEFAULT_FREQUENCY = 2E6;
    private static final double DEFAULT_WAVELENGTH = PhysicsUtil.frequencyToWavelength( DEFAULT_FREQUENCY );
    private static final double MAX_PHOTONS_PER_SEC = 5 / ( MriConfig.DT / 1000 );

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------
    public static class Orientation {
    }

    public static Orientation VERTICAL;
    public static Orientation HORIZONTAL;

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private double power;
    private double currentAmplitude;
    private double runningTime;
    private double period = 100;
    private double phaseAngle;

    /**
     * Constructor
     *
     * @param location
     * @param length
     * @param direction
     */
    public RadiowaveSource( Point2D location, double length, Vector2D direction ) {
        this( location, length, direction, 0 );
    }

    public RadiowaveSource( Point2D location, double length, Vector2D direction, double phaseAngle ) {
        super( DEFAULT_WAVELENGTH, location, length, length, direction, MAX_POWER, 0 );

        // Set enabled by default, but with power off
        setEnabled( true );
        setPower( 0 );

        this.phaseAngle = phaseAngle;
        setWavelength( 500E-9 );

        double freq = MriConfig.MAX_FADING_COIL_FIELD * SampleMaterial.HYDROGEN.getMu() / 2;
        double wavelength = PhysicsUtil.frequencyToWavelength( freq );
        setWavelength( wavelength );
    }

    public Orientation getOrientation() {
        Orientation orientation = null;
//        if( getDirection().getX() == 0 && getDirection().getY() != 0 ) {
//            orientation = HORIZONTAL;
//        }
//        if( getDirection().getX() != 0 && getDirection().getY() == 0 ) {
//            orientation = VERTICAL;
//        }
        return orientation;
    }

    public double getValue() {
        return currentAmplitude;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        if( isEnabled() ) {
            runningTime += dt;
            // amplitude is always >= 0
            currentAmplitude = power * 0.5 * ( 1 + Math.sin( phaseAngle + ( runningTime % period ) / period * Math.PI * 2 ) );
        }
        else {
            currentAmplitude = 0;
        }
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------

    public void setPower( double value ) {
        power = value;
        setPhotonsPerSecond( MAX_PHOTONS_PER_SEC * power / MAX_POWER );
    }

    public double getPower() {
        return power;
    }

    public double getFrequency() {
        return PhysicsUtil.wavelengthToFrequency( getWavelength() );
    }

    public void setFrequency( double frequency ) {
        setWavelength( PhysicsUtil.frequencyToWavelength( frequency ) );
        period = 1 / ( frequency * MriConfig.ModelToView.FREQUENCY );
    }
}
