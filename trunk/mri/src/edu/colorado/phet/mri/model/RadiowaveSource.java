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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.quantum.model.PhotonSource;
import edu.colorado.phet.quantum.model.PhotonEmissionListener;
import edu.colorado.phet.quantum.model.Beam;
import edu.colorado.phet.mri.MriConfig;

import java.awt.geom.Point2D;
import java.awt.*;
import java.util.EventListener;

/**
 * RadiowaveSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RadiowaveSource extends Beam {
    private static final double MAX_POWER = MriConfig.MAX_POWER;
    private static final double DEFAULT_FREQUENCY = 1E9;
    private static final double DEFAULT_WAVELENGTH = 1 / DEFAULT_FREQUENCY;

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------
    public static class Orientation{};
    public static Orientation VERTICAL;
    public static Orientation HORIZONTAL;


    private double frequency;
    private double power;
    private Point2D location;
    private double length;
//    private Object orientation;

    public RadiowaveSource( Point2D location, double length, Vector2D direction ) {
        super( DEFAULT_WAVELENGTH, location, length, length, direction, MAX_POWER, 0 );
        this.location = location;
        this.length = length;

        setPhotonsPerSecond( 1 / ( MriConfig.DT / 1000 ) );
        setWavelength( 500E-9 );
    }

    public Orientation getOrientation() {
        Orientation direction = null;
        if( getDirection().getX() == 0 && getDirection().getY() != 0  ) {
            direction = HORIZONTAL;
        }
        if( getDirection().getX() != 0 && getDirection().getY() == 0  ) {
            direction = VERTICAL;
        }
        return direction;
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------

    public void setPower( double value ) {
        power = value;
    }


    public double getFrequency() {
        return 1 / getWavelength();
    }

    public void setFrequency( double frequency ) {
        setWavelength( 1 / frequency );
    }

//    public double getMaxPower() {
//        return MAX_POWER;
//    }
//
//    public double getPower() {
//        return power;
//    }
//
//    public void setPower( double power ) {
//        this.power = power;
//        changeListenerProxy.rateChangeOccurred( new ChangeEvent( this ) );
//    }

//    public Point2D getLocation() {
//        return location;
//    }
//
//    public void setLocation( Point2D location ) {
//        this.location = location;
//    }
//
//    public double getLength() {
//        return length;
//    }
//
//    public void setLength( double length ) {
//        this.length = length;
//    }

//    public Object getOrientation() {
//        return orientation;
//    }
//
//    public void setOrientation( Object orientation ) {
//        this.orientation = orientation;
//    }


    //----------------------------------------------------------------
    // PhotonSource implementation
    //----------------------------------------------------------------
//    private EventChannel changeListenerChannel = new EventChannel( ChangeListener.class );
//    private ChangeListener changeListenerProxy = (ChangeListener)changeListenerChannel.getListenerProxy();
//    private EventChannel photonEmissionChannel = new EventChannel( PhotonEmissionListener.class );
//    private PhotonEmissionListener emissionListenerProxy = (PhotonEmissionListener)photonEmissionChannel.getListenerProxy();
//
//    public Shape getBounds() {
//        return null;
//    }
//
//    public double getPhotonsPerSecond() {
//        return getPower();
//    }
//
//    public double getMaxPhotonsPerSecond() {
//        return getMaxPower();
//    }
//
//    public double getWavelength() {
//        return 1 / getFrequency();
//    }
//
//    public void addChangeListener( ChangeListener listener ) {
//        changeListenerChannel.addListener( listener );
//    }
//
//    public void addPhotonEmissionListener( PhotonEmissionListener listener ) {
//        photonEmissionChannel.addListener( listener );
//    }
//
//    public void removePhotonEmissionListener( PhotonEmissionListener listener ) {
//        photonEmissionChannel.removeListener( listener );
//    }
}
